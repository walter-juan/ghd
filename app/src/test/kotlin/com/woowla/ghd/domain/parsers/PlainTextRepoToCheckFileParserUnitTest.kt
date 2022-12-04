package com.woowla.ghd.domain.parsers

import com.woowla.ghd.domain.entities.RepoToCheck
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PlainTextRepoToCheckFileParserUnitTest : StringSpec({
    "given an empty line nothing is decoded" {
        val emptyLines = ""
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(emptyLines)
        repos shouldBe emptyList()
    }

    "given invalid line without slash then nothing is decoded" {
        val invalidLine = "this is an invalid line"
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(invalidLine)
        repos shouldBe emptyList()
    }

    "given a single line with an empty name nothing is decoded" {
        val lineWithEmptyName = "walter/ "
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(lineWithEmptyName)
        repos shouldBe emptyList()
    }

    "given a single line with an empty owner nothing is decoded" {
        val lineWithEmptyOwner = " /test"
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(lineWithEmptyOwner)
        repos shouldBe emptyList()
    }

    "given a single line with repo and notifications enabled then it is decoded" {
        val lineWithRepoAndNotificationsEnable = "walter/ghd true"
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(lineWithRepoAndNotificationsEnable)
        repos.size shouldBe 1
        repos[0] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "walter",
            name = "ghd",
            pullNotificationsEnabled = true,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null,
        )
    }

    "given a single correct line it is decoded" {
        val validLine = "walter/test"
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(validLine)
        repos.size shouldBe 1
        repos[0] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "walter",
            name = "test",
            pullNotificationsEnabled = false,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null
        )
    }

    "given a three correct lines they are decoded" {
        val validLine = """
            walter/test
            walter/ghd true
            alvaro/vscode false
        """.trimIndent()
        val parser = PlainTextRepoToCheckFileParser()
        val repos = parser.decode(validLine)
        repos.size shouldBe 3
        repos[0] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "walter",
            name = "test",
            pullNotificationsEnabled = false,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null
        )
        repos[1] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "walter",
            name = "ghd",
            pullNotificationsEnabled = true,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null
        )
        repos[2] shouldBe RepoToCheck(
            id = RepoToCheckFileParser.ID,
            owner = "alvaro",
            name = "vscode",
            pullNotificationsEnabled = false,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null
        )
    }

    "two repos are encoded" {
        val twoRepos = listOf(
            RepoToCheck(
                id = 23L,
                owner = "walter",
                name = "ghd",
                pullNotificationsEnabled = true,
                releaseNotificationsEnabled = false,
                groupName = null,
                pullBranchRegex = null
            ),
            RepoToCheck(
                id = 23L,
                owner = "alvaro",
                name = "ghd",
                pullNotificationsEnabled = true,
                releaseNotificationsEnabled = true,
                groupName = "name of the group",
                pullBranchRegex = "the regex"
            ),
        )
        val parser = PlainTextRepoToCheckFileParser()
        val encodedContent = parser.encode(twoRepos)
        val decodedContent = parser.decode(encodedContent)
        decodedContent.size shouldBe 2
        decodedContent[0] shouldBe twoRepos[0].copy(
            id = RepoToCheckFileParser.ID,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null,
        )
        decodedContent[1] shouldBe twoRepos[1].copy(
            id = RepoToCheckFileParser.ID,
            releaseNotificationsEnabled = false,
            groupName = null,
            pullBranchRegex = null,
        )
    }
})