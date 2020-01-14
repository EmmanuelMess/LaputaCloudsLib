package com.amaze.laputacloudslib.ui.tools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amaze.laputacloudslib.AbstractCloudFile

class FileManagerViewModel : ViewModel() {
    val selectedFile = MutableLiveData<AbstractCloudFile>()
}