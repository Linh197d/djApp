package com.gambi.quanglinh.djmixer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.listener.ChooseAudioListener
import com.gambi.quanglinh.djmixer.listener.ItopDJFxListener
import com.gambi.quanglinh.djmixer.model.AudiObject

class TopDJApdapter(private val context: Context, val listener: ItopDJFxListener) :
    RecyclerView.Adapter<TopDJApdapter.ViewHolder>() {
    private var listTopDJFx = mutableListOf<String>()

    fun initDJFx() {
            listTopDJFx.add(context.resources.getString(R.string.top_dj_fx))
            listTopDJFx.add(context.resources.getString(R.string.house))
            listTopDJFx.add(context.getString(R.string.edm_vocals))
            listTopDJFx.add(context.getString(R.string.electro_vol_1))
            listTopDJFx.add(context.getString(R.string.electro_vol_2))
        }

        fun clearData() {
            notifyItemRangeRemoved(0, itemCount)
            listTopDJFx.clear()
        }


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameTopDJ: TextView
            var checkSelect: ImageView

            init {
                nameTopDJ = itemView.findViewById(R.id.tvtTopDJ)
                checkSelect = itemView.findViewById(R.id.check)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.rcv_top_dj_fx, parent, false)
            return ViewHolder(view)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
            val audio: String = listTopDJFx[position]
//        positionClicked = position
            holder.nameTopDJ.text = audio
            holder.checkSelect.visibility = View.VISIBLE
            holder.itemView.setOnClickListener{
                listener.onTopDJChoosed(audio)
            }
        }

        override fun getItemCount(): Int {
            return listTopDJFx.size
        }
    }