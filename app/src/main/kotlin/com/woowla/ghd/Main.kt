import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.apollographql.apollo3.ApolloClient
import com.kdroid.composetray.utils.SingleInstanceManager
import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.AppLogger
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.data.remote.AuthorizationInterceptor
import com.woowla.ghd.data.remote.GitHubPATTokenProvider
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import com.woowla.ghd.domain.parsers.YamlRepoToCheckFileParser
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.AppVersionService
import com.woowla.ghd.domain.services.PullRequestService
import com.woowla.ghd.domain.services.ReleaseService
import com.woowla.ghd.domain.services.RepoToCheckService
import com.woowla.ghd.domain.services.SyncSettingsService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.notifications.NotificationClient
import com.woowla.ghd.notifications.NotificationsSender
import com.woowla.ghd.notifications.NotificationsSenderDefault
import com.woowla.ghd.presentation.app.App
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Launcher
import com.woowla.ghd.presentation.app.TrayIcon
import com.woowla.ghd.presentation.app.i18n
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
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.slf4j.LoggerFactory

fun main() {
    startKoin {
        modules(
            module {
                // synchronization
                single<Synchronizer> {
                    Synchronizer(
                        repoToCheckService = get(),
                        syncSettingsService = get(),
                        synchronizableServiceList = listOf(get<PullRequestService>(), get<ReleaseService>()),
                        localDataSource = get(),
                        eventBus = get(),
                        appLogger = get(),
                    )
                }

                // others
                single { EventBus(get()) }
                single { AppLogger(logger = LoggerFactory.getLogger(AppLogger::class.java)) }
                single<AppFolderFactory> { AppFolderFactory(BuildConfig.DEBUG, BuildConfig.DEBUG_APP_FOLDER) }

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
                single<NotificationClient> { NotificationClient(get()) }
                single<TrayState> { TrayState() }

                // services
                single<AppSettingsService> { AppSettingsService(get(), get()) }
                single<SyncSettingsService> { SyncSettingsService(get(), get()) }
                single<RepoToCheckService> { RepoToCheckService(get(), get(), get()) }
                single<AppVersionService> { AppVersionService(get()) }
                single<PullRequestService> { PullRequestService(get(), get(), get(), get(), get()) }
                single<ReleaseService> { ReleaseService(get(), get(), get(), get()) }

                // remote data layer
                single<RemoteDataSource> { RemoteDataSource(get(), get()) }
                single<ApolloClient> { RemoteDataSource.apolloClientInstance(get()) }
                single<HttpClient> { RemoteDataSource.ktorClientInstance() }
                single<GitHubPATTokenProvider> { GitHubPATTokenProvider(get()) }
                single<AuthorizationInterceptor> { AuthorizationInterceptor(get()) }

                // local data layer
                single<LocalDataSource> { LocalDataSource(get(), get()) }
                single<AppDatabase> { AppDatabase.getRoomDatabase(get()) }
                single<AppProperties> { AppProperties(get()) }
                single<RepoToCheckFileParser> { YamlRepoToCheckFileParser() }
            }
        )
    }

    application {
        val synchronizer: Synchronizer = GlobalContext.get().get()
        synchronizer.initialize()
        val trayState: TrayState = GlobalContext.get().get()

        val coroutineScope = rememberCoroutineScope()
        var isWindowVisible by remember { mutableStateOf(true) }
        val isSingleInstance = SingleInstanceManager.isSingleInstance(onRestoreRequest = {
            isWindowVisible = true
        })

        if (!isSingleInstance) {
            exitApplication()
            return@application
        }

        Window(
            title = i18n.app_name,
            icon = AppIconsPainter.Launcher,
            visible = isWindowVisible,
            state = rememberWindowState(width = AppDimens.windowWidth, height = AppDimens.windowHeight),
            onCloseRequest = { isWindowVisible = false },
        ) {
            MenuBar {
                Menu(i18n.menu_bar_menu_actions) {
                    Item(i18n.menu_bar_menu_item_synchronize, onClick = { coroutineScope.launch { synchronizer.sync() } })
                }
            }
            App()
        }

        Tray(
            icon = AppIconsPainter.TrayIcon,
            state = trayState,
            tooltip = i18n.tray_tooltip,
            onAction = { isWindowVisible = true },
            menu = {
                Item(i18n.tray_item_synchronize, onClick = { coroutineScope.launch { synchronizer.sync() } })
                if (isWindowVisible) {
                    Item(i18n.tray_item_hide_app, onClick = { isWindowVisible = false })
                } else {
                    Item(i18n.tray_item_show_app, onClick = { isWindowVisible = true })
                }
                Item(i18n.tray_item_exit, onClick = ::exitApplication)
            },
        )
    }
}
