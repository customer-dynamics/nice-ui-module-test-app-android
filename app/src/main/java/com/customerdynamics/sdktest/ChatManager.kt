package com.customerdynamics.sdktest

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.nice.cxonechat.Cancellable
import com.nice.cxonechat.ChatInstanceProvider
import com.nice.cxonechat.SocketFactoryConfiguration
import com.nice.cxonechat.enums.CXoneEnvironment
import com.nice.cxonechat.log.LoggerAndroid
import com.nice.cxonechat.log.ProxyLogger
import com.nice.cxonechat.ui.screen.ChatActivity


object ChatManager {
    private var isReady = false
    private var cancellableThreadsCallback: Cancellable? = null

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
            ),
            deviceTokenProvider = { setToken ->
                Firebase.messaging.token.addOnSuccessListener(setToken)
            },
        )
        chatInstanceProvider.prepare(context)
        isReady = true
    }

    fun startChat(activity: Activity) {
        val chat = ChatInstanceProvider.get().chat ?: run {
            Log.d("LOG", "No chat instance available for custom fields")
            return
        }

        // You can set customer custom fields:
        chat.customFields().add(mapOf(
            "phone_number" to "+1234567890",
        ))

        // For contact custom fields, you need to use an existing thread handler:
        val chatThreadsHandler = chat.threads()
        cancellableThreadsCallback = chatThreadsHandler.threads { threadsList ->
            Log.d("LOG", "Current chat threads: $threadsList")
            val firstThread = threadsList.firstOrNull() ?: run {
                // Thread might not be created yet; keep listening
                Log.d("LOG", "No existing threads found.")
                return@threads
            }
            val firstThreadHandler = chatThreadsHandler.thread(firstThread)
            Log.d("LOG", "Adding custom fields to first thread: $firstThread")
            firstThreadHandler.customFields().add(mapOf(
                "p1" to "another_value",
            ))
        }

        ChatActivity.startChat(activity)
    }
}