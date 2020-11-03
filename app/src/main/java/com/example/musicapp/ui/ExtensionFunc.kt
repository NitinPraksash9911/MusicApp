package com.example.musicapp.ui

import android.media.MediaMetadataRetriever
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.musicapp.R


@BindingAdapter("setImageArt")
fun setAlbumArt(imageView: ImageView, art: ByteArray?) {

//    val retriever = MediaMetadataRetriever()
//    retriever.setDataSource(uri)
//    val art = retriever.embeddedPicture
    if (art != null) {
        Glide.with(imageView.context)
            .load(art)
            .error(R.drawable.music_icon)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load(R.drawable.music_icon)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}