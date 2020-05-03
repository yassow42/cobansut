package com.creativeoffice.cobansut.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.TimeAgo
import kotlinx.android.synthetic.main.item_siparisler.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusteriSiparisleriAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>) : RecyclerView.Adapter<MusteriSiparisleriAdapter.SiparisMusteriHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriSiparisleriAdapter.SiparisMusteriHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)


        view.tvSiparisVeren.visibility = View.GONE
        view.tvSiparisTel.visibility = View.GONE
        view.tvSiparisAdres.visibility = View.GONE
        view.tvNot.visibility = View.GONE
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
        val tv5lt = itemView.tv5lt
        val tvYumurta = itemView.tvYumurta
        val tvZaman = itemView.tvZaman
        val tvTeslimZaman = itemView.tvTeslimZamani
        val tvNot = itemView.tvNot
        val tvFiyat = itemView.tvFiyat

        fun setData(siparisData: SiparisData) {

            siparisVeren.text = siparisData.siparis_veren
            siparisAdres.text = siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres
            siparisTel.text = siparisData.siparis_tel
            tvNot.text = siparisData.siparis_notu
            tv3lt.text = siparisData.sut3lt
            tv5lt.text = siparisData.sut5lt
            tvYumurta.text = siparisData.yumurta


            tvZaman.text = TimeAgo.getTimeAgo(siparisData.siparis_zamani.toString().toLong())
            tvTeslimZaman.text = formatDate(siparisData.siparis_teslim_tarihi).toString()

            var sut3ltFiyat = siparisData.sut3lt.toString().toInt()
            var sut5ltFiyat = siparisData.sut5lt.toString().toInt()
            var yumurtaFiyat = siparisData.yumurta.toString().toInt()
            tvFiyat.text = ((sut3ltFiyat * 16) + (sut5ltFiyat * 22) + yumurtaFiyat).toString() + " tl"


        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat(" d MMM yyyy ", Locale("tr"))
            return sdf.format(date)

        }

    }


}