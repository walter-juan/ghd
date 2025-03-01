package com.woowla.ghd.domain.entities

data class Repository(
    val id: String,
    val name: String,
    val owner: String,
    val url: String,
    val imageUrl: String?,
    val description: String?,
    val stargazerCount: Int,
    val licenseInfo: String?,
    val primaryLanguageName: String?,
    val primaryLanguageColor: String?,
) {
    val fullName: String
        get() = "$owner/$name"
}