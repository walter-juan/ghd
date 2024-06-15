package com.woowla.ghd.domain.parsers

import com.woowla.ghd.domain.entities.RepoToCheck

interface RepoToCheckFileParser {
    companion object {
        const val ID = 0L
    }
    /**
     * Decode a String content to a RepoToCheck
     * The [RepoToCheck.id] will be set to [ID]
     */
    fun decode(content: String): List<RepoToCheck>

    /**
     * Encode a list of [RepoToCheck] to a String
     */
    fun encode(repoToCheckList : List<RepoToCheck>) : String
}