package com.creativeoffice.cobansut.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_teslim.view.*

class TeslimEdilenlerAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>) : RecyclerView.Adapter<TeslimEdilenlerAdapter.SiparisHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeslimEdilenlerAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_teslim, parent, false)


        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: SiparisHolder, position: Int) {
        holder.setData(siparisler[position])
        holder.itemView.setOnLongClickListener {
            var alert = AlertDialog.Builder(myContext)
                .setTitle("Siparişi Sil")
                .setMessage("Emin Misin ?")
                .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).removeValue()
                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(siparisler[position].siparis_key.toString())
                            .removeValue()

                    }
                })
                .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                    }
                }).create()

            alert.show()

            return@setOnLongClickListener true
        }

    }

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val musteriAdSoyad = itemView.tvMusteriAdSoyad
        val sut3lt = itemView.tv3lt
        val sut5lt = itemView.tv5lt
        val yumurta = itemView.tvYumurta
        val zaman = itemView.tvZaman


        fun setData(siparisData: SiparisData) {
            musteriAdSoyad.text = siparisData.siparis_veren
            sut3lt.text = siparisData.sut3lt
            sut5lt.text = siparisData.sut5lt
            yumurta.text = siparisData.yumurta
            zaman.text = TimeAgo.getTimeAgo(siparisData.siparis_teslim_zamani.toString().toLong())

        }

    }


}