package com.woowla.ghd.domain.usecases

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

open class TestablePlainTextFileParser(val lines: List<String>): PlainTextFileParser(){
    override fun getFileLines(params: File): List<String> {
        return lines
    }
}

class PlainTextFileParserUnitTest : StringSpec({
    suspend fun parseRepos(lines: List<String>): List<SaveRepoToCheckBulkUseCase.Repository> {
        val uselessFile = File("this/is/a/fake/path/and-this-is-a-file.txt")
        val fileParser = TestablePlainTextFileParser(lines)
        return fileParser.parse(uselessFile)
    }

    "given an empty line nothing is parsed" {
        val emptyLines = listOf("")
        val repos = parseRepos(emptyLines)
        repos shouldBe emptyList()
    }

    "given invalid line without slash then nothing is parsed" {
        val invalidLine = listOf("this is an invalid line")
        val repos = parseRepos(invalidLine)
        repos shouldBe emptyList()
    }

    "given a single line with an empty name nothing is parsed" {
        val lineWithEmptyName = listOf("walter/ ")
        val repos = parseRepos(lineWithEmptyName)
        repos shouldBe emptyList()
    }

    "given a single line with an empty owner nothing is parsed" {
        val lineWithEmptyOwner = listOf(" /test")
        val repos = parseRepos(lineWithEmptyOwner)
        repos shouldBe emptyList()
    }

    "given a single line with repo and notifications enabled then it is parsed" {
        val lineWithRepoAndNotificationsEnable = listOf("walter/ghd true")
        val repos = parseRepos(lineWithRepoAndNotificationsEnable)
        repos.size shouldBe 1
        repos[0] shouldBe SaveRepoToCheckBulkUseCase.Repository(
            owner = "walter",
            name = "ghd",
            notificationsEnabled = true
        )
    }

    "given a single correct line it is parsed" {
        val validLine = listOf("walter/test")
        val repos = parseRepos(validLine)
        repos.size shouldBe 1
        repos[0] shouldBe SaveRepoToCheckBulkUseCase.Repository(owner = "walter", name = "test")
    }

    "given a three correct lines they are parsed" {
        val validLine = listOf("walter/test", "walter/ghd true", "alvaro/vscode false")
        val repos = parseRepos(validLine)
        repos.size shouldBe 3
        repos[0] shouldBe SaveRepoToCheckBulkUseCase.Repository(owner = "walter", name = "test")
        repos[1] shouldBe SaveRepoToCheckBulkUseCase.Repository(
            owner = "walter",
            name = "ghd",
            notificationsEnabled = true
        )
        repos[2] shouldBe SaveRepoToCheckBulkUseCase.Repository(owner = "alvaro", name = "vscode")
    }
})