package com.amaze.laputacloudslib

import com.amaze.laputacloudslib.AbstractFileStructureDriver.Companion.SEPARATOR
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.extensions.Item
import java.io.File

abstract class AbstractCloudFile {
    abstract val name: String
    abstract val path: String
    abstract val isDirectory: Boolean
    abstract val isRootDirectory: Boolean

    abstract fun getParent(callback: (AbstractCloudFile?) -> Unit)
}

class OneDriveCloudFile(
    val driver: OneDriveDriver,
    override val path: String,
    val item: Item,
    override val isRootDirectory: Boolean = false
): AbstractCloudFile() {
    override val name: String
        get() = item.name
    override val isDirectory: Boolean
        get() = item.folder != null
    override fun getParent(callback: (AbstractCloudFile?) -> Unit) {
        if(isRootDirectory) {
            callback(null)
        }

        val guessedParentPath = getParentFromPath(path)!!

        driver.oneDriveClient.drive.root.getItemWithPath(guessedParentPath).buildRequest().get(object :ICallback<Item> {
            override fun success(result: Item) {
                callback(OneDriveCloudFile(driver, guessedParentPath, result, guessedParentPath == "/"))
            }

            override fun failure(ex: ClientException) {
                throw ex
            }
        })
    }

    companion object {
        @JvmStatic
        private fun getParentFromPath(path: String): String? = File(path).parent
    }
}