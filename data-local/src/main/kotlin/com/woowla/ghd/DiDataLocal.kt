package com.woowla.ghd

import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.local.LocalDataSourceImpl
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

object DiDataLocal {
    fun module(): Module = module {
        // local data layer
        single<LocalDataSource> { LocalDataSourceImpl(get(), get()) }
        single<AppDatabase> { AppDatabase.getRoomDatabase(get()) }
        single<AppProperties> { AppProperties(get()) }
    }
}

