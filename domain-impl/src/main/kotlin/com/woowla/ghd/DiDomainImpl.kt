package com.woowla.ghd

import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import com.woowla.ghd.domain.parsers.YamlRepoToCheckFileParser
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.AppSettingsServiceImpl
import com.woowla.ghd.domain.services.AppVersionService
import com.woowla.ghd.domain.services.AppVersionServiceImpl
import com.woowla.ghd.domain.services.PullRequestService
import com.woowla.ghd.domain.services.PullRequestServiceImpl
import com.woowla.ghd.domain.services.ReleaseService
import com.woowla.ghd.domain.services.ReleaseServiceImpl
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.domain.services.RepoToCheckServiceImpl
import com.woowla.ghd.domain.services.RepositoryService
import com.woowla.ghd.domain.services.RepositoryServiceImpl
import com.woowla.ghd.domain.services.SyncSettingsService
import com.woowla.ghd.domain.services.SyncSettingsServiceImpl
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.domain.synchronization.SynchronizerImpl
import net.swiftzer.semver.SemVer
import org.koin.core.module.Module
import org.koin.dsl.module

object DiDomainImpl {
    fun module(
        appVersion: SemVer,
    ): Module = module {
        // synchronization
        single<Synchronizer> {
            SynchronizerImpl(
                repoToCheckService = get(),
                syncSettingsService = get(),
                synchronizableServiceList = listOf(get<PullRequestService>(), get<ReleaseService>()),
                localDataSource = get(),
                eventBus = get(),
                appLogger = get(),
            )
        }

        // services
        single<AppSettingsService> { AppSettingsServiceImpl(get(), get()) }
        single<SyncSettingsService> { SyncSettingsServiceImpl(get(), get()) }
        single<RepoToCheckService> { RepoToCheckServiceImpl(get(), get(), get()) }
        single<AppVersionService> { AppVersionServiceImpl(currentVersion = appVersion, remoteDataSource = get()) }
        single<PullRequestService> { PullRequestServiceImpl(get(), get(), get(), get(), get()) }
        single<ReleaseService> { ReleaseServiceImpl(get(), get(), get(), get(), get()) }
        single<RepositoryService> { RepositoryServiceImpl(get()) }

        // parsers
        single<RepoToCheckFileParser> { YamlRepoToCheckFileParser() }
    }
}

