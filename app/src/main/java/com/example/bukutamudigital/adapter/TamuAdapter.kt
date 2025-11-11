package com.example.bukutamudigital.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bukutamudigital.R
import com.example.bukutamudigital.model.Tamu

// Pastikan konstruktor Anda menerima sebuah List<Tamu>
class TamuAdapter(private var tamuList: List<Tamu>) : RecyclerView.Adapter<TamuAdapter.TamuViewHolder>() {

    class TamuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaTamu)
        val tvInstansi: TextView = itemView.findViewById(R.id.tvInstansiTamu)
        val tvKeperluan: TextView = itemView.findViewById(R.id.tvKeperluanTamu)
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
    }

    override fun getItemCount(): Int {
        return tamuList.size
    }

    // ==========================================================
    // === TAMBAHKAN FUNGSI INI UNTUK MEMPERBAIKI ERROR ===
    // ==========================================================
    fun updateData(newData: List<Tamu>) {
        this.tamuList = newData
        notifyDataSetChanged() // Perintah penting untuk refresh RecyclerView
    }
    // ==========================================================
}
    