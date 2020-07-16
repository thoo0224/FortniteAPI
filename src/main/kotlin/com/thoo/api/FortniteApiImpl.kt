package com.thoo.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thoo.api.enum.ClientToken
import com.thoo.api.interceptor.DefaultInterceptor
import com.thoo.api.model.Account
import com.thoo.api.model.Device
import com.thoo.api.exception.EpicError
import com.thoo.api.exception.FortniteApiError
import com.thoo.api.service.AccountPublicService
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class FortniteApiImpl(
    private val authorizationCode: String?,
    private var device: Device?
): FortniteApi {

    //private val authorizationUrl = "https://www.epicgames.com/id/logout?redirectUrl=https://www.epicgames.com/id/login?redirectUrl=https%3A%2F%2Fwww.epicgames.com%2Fid%2Fapi%2Fredirect%3FclientId%3Dec684b8c687f479fadea3cb2ad83f5c6%26responseType%3Dcode"

    var account: Account? = null

    private val gson: Gson = GsonBuilder()
        .create()
    private val cookieManager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
    private val cookieJar = JavaNetCookieJar(cookieManager)
    private val okhttp = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(DefaultInterceptor(this)).build()

    override val accountPublicService: AccountPublicService

    init {
        val retrofitBuilder = Retrofit.Builder()
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
        accountPublicService = retrofitBuilder.baseUrl(AccountPublicService.BASE_URL).build().create(AccountPublicService::class.java)
    }

    override fun loginWithDevice(token: ClientToken) {
        if(device == null) throw FortniteApiError("Tried to login with device auth without device credentials")
        val deviceResponse = accountPublicService.grantToken("basic ${ClientToken.FN_IOS_GAME_CLIENT.token}", "device_auth",
        mapOf(
            "account_id" to device!!.accountId!!,
            "device_id" to device!!.deviceId!!,
            "secret" to device!!.secret!!
        )).execute()
        if(!deviceResponse.isSuccessful){
            val error = gson.fromJson(deviceResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        account = deviceResponse.body()
        val exchangeResponse = accountPublicService.exchange().execute()
        if(!exchangeResponse.isSuccessful){
            val error = gson.fromJson(deviceResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        val exchangeAuthResponse = accountPublicService.grantToken("basic ${ClientToken.FN_IOS_GAME_CLIENT}", "exchange_code",
        mapOf("exchange_code" to exchangeResponse.body()?.code!!)).execute()
        if(!deviceResponse.isSuccessful){
            val error = gson.fromJson(deviceResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        account = exchangeAuthResponse.body()
    }

    override fun loginWithAuthorization(token: ClientToken) {
        if(authorizationCode == null) throw FortniteApiError("Tried to authenticate with authorization code without code")
        val authorizationResponse = accountPublicService.grantToken("basic ${token.token}", "authorization_code",
        mapOf("code" to authorizationCode)).execute()
        if(!authorizationResponse.isSuccessful) {
            val error = gson.fromJson(authorizationResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        account = authorizationResponse.body()
        val exchangeResponse = accountPublicService.exchange().execute()
        if(!exchangeResponse.isSuccessful) {
            val error = gson.fromJson(authorizationResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        val exchangeAuthResponse = accountPublicService.grantToken("basic ${ClientToken.FN_IOS_GAME_CLIENT.token}", "exchange_code",
        mapOf("exchange_code" to exchangeResponse.body()?.code!!)).execute()
        if(!exchangeAuthResponse.isSuccessful) {
            val error = gson.fromJson(authorizationResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        account = exchangeAuthResponse.body()
        val createDeviceResponse = accountPublicService.createDevice(account?.account_id!!).execute()
        if(!createDeviceResponse.isSuccessful){
            println(createDeviceResponse.code())
            val error = gson.fromJson(authorizationResponse.errorBody()?.string(), EpicError::class.java)
            throw FortniteApiError("${error.errorMessage} (${error.errorCode})")
        }
        device = createDeviceResponse.body()
        loginWithDevice(token)
    }

    private fun verifyToken() {
        require(account == null) { "Not logged in" }
        if(System.currentTimeMillis() >= (System.currentTimeMillis() +
                    TimeUnit.MILLISECONDS.convert(account?.expires_in!!, TimeUnit.SECONDS))) {
            val response = accountPublicService.grantToken("basic ${ClientToken.FN_IOS_GAME_CLIENT.token}", "refresh_token",
            mapOf("refresh_token" to account?.refresh_token!!)).execute()
            if(!response.isSuccessful){
                System.err.println("Error while refreshing token: " +
                        gson.fromJson(response.errorBody()!!.string(), EpicError::class.java).errorMessage)
                return
            }
            account = response.body()
        }
    }

}