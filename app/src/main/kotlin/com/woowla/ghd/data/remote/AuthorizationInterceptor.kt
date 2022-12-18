package com.woowla.ghd.data.remote

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain

class AuthorizationInterceptor(
    private val gitHubPATTokenProvider: GitHubPATTokenProvider = GitHubPATTokenProvider()
): HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        val requestBuilder = request.newBuilder()
        val token = gitHubPATTokenProvider.get()
        token?.let { requestBuilder.addHeader("Authorization", "token $token") }
        return chain.proceed(requestBuilder.build())
    }
}