package com.woowla.ghd.domain.entities

enum class DeploymentStatusState {
    ERROR,
    FAILURE,
    INACTIVE,
    IN_PROGRESS,
    PENDING,
    QUEUED,
    SUCCESS,
    WAITING,
    UNKNOWN,
}