package com.amaze.laputacloudslib

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PROTECTED
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.extensions.IItemCollectionPage
import com.onedrive.sdk.extensions.IOneDriveClient
import com.onedrive.sdk.extensions.Item
import com.onedrive.sdk.extensions.ItemCollectionPage
import java.io.File

abstract class AbstractFileStructureDriver {
    companion object {
        val SEPARATOR = "/"

        @VisibleForTesting
        fun removeScheme(path: String, scheme: String) = path.substringAfter(scheme)

        @VisibleForTesting(otherwise = PROTECTED)
        fun sanitizeRawPath(rawPath: String): String {
            var rawPath = rawPath

            if(!rawPath.startsWith(SEPARATOR)) {
                rawPath = SEPARATOR + rawPath
            }

            return File(rawPath).canonicalPath
        }
    }

    abstract val SCHEME: String

    abstract fun getFiles(path: String, callback: (List<AbstractCloudFile>) -> Unit)

    abstract fun getFile(path: String, callback: (AbstractCloudFile) -> Unit)

    protected fun removeScheme(path: String) = removeScheme(path, SCHEME)
}

class OneDriveDriver(val oneDriveClient: IOneDriveClient) : AbstractFileStructureDriver() {
    companion object {
        const val SCHEME = "onedrive:"
    }

    override val SCHEME: String = OneDriveDriver.SCHEME

    override fun getFiles(path: String, callback: (List<AbstractCloudFile>) -> Unit) {
        val rawPath = sanitizeRawPath(removeScheme(path))

        oneDriveClient.drive.root.getItemWithPath(rawPath).children.buildRequest().get(object: ICallback<IItemCollectionPage> {
            override fun success(requestForFile: IItemCollectionPage) {
                callback(requestForFile.currentPage.map {
                    OneDriveCloudFile(this@OneDriveDriver, rawPath + SEPARATOR + it.name, it)
                })
            }

            override fun failure(ex: ClientException) {
                throw ex
            }
        })
    }

    override fun getFile(path: String, callback: (AbstractCloudFile) -> Unit) {
        val rawPath = sanitizeRawPath(removeScheme(path))

        oneDriveClient.drive.root.getItemWithPath(rawPath).buildRequest().get(object: ICallback<Item> {
            override fun success(requestForFile: Item) {
                callback(OneDriveCloudFile(
                    this@OneDriveDriver,
                    rawPath,
                    requestForFile,
                    isRootDirectory = rawPath == SEPARATOR
                ))
            }

            override fun failure(ex: ClientException) {
                throw ex
            }
        })
    }
}

