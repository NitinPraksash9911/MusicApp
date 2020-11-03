package com.example.musicapp.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.AppClass
import com.example.musicapp.R
import com.example.musicapp.data.AudioModel
import com.example.musicapp.databinding.FragmentHomeBinding
import com.example.musicapp.helper.PERMISSION_CODE
import com.example.musicapp.helper.Utils.isOreo
import com.example.musicapp.viewmodel.MusicDataViewModel
import com.example.musicapp.viewmodel.ViewModelProviderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    private val viewModel: MusicDataViewModel by navGraphViewModels(R.id.nav_graph, ({
        viewModelProviderFactory
    }))

    private val mAdapter by lazy {
        MusicListAdapter()
    }

    companion object {

        var tempAudioList: MutableList<AudioModel> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        AppClass.getComponent()!!.inject(this)

        initSetup()
        return binding.root
    }

    private fun initSetup() {


        if (isOreo()) {
            if (checkForPermissionAllowed()) {
                initViews()
            } else {
                requestPermission()
            }
        } else {
            initViews()
        }
    }

    private fun initViews() {

        binding.musicRv.apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = mAdapter

            this.addItemDecoration(
                MusicItemDecoration(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.divider_medium
                    )!!
                )
            )
        }

        viewModel.getMusicList().observe(viewLifecycleOwner,
            Observer {

                mAdapter.submitList(it)

            })


    }

    @TargetApi(23)
    private fun checkForPermissionAllowed(): Boolean =
        hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

    @TargetApi(23)
    private fun hasPermission(permission: String?): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(23)
    fun requestPermission() {
        requireActivity().requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initViews()
            } else {

                showMessageOKCancel(message = getString(R.string.allow_permission),
                    okListener = { dialog, which ->
                        if (isOreo()) {
                            requestPermission()
                        }
                    })

            }
        }

    }

    @TargetApi(23)
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }


}