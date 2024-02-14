package com.gambi.quanglinh.djmixer.ui.fragment

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelFragment1 : ViewModel() {
    var dataEqualizer1 = MutableLiveData<IntArray>()
    var dataEqualizer2 = MutableLiveData<IntArray>()
    var dataBass1 = MutableLiveData<Short>()
    var dataBass2 = MutableLiveData<Short>()
    var dataLoopPosition1 = MutableLiveData<Int>()
    var dataLoopPosition2 = MutableLiveData<Int>()
    var dataLoopIn1 = MutableLiveData<Int>()
    var dataLoopIn2 = MutableLiveData<Int>()
    var dataArrayCues1 = MutableLiveData<IntArray>()
    var dataArrayCues2 = MutableLiveData<IntArray>()
    var dataIsDeleteOn1 = MutableLiveData<Boolean>()
    var dataIsDeleteOn2 = MutableLiveData<Boolean>()
    var dataSbSample1 = MutableLiveData<Int>()
    var dataSbSample2 = MutableLiveData<Int>()
    var arrayCues1 = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    var arrayCues2 = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    var isDeleteOn1 = false
    var isDeleteOn2 = false
    var sbPositionSample1 = -1
    var sbPositionSample2 = -1
    var isResetEnable1 = MutableLiveData<Boolean>()
    var isResetEnable2 = MutableLiveData<Boolean>()
    var media1 = MutableLiveData<MediaPlayer>()
    var media2 = MutableLiveData<MediaPlayer>()
    fun setResetEnable(boolean: Boolean) {
        isResetEnable1.postValue(boolean)
    }

    fun setReset2Enable(boolean: Boolean) {
        isResetEnable2.postValue(boolean)
    }

    fun saveSeekbarEqualizer() {

    }

    fun saveBass1(data: Short) {
        dataBass1.postValue(data)
    }

    fun saveBass2(data: Short) {
        dataBass2.postValue(data)
    }

    fun saveLoop() {

    }

    fun setMediaPlayer1(media: MediaPlayer) {
        media1.postValue(media)
    }

    fun setMediaPlayer2(media: MediaPlayer) {
        media2.postValue(media)
    }

    fun resetAll1() {
        dataEqualizer1.postValue(intArrayOf(1500,1500,1500,1500,1500))
        dataBass1.postValue(1)
        dataLoopPosition1.postValue(-1)
        dataLoopIn1.postValue(-1)
        dataArrayCues1.postValue(IntArray(8))
        dataIsDeleteOn1.postValue(false)
        dataSbSample1.postValue(-1)
    }

    fun resetAll2() {
        dataEqualizer2.postValue(intArrayOf(5, 5, 5, 5, 5))
        dataBass2.postValue(1)
        dataLoopPosition2.postValue(-1)
        dataLoopIn2.postValue(-1)
        dataArrayCues2.postValue(IntArray(8))
        dataIsDeleteOn2.postValue(false)
        dataSbSample2.postValue(-1)
    }
}