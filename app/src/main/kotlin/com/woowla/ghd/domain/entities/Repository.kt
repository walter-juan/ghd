package com.woowla.ghd.domain.entities

data class Repository(val owner: String, val name: String) {
    companion object {
        private val regex = """(http|https)://(www\.)?github.com/([^/.]+)/([^/.]+)(.git)?(/)?""".toRegex()

        fun fromUrl(url: String): Repository {
            val match = regex.find(url)
            requireNotNull(match) { "Invalid GitHub URL: $url" }
            val (protocol, _, owner, project) = match.destructured
            return Repository(owner = owner, name = project)
        }

        fun fromUrlOrNull(url: String): Repository? {
            return try {
                fromUrl(url)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    val url: String = "https://github.com/$owner/$name"

    init {
        require(url.matches(regex)) { "Invalid GitHub URL: $url" }
    }

}