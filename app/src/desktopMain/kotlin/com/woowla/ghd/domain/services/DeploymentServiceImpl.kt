package com.woowla.ghd.domain.services

import com.woowla.ghd.core.AppLogger
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.mappers.toSyncResultEntry
import kotlin.time.Clock
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class DeploymentServiceImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val appLogger: AppLogger,
) : DeploymentService {
    override suspend fun getAll(): Result<List<DeploymentWithRepo>> {
        // TODO implement
        val environments = listOf<String>("Production")
        return localDataSource.getAllReposToCheck().map { repoToChecks ->
            repoToChecks.filter { it.groupName == "deployments_enabled" }.mapNotNull { remoteDataSource.getDeployments(it, environments).getOrNull()?.data }.flatten()
        }
    }

    override suspend fun cleanUp(syncSettings: SyncSettings) {
        // TODO implement
    }

    override suspend fun synchronize(
        syncResultId: Long,
        syncSettings: SyncSettings,
        repoToCheckList: List<RepoToCheck>
    ): List<SyncResultEntry> {
        appLogger.d("Synchronizer :: sync :: deployments :: start")
        val deploymentsSyncStartAt = Clock.System.now()
        val deploymentsBefore = getAll().getOrDefault(listOf())
        // TODO do not force the environments
        val environments = listOf<String>("Production")
        // TODO implement areDeploymentsEnabled -> repoToCheckList.filter { it.areDeploymentsEnabled }
        val enabledRepoToCheckList = repoToCheckList.filter { it.groupName == "deployments_enabled" }

        // fetch all remote deployments
        val apiDeploymentsResultsDeferred = coroutineScope {
            enabledRepoToCheckList.map { repoToCheck ->
                val startAt = Clock.System.now()
                async {
                    val deployments = remoteDataSource.getDeployments(repoToCheck, environments)
                    Triple(repoToCheck, startAt, deployments)
                }
            }
        }
        val apiResponseResults = apiDeploymentsResultsDeferred.awaitAll()
        appLogger.d("Synchronizer :: sync :: deployments :: fetch remote took ${(Clock.System.now() - deploymentsSyncStartAt).inWholeMilliseconds} ms")

        // map to sync results
        val syncResultEntries = apiResponseResults.map { (repoToCheck, startAt, apiResponseResult) ->
            apiResponseResult.toSyncResultEntry(
                syncResultId = syncResultId,
                repoToCheckId = repoToCheck.id,
                origin = SyncResultEntry.Origin.DEPLOYMENT,
                startAt = startAt
            )
        }

        // TODO update the local deployments
        // TODO remove deployments non returned from remote
        // TODO send the notifications

        appLogger.d("Synchronizer :: sync :: deployments :: finish took ${(Clock.System.now() - deploymentsSyncStartAt).inWholeMilliseconds} ms")
        return syncResultEntries
    }
}