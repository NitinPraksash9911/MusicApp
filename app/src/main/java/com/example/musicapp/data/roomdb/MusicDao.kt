package com.example.musicapp.data.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapp.data.AudioModel

@Dao
interface MusicDao {

    @Query("SELECT * from music_table ORDER BY aAlbum ASC")
    fun getMusicList(): LiveData<List<AudioModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(musicList: List<AudioModel>)

    @Query("DELETE FROM music_table")
    suspend fun deleteAll()
}