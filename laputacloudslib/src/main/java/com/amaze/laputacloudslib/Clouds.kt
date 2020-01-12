package com.amaze.laputacloudslib

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Clouds {

    fun init(account: AbstractAccount, callback: (AbstractFileStructureDriver) -> Unit) = GlobalScope.launch {
        val user = account.tryLogInAsync().await()
        val fileStructureDriver = user.getFileStructureDriverAsync().await()
        callback(fileStructureDriver)
    }

}