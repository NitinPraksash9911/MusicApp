package com.example.musicapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "music_table")
data class AudioModel(
    @PrimaryKey
    val _id: Long,
    val aName: String,
    val aAlbum: String,
    val aArtist: String,
    val duration: Long,
    val path: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val imageArray: ByteArray? = null

) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioModel

        if (_id != other._id) return false
        if (aName != other.aName) return false
        if (aAlbum != other.aAlbum) return false
        if (aArtist != other.aArtist) return false
        if (duration != other.duration) return false
        if (path != other.path) return false
        if (imageArray != null) {
            if (other.imageArray == null) return false
            if (!imageArray.contentEquals(other.imageArray)) return false
        } else if (other.imageArray != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + aName.hashCode()
        result = 31 * result + aAlbum.hashCode()
        result = 31 * result + aArtist.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + (imageArray?.contentHashCode() ?: 0)
        return result
    }


}