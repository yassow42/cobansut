package com.creativeoffice.cobansut.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_teslim.view.*

class TeslimEdilenlerAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>) : RecyclerView.Adapter<TeslimEdilenlerAdapter.SiparisHolder>() {
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var saticiYetki: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeslimEdilenlerAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_teslim, parent, false)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        //Log.e("sad",userID)
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                saticiYetki = p0.child("yetki").value.toString()
            }

        })

        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: SiparisHolder, position: Int) {
        holder.setData(siparisler[position])
        holder.itemView.setOnLongClickListener {
            if (saticiYetki == "Yönetici") {
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
            }
            return@setOnLongClickListener true


        }

    }

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val musteriAdSoyad = itemView.tvMusteriAdSoyad
        val teslimEden = itemView.tvSiparisGiren
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
            if (!siparisData.siparisi_giren.isNullOrEmpty()) {
                teslimEden.text = siparisData.siparisi_giren.toString()
            } else {
                teslimEden.visibility = View.GONE
            }




        }

    }


}