package com.amaze.laputacloudslib

import com.onedrive.sdk.extensions.Drive
import com.onedrive.sdk.extensions.IOneDriveClient
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

abstract class AbstractUser {
    abstract fun getFileStructureDriverAsync(): Deferred<AbstractFileStructureDriver>
}

class OneDriveUser(val oneDriveClient: IOneDriveClient) : AbstractUser() {
    @ExperimentalCoroutinesApi
    override fun getFileStructureDriverAsync() = GlobalScope.async {
        val drive = awaitCallback<Drive> {
            oneDriveClient.drive.buildRequest().get(it)
        }

        OneDriveDriver(drive)
    }
}