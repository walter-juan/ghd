package com.woowla.ghd

import com.woowla.ghd.notifications.NotificationsSender
import com.woowla.ghd.notifications.NotificationsSenderDefault
import com.woowla.ghd.presentation.viewmodels.AboutViewModel
import com.woowla.ghd.presentation.viewmodels.NotificationsStateMachine
import com.woowla.ghd.presentation.viewmodels.NotificationsViewModel
import com.woowla.ghd.presentation.viewmodels.PullRequestsStateMachine
import com.woowla.ghd.presentation.viewmodels.PullRequestsViewModel
import com.woowla.ghd.presentation.viewmodels.ReleasesStateMachine
import com.woowla.ghd.presentation.viewmodels.ReleasesViewModel
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditStateMachine
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel
import com.woowla.ghd.presentation.viewmodels.ReposToCheckBulkStateMachine
import com.woowla.ghd.presentation.viewmodels.ReposToCheckBulkViewModel
import com.woowla.ghd.presentation.viewmodels.ReposToCheckStateMachine
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel
import com.woowla.ghd.presentation.viewmodels.SettingsStateMachine
import com.woowla.ghd.presentation.viewmodels.SettingsViewModel
import com.woowla.ghd.presentation.viewmodels.SplashViewModel
import com.woowla.ghd.presentation.viewmodels.SyncResultEntriesStateMachine
import com.woowla.ghd.presentation.viewmodels.SyncResultEntriesViewModel
import com.woowla.ghd.presentation.viewmodels.SyncResultsStateMachine
import com.woowla.ghd.presentation.viewmodels.SyncResultsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DiUi {
    enum class Name {
        ABOUT_LIBRARIES_JSON_FILE_NAME
    }

    fun module(
        aboutLibrariesJsonFileName: String = "aboutlibraries.json"
    ): Module = module {
        // view models
        viewModel<SplashViewModel> { SplashViewModel(get()) }
        viewModel<AboutViewModel> { AboutViewModel(get()) }
        viewModel<PullRequestsViewModel> { PullRequestsViewModel(get(), get()) }
        viewModel<NotificationsViewModel> { NotificationsViewModel(get()) }
        viewModel<ReleasesViewModel> { ReleasesViewModel(get(), get()) }
        viewModel<ReposToCheckBulkViewModel> { ReposToCheckBulkViewModel(get(), get()) }
        viewModel<ReposToCheckViewModel> { ReposToCheckViewModel(get(), get()) }
        viewModel<RepoToCheckEditViewModel> { (repoToCheckId: Long?) ->
            RepoToCheckEditViewModel(get { parametersOf(repoToCheckId) })
        }
        viewModel<SettingsViewModel> { SettingsViewModel(get()) }
        viewModel<SyncResultEntriesViewModel> { (syncResultId: Long) ->
            SyncResultEntriesViewModel(get { parametersOf(syncResultId) })
        }
        viewModel<SyncResultsViewModel> { SyncResultsViewModel(get(), get()) }

        // state machines
        factory<PullRequestsStateMachine> { PullRequestsStateMachine(get(), get(), get()) }
        factory<NotificationsStateMachine> { NotificationsStateMachine(get()) }
        factory<ReleasesStateMachine> { ReleasesStateMachine(get(), get(), get()) }
        factory<ReposToCheckBulkStateMachine> { ReposToCheckBulkStateMachine(get()) }
        factory<ReposToCheckStateMachine> { ReposToCheckStateMachine(get(), get()) }
        factory<RepoToCheckEditStateMachine> { (repoToCheckId: Long?) ->
            RepoToCheckEditStateMachine(repoToCheckId = repoToCheckId, get())
        }
        factory<SettingsStateMachine> { SettingsStateMachine(get(), get()) }
        factory<SyncResultEntriesStateMachine> { (syncResultId: Long) ->
            SyncResultEntriesStateMachine(syncResultId = syncResultId, get())
        }
        factory<SyncResultsStateMachine> { SyncResultsStateMachine(get()) }

        // notifications
        single<NotificationsSender> { NotificationsSenderDefault(get()) }

        // others
        single<String>(named(Name.ABOUT_LIBRARIES_JSON_FILE_NAME)) { aboutLibrariesJsonFileName }
    }
}
