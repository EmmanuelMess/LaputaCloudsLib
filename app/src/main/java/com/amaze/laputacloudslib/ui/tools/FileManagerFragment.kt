package com.amaze.laputacloudslib.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amaze.laputacloudslib.AbstractAccount
import com.amaze.laputacloudslib.Clouds
import com.amaze.laputacloudslib.OneDriveAccount
import com.amaze.laputacloudslib.R
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalArgumentException

class FileManagerFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var fileManagerViewModel: FileManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fileManagerViewModel = ViewModelProviders.of(this).get(FileManagerViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_filemanager, container, false)
        val filesListView: ListView = root.findViewById(R.id.filesListView)

        val clouds = Clouds.init(getCloudAccount(arguments!!.getInt("cloudName"))) {

        }


        fileManagerViewModel.files.observe(this, Observer {
            if(filesListView.adapter == null) {
                filesListView.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
                filesListView.onItemClickListener = this
            }

            (filesListView.adapter as ArrayAdapter<String>).addAll(it)
        })

        return root
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        Snackbar.make(view, "Not implemented yet :(", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun getCloudAccount(id: Int) = when (id) {
        1 -> OneDriveAccount(requireActivity(), "", "")
        else -> throw IllegalArgumentException()
    }
}