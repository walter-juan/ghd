package com.woowla.ghd.domain.usecases

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File
import com.woowla.ghd.domain.usecases.SaveRepoToCheckBulkUseCase.Repository

open class TestableSaveRepoToCheckBulkUseCase(val lines: List<String>): SaveRepoToCheckBulkUseCase(fileParser = TestablePlainTextFileParser(lines)) {
    val repos = mutableListOf<Repository>()

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

    "given a three correct lines they are stored" {
        val validLine = listOf("walter/test", "walter/ghd true", "alvaro/vscode false")
        val repos = parseRepos(validLine)
        repos.size shouldBe 3
        repos[0] shouldBe Repository(owner = "walter", name = "test")
        repos[1] shouldBe Repository(owner = "walter", name = "ghd", pullNotificationsEnabled = true)
        repos[2] shouldBe Repository(owner = "alvaro", name = "vscode")
    }
})
