package com.thoo.api.model

import java.util.*

@SuppressWarnings
class Account(
    val account_id: String,
    val client_id: String,
    val client_service: String,
    val device_id: String,
    val expires_in: Long,
    val expires_at: Date,
    val in_app_id: String,
    val internal_client: Boolean?,
    val lastPasswordValidation: Date?,
    val perms: Array<Perm>,
    val access_token: String,
    val app: String,
    val refresh_expires: String,
    val refresh_expires_at: Date,
    val refresh_token: String,
    val token_type: String
) {
    data class Perm(
        val resource: String,
        val action: Int
    )

}