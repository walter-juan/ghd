import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.notifications.NotificationClient
import com.woowla.ghd.presentation.app.App
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Launcher
import com.woowla.ghd.presentation.app.TrayIcon
import com.woowla.ghd.presentation.app.i18n
import kotlinx.coroutines.launch

fun main() {
    val synchronizer = Synchronizer.INSTANCE

    application {
        val coroutineScope = rememberCoroutineScope()
        var isVisible by remember { mutableStateOf(true) }
        var appUnlocked by remember { mutableStateOf(false) }

        LaunchedEffect("main-synchronizer") {
            EventBus.subscribe("main-subscriber", this, Event.APP_UNLOCKED) {
                appUnlocked = true
            }
        }

        Window(
            title = i18n.app_name,
            icon = AppIconsPainter.Launcher,
            visible = isVisible,
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = { isVisible = false },
        ) {
            MenuBar {
                Menu(i18n.menu_bar_menu_actions) {
                    Item(i18n.menu_bar_menu_item_synchronize, enabled = appUnlocked, onClick = { coroutineScope.launch { synchronizer.sync() } })
                }
            }
            App()
        }

        Tray(
            AppIconsPainter.TrayIcon,
            state = NotificationClient.trayStateInstance,
            tooltip = i18n.tray_tooltip,
            onAction = { isVisible = true },
            menu = {
                Item(i18n.tray_item_synchronize, enabled = appUnlocked, onClick = { coroutineScope.launch { synchronizer.sync() } })
                if (isVisible) {
                    Item(i18n.tray_item_hide_app, onClick = { isVisible = false })
                } else {
                    Item(i18n.tray_item_show_app, onClick = { isVisible = true })
                }
                Item(i18n.tray_item_exit, onClick = ::exitApplication)
            },
        )
    }
}
