package com.woowla.ghd.data.remote.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiGhdRelease(@SerialName("tag_name") val tag: String)