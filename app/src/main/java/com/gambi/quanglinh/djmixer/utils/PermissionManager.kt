package com.gambi.quanglinh.djmixer.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class PermissionManager {

    companion object {
        fun hasReadAudioPermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context.checkSelfPermission(
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            else context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        fun hasRecordAudioPermission(context: Context): Boolean {
            return context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        }

    }
}