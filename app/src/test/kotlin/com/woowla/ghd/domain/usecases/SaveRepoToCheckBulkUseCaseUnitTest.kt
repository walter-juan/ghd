package com.woowla.ghd.domain.usecases

import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.parsers.PlainTextRepoToCheckFileParser
import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

open class TestableImportRepoToCheckUseCase(
    fileParser: RepoToCheckFileParser
): ImportRepoToCheckUseCase(fileParser = fileParser) {
    val repos = mutableListOf<RepoToCheck>()

    override suspend fun storeToDataSource(repo: RepoToCheck) {
        repos.add(repo)
    }
}

class SaveRepoToCheckBulkUseCaseUnitTest : StringSpec({
    suspend fun parseRepos(content: String): List<RepoToCheck> {
        val useCase = TestableImportRepoToCheckUseCase(
            fileParser = PlainTextRepoToCheckFileParser()
        )
        useCase.execute(content)
        return useCase.repos
    }

    "given a three correct lines they are stored" {
        val validLine = """
            walter/test
            walter/ghd true
            alvaro/vscode false
        """.trimIndent()
        val repos = parseRepos(validLine)
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
})
