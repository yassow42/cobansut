package com.creativeoffice.cobansut.Adapter


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.EnYeni.SiparisActivity
import com.creativeoffice.cobansut.TimeAgo
import com.creativeoffice.cobansut.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import kotlinx.android.synthetic.main.item_siparisler.view.*
import kotlinx.android.synthetic.main.item_siparisler.view.tv3litre
import kotlinx.android.synthetic.main.item_siparisler.view.tv5litre
import kotlinx.android.synthetic.main.item_siparisler.view.tvYumurta
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SiparisAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>, val kullaniciAdi: String, var bolge:String?) : RecyclerView.Adapter<SiparisAdapter.SiparisHolder>() {
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var saticiYetki: String

    var refBolge = FirebaseDatabase.getInstance().reference.child(bolge.toString())
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SiparisAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        //Log.e("sad",userID)

        saticiYetki = Utils.yetki

        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: SiparisAdapter.SiparisHolder, position: Int) {
        var item = siparisler[position]
        try {
            holder.setData(item)
            holder.itemView.setOnLongClickListener {

                val popup = PopupMenu(myContext, holder.itemView)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.popTeslim -> {
                            var alert = AlertDialog.Builder(myContext)
                                .setTitle("Sipariş Teslim Edildi")
                                .setMessage("Emin Misin ?")
                                .setPositiveButton("Onayla", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {


                                        var siparisData = SiparisData(
                                            siparisler[position].siparis_zamani,
                                            siparisler[position].siparis_teslim_zamani,
                                            siparisler[position].siparis_teslim_tarihi,
                                            siparisler[position].siparis_adres,
                                            siparisler[position].siparis_apartman,
                                            siparisler[position].siparis_tel,
                                            siparisler[position].siparis_veren,
                                            siparisler[position].siparis_mah,
                                            siparisler[position].siparis_notu,
                                            siparisler[position].siparis_key,
                                            siparisler[position].yumurta,
                                            siparisler[position].yumurta_fiyat,
                                            siparisler[position].sut3lt,
                                            siparisler[position].sut3lt_fiyat,
                                            siparisler[position].sut5lt,
                                            siparisler[position].sut5lt_fiyat,
                                            siparisler[position].dokme_sut,
                                            siparisler[position].dokme_sut_fiyat,
                                            siparisler[position].toplam_fiyat,
                                            siparisler[position].musteri_zkonum,
                                            siparisler[position].promosyon_verildimi,
                                            siparisler[position].musteri_zlat,
                                            siparisler[position].musteri_zlong,
                                            kullaniciAdi
                                        )

                                        //Musteriye kaydettık
                                        refBolge.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(siparisler[position].siparis_key.toString()).setValue(siparisData)
                                        //Teslim kısmına kaydettık
                                        refBolge.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).setValue(siparisData)
                                            .addOnCompleteListener {
                                                myContext.startActivity(Intent(myContext, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                                Toast.makeText(myContext, "Sipariş Teslim Edildi", Toast.LENGTH_LONG).show()

                                                ref.child("Siparisler").child(siparisler[position].siparis_key.toString()).removeValue()
                                                refBolge.child("Siparisler").child(siparisler[position].siparis_mah.toString()).child(siparisler[position].siparis_key.toString()).removeValue()


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
                        R.id.popDüzenle -> {
                            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)
                            var viewDuzenle: View = View.inflate(myContext, R.layout.dialog_siparis_ekle, null)

                            builder.setTitle(siparisler[position].siparis_veren)
                            builder.setIcon(R.drawable.cow)

                            viewDuzenle.et3lt.setText(siparisler[position].sut3lt)
                            viewDuzenle.et3ltFiyat.setText(siparisler[position].sut3lt_fiyat.toString())
                            viewDuzenle.et5lt.setText(siparisler[position].sut5lt)
                            viewDuzenle.et5ltFiyat.setText(siparisler[position].sut5lt_fiyat.toString())
                            viewDuzenle.etYumurta.setText(siparisler[position].yumurta)
                            viewDuzenle.etYumurtaFiyat.setText(siparisler[position].yumurta_fiyat.toString())
                            viewDuzenle.etDokmeSut.setText(siparisler[position].dokme_sut)
                            viewDuzenle.etDokmeSutFiyat.setText(siparisler[position].dokme_sut_fiyat.toString())
                            viewDuzenle.etSiparisNotu.setText(siparisler[position].siparis_notu)

                            viewDuzenle.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
                            var cal = Calendar.getInstance()
                            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, monthOfYear)
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                                val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                                val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                                viewDuzenle.tvZamanEkleDialog.text = sdf.format(cal.time)
                            }

                            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                cal.set(Calendar.MINUTE, minute)
                            }

                            viewDuzenle.tvZamanEkleDialog.setOnClickListener {
                                DatePickerDialog(myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                TimePickerDialog(myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                            }


                            builder.setView(viewDuzenle)


                            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.dismiss()
                                }

                            })
                            builder.setPositiveButton("Güncelle", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {

                                    var sut3ltAdet = "0"
                                    if (viewDuzenle.et3lt.text.isNotEmpty()) sut3ltAdet = viewDuzenle.et3lt.text.toString()

                                    var sut5ltAdet = "0"
                                    if (viewDuzenle.et5lt.text.isNotEmpty()) sut5ltAdet = viewDuzenle.et5lt.text.toString()

                                    var yumurtaAdet = "0"
                                    if (viewDuzenle.etYumurta.text.isNotEmpty()) yumurtaAdet = viewDuzenle.etYumurta.text.toString()

                                    var dokmeSutAdet = "0"
                                    if (viewDuzenle.etDokmeSut.text.isNotEmpty()) dokmeSutAdet = viewDuzenle.etDokmeSut.text.toString()


                                    var sut3ltFiyat = viewDuzenle.et3ltFiyat.text.toString().toDouble()
                                    var sut5ltFiyat = viewDuzenle.et5ltFiyat.text.toString().toDouble()
                                    var yumurtaFiyat = viewDuzenle.etYumurtaFiyat.text.toString().toDouble()
                                    var dokmeSutFiyat = viewDuzenle.etDokmeSutFiyat.text.toString().toDouble()

                                    var not = viewDuzenle.etSiparisNotu.text.toString()
                                    var siparisKey = siparisler[position].siparis_key.toString()
                                    var siparisVeren = siparisler[position].siparis_veren.toString()
                                    var toplamFiyat = (sut3ltAdet.toDouble() * sut3ltFiyat) + (sut5ltAdet.toDouble() * sut5ltFiyat) + (yumurtaAdet.toDouble() * yumurtaFiyat) + (dokmeSutAdet.toDouble() * dokmeSutFiyat)

                                    var data = SiparisData(
                                        item.siparis_zamani,
                                        item.siparis_teslim_zamani,
                                        cal.timeInMillis,
                                        item.siparis_adres,
                                        item.siparis_apartman,
                                        item.siparis_tel,
                                        item.siparis_veren,
                                        item.siparis_mah,
                                        not,
                                        item.siparis_key,
                                        yumurtaAdet,
                                        yumurtaFiyat,
                                        sut3ltAdet,
                                        sut3ltFiyat,
                                        sut5ltAdet,
                                        sut5ltFiyat,
                                        dokmeSutAdet,
                                        dokmeSutFiyat,
                                        toplamFiyat,
                                        item.musteri_zkonum,
                                        item.promosyon_verildimi,
                                        item.musteri_zlat,
                                        item.musteri_zlong,
                                        item.siparisi_giren
                                    )
                                    ref.child("Siparisler").child(siparisKey).setValue(data)
                                    ref.child("Siparisler").child(siparisKey).child("siparis_teslim_tarihi").setValue(cal.timeInMillis)

                                    refBolge.child("Siparisler").child(item.siparis_mah.toString()).child(siparisKey).setValue(data)
                                    refBolge.child("Siparisler").child(item.siparis_mah.toString()).child(siparisKey).child("siparis_teslim_tarihi").setValue(cal.timeInMillis)


                                    refBolge.child("Musteriler").child(siparisVeren).child("siparisleri").child(siparisKey).child("sut3lt").setValue(sut3ltAdet)

                                    var intent = Intent(myContext, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                    myContext.startActivity(intent)


                                }
                            })

                            var dialog: Dialog = builder.create()

                            dialog.show()
                        }

                        R.id.popSil -> {


                            var alert = AlertDialog.Builder(myContext)
                                .setTitle("Siparişi Sil")
                                .setMessage("Emin Misin ?")
                                .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {

                                        ref.child("Siparisler").child(item.siparis_key.toString()).removeValue()
                                        refBolge.child("Siparisler").child(item.siparis_mah.toString()).child(item.siparis_key.toString()).removeValue()
                                            .addOnCompleteListener {
                                                Toast.makeText(myContext, "Sipariş Silindi...", Toast.LENGTH_LONG).show()
                                            }

                                        ref.child("Musteriler").child(item.siparis_veren.toString()).child("siparisleri").child(item.siparis_key.toString()).removeValue()
                                        refBolge.child("Musteriler").child(item.siparis_veren.toString()).child("siparisleri").child(item.siparis_key.toString()).removeValue()
                                        notifyDataSetChanged()
                                        //  myContext.startActivity(Intent(myContext, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))

                                    }
                                })
                                .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        p0!!.dismiss()
                                    }
                                }).create()

                            alert.show()


                        }
                    }
                    return@OnMenuItemClickListener true
                })
                popup.show()

                return@setOnLongClickListener true
            }
            holder.siparisVeren.setOnClickListener {

                var dialogMsDznle: Dialog

                var musteriAdi = item.siparis_veren.toString()
                var builder = AlertDialog.Builder(myContext)

                var dialogView: View = View.inflate(myContext, R.layout.dialog_gidilen_musteri, null)
                builder.setView(dialogView)


                dialogMsDznle = builder.create()

                dialogView.imgMaps.visibility = View.GONE


                dialogView.swKonumKaydet.visibility = View.GONE

                dialogView.imgCheck.visibility = View.GONE

                dialogView.imgBack.setOnClickListener {
                    dialogMsDznle.dismiss()
                }

                dialogView.tvAdSoyad.visibility = View.GONE//.text =  item.siparis_veren.toString()
                dialogView.tvMahalle.visibility = View.GONE//.setText( item.siparis_mah.toString())
                dialogView.etApartman.visibility = View.GONE//.setText( item.siparis_apartman.toString())
                dialogView.etAdresGidilen.visibility = View.GONE
                dialogView.etTelefonGidilen.visibility = View.GONE

                refBolge.child("Musteriler").child(musteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var adres = p0.child("musteri_adres").value.toString()
                        var telefon = p0.child("musteri_tel").value.toString()
                        var konum = p0.child("musteri_zkonum").value.toString().toBoolean()

                        dialogView.swKonumKaydet.isChecked = konum
                        dialogView.etAdresGidilen.setText(adres)
                        dialogView.etTelefonGidilen.setText(telefon)

                        var list = ArrayList<SiparisData>()
                        list = ArrayList()
                        if (p0.child("siparisleri").hasChildren()) {

                            var sut3ltSayisi = 0
                            var sut5ltSayisi = 0
                            var dokmeSayisi = 0
                            var yumurtaSayisi = 0

                            for (ds in p0.child("siparisleri").children) {
                                var gelenData = ds.getValue(SiparisData::class.java)!!
                                list.add(gelenData)

                                sut3ltSayisi += gelenData.sut3lt!!.toInt()
                                sut5ltSayisi += gelenData.sut5lt!!.toInt()

                                yumurtaSayisi += gelenData.yumurta!!.toInt()

                                if (gelenData.dokme_sut != null) {
                                    dokmeSayisi += gelenData.dokme_sut!!.toInt()

                                }

                            }

                            list.sortByDescending { it.siparis_teslim_zamani }

                            dialogView.tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                            dialogView.tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                            dialogView.tvDokme.text = "Dökme: " + dokmeSayisi.toString()
                            dialogView.tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                            dialogView.tvFiyatGenel.visibility = View.GONE


                            dialogView.rcSiparisGidilen.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
                            //dialogView.rcSiparisGidilen.layoutManager = StaggeredGridLayoutManager(myContext, LinearLayoutManager.VERTICAL, 2)
                            val Adapter = MusteriSiparisleriAdapter(myContext, list)
                            dialogView.rcSiparisGidilen.adapter = Adapter
                            dialogView.rcSiparisGidilen.setHasFixedSize(true)


                        }
                    }


                })
                dialogMsDznle.setCancelable(false)
                dialogMsDznle.show()

            }
        } catch (e: IOException) {
            refBolge.child("Hatalar/Siparis adapter/277.satır hatası").setValue(e.message.toString())

        }


    }

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tumLayout = itemView.tumLayout
        val siparisVeren = itemView.tvSiparisVeren
        val siparisAdres = itemView.tvSiparisAdres
        val siparisTel = itemView.tvSiparisTel
        val tv3lt = itemView.tv3lt
        val tv3ltFiyat = itemView.tv3ltFiyat
        val tv5lt = itemView.tv5lt
        val tv5ltFiyat = itemView.tv5ltFiyat
        val tvDokmeSut = itemView.tvDokmeSut
        val tvDokmeSutFiyat = itemView.tvDokmeSutFiyat
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
            siparisAdres.text = siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres + " " + siparisData.siparis_apartman
            siparisTel.text = siparisData.siparis_tel
            tvNot.text = siparisData.siparis_notu
            tvFiyat.textSize = 18f




            siparisSayiGizle(siparisData)
            swSiparisPromosyon.visibility = View.GONE
            swSiparisPromosyon.setOnClickListener {
                if (swSiparisPromosyon.isChecked) {
                    refBolge.child("Musteriler").child(siparisData.siparis_veren.toString()).child("promosyon_verildimi").setValue(true)
                } else {
                    refBolge.child("Musteriler").child(siparisData.siparis_veren.toString()).child("promosyon_verildimi").setValue(false)

                }

            }

            if (siparisData.promosyon_verildimi != null) {
                var boolean = siparisData.promosyon_verildimi
                swSiparisPromosyon.isChecked = boolean.toString().toBoolean()
            }



            if (siparisData.siparis_zamani != null) {
                tvZaman.text = TimeAgo.getTimeAgo(siparisData.siparis_zamani.toString().toLong())
            } else {
                tvZaman.text = "yok"
                hataMesajiYazdir("sipariş zamanı yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }
            if (siparisData.siparis_teslim_tarihi != null) {

                tvTeslimZaman.text = formatDate(siparisData.siparis_teslim_tarihi).toString()
            } else {
                tvTeslimZaman.text = "yok"
                hataMesajiYazdir("teslim tarihi ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            var sut3ltAdet = 0
            var sut3ltFiyat: Double? = null
            var sut5ltAdet = 0
            var sut5ltFiyat: Double? = null
            var yumurtaAdet = 0
            var yumurtaFiyat: Double? = null
            var dokmeSutAdet = 0
            var dokmeSutFiyat: Double? = null



            if (!siparisData.sut3lt.isNullOrEmpty()) {
                tv3lt.text = siparisData.sut3lt
                sut3ltAdet = siparisData.sut3lt.toString().toInt()
                sut3ltFiyat = siparisData.sut3lt_fiyat.toString().toDouble()
            } else {
                hataMesajiYazdir("sut3 yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.sut5lt.isNullOrEmpty()) {
                tv5lt.text = siparisData.sut5lt
                sut5ltAdet = siparisData.sut5lt.toString().toInt()
                sut5ltFiyat = siparisData.sut5lt_fiyat.toString().toDouble()
            } else {
                hataMesajiYazdir("sut5 yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.yumurta.isNullOrEmpty()) {
                tvYumurta.text = siparisData.yumurta
                yumurtaAdet = siparisData.yumurta.toString().toInt()
                yumurtaFiyat = siparisData.yumurta_fiyat.toString().toDouble()
            } else {
                hataMesajiYazdir("yumurta yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.dokme_sut.isNullOrEmpty() && siparisData.dokme_sut_fiyat.toString() != "null") {
                tvDokmeSut.text = siparisData.dokme_sut
                dokmeSutAdet = siparisData.dokme_sut.toString().toInt()
                dokmeSutFiyat = siparisData.dokme_sut_fiyat
            } else {
                hataMesajiYazdir("dokme yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
                Log.e("dokme", "dokme yok ${siparisData.siparis_key} siparisData.siparis_veren.toString()")
            }



            if (sut3ltAdet.toString() != "null" && sut5ltAdet.toString() != "null" && yumurtaAdet.toString() != "null" && sut3ltFiyat.toString() != "null"
                && sut5ltFiyat.toString() != "null" && yumurtaFiyat.toString() != "null" && dokmeSutAdet.toString() != "null" && dokmeSutFiyat.toString() != "null"
            ) {
                tvFiyat.text = ((sut3ltAdet * sut3ltFiyat!!) + (sut5ltAdet * sut5ltFiyat!!) + (yumurtaAdet * yumurtaFiyat!!) + (dokmeSutAdet * dokmeSutFiyat!!)).toString() + " tl"
                tv3ltFiyat.text = siparisData.sut3lt_fiyat.toString()
                tv5ltFiyat.text = siparisData.sut5lt_fiyat.toString()
                tvDokmeSutFiyat.text = siparisData.dokme_sut_fiyat.toString()
                tvYumurtaFiyat.text = siparisData.yumurta_fiyat.toString()
            }





            siparisTel.setOnClickListener {
                val arama = Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                arama.data = Uri.parse("tel:" + siparisData.siparis_tel)
                myContext.startActivity(arama)
            }

            siparisAdres.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q= " + siparisData.siparis_adres))
                myContext.startActivity(intent)
            }


        }


        fun hataMesajiYazdir(s: String, isim: String) {
            FirebaseDatabase.getInstance().reference.child("Hatalar/SiparisAdapter").push().setValue(s)
            Toast.makeText(myContext, "Bu Kişinin Siparişi Hatalı Lütfen Sil $isim", Toast.LENGTH_LONG).show()
        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)
        }

        fun siparisSayiGizle(siparisData: SiparisData) {

            if (siparisData.dokme_sut.isNullOrEmpty()) refBolge.child("Siparisler").child(siparisData.siparis_key.toString()).child("dokme_sut").setValue("0")

            if (siparisData.dokme_sut_fiyat.toString() == "null") refBolge.child("Siparisler").child(siparisData.siparis_key.toString()).child("dokme_sut_fiyat").setValue(0.0)

        }


    }


}