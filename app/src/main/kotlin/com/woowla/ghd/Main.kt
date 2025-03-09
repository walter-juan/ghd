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
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.StatusPrinter
import com.kdroid.composetray.utils.SingleInstanceManager
import com.woowla.ghd.core.AppFolderFactory
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.core.DiCore
import com.woowla.ghd.data.DiData
import com.woowla.ghd.domain.DiDomainImpl
import com.woowla.ghd.presentation.DiUi
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.app.App
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.app.AppIconsPainter
import com.woowla.ghd.app.Launcher
import com.woowla.ghd.app.TrayIcon
import com.woowla.ghd.app.i18nApp
import kotlinx.coroutines.launch
import net.swiftzer.semver.SemVer
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory

fun main() {
    val logger = setupLogger()
    setupKodein(logger)

    addConsoleAppender(logger)
    addFileAppender(logger = logger, appFolderFactory = GlobalContext.get().get())

    application {
        val synchronizer: Synchronizer = GlobalContext.get().get()
        synchronizer.initialize()
        val trayState: TrayState = GlobalContext.get().get()

        val coroutineScope = rememberCoroutineScope()
        var isWindowVisible by remember { mutableStateOf(true) }
        val isSingleInstance = SingleInstanceManager.isSingleInstance(onRestoreRequest = {
            isWindowVisible = true
        })

        if (!isSingleInstance && !BuildConfig.DEBUG) {
            exitApplication()
            return@application
        }

        Window(
            title = i18nApp.app_name,
            icon = AppIconsPainter.Launcher,
            visible = isWindowVisible,
            state = rememberWindowState(width = AppDimens.windowWidth, height = AppDimens.windowHeight),
            onCloseRequest = { isWindowVisible = false },
        ) {
            MenuBar {
                Menu(i18nApp.menu_bar_menu_actions) {
                    Item(i18nApp.menu_bar_menu_item_synchronize, onClick = { coroutineScope.launch { synchronizer.sync() } })
                }
            }
            App()
        }

        Tray(
            icon = AppIconsPainter.TrayIcon,
            state = trayState,
            tooltip = i18nApp.tray_tooltip,
            onAction = { isWindowVisible = true },
            menu = {
                Item(i18nApp.tray_item_synchronize, onClick = { coroutineScope.launch { synchronizer.sync() } })
                if (isWindowVisible) {
                    Item(i18nApp.tray_item_hide_app, onClick = { isWindowVisible = false })
                } else {
                    Item(i18nApp.tray_item_show_app, onClick = { isWindowVisible = true })
                }
                Item(i18nApp.tray_item_exit, onClick = ::exitApplication)
            },
        )
    }
}

private fun setupKodein(logger: Logger) {
    startKoin {
        modules(
            DiCore.module(
                isDebug = BuildConfig.DEBUG,
                appFolder = BuildConfig.DEBUG_APP_FOLDER,
                logger = logger,
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
}

private fun setupLogger(): Logger {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    context.reset()

    val rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME)
    rootLogger.level = ch.qos.logback.classic.Level.DEBUG

    StatusPrinter.printInCaseOfErrorsOrWarnings(context)

    return rootLogger
}

private fun addConsoleAppender(logger: Logger): Logger {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext

    val consoleAppender = ConsoleAppender<ILoggingEvent>()
    consoleAppender.context = context
    consoleAppender.name = "CONSOLE"
    val consolePatterEncoder = PatternLayoutEncoder()
    consolePatterEncoder.context = context
    consolePatterEncoder.pattern = "%highlight(%date{ISO8601} %-5level %logger{36} - %msg%n%ex)"
    consolePatterEncoder.setParent(consoleAppender)
    consolePatterEncoder.start()
    consoleAppender.encoder = consolePatterEncoder
    consoleAppender.start()

    logger.addAppender(consoleAppender)

    return logger
}

private fun addFileAppender(logger: Logger, appFolderFactory: AppFolderFactory): Logger {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext

    // rolling file
    val logDir = appFolderFactory.folder.resolve("logs").toString()
    val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
    rollingFileAppender.context = context
    rollingFileAppender.name = "FILE"
    rollingFileAppender.file = "$logDir/app.log"
    val rollingFilePatterEncoder = PatternLayoutEncoder()
    rollingFilePatterEncoder.context = context
    rollingFilePatterEncoder.pattern = "%date{ISO8601} %-5level %logger{36} - %msg%n%ex"
    rollingFilePatterEncoder.setParent(rollingFileAppender)
    rollingFilePatterEncoder.start()
    rollingFileAppender.encoder = rollingFilePatterEncoder

    val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
    rollingPolicy.context = context
    rollingPolicy.fileNamePattern = "$logDir/app.%d{yyyy-MM-dd}.log"
    rollingPolicy.maxHistory = 7
    rollingPolicy.setTotalSizeCap(FileSize.valueOf("1GB"))
    rollingPolicy.setParent(rollingFileAppender)
    rollingPolicy.start()
    rollingFileAppender.rollingPolicy = rollingPolicy
    rollingFileAppender.start()

    logger.addAppender(rollingFileAppender)

    return logger
}


