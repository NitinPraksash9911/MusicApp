package com.example.musicapp.data.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.musicapp.data.AudioModel


@Dao
interface CurrentMusicDao {


    @Query("SELECT * from music_table")
    suspend fun getCurrentMusic():LiveData<AudioModel>
}