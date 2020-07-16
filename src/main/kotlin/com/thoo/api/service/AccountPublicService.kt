package com.thoo.api.service

import com.thoo.api.model.Account
import com.thoo.api.model.Device
import com.thoo.api.model.Exchange
import retrofit2.Call
import retrofit2.http.*

interface AccountPublicService {

    companion object {
        const val BASE_URL = "https://account-public-service-prod.ol.epicgames.com"
    }

    @FormUrlEncoded
    @POST("/account/api/oauth/token")
    fun grantToken(@Header("Authorization") auth: String,
        @Field("grant_type") grantType: String, @FieldMap fields: Map<String, String>): Call<Account>

    @GET("/account/api/oauth/exchange")
    fun exchange(): Call<Exchange>

    @POST("/account/api/public/account/{accountId}/deviceAuth")
    fun createDevice(@Path("accountId") accountId: String): Call<Device>

}