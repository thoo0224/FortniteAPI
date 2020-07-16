package com.thoo.api

import com.google.gson.Gson
import com.thoo.api.enum.ClientToken
import com.thoo.api.model.Device
import com.thoo.api.service.AccountPublicService

interface FortniteApi {

    class Builder {

        private var gson = Gson()
        private var authorizationCode: String? = null
        private var device: Device? = null
        private var createFile: Boolean = false
        private var deviceFileName: String? = null

        fun authorizationCode(code: String): Builder {
            this.authorizationCode = code
            return this
        }

        fun device(accountId: String, deviceId: String, secret: String): Builder {
            this.device = Device(accountId, deviceId, secret)
            return this
        }

        fun build(): FortniteApiImpl {
            return FortniteApiImpl(authorizationCode, device)
        }

    }

    fun loginWithDevice(token: ClientToken = ClientToken.FN_PC_GAME_CLIENT)

    fun loginWithAuthorization(token: ClientToken = ClientToken.FN_PC_GAME_CLIENT)

    val accountPublicService: AccountPublicService

}