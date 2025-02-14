package com.woowla.ghd

import com.apollographql.apollo3.ApolloClient
import com.woowla.ghd.data.local.LocalDataSource
import com.woowla.ghd.data.local.LocalDataSourceImpl
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.data.remote.AuthorizationInterceptor
import com.woowla.ghd.data.remote.GitHubPATTokenProvider
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.data.remote.RemoteDataSourceImpl
import io.ktor.client.*
import org.koin.core.module.Module
import org.koin.dsl.module

object DiData {
    fun module(
        ghOwner: String,
        ghRepo: String,
    ): Module = module {
        // local data layer
        single<LocalDataSource> { LocalDataSourceImpl(get(), get()) }
        single<AppDatabase> { AppDatabase.getRoomDatabase(get()) }
        single<AppProperties> { AppProperties(get()) }

        // remote data layer
        single<RemoteDataSource> { RemoteDataSourceImpl(
            ghOwner = ghOwner,
            ghRepo = ghRepo,
            apolloClient = get(),
            ktorClient = get(),
        ) }
        single<ApolloClient> { RemoteDataSourceImpl.apolloClientInstance(get()) }
        single<HttpClient> { RemoteDataSourceImpl.ktorClientInstance() }
        single<GitHubPATTokenProvider> { GitHubPATTokenProvider(get()) }
        single<AuthorizationInterceptor> { AuthorizationInterceptor(get()) }
    }
}

