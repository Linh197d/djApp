package com.gambi.quanglinh.djmixer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.listener.ChooseAudioListener
import com.gambi.quanglinh.djmixer.model.AudiObject
import com.gambi.quanglinh.djmixer.utils.MyUtils.Companion.getAlbumImage


class ChooseAudioAdapter(private val context: Context, val listener: ChooseAudioListener) :
    RecyclerView.Adapter<ChooseAudioAdapter.ViewHolder>() {
    private var listAudio = mutableListOf<AudiObject>()
    private var bitmapDisco :Bitmap?=null

    val mr = MediaMetadataRetriever()
    fun addAudio(audioObj: AudiObject) {
        listAudio.add(audioObj)
        notifyItemInserted(itemCount)
    }

    fun clearData() {
        notifyItemRangeRemoved(0, itemCount)
        listAudio.clear()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameAudio: TextView
        var audioArtist: TextView
        var duration: TextView
        var lnAudio: LinearLayout
        var thumbDefault: ImageView
        var thumbAudio: ImageView
        var popupMenu: ImageView

        init {
            nameAudio = itemView.findViewById(R.id.nameAudio)
            audioArtist = itemView.findViewById(R.id.audioArtist)
            duration = itemView.findViewById(R.id.duration)
            lnAudio = itemView.findViewById(R.id.ln_audio)
            thumbDefault = itemView.findViewById(R.id.thumb_default)
            thumbAudio = itemView.findViewById(R.id.thumb_audio)
            popupMenu = itemView.findViewById(R.id.popup_menu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.rcv_choose_audio, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val audio: AudiObject = listAudio[position]
//        positionClicked = position

        if(getAlbumImage(audio.filePath)!=null){
            bitmapDisco = getAlbumImage(audio.filePath)
//            bitmapDisco = BitmapFactory.decodeFile(audio.filePath)
            audio.thumbnail=true
            Glide.with(context).load(bitmapDisco)
                .into(holder.thumbAudio)
            holder.thumbDefault.visibility=View.GONE
            holder.thumbAudio.visibility=View.VISIBLE
        }else{
            holder.thumbDefault.visibility=View.VISIBLE
            holder.thumbAudio.visibility=View.GONE
        }
        holder.popupMenu.setOnClickListener {
            onPopUpMenuClickListener(holder.popupMenu, position)
        }

        holder.nameAudio.text = audio.nameSong
        holder.audioArtist.text = audio.nameSinger
        holder.duration.text = audio.timetoMinutes(audio.duration)
        holder.lnAudio.setOnClickListener {
            listener.onAudioChoosed(audio)
            Log.e("click", "clicked adapter")
        }

    }


    override fun getItemCount(): Int {
        return listAudio.size
    }
    private fun onPopUpMenuClickListener(v: View, position: Int) {
        val menu = PopupMenu(context, v)
        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_addqueue -> addtoQueue(position)
                R.id.popup_add_playlist -> addToPlaylist(position)
                R.id.popup_preview -> previewAudio(position)
            }
            false
        }
        menu.inflate(R.menu.popup_song)
        menu.show()
    }

    private fun addtoQueue(position: Int) {

    }

    private fun addToPlaylist(position: Int) {
    }

    private fun previewAudio(position: Int) {
        
    }
}