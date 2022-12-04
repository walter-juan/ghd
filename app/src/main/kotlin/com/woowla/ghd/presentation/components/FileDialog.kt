package com.woowla.ghd.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import com.woowla.ghd.presentation.app.i18n
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun FileLoadDialog(
    parent: Frame? = null,
    onCloseRequest: (file: File?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, i18n.file_dialog_choose_file, LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(files.firstOrNull())
                }
            }
        }
    },
    dispose = FileDialog::dispose
)
@Composable
fun FileSaveDialog(
    parent: Frame? = null,
    fileName: String,
    onCloseRequest: (file: File?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, i18n.file_dialog_save_file, SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(files.firstOrNull())
                }
            }
        }.apply {
            file = fileName
        }
    },
    dispose = FileDialog::dispose
)