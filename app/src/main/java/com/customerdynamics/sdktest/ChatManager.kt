package com.customerdynamics.sdktest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nice.cxonechat.Cancellable
import com.nice.cxonechat.ChatInstanceProvider
import com.nice.cxonechat.SocketFactoryConfiguration
import com.nice.cxonechat.enums.CXoneEnvironment
import com.nice.cxonechat.log.LoggerAndroid
import com.nice.cxonechat.log.ProxyLogger
import com.nice.cxonechat.ui.screen.ChatActivity
import androidx.core.net.toUri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.Color
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.nice.cxonechat.message.Message
import com.nice.cxonechat.ui.composable.theme.ChatThemeDetails
import com.nice.cxonechat.ui.composable.theme.ThemeColorTokens


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
        cleanUp()
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
                    val latestMessage = chatThread.messages.lastOrNull() as? Message.Text ?: run {
                        Log.d("LOG", "No need to handle TORM messages")
                        return@get
                    }

                    // Parse the URL from text if it's from a survey user.
                    if (latestMessage.author?.firstName != "Satisfaction Survey Service") {
                        return@get
                    }
                    val messageText = latestMessage.text
                    val url = Regex("(https?://\\S+)").find(messageText)?.value ?: run {
                        Log.d("LOG", "No URL found in message text: $messageText")
                        return@get
                    }

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
        }

        // Currently broken in 3.1.0
//        ChatThemeDetails.lightTokens.background = ThemeColorTokens.Background(
//            default = Color(0xFFF6F6F6),
//            inverse = Color(0xFF222222),
//            surface = ThemeColorTokens.Background.Surface(
//                default = Color.White,
//                variant = Color(0xFFE0E0E0),
//                container = Color(0xFFFAFAFA),
//                subtle = Color(0xFFF0F0F0),
//                emphasis = Color(0xFF007AFF)
//            )
//        )

        ContactCustomFieldsProvider.accountNumber = "123456"
        ContactCustomFieldsProvider.email = "test@test.com"
        ChatActivity.startChat(activity)
    }

    fun cleanUp() {
        cancellableThreadsCallback?.cancel()
        cancellableThreadsCallback = null

        cancellableThreadCallback?.cancel()
        cancellableThreadCallback = null

        showingSurvey = false
    }
}