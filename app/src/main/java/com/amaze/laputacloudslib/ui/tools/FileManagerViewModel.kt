package com.amaze.laputacloudslib.ui.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FileManagerViewModel : ViewModel() {

    private val _text = MutableLiveData<List<String>>().apply {
        value = listOf("File 1", "File 2")
    }

    val files: LiveData<List<String>> = _text
}