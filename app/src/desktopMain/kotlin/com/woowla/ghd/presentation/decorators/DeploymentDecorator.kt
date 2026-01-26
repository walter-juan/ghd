package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.core.extensions.toRelativeString
import com.woowla.ghd.domain.entities.Deployment
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.presentation.i18nUi

class DeploymentDecorator(deployment: Deployment) {
    val createdAt: String = deployment.createdAt.toRelativeString() ?: i18nUi.generic_unknown
}