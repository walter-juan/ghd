package com.woowla.ghd.data

import com.apollographql.apollo3.ApolloClient
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.data.local.LocalDataSourceImpl
import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.data.remote.AuthorizationInterceptor
import com.woowla.ghd.data.remote.GitHubPATTokenProvider
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.data.remote.RemoteDataSourceImpl
import io.ktor.client.HttpClient
import org.koin.core.module.Module

object DiData {
    fun module(
        ghOwner: String,
        ghRepo: String,
    ): Module = org.koin.dsl.module {
        // local data layer
        single<LocalDataSource> { LocalDataSourceImpl(get(), get()) }
        single<AppDatabase> { AppDatabase.Companion.getRoomDatabase(get()) }
        single<AppProperties> { AppProperties(get()) }

        // remote data layer
        single<RemoteDataSource> {
            RemoteDataSourceImpl(
                ghOwner = ghOwner,
                ghRepo = ghRepo,
                apolloClient = get(),
                ktorClient = get(),
                appLogger = get(),
            )
        }
        single<ApolloClient> { RemoteDataSourceImpl.Companion.apolloClientInstance(get()) }
        single<HttpClient> { RemoteDataSourceImpl.Companion.ktorClientInstance() }
        single<GitHubPATTokenProvider> { GitHubPATTokenProvider(get()) }
        single<AuthorizationInterceptor> { AuthorizationInterceptor(get()) }
    }
}