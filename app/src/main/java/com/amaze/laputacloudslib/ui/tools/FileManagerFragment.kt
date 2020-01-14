package com.amaze.laputacloudslib.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amaze.laputacloudslib.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FileManagerFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var fileManagerViewModel: FileManagerViewModel

    private var files: List<AbstractCloudFile>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fileManagerViewModel = ViewModelProviders.of(this).get(FileManagerViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_filemanager, container, false)
        val filesListView: ListView = root.findViewById(R.id.filesListView)
        val swipeRefreshLayout: SwipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)
        val account = getCloudAccount(arguments!!.getInt("cloudName"))
        lateinit var rootPath: String

        Clouds.init(account) { driver ->
            rootPath = driver.SCHEME + "/"

            driver.getFile(rootPath) { file ->
                fileManagerViewModel.selectedFile.value = file
            }
        }

        fileManagerViewModel.selectedFile.observe(this, Observer {
            swipeRefreshLayout.startLoad()

            if(filesListView.adapter == null) {
                filesListView.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
                filesListView.onItemClickListener = this
            }

            Clouds.init(account) { driver ->
                val file = fileManagerViewModel.selectedFile.value!!

                driver.getFiles(file.path) { files ->
                    this.files = files

                    val adapter = (filesListView.adapter as ArrayAdapter<String>)

                    adapter.clear()
                    if(!file.isRootDirectory) {
                        adapter.add("..")
                    }
                    adapter.addAll(files.map(AbstractCloudFile::name))

                    swipeRefreshLayout.endLoad()
                }
            }
        })

        return root
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val selectedFile = fileManagerViewModel.selectedFile.value!!

        if(position == 0 && !selectedFile.isRootDirectory) {
            selectedFile.getParent {
                fileManagerViewModel.selectedFile.value = it
            }
            return
        }

        val file = if(selectedFile.isRootDirectory) files!![position] else files!![position + 1]

        if(file.isDirectory) {
            fileManagerViewModel.selectedFile.value = file
        }
    }

    private fun getCloudAccount(id: Int) = when (id) {
        1 -> OneDriveAccount(requireActivity(), MSAAClientId, redirectionUri, null)
        else -> throw IllegalArgumentException()
    }

    private fun SwipeRefreshLayout.startLoad() {
        isEnabled = true
        isRefreshing = true
    }

    private fun SwipeRefreshLayout.endLoad() {
        isRefreshing = false
        isEnabled = false
    }
}