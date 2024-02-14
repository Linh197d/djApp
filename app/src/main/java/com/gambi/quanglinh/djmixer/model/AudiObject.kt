package com.gambi.quanglinh.djmixer.model

import android.graphics.Bitmap
import java.io.Serializable
import java.util.Locale

class AudiObject
    (var filePath: String, var nameSong: String, var nameSinger: String, var duration: Int,var thumbnail: Boolean =false) : Serializable {

    fun timetoMinutes(dr: Int): String {
        val seconds = dr / 1000 % 60
        val minutes = dr / (1000 * 60) % 60
        val hours = dr / (1000 * 60 * 60) % 24
        return if (hours > 0)
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        else
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun toString(): String {
        return "AudiObject(filePath='$filePath', nameSong='$nameSong', nameSinger='$nameSinger', duration=$duration)"
    }


}