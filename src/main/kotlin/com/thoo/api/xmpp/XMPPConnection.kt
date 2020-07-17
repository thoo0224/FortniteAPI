package com.thoo.api.xmpp

import com.thoo.api.FortniteApiImpl
import com.thoo.api.enum.Platform
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.filter.MessageTypeFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.ping.PingManager
import java.util.*

class XMPPConnection(
    platform: Platform = Platform.WIN,
    private val api: FortniteApiImpl
): StanzaListener {

    private val connection = XMPPTCPConnection(XMPPTCPConnectionConfiguration
        .builder()
        .setXmppDomain("prod.ol.epicgames.com")
        .setHost("xmpp-service-prod.ol.epicgames.com")
        .setPort(5222)
        .setConnectTimeout(60000)
        .setResource("V2:Fortnite:${platform}::${UUID.randomUUID().toString().replace("-", "")}")
        .build())
    private var chatManager: ChatManager? = null
    private var roser: Roster? = null

    init {
        connection.addAsyncStanzaListener(this, MessageTypeFilter.NORMAL)
        connection.replyTimeout = 60000
    }

    fun connect() {
        connection.connect().login(api.account?.account_id, api.account?.access_token)
        PingManager.getInstanceFor(connection).pingInterval = 60
        chatManager = ChatManager.getInstanceFor(connection)
        roser = Roster.getInstanceFor(connection)
    }

    override fun processStanza(packet: Stanza?) {
        val message = packet as Message
        println(message.body)
    }

}