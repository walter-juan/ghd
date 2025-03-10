package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.BrandGithub
import com.woowla.compose.icon.collections.tabler.tabler.outline.ChevronRight
import com.woowla.compose.icon.collections.tabler.tabler.outline.ExternalLink
import com.woowla.compose.icon.collections.tabler.tabler.outline.Folder
import com.woowla.compose.icon.collections.tabler.tabler.outline.InfoCircle
import com.woowla.compose.icon.collections.tabler.tabler.outline.License
import com.woowla.compose.icon.collections.tabler.tabler.outline.PhotoCircle
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.Section
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.AboutViewModel
import com.woowla.ghd.core.utils.openFolder
import com.woowla.ghd.core.utils.openWebpage

object AboutScreen {
    @Composable
    fun Content(
        viewModel: AboutViewModel,
        appVersion: String,
        onAboutLicensesClick: (() -> Unit),
        onBackClick: (() -> Unit)? = null,
    ) {
        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18nUi.top_bar_title_about,
                    navOnClick = onBackClick
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                Section(
                    title = "App Overview"
                ) {
                    SectionItem(
                        title = "What is this app?",
                        description = "GHD (GitHub dashboard) is designed to display your GitHub pull requests and release updates in a streamlined interface.",
                        leadingIcon = {
                            Icon(
                                imageVector = Tabler.Outline.InfoCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                    )
                    SectionItem(
                        title = "Version",
                        description = appVersion,
                        leadingIcon = {
                            Icon(
                                imageVector = Tabler.Outline.InfoCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    SectionItem(
                        title = "GitHub Repository",
                        description = "View source code and report issues",
                        leadingIcon = {
                            Icon(
                                imageVector = Tabler.Outline.BrandGithub,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    openWebpage(i18nUi.githubRepoLink)
                                }
                            ) {
                                Icon(
                                    imageVector = Tabler.Outline.ExternalLink,
                                    contentDescription = null,
                                )
                            }
                        }
                    )
                }

                Section(title = "App Information") {
                    SectionItem(
                        title = "Application directory",
                        description = "Data for this application is stored in the following location: '${viewModel.appDir}'",
                        leadingIcon = {
                            Icon(
                                imageVector = Tabler.Outline.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    openFolder(viewModel.appDir.toFile())
                                }
                            ) {
                                Icon(
                                    imageVector = Tabler.Outline.ExternalLink,
                                    contentDescription = null,
                                )
                            }
                        }
                    )
                    SectionItem(
                        title = "Icons",
                        description = "This app utilizes icons from the Tabler Icons library",
                        leadingIcon = {
                            Icon(
                                imageVector = Tabler.Outline.PhotoCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    openWebpage(i18nUi.tablerIconsRepoLink)
                                }
                            ) {
                                Icon(
                                    imageVector = Tabler.Outline.ExternalLink,
                                    contentDescription = null,
                                )
                            }
                        }
                    )
                    SectionItem(
                        title = "Third-Party Software and Licenses",
                        description = "View third-party software and licenses",
                        leadingIcon = {
                            Icon(
                                imageVector = Tabler.Outline.License,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = onAboutLicensesClick) {
                                Icon(
                                    imageVector = Tabler.Outline.ChevronRight,
                                    contentDescription = null,
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}