package com.example.uimodule

import android.app.Activity
import android.content.Context
import com.nice.cxonechat.ChatInstanceProvider
import com.nice.cxonechat.SocketFactoryConfiguration
import com.nice.cxonechat.enums.CXoneEnvironment
import com.nice.cxonechat.ui.screen.ChatActivity


object ChatManager {
    private var isReady = false

    fun prepareIfNeeded(context: Context) {
        if (isReady) {
            return
        }

        val chatInstanceProvider = ChatInstanceProvider.create(
            SocketFactoryConfiguration.create(
                environment = CXoneEnvironment.NA1.value,
                brandId = 1390,
                channelId = "chat_7e079ff5-6ffd-4001-8dd9-413cd041ce54"
            )
        )
        chatInstanceProvider.prepare(context)

        isReady = true
    }

    fun startChat(activity: Activity) {
        ChatActivity.startChat(activity)
    }
}