// TODO relations
//package com.woowla.ghd.data.local.room.entities
//
//import androidx.room.Embedded
//import androidx.room.Relation
//
//data class DbSyncResultWithEntriesAndRepos(
//    @Embedded val dbSyncResult: DbSyncResult,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "sync_result_id",
//        entity = DbSyncResultEntry::class,
//    )
//    val dbSyncResultEntries: List<DbSyncResultEntryWithRepoToCheck>,
//)
//
