package com.woowla.ghd.presentation.decorators

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.AlertTriangle
import com.woowla.compose.icon.collections.tabler.tabler.outline.Ban
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleX
import com.woowla.compose.icon.collections.tabler.tabler.outline.Clock
import com.woowla.compose.icon.collections.tabler.tabler.outline.Hourglass
import com.woowla.compose.icon.collections.tabler.tabler.outline.Loader
import com.woowla.compose.icon.collections.tabler.tabler.outline.PlayerPause
import com.woowla.compose.icon.collections.tabler.tabler.outline.QuestionMark
import com.woowla.compose.icon.collections.tabler.tabler.outline.Rocket
import com.woowla.compose.icon.collections.tabler.tabler.outline.Trash
import com.woowla.ghd.domain.entities.DeploymentState

class DeploymentStateDecorator(private val deploymentState: DeploymentState) {
    val text: String = when (deploymentState) {
        DeploymentState.ABANDONED -> "Abandoned"
        DeploymentState.ACTIVE -> "Active"
        DeploymentState.DESTROYED -> "Destroyed"
        DeploymentState.ERROR -> "Error"
        DeploymentState.FAILURE -> "Failure"
        DeploymentState.INACTIVE -> "Inactive"
        DeploymentState.IN_PROGRESS -> "In progress"
        DeploymentState.PENDING -> "Pending"
        DeploymentState.QUEUED -> "Queued"
        DeploymentState.SUCCESS -> "Success"
        DeploymentState.WAITING -> "Waiting"
        DeploymentState.UNKNOWN -> "Unknown"
    }

    val icon: ImageVector = when (deploymentState) {
        DeploymentState.ABANDONED -> Tabler.Outline.Ban
        DeploymentState.ACTIVE -> Tabler.Outline.Rocket
        DeploymentState.DESTROYED -> Tabler.Outline.Trash
        DeploymentState.ERROR -> Tabler.Outline.AlertTriangle
        DeploymentState.FAILURE -> Tabler.Outline.CircleX
        DeploymentState.INACTIVE -> Tabler.Outline.PlayerPause
        DeploymentState.IN_PROGRESS -> Tabler.Outline.Loader
        DeploymentState.PENDING -> Tabler.Outline.Clock
        DeploymentState.QUEUED -> Tabler.Outline.Hourglass
        DeploymentState.SUCCESS -> Tabler.Outline.CircleCheck
        DeploymentState.WAITING -> Tabler.Outline.Clock
        DeploymentState.UNKNOWN -> Tabler.Outline.QuestionMark
    }

    @Composable
    fun iconTint(): Color = when (deploymentState) {
        DeploymentState.ACTIVE,
        DeploymentState.SUCCESS -> Color(0xFF28A745) // GitHub Green

        DeploymentState.ERROR,
        DeploymentState.FAILURE -> Color(0xFFCB2431) // GitHub Red

        DeploymentState.IN_PROGRESS -> Color(0xFF0366D6) // GitHub Blue

        DeploymentState.ABANDONED,
        DeploymentState.PENDING,
        DeploymentState.DESTROYED ,
        DeploymentState.INACTIVE ,
        DeploymentState.QUEUED ,
        DeploymentState.WAITING ,
        DeploymentState.UNKNOWN -> Color.Gray
    }
}