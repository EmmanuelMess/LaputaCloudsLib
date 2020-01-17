package com.amaze.laputacloudslib

import com.onedrive.sdk.extensions.IOneDriveClient

abstract class AbstractUser<Driver: AbstractFileStructureDriver> {
    abstract fun getFileStructureDriverAsync(callback: (Driver) -> Unit)
}

class OneDriveUser(val oneDriveClient: IOneDriveClient) : AbstractUser<OneDriveDriver>() {
    override fun getFileStructureDriverAsync(callback: (OneDriveDriver) -> Unit) {
        callback(OneDriveDriver(oneDriveClient))
    }
}