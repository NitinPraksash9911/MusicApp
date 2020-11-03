package com.example.musicapp.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicapp.data.AudioModel

const val DATABASE_NAME = "music_database"

@Database(entities = [AudioModel::class], version = 1, exportSchema = false)
abstract class MusicDb : RoomDatabase() {

    abstract fun musicDao(): MusicDao

}