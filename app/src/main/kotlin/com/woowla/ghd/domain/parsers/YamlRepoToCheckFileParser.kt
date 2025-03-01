package com.woowla.ghd.domain.parsers

import com.charleskorn.kaml.Yaml
import com.woowla.ghd.domain.entities.RepoToCheck
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class YamlRepoToCheckFileParser : RepoToCheckFileParser {
    override fun decode(content: String): List<RepoToCheck> {
        val result = Yaml.default.decodeFromString(YamlConfig.serializer(), content)

        return result.repos.map { yamlRepo ->
            RepoToCheck(
                id = RepoToCheckFileParser.ID,
                owner = yamlRepo.owner.trim(),
                name = yamlRepo.name.trim(),
                groupName = yamlRepo.group?.trim(),
                pullBranchRegex = yamlRepo.pulls.branchRegexFilter?.trim(),
                arePullRequestsEnabled = yamlRepo.pulls.enabled ?: true,
                areReleasesEnabled = yamlRepo.releases.enabled ?: true,
                arePullRequestsNotificationsEnabled = yamlRepo.pulls.notificationsEnabled ?: false,
                areReleasesNotificationsEnabled = yamlRepo.releases.notificationsEnabled ?: false,
            )
        }
    }

    override fun encode(repoToCheckList: List<RepoToCheck>): String {
        val yamlRepos = repoToCheckList.map { repoToCheck ->
            YamlRepo(
                owner = repoToCheck.owner,
                name = repoToCheck.name,
                group = repoToCheck.groupName,
                pulls = YamlPullConfig(
                    enabled = repoToCheck.arePullRequestsEnabled,
                    notificationsEnabled = repoToCheck.arePullRequestsNotificationsEnabled,
                    branchRegexFilter = repoToCheck.pullBranchRegex,
                ),
                releases = YamlReleaseConfig(
                    enabled = repoToCheck.areReleasesEnabled,
                    notificationsEnabled = repoToCheck.areReleasesNotificationsEnabled,
                )
            )
        }

        val yamlConfig = YamlConfig(repos = yamlRepos)
        return Yaml.default.encodeToString(YamlConfig.serializer(), yamlConfig)
    }

    @Serializable
    data class YamlConfig(
        @SerialName("repositories")
        val repos: List<YamlRepo>
    )

    @Serializable
    data class YamlRepo(
        @SerialName("owner")
        val owner: String,
        @SerialName("name")
        val name: String,
        @SerialName("group")
        val group: String? = null,
        @SerialName("pulls")
        val pulls: YamlPullConfig = YamlPullConfig(),
        @SerialName("releases")
        val releases: YamlReleaseConfig = YamlReleaseConfig(),
    )

    @Serializable
    data class YamlPullConfig(
        @SerialName("enabled")
        val enabled: Boolean? = null,
        @SerialName("branch-regex")
        val branchRegexFilter: String? = null,
        @SerialName("notifications-enabled")
        val notificationsEnabled: Boolean? = null,
    )

    @Serializable
    data class YamlReleaseConfig(
        @SerialName("enabled")
        val enabled: Boolean? = null,
        @SerialName("notifications-enabled")
        val notificationsEnabled: Boolean? = null,
    )
}