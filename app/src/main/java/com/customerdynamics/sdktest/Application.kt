package com.customerdynamics.sdktest

import android.app.Application
import com.google.firebase.FirebaseApp
import com.nice.cxonechat.log.Logger
import com.nice.cxonechat.log.LoggerAndroid
import com.nice.cxonechat.log.ProxyLogger
import com.nice.cxonechat.ui.api.UiCustomFieldsProvider
import com.nice.cxonechat.ui.UiModule.Companion.chatUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.includes
import org.koin.dsl.module


class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        startDi()
    }

    private fun startDi() {
        // You can set custom fields right away to be used when starting a chat:
        val customerCustomFieldsProvider = UiCustomFieldsProvider {
            mapOf(
                "p1" to "some_value"
            )
        }
        val contactCustomFieldsProvider = UiCustomFieldsProvider {
            mapOf(
                "batman" to "yes",
            )
        }

        startKoin {
            androidContext(this@Application)
            modules(
                module {
                    single<Logger> {
                        LoggerAndroid("SDKTest")
                    }
                }
            )
            includes(
                chatUiModule(
                    logger = ProxyLogger(
                        LoggerAndroid("SDKTest")
                    ),
                    customerFieldsProvider = customerCustomFieldsProvider,
                    contactFieldsProvider = contactCustomFieldsProvider
                )
            )
        }
    }
}