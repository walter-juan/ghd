package com.woowla.ghd.data.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.woowla.ghd.domain.entities.RepoToCheck
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo

@Entity(tableName = "repo_to_check")
@KonvertFrom(RepoToCheck::class)
@KonvertTo(RepoToCheck::class)
data class DbRepoToCheck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @Embedded val repository: DbRepository?,
    @ColumnInfo(name = "group_name") val groupName: String?,
    @ColumnInfo(name = "pull_branch_regex") val pullBranchRegex: String?,
    @ColumnInfo(name = "are_pull_requests_enabled") val arePullRequestsEnabled: Boolean,
    @ColumnInfo(name = "are_releases_enabled") val areReleasesEnabled: Boolean,
    @ColumnInfo(name = "are_pull_requests_notifications_enabled", defaultValue = "0") val arePullRequestsNotificationsEnabled: Boolean,
    @ColumnInfo(name = "are_releases_notifications_enabled", defaultValue = "0") val areReleasesNotificationsEnabled: Boolean,
    @ColumnInfo(name = "are_deployments_enabled", defaultValue = "0") val areDeploymentsEnabled: Boolean,
) {
    companion object
}