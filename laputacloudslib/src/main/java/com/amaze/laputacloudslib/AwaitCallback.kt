package com.amaze.laputacloudslib

import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.ClientException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
suspend fun <T> awaitCallback(block: (ICallback<T>) -> Unit) : T =
    suspendCancellableCoroutine { cont ->
        block(object : ICallback<T> {
            override fun success(result: T) = cont.resume(result, {})
            override fun failure(e: ClientException) = cont.resumeWithException(e)
        })
    }
