package com.woowla.ghd.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repo_to_check")
data class RepoToCheck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "group_name") val groupName: String?,
    @ColumnInfo(name = "pull_branch_regex") val pullBranchRegex: String?,
    @ColumnInfo(name = "are_pull_requests_enabled") val arePullRequestsEnabled: Boolean,
    @ColumnInfo(name = "are_releases_enabled") val areReleasesEnabled: Boolean,
) {
    companion object {
        fun newInstance() = RepoToCheck(
            owner = "",
            name = "",
            groupName = null,
            pullBranchRegex = null,
            arePullRequestsEnabled = true,
            areReleasesEnabled = true
        )
    }
}