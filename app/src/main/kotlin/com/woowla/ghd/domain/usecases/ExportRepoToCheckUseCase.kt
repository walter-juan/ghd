package com.woowla.ghd.domain.usecases

import com.woowla.ghd.domain.parsers.RepoToCheckFileParser
import com.woowla.ghd.domain.parsers.YamlRepoToCheckFileParser
import com.woowla.ghd.utils.UseCaseWithoutParams

open class ExportRepoToCheckUseCase(
    private val getAllReposToCheckUseCase: GetAllReposToCheckUseCase = GetAllReposToCheckUseCase(),
    private val fileParser: RepoToCheckFileParser = YamlRepoToCheckFileParser(),
) : UseCaseWithoutParams<String>() {
    override suspend fun perform(): Result<String> {
        return getAllReposToCheckUseCase.execute().map(fileParser::encode)
    }
}

