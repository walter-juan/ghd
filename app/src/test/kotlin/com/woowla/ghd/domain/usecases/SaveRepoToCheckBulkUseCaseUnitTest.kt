package com.woowla.ghd.domain.usecases

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File
import com.woowla.ghd.domain.usecases.SaveRepoToCheckBulkUseCase.Repository

open class TestableSaveRepoToCheckBulkUseCase(val lines: List<String>): SaveRepoToCheckBulkUseCase() {
    val repos = mutableListOf<Repository>()

    override fun getFileLines(params: File): List<String> {
        return lines
    }

    override suspend fun storeToDataSource(repo: Repository) {
        repos.add(repo)
    }
}

class SaveRepoToCheckBulkUseCaseUnitTest : StringSpec({
    suspend fun parseRepos(lines: List<String>): List<Repository> {
        val uselessFile = File("this/is/a/fake/path/and-this-is-a-file.txt")
        val useCase = TestableSaveRepoToCheckBulkUseCase(lines)
        useCase.execute(uselessFile)
        return useCase.repos
    }

    "given an empty line nothing is stored" {
        val emptyLines = listOf("")
        val repos = parseRepos(emptyLines)
        repos shouldBe emptyList()
    }

    "given invalid line without slash then nothing is stored" {
        val invalidLine = listOf("this is an invalid line")
        val repos = parseRepos(invalidLine)
        repos shouldBe emptyList()
    }

    "given a single line with an empty name nothing is stored" {
        val lineWithEmptyName = listOf("walter/ ")
        val repos = parseRepos(lineWithEmptyName)
        repos shouldBe emptyList()
    }

    "given a single line with an empty owner nothing is stored" {
        val lineWithEmptyOwner = listOf(" /test")
        val repos = parseRepos(lineWithEmptyOwner)
        repos shouldBe emptyList()
    }

    "given a single line with repo and notifications enabled then it is store" {
        val lineWithRepoAndNotificationsEnable = listOf("walter/ghd true")
        val repos = parseRepos(lineWithRepoAndNotificationsEnable)
        repos.size shouldBe 1
        repos[0] shouldBe Repository(owner = "walter", name = "ghd", notificationsEnabled = true)
    }
    
    "given a single correct line it is stored" {
        val validLine = listOf("walter/test")
        val repos = parseRepos(validLine)
        repos.size shouldBe 1
        repos[0] shouldBe Repository(owner = "walter", name = "test")
    }

    "given a three correct lines they are stored" {
        val validLine = listOf("walter/test", "walter/ghd true", "alvaro/vscode false")
        val repos = parseRepos(validLine)
        repos.size shouldBe 3
        repos[0] shouldBe Repository(owner = "walter", name = "test")
        repos[1] shouldBe Repository(owner = "walter", name = "ghd", notificationsEnabled = true)
        repos[2] shouldBe Repository(owner = "alvaro", name = "vscode")
    }
})