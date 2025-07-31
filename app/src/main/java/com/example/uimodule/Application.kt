package com.example.uimodule

import android.app.Application
import com.nice.cxonechat.log.LoggerAndroid
import com.nice.cxonechat.log.ProxyLogger
import com.nice.cxonechat.ui.api.UiCustomFieldsProvider
import com.nice.cxonechat.ui.UiModule.Companion.chatUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startDi()
    }

    private fun startDi() {
        val customerCustomFieldsProvider = UiCustomFieldsProvider {
            mapOf(
                "p1" to "something"
            )
        }

        val contactCustomFieldsProvider = UiCustomFieldsProvider {
            mapOf(
                "location" to "San Francisco",
                "fname" to "John",
            )
        }

        startKoin {
            chatUiModule(
                logger = ProxyLogger(
                    LoggerAndroid("CXoneChatUi")
                ),
                customerFieldsProvider = customerCustomFieldsProvider,
                contactFieldsProvider = contactCustomFieldsProvider
            )
        }
    }
}