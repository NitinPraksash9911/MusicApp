package com.example.musicapp.helper

import android.os.Build

object Utils {

    fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}
