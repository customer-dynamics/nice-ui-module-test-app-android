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
import androidx.core.net.toUri
import androidx.browser.customtabs.CustomTabsIntent
import com.nice.cxonechat.message.Message


object ChatManager {
    private var isReady = false
    private var cancellableThreadsCallback: Cancellable? = null
    private var cancellableThreadCallback: Cancellable? = null
    private var showingSurvey = false

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

        val chatThreadsHandler = chat.threads()
        cancellableThreadsCallback = chatThreadsHandler.threads { threadsList ->
            Log.d("LOG", "Current chat threads: $threadsList")

            if (threadsList.isNotEmpty()) {
                val existingThread = threadsList.first()
                val chatThreadHandler = chatThreadsHandler.thread(existingThread)

                cancellableThreadCallback = chatThreadHandler.get { chatThread ->
                    // Get the last message in the thread.
                    val latestMessage = chatThread.messages.lastOrNull() as Message.Text ?: return@get

                    // Parse the URL from text if it's from a survey user.
                    if (latestMessage.author?.firstName != "Satisfaction Survey Service") {
                        return@get
                    }
                    val messageText = latestMessage.text
                    val url = "https://ahoylink.com/8H0EJprOJ0" // Parse this instead of hardcoding in real use case

                    // Open a web browser popup with the URL found in the message text:
                    if (showingSurvey) {
                        return@get
                    }
                    showingSurvey = true
                    Log.d("LOG", "Opening survey URL: $messageText")
                    val customTabsIntent = CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .build()
                    customTabsIntent.launchUrl(activity, url.toUri())
                }
                return@threads
            }

//            val firstThread = threadsList.firstOrNull() ?: run {
//                if (!threadCreated) {
//                    // Create the thread manually, in order to set the contact custom fields on it:
//                    Log.d("LOG", "No existing threads found. Creating a thread.")
//                    try {
//                        chatThreadsHandler.create(mapOf(
//                            "p1" to "another_value",
//                        ), )
//                    } catch (error: Exception) {
//                        Log.e("LOG", "Error creating thread: ${error.message}")
//                    }
//                    threadCreated = true
//                }
//                return@threads
//            }
        }

        ChatActivity.startChat(activity)
    }
}