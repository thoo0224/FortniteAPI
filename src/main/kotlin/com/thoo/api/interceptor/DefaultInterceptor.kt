package com.thoo.api.interceptor

import com.thoo.api.FortniteApiImpl
import okhttp3.Interceptor
import okhttp3.Response

class DefaultInterceptor(private val api: FortniteApiImpl): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if(chain.request().url.toString() != "https://account-public-service-prod.ol.epicgames.com/account/api/oauth/token" &&
            api.account != null){
            requestBuilder.addHeader("Authorization", "${api.account?.token_type ?: "bearer"} ${api.account?.access_token}")
        }
        return chain.proceed(requestBuilder.build())
    }

}