package com.woowla.ghd.domain.entities

data class GitHubRepository(val protocol: String = "https", val owner: String, val project: String) {
    companion object {
        private val regex = """(http|https)://github.com/([^/.]+)/([^/.]+)(.git)?(/)?""".toRegex()

        fun fromUrl(url: String): GitHubRepository {
            val match = regex.find(url)
            requireNotNull(match) { "Invalid GitHub URL: $url" }
            val (protocol, owner, project) = match.destructured
            return GitHubRepository(protocol = protocol, owner = owner, project = project)
        }

        fun fromUrlOrNull(url: String): GitHubRepository? {
            return try {
                fromUrl(url)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    val url: String = "$protocol://github.com/$owner/$project"

    init {
        require(url.matches(regex)) { "Invalid GitHub URL: $url" }
    }

}