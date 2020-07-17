package com.thoo.api

import com.google.gson.Gson
import com.thoo.api.enum.ClientToken
import com.thoo.api.enum.Platform
import com.thoo.api.model.Device
import com.thoo.api.service.AccountPublicService
import com.thoo.api.xmpp.XMPPConnection

interface FortniteApi {

    class Builder {

        private var gson = Gson()
        private var authorizationCode: String? = null
        private var device: Device? = null
        private var platform = Platform.WIN

        fun authorizationCode(code: String): Builder {
            this.authorizationCode = code
            return this
        }

        fun device(accountId: String, deviceId: String, secret: String): Builder {
            this.device = Device(accountId, deviceId, secret)
            return this
        }

        fun platform(platform: Platform): Builder {
            this.platform = platform
            return this
        }

        fun build(): FortniteApiImpl {
            return FortniteApiImpl(authorizationCode, device, platform)
        }

    }

    fun loginWithDevice(token: ClientToken = ClientToken.FN_PC_GAME_CLIENT)

    fun loginWithAuthorization(token: ClientToken = ClientToken.FN_PC_GAME_CLIENT)

    fun connectXMPP()

    val accountPublicService: AccountPublicService

    val xmpp: XMPPConnection

}