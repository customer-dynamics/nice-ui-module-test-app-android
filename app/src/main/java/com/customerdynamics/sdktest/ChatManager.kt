package com.customerdynamics.sdktest

import android.app.Activity
import android.content.Context
import com.nice.cxonechat.ChatInstanceProvider
import com.nice.cxonechat.SocketFactoryConfiguration
import com.nice.cxonechat.enums.CXoneEnvironment
import com.nice.cxonechat.log.LoggerAndroid
import com.nice.cxonechat.log.ProxyLogger
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
                channelId = "chat_955c2f5e-5cc1-4131-92ed-6a6aa0878b00",
            ),
            logger = ProxyLogger(
                LoggerAndroid("SDKTest"),
            )
        )
        chatInstanceProvider.prepare(context)

        isReady = true
    }

    fun startChat(activity: Activity) {
        ChatActivity.startChat(activity)
    }
}