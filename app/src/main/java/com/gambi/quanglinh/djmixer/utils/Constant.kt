package com.gambi.quanglinh.djmixer.utils

import android.os.Environment

class Constant {
    companion object {
        val audioMixedRecordFolder =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/MusicMixer/Records"
        const val AUDIO_SESSION_ID = "audio_id"
        const val LIST_FILE_1 = 101
        const val LIST_FILE_2 = 102
        const val TAG = "linhd"

    }
}