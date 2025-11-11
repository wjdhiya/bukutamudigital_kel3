package com.example.bukutamudigital.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bukutamudigital.R
import com.example.bukutamudigital.model.Tamu

/** 💡 1. DEFINISI INTERFACE */
interface OnItemClickListener {
    fun onEditClick(tamu: Tamu)
    fun onDeleteClick(tamu: Tamu)
}


class TamuAdapter(
    private var tamuList: List<Tamu>,
    private val listener: OnItemClickListener // Listener untuk Edit/Hapus
) : RecyclerView.Adapter<TamuAdapter.TamuViewHolder>() {

    /** 💡 2. VIEWHOLDER: Termasuk TextViews dan ImageButtons */
    class TamuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaTamu)
        val tvInstansi: TextView = itemView.findViewById(R.id.tvInstansiTamu)
        val tvKeperluan: TextView = itemView.findViewById(R.id.tvKeperluanTamu)

        // Tombol Aksi
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnHapus: ImageButton = itemView.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TamuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tamu, parent, false)
        return TamuViewHolder(view)
    }

    override fun onBindViewHolder(holder: TamuViewHolder, position: Int) {
        val tamu = tamuList[position]
        holder.tvNama.text = tamu.nama
        holder.tvInstansi.text = tamu.instansi
        holder.tvKeperluan.text = tamu.keperluan

        // 💡 3. TERAPKAN LISTENER
        holder.btnEdit.setOnClickListener {
            listener.onEditClick(tamu)
        }

        holder.btnHapus.setOnClickListener {
            listener.onDeleteClick(tamu)
        }
    }

    override fun getItemCount(): Int {
        return tamuList.size
    }

    /** 💡 4. FUNGSI UPDATE DATA (Kunci Refresh) */
    fun updateData(newData: List<Tamu>) {
        this.tamuList = newData
        notifyDataSetChanged()
    }
}