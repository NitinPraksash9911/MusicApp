package com.example.musicapp.data

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.liveData
import com.example.musicapp.data.roomdb.MusicDao
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MusicRepository @Inject constructor(
    private val musicDb: MusicDao,
    private val application: Application
) {


    private val audioList = ArrayList<AudioModel>()

    fun getMusicLiveData() = liveData(Dispatchers.IO) {

        val musicList = musicDb.getMusicList()
        emitSource(musicList)
        val localData = loadLocalMusicData()
        if (musicList.value.isNullOrEmpty() || musicList.value!!.size != localData.size) {
            musicDb.insertList(localData)
        }
    }

    private fun loadLocalMusicData(): ArrayList<AudioModel> {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA


        )

//        val selection = "${MediaStore.Video.Media.DURATION} >= ?"
//        val selectionArgs = arrayOf(
//            TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES).toString()
//        )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        val cursor: Cursor? = application.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )
        if (cursor != null) {
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)


            while (cursor.moveToNext()) {
                val id: Long = cursor.getLong(idColumn)
                val name: String = cursor.getString(nameColumn)
                val album: String = cursor.getString(albumColumn)
                val artist: String = cursor.getString(artistColumn)
                val duration: Long = cursor.getLong(durationColumn)
                val path: String = cursor.getString(pathColumn)


                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )


                val audioModel = AudioModel(
                    id, name, album, artist, TimeUnit.MILLISECONDS.toSeconds(
                        duration
                    ), path.trim(), getByteArray(path.trim())
                )

                Log.e("Name :$name", " Album :$album")
                Log.e("id :$contentUri", " Artist :$path")


                audioList.add(audioModel)

            }
            cursor.close()
        }
        return audioList
    }

    private fun getByteArray(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        return retriever.embeddedPicture
    }


}