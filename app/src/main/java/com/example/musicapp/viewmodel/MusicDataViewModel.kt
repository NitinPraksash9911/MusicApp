package com.example.musicapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.data.AudioModel
import com.example.musicapp.data.MusicRepository
import javax.inject.Inject

class MusicDataViewModel @Inject constructor(private val repository: MusicRepository) :
    ViewModel() {

    private var currentMusicLiveData = MutableLiveData<AudioModel>()

     var data = MutableLiveData<Int>(0)

    fun getMusicList() = repository.getMusicLiveData()


    fun getCurrentMusic(): LiveData<AudioModel> = currentMusicLiveData

    fun updateCurrentMusic(audioModel: AudioModel) {

        currentMusicLiveData.value = audioModel
    }

}