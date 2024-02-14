package com.gambi.quanglinh.djmixer.listener

import com.gambi.quanglinh.djmixer.model.AudiObject


interface ChooseAudioListener {
    fun onAudioChoosed(audio: AudiObject)
    fun onMenuDotAudio(audio: AudiObject, position: Int)
}