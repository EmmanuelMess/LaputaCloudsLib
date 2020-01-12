package com.amaze.laputacloudslib

import android.app.Activity
import com.onedrive.sdk.authentication.ADALAuthenticator
import com.onedrive.sdk.authentication.MSAAuthenticator
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.core.DefaultClientConfig
import com.onedrive.sdk.extensions.IOneDriveClient
import com.onedrive.sdk.extensions.OneDriveClient
import kotlinx.coroutines.*
import kotlin.coroutines.resumeWithException


abstract class AbstractAccount {
    abstract fun tryLogInAsync(): Deferred<AbstractUser>
}

class OneDriveAccount(val activity: Activity, msaaClientId: String, adalClientId: String): AbstractAccount() {
    val msaAuthenticator = object : MSAAuthenticator() {
        override fun getClientId()= msaaClientId

        override fun getScopes() = arrayOf("onedrive.appfolder")
    }

    val adalAuthenticator = object : ADALAuthenticator() {
        override fun getClientId()= adalClientId

        override fun getRedirectUrl() = "https://localhost"
    }

    val oneDriveConfig = DefaultClientConfig.createWithAuthenticators(
        msaAuthenticator,
        adalAuthenticator
    )

    @ExperimentalCoroutinesApi
    override fun tryLogInAsync() = GlobalScope.async {
        val oneDriveClient = awaitCallback<IOneDriveClient> {
            OneDriveClient.Builder()
                .fromConfig(oneDriveConfig)
                .loginAndBuildClient(activity, it)
        }

        OneDriveUser(oneDriveClient)
    }
}