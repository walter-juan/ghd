package com.woowla.ghd.domain.parsers

import com.woowla.ghd.domain.entities.RepoToCheck

class PlainTextRepoToCheckFileParser : RepoToCheckFileParser {
    override fun decode(content: String): List<RepoToCheck> {
        val lines = content.lines()

        val repositoryList = lines
            .filter { line ->
                validate(line)
            }
            .map { line ->
                parse(line)
            }
        return repositoryList
    }

    override fun encode(repoToCheckList: List<RepoToCheck>): String {
        return repoToCheckList.map { repoToCheck ->
            "${repoToCheck.owner}/${repoToCheck.name} ${repoToCheck.pullNotificationsEnabled}"
        }.joinToString(separator = "\n")
    }

    private fun parse(line: String): RepoToCheck {
        val owner = line.split("/").getOrNull(0)
        val name = line.split("/").getOrNull(1)?.split(" ")?.getOrNull(0)
        val notificationsEnabled = line.split(" ").getOrElse(1) { "false" }
        require(!owner.isNullOrBlank()) { "owner is null or blank" }
        require(!name.isNullOrBlank()) { "name is null or blank" }
        return RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = owner,
            name = name,
            pullNotificationsEnabled = notificationsEnabled.toBoolean(),
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null,
        )
    }

    private fun validate(line: String): Boolean {
        return line.matches("""^(\w|-)+(/)(\w|-)+( (true|false))*${'$'}""".toRegex())
    }
}