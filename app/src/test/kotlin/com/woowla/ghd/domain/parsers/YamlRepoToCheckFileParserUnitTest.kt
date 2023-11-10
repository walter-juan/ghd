package com.woowla.ghd.domain.parsers

import com.charleskorn.kaml.EmptyYamlDocumentException
import com.woowla.ghd.domain.entities.RepoToCheck
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class YamlRepoToCheckFileParserUnitTest : StringSpec({
    "nothing is decoded with an invalid file" {
        val invalidText = """
            this is an invalid file with
            some line
        """.trimIndent()

        shouldThrowAny {
            val parser = YamlRepoToCheckFileParser()
            parser.decode(invalidText)
        }
    }

    "nothing is decoded with empty file" {
        val emptyText = ""

        shouldThrow<EmptyYamlDocumentException> {
            val parser = YamlRepoToCheckFileParser()
            parser.decode(emptyText)
        }
    }

    "one repo is decoded" {
        val oneRepo = """
            
            repositories:
              - owner: the owner
                name: the repo name
                group: the group
                pulls:
                  enabled: true
                  branch-regex: "the regex"
                releases:
                  enabled: false


        """.trimIndent()

        val parser = YamlRepoToCheckFileParser()
        val repos = parser.decode(oneRepo)

        repos.size shouldBe 1
        repos[0] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "the owner",
            name = "the repo name",
            groupName = "the group",
            pullBranchRegex = "the regex",
            arePullRequestsEnabled = true,
            areReleasesEnabled = false
        )
    }

    "one repo with minimum data is decoded with default values" {
        val oneRepo = """
            repositories:
              - owner: the owner
                name: the repo name
        """.trimIndent()

        val parser = YamlRepoToCheckFileParser()
        val repos = parser.decode(oneRepo)

        repos.size shouldBe 1
        repos[0] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "the owner",
            name = "the repo name",
            groupName = null,
            pullBranchRegex = null,
            arePullRequestsEnabled = true,
            areReleasesEnabled = true
        )
    }

    "two repos are decoded" {
        val twoRepos = """
            repositories:
              - owner: the owner
                name: the repo name
                group: the group
                pulls:
                  enabled: true
                  branch-regex: "the regex"
                releases:
                  enabled: true
              - owner: the second owner
                name: the repo name of the second
                group: the group 2
                pulls:
                  enabled: false
                  branch-regex: "another regex"
                releases:
                  enabled: true
        """.trimIndent()

        val parser = YamlRepoToCheckFileParser()
        val repos = parser.decode(twoRepos)

        repos.size shouldBe 2
        repos[0] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "the owner",
            name = "the repo name",
            groupName = "the group",
            pullBranchRegex = "the regex",
            arePullRequestsEnabled = true,
            areReleasesEnabled = true
        )
        repos[1] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "the second owner",
            name = "the repo name of the second",
            groupName = "the group 2",
            pullBranchRegex = "another regex",
            arePullRequestsEnabled = false,
            areReleasesEnabled = true
        )
    }

    "empty repos are encoded to an empty list" {
        val emptyRepos = emptyList<RepoToCheck>()
        val parser = YamlRepoToCheckFileParser()
        val encodedContent = parser.encode(emptyRepos)
        val decodedContent = parser.decode(encodedContent)
        decodedContent shouldBe emptyList()
    }

    "two repos are encoded" {
        val twoRepos = listOf(
            RepoToCheck(
                id = 23L,
                owner = "walter",
                name = "ghd",
                groupName = null,
                pullBranchRegex = null,
                arePullRequestsEnabled = false,
                areReleasesEnabled = true
            ),
            RepoToCheck(
                id = 23L,
                owner = "alvaro",
                name = "ghd",
                groupName = "name of the group",
                pullBranchRegex = "the regex",
                arePullRequestsEnabled = true,
                areReleasesEnabled = true
            ),
        )
        val parser = YamlRepoToCheckFileParser()
        val encodedContent = parser.encode(twoRepos)
        val decodedContent = parser.decode(encodedContent)
        decodedContent.size shouldBe 2
        decodedContent[0] shouldBe twoRepos[0].copy(id = RepoToCheckFileParser.ID)
        decodedContent[1] shouldBe twoRepos[1].copy(id = RepoToCheckFileParser.ID)
    }
})