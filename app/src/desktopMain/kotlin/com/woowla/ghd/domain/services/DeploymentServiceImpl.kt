package com.woowla.ghd.domain.services

import com.woowla.ghd.core.AppLogger
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.entities.filterDeployments
import com.woowla.ghd.domain.entities.filterNotSyncValid
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
        return localDataSource.getAllDeployments()
            .mapCatching { deployments ->
                deployments.sorted()
            }
    }

    override suspend fun cleanUp(syncSettings: SyncSettings) {
        getAll()
            .mapCatching { deployment ->
                deployment.filterNotSyncValid()
            }
            .mapCatching { deployment ->
                deployment.map { it.deployment.id }
            }
            .onSuccess { deploymentIds ->
                localDataSource.removeDeployments(deploymentIds)
            }
    }

    override suspend fun synchronize(
        syncResultId: Long,
        syncSettings: SyncSettings,
        repoToCheckList: List<RepoToCheck>
    ): List<SyncResultEntry> {
        appLogger.d("Synchronizer :: sync :: deployments :: start")
        val deploymentsSyncStartAt = Clock.System.now()
        val deploymentsBefore = getAll().getOrDefault(listOf())
        val enabledRepoToCheckList = repoToCheckList.filter { it.areDeploymentsEnabled }

        // fetch all remote deployments
        val apiDeploymentsResultsDeferred = coroutineScope {
            enabledRepoToCheckList.map { repoToCheck ->
                val startAt = Clock.System.now()
                async {
                    val deployments = remoteDataSource.getDeployments(
                        repoToCheck = repoToCheck,
                        environments = repoToCheck.deploymentEnvironmentsList,
                        limit = repoToCheck.deploymentDownloadLimit,
                    )
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
        // update the local deployments
        val deploymentsWithRepos = apiResponseResults
            .map { (repoToCheck, _, apiResponseResult) ->
                apiResponseResult.getOrNull()?.data ?: listOf()
            }
            .flatten()
            .filterDeployments()
        localDataSource.upsertDeployments(deploymentsWithRepos.map { it.deployment })
        // remove pull requests non returned from remote
        val deploymentsIdsToRemove = deploymentsBefore.map { it.deployment.id } - deploymentsWithRepos.map { it.deployment.id }.toSet()
        localDataSource.removeDeployments(deploymentsIdsToRemove)
        cleanUp(syncSettings)

        appLogger.d("Synchronizer :: sync :: deployments :: finish took ${(Clock.System.now() - deploymentsSyncStartAt).inWholeMilliseconds} ms")
        return syncResultEntries
    }
}