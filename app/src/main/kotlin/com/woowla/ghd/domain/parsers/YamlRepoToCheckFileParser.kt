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
                owner = yamlRepo.owner,
                name = yamlRepo.name,
                groupName = yamlRepo.group,
                pullBranchRegex = yamlRepo.pulls.branchRegexFilter,
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
                    branchRegexFilter = repoToCheck.pullBranchRegex
                ),
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
    )

    @Serializable
    data class YamlPullConfig(
        @SerialName("branch-regex")
        val branchRegexFilter: String? = null
    )
}