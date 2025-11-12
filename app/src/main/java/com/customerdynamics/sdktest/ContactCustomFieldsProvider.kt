package com.customerdynamics.sdktest

import com.nice.cxonechat.ui.api.UiCustomFieldsProvider

object ContactCustomFieldsProvider: UiCustomFieldsProvider {
    var email: String? = null
    var accountNumber: String? = null

    override fun customFields(): Map<String, String> {
        return mapOf(
            "p2" to email.orEmpty(),
            "accountnumber" to accountNumber.orEmpty(),
            "batman" to "yes"
        )
    }


}