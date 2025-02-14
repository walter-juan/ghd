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
import com.kdroid.composetray.utils.SingleInstanceManager
import com.woowla.ghd.AppLogger
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.DiCore
import com.woowla.ghd.DiData
import com.woowla.ghd.DiDomainImpl
import com.woowla.ghd.DiUi
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.presentation.app.App
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Launcher
import com.woowla.ghd.presentation.app.TrayIcon
import com.woowla.ghd.presentation.app.i18n
import kotlinx.coroutines.launch
import net.swiftzer.semver.SemVer
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory

fun main() {
    startKoin {
        modules(
            DiCore.module(
                isDebug = BuildConfig.DEBUG,
                appFolder = BuildConfig.DEBUG_APP_FOLDER,
                logger = LoggerFactory.getLogger(AppLogger::class.java),
            ),
            DiData.module(
                ghOwner = BuildConfig.GH_GHD_OWNER,
                ghRepo = BuildConfig.GH_GHD_REPO,
            ),
            DiDomainImpl.module(
                appVersion = SemVer.parse(BuildConfig.APP_VERSION),
            ),
            DiUi.module(),
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
