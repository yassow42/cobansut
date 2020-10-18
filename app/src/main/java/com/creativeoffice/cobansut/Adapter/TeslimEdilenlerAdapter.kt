package com.creativeoffice.cobansut.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class TeslimEdilenlerAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>, val bolge: String) : RecyclerView.Adapter<TeslimEdilenlerAdapter.SiparisHolder>() {
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var saticiYetki: String
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeslimEdilenlerAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_teslim, parent, false)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid

        if (bolge == "Corlu") {
            ref = FirebaseDatabase.getInstance().reference.child("Corlu")
        }else if (bolge == "Cerkez") {
            ref = FirebaseDatabase.getInstance().reference.child("Cerkez")

        } else {
            ref = FirebaseDatabase.getInstance().reference
        }
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
                    .setTitle("Sil")
                    .setMessage("Emin Misin ?")
                    .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                            ref.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).removeValue()
                            ref.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(siparisler[position].siparis_key.toString())
                                .removeValue().addOnCompleteListener {
                                    Toast.makeText(myContext, "Sipariş silindi sayfayı yenileyebilirsin...", Toast.LENGTH_SHORT).show()
                                }

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
        val sut3ltf = itemView.tv3ltFiyat
        val sut5lt = itemView.tv5lt
        val sut5ltf = itemView.tv5ltFiyat
        val dokmeSut = itemView.tvDokumSut
        val dokmeSutFiyat = itemView.tvDokumSutFiyat
        val yumurta = itemView.tvYumurta
        var yumurtaf = itemView.tvYumurtaFiyat
        val zaman = itemView.tvZaman
        val siparisFiyatı = itemView.tvSiparisFiyat


        fun setData(siparisData: SiparisData) {
            musteriAdSoyad.text = siparisData.siparis_veren
            sut3lt.text = siparisData.sut3lt
            sut5lt.text = siparisData.sut5lt
            dokmeSut.text = siparisData.dokme_sut
            yumurta.text = siparisData.yumurta

            sut3ltf.text = siparisData.sut3lt_fiyat.toString()
            sut5ltf.text = siparisData.sut5lt_fiyat.toString()
            dokmeSutFiyat.text = siparisData.dokme_sut_fiyat.toString()
            yumurtaf.text = siparisData.yumurta_fiyat.toString()

            siparisFiyatı.setText(siparisData.toplam_fiyat.toString())

            zaman.text = TimeAgo.getTimeAgo(siparisData.siparis_teslim_zamani.toString().toLong())
            if (!siparisData.siparisi_giren.isNullOrEmpty()) {
                teslimEden.text = siparisData.siparisi_giren.toString()
            } else {
                teslimEden.visibility = View.GONE
            }


            var sut3ltAdet = siparisData.sut3lt.toString().toDouble()
            var sut3ltFiyat: Double = siparisData.sut3lt_fiyat.toString().toDouble()
            var sut5ltAdet = siparisData.sut5lt.toString().toDouble()
            var sut5ltFiyat: Double = siparisData.sut5lt_fiyat.toString().toDouble()
            var dokmeSutAdet = siparisData.dokme_sut.toString().toDouble()
            var dokmeSutFiyat: Double = siparisData.dokme_sut_fiyat.toString().toDouble()
            var yumurtaAdet = siparisData.yumurta.toString().toDouble()
            var yumurtaFiyat: Double = siparisData.yumurta_fiyat.toString().toDouble()


            var toplamFiyat = (sut3ltAdet * sut3ltFiyat) + (sut5ltAdet * sut5ltFiyat) + (yumurtaAdet * yumurtaFiyat) +(dokmeSutAdet*dokmeSutFiyat)
            ref.child("Teslim_siparisler").child(siparisData.siparis_key.toString()).child("toplam_fiyat").setValue(toplamFiyat)
            siparisFiyatı.text = toplamFiyat.toString() + " tl"

        }

    }


}