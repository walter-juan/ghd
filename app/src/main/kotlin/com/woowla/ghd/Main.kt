import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kdroid.composetray.tray.api.Tray
import com.kdroid.composetray.utils.SingleInstanceManager
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.app.App
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Launcher
import com.woowla.ghd.presentation.app.i18n
import kotlinx.coroutines.launch
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main() {
    val synchronizer = Synchronizer.INSTANCE

    application {
        val iconPath = Paths.get("src/main/resources/icons/ic_launcher.png").toAbsolutePath().toString()
        val windowsIconPath = Paths.get("src/main/resources/icons/ic_launcher.ico").toAbsolutePath().toString()

        val coroutineScope = rememberCoroutineScope()
        var isWindowVisible by remember { mutableStateOf(true) }
        var appUnlocked by remember { mutableStateOf(false) }
        val isSingleInstance = SingleInstanceManager.isSingleInstance(onRestoreRequest = {
            isWindowVisible = true
        })

        if (!isSingleInstance) {
            exitApplication()
            return@application
        }

        LaunchedEffect("main-synchronizer") {
            EventBus.subscribe("main-subscriber", this, Event.APP_UNLOCKED) {
                appUnlocked = true
            }
        }

        Tray(
            iconPath = iconPath,
            windowsIconPath = windowsIconPath,
            tooltip = i18n.tray_tooltip,
            primaryAction = {
                isWindowVisible = true
            },
            primaryActionLinuxLabel = i18n.tray_linux_primary_action_label,
        ) {
            Item(label = i18n.tray_item_synchronize) {
                if (appUnlocked) {
                    coroutineScope.launch { synchronizer.sync() }
                }
            }
            Divider()
            Item(label = i18n.tray_item_show_app) {
                isWindowVisible = true
            }
            Item(label = i18n.tray_item_hide_app) {
                isWindowVisible = false
            }
            Divider()
            Item(label = i18n.tray_item_exit) {
                dispose()
                exitProcess(0)
            }
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
                    Item(i18n.menu_bar_menu_item_synchronize, enabled = appUnlocked, onClick = { coroutineScope.launch { synchronizer.sync() } })
                }
            }
            App()
        }
    }
}
