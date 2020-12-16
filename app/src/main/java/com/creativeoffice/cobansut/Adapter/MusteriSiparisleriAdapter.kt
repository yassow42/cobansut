package com.creativeoffice.cobansut.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.database.FirebaseDatabase
import com.simplecityapps.recyclerview_fastscroll.utils.Utils
import kotlinx.android.synthetic.main.item_siparisler.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusteriSiparisleriAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>, var bolge: String) : RecyclerView.Adapter<MusteriSiparisleriAdapter.SiparisMusteriHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriSiparisleriAdapter.SiparisMusteriHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)


        view.tvSiparisVeren.visibility = View.GONE
        view.tvSiparisTel.visibility = View.GONE
        view.tvSiparisAdres.visibility = View.GONE
        view.swSiparisPro.visibility = View.GONE
        view.tvNot.textSize = 12f
        view.view.visibility = View.GONE


        return SiparisMusteriHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: MusteriSiparisleriAdapter.SiparisMusteriHolder, position: Int) {

        holder.setData(siparisler[position])


    }

    inner class SiparisMusteriHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tumLayout = itemView.tumLayout
        val siparisVeren = itemView.tvSiparisVeren
        val siparisAdres = itemView.tvSiparisAdres
        val siparisTel = itemView.tvSiparisTel
        val tv3lt = itemView.tv3lt
        val tv3ltFiyat = itemView.tv3ltFiyat
        val tv5lt = itemView.tv5lt
        val tv5ltFiyat = itemView.tv5ltFiyat
        val tvDokme = itemView.tvDokmeSut
        val tvDokmeFiyat = itemView.tvDokmeSutFiyat
        val tvYumurta = itemView.tvYumurta
        val tvYumurtaFiyat = itemView.tvYumurtaFiyat
        val tvZaman = itemView.tvZaman
        val tvTeslimZaman = itemView.tvTeslimZamani
        val tvNot = itemView.tvNot
        val tvFiyat = itemView.tvFiyat
        val swSiparisPromosyon = itemView.swSiparisPro
        val siparisGiren = itemView.tvSiparisGiren

        val tv3litre = itemView.tv3litre
        val tv5litre = itemView.tv5litre
        val tvYumurtaYazi = itemView.tvYumurtaYazisi

        fun setData(siparisData: SiparisData) {

            siparisVeren.text = siparisData.siparis_veren
            siparisGiren.text = siparisData.siparisi_giren
            siparisAdres.text = siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres
            siparisTel.text = siparisData.siparis_tel
            tvNot.text = siparisData.siparis_notu
            tv3lt.text = siparisData.sut3lt
            tv5lt.text = siparisData.sut5lt
            tvDokme.text = siparisData.dokme_sut
            tvYumurta.text = siparisData.yumurta


            tvZaman.text = TimeAgo.getTimeAgo(siparisData.siparis_zamani.toString().toLong())
            tvTeslimZaman.text = formatDate(siparisData.siparis_teslim_tarihi).toString()
            tvFiyat.visibility = View.GONE

            var sut3ltAdet = 0
            var sut3ltFiyat = 0.0
            var sut5ltAdet = 0
            var sut5ltFiyat = 0.0
            var yumurtaAdet = 0
            var yumurtaFiyat = 0.0



            if (!siparisData.sut3lt.isNullOrEmpty()) {
                tv3lt.text = siparisData.sut3lt
                sut3ltAdet = siparisData.sut3lt.toString().toInt()
            }

            if (!siparisData.sut5lt.isNullOrEmpty()) {
                tv5lt.text = siparisData.sut5lt
                sut5ltAdet = siparisData.sut5lt.toString().toInt()
            }

            if (!siparisData.dokme_sut.isNullOrEmpty()) {
                tvDokme.text = siparisData.dokme_sut
                tvDokmeFiyat.text = siparisData.dokme_sut_fiyat.toString()
            }

            if (!siparisData.yumurta.isNullOrEmpty()) {
                tvYumurta.text = siparisData.yumurta
                yumurtaAdet = siparisData.yumurta.toString().toInt()
            }


            if (!siparisData.sut3lt_fiyat.toString().isNullOrEmpty() && !siparisData.sut5lt_fiyat.toString().isNullOrEmpty() &&
                !siparisData.yumurta_fiyat.toString().isNullOrEmpty() && !siparisData.dokme_sut_fiyat.toString().isNullOrEmpty()
            ) {
                tv3ltFiyat.text = siparisData.sut3lt_fiyat.toString()
                tv5ltFiyat.text = siparisData.sut5lt_fiyat.toString()
                tvYumurtaFiyat.text = siparisData.yumurta_fiyat.toString()
                tvDokmeFiyat.text = siparisData.dokme_sut_fiyat.toString()
                //        sut3ltFiyat = siparisData.sut3lt_fiyat.toString().toDouble()
                //        sut5ltFiyat = siparisData.sut5lt_fiyat.toString().toDouble()
                // yumurtaFiyat = siparisData.yumurta_fiyat.toString().toDouble()

                //     var toplamFiyat = (sut3ltAdet * sut3ltFiyat!!) + (sut5ltAdet * sut5ltFiyat!!) + (yumurtaAdet * yumurtaFiyat!!)
                //      FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisData.siparis_key.toString()).child("toplam_fiyat").setValue(toplamFiyat)
                //    tvFiyat.text = ((sut3ltAdet * sut3ltFiyat!!) + (sut5ltAdet * sut5ltFiyat!!) + (yumurtaAdet * yumurtaFiyat!!)).toString() + " tl"


            }
            /*
            var refBolge = FirebaseDatabase.getInstance().reference.child(bolge)
            if (siparisData.dokme_sut_fiyat == null) {
               refBolge.child("Musteriler/${siparisData.siparis_veren}").child("siparisleri").child(siparisData.siparis_key.toString())
                   .child("dokme_sut_fiyat").setValue(3.5)
                refBolge.child("Musteriler/${siparisData.siparis_veren}").child("siparisleri").child(siparisData.siparis_key.toString())
                   .child("dokme_sut").setValue("0")
            }
            if (siparisData.sut3lt_fiyat == null) {
               refBolge.child("Musteriler/${siparisData.siparis_veren}").child("siparisleri").child(siparisData.siparis_key.toString())
                   .child("sut3lt_fiyat").setValue(16)
                refBolge.child("Musteriler/${siparisData.siparis_veren}").child("siparisleri").child(siparisData.siparis_key.toString())
                   .child("sut5lt_fiyat").setValue(22)
                refBolge.child("Musteriler/${siparisData.siparis_veren}").child("siparisleri").child(siparisData.siparis_key.toString())
                   .child("toplam_fiyat").setValue(0)
                refBolge.child("Musteriler/${siparisData.siparis_veren}").child("siparisleri").child(siparisData.siparis_key.toString())
                   .child("yumurta_fiyat").setValue(1)
            }
*/

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat(" d MMM yyyy ", Locale("tr"))
            return sdf.format(date)

        }

    }


}