package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.woowla.ghd.AppFolderFactory
import java.nio.file.Path

class AboutViewModel(
    appFolderFactory: AppFolderFactory,
) : ViewModel() {
    val appDir: Path = appFolderFactory.folder
}
