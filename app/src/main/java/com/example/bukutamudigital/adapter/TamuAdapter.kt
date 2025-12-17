package com.example.bukutamudigital.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bukutamudigital.R
import com.example.bukutamudigital.model.Tamu

interface OnItemClickListener {
    fun onDeleteClick(tamu: Tamu)
    fun onEditClick(tamu: Tamu) // Tambahkan fungsi untuk edit
}

class TamuAdapter(
    private var tamuList: MutableList<Tamu>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TamuAdapter.TamuViewHolder>() {

    class TamuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaTamu)
        val tvInstansi: TextView = itemView.findViewById(R.id.tvInstansiTamu)
        val tvKeperluan: TextView = itemView.findViewById(R.id.tvKeperluanTamu)
        val btnHapus: ImageButton = itemView.findViewById(R.id.btnHapus)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit) // Referensi ke tombol edit
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

        holder.btnHapus.setOnClickListener {
            listener.onDeleteClick(tamu)
        }

        // Pasang listener di tombol edit
        holder.btnEdit.setOnClickListener {
            listener.onEditClick(tamu)
        }
    }

    override fun getItemCount(): Int {
        return tamuList.size
    }

    fun updateData(newData: List<Tamu>) {
        this.tamuList.clear()
        this.tamuList.addAll(newData)
        notifyDataSetChanged()
    }
}
