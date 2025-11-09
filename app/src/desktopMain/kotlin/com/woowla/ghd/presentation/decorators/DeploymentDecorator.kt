package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.core.extensions.toRelativeString
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.presentation.i18nUi

class DeploymentDecorator(deploymentWithRepo: DeploymentWithRepo) {
    val createdAt: String = deploymentWithRepo.deployment.createdAt.toRelativeString() ?: i18nUi.generic_unknown
    val fullRepo = "${deploymentWithRepo.repoToCheck.repository?.owner}/${deploymentWithRepo.repoToCheck.repository?.name}"
}