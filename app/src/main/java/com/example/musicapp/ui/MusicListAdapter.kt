package com.example.musicapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.data.AudioModel
import com.example.musicapp.databinding.ItemMusicBinding

class MusicListAdapter : ListAdapter<AudioModel, MusicListAdapter.MusicListViewHolder>(DIFF_CALL) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListViewHolder {

        return MusicListViewHolder(
            ItemMusicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: MusicListViewHolder, position: Int) {

        holder.bindData(getItem(position))

    }


    class MusicListViewHolder(private val itemMusicBinding: ItemMusicBinding) :
        RecyclerView.ViewHolder(itemMusicBinding.root) {

        fun bindData(audioModel: AudioModel) {


            itemMusicBinding.music = audioModel
            itemMusicBinding.root.setOnClickListener {

                it.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToPlayerFragment(audioModel,absoluteAdapterPosition))

            }
        }


    }

    object DIFF_CALL : DiffUtil.ItemCallback<AudioModel>() {

        override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem.aAlbum == newItem.aAlbum && oldItem.aArtist == newItem.aArtist && oldItem.aName == newItem.aName
        }

        override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem.aAlbum == newItem.aAlbum && oldItem.aArtist == newItem.aArtist && oldItem.aName == newItem.aName
        }

    }

}