package com.creativeoffice.cobansut.CorluAdapter


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.Activity.SiparislerActivity
import com.creativeoffice.cobansut.CorluActivity.SiparislerCorluActivity
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import kotlinx.android.synthetic.main.item_siparisler.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SiparisCorluAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>, val kullaniciAdi: String) : RecyclerView.Adapter<SiparisCorluAdapter.SiparisHolder>() {
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var saticiYetki: String
    val refCorlu = FirebaseDatabase.getInstance().reference.child("Corlu")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SiparisCorluAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)
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

    override fun onBindViewHolder(holder: SiparisCorluAdapter.SiparisHolder, position: Int) {

        holder.setData(siparisler[position])


        holder.itemView.setOnLongClickListener {

            val popup = PopupMenu(myContext, holder.itemView)
            popup.inflate(R.menu.popup_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popTeslim -> {
                        var alert = AlertDialog.Builder(myContext).setTitle("Sipariş Teslim Edildi").setMessage("Emin Misin ?").setPositiveButton("Onayla", object : DialogInterface.OnClickListener {
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
                                    siparisler[position].toplam_fiyat,
                                    siparisler[position].musteri_zkonum,
                                    siparisler[position].promosyon_verildimi,
                                    siparisler[position].musteri_zlat,
                                    siparisler[position].musteri_zlong,
                                    kullaniciAdi
                                )

                                refCorlu.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(siparisler[position].siparis_key.toString()).setValue(siparisData)

                                refCorlu.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).setValue(siparisData).addOnCompleteListener {

                                    myContext.startActivity(Intent(myContext, SiparislerCorluActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                    Toast.makeText(myContext, "Sipariş Teslim Edildi", Toast.LENGTH_LONG).show()
                                    refCorlu.child("Siparisler").child(siparisler[position].siparis_key.toString()).removeValue()
                                    refCorlu.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                    refCorlu.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
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
                        //   view.tvMusteriAdSoyad.setText(siparisler[position].siparis_veren)
                        viewDuzenle.et3lt.setText(siparisler[position].sut3lt)
                        viewDuzenle.et3ltFiyat.setText(siparisler[position].sut3lt_fiyat.toString())
                        viewDuzenle.et5lt.setText(siparisler[position].sut5lt)
                        viewDuzenle.et5ltFiyat.setText(siparisler[position].sut5lt_fiyat.toString())
                        viewDuzenle.etYumurta.setText(siparisler[position].yumurta)
                        viewDuzenle.etYumurtaFiyat.setText(siparisler[position].yumurta_fiyat.toString())
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

                                var sut3lt = "0"
                                if (viewDuzenle.et3lt.text.isNotEmpty()) {
                                    sut3lt = viewDuzenle.et3lt.text.toString()
                                }
                                var sut5lt = "0"
                                if (viewDuzenle.et5lt.text.isNotEmpty()) {
                                    sut5lt = viewDuzenle.et5lt.text.toString()
                                }
                                var yumurta = "0"
                                if (viewDuzenle.etYumurta.text.isNotEmpty()) {
                                    yumurta = viewDuzenle.etYumurta.text.toString()
                                }

                                var sut3ltFiyat = viewDuzenle.et3ltFiyat.text.toString().toDouble()
                                var sut5ltFiyat = viewDuzenle.et5ltFiyat.text.toString().toDouble()
                                var yumurtaFiyat = viewDuzenle.etYumurtaFiyat.text.toString().toDouble()

                                var not = viewDuzenle.etSiparisNotu.text.toString()
                                var siparisKey = siparisler[position].siparis_key.toString()
                                var siparisVeren = siparisler[position].siparis_veren.toString()
                                refCorlu.child("Siparisler").child(siparisKey).child("sut3lt").setValue(sut3lt)
                                refCorlu.child("Siparisler").child(siparisKey).child("sut3lt_fiyat").setValue(sut3ltFiyat)
                                refCorlu.child("Siparisler").child(siparisKey).child("sut5lt").setValue(sut5lt)
                                refCorlu.child("Siparisler").child(siparisKey).child("sut5lt_fiyat").setValue(sut5ltFiyat)
                                refCorlu.child("Siparisler").child(siparisKey).child("yumurta").setValue(yumurta)
                                refCorlu.child("Siparisler").child(siparisKey).child("yumurta_fiyat").setValue(yumurtaFiyat)
                                refCorlu.child("Siparisler").child(siparisKey).child("siparis_notu").setValue(not)
                                refCorlu.child("Siparisler").child(siparisKey).child("siparis_teslim_tarihi").setValue(cal.timeInMillis)

                                refCorlu.child("Musteriler").child(siparisVeren).child("siparisleri").child(siparisKey).child("sut3lt").setValue(sut3lt)
                                refCorlu.child("Musteriler").child(siparisVeren).child("siparisleri").child(siparisKey).child("sut5lt").setValue(sut5lt)
                                refCorlu.child("Musteriler").child(siparisVeren).child("siparisleri").child(siparisKey).child("yumurta").setValue(yumurta)
                                refCorlu.child("Musteriler").child(siparisVeren).child("siparisleri").child(siparisKey).child("siparis_notu").setValue(not)
                                var intent = Intent(myContext, SiparislerCorluActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                myContext.startActivity(intent)
                            }
                        })

                        var dialog: Dialog = builder.create()

                        dialog.show()
                    }

                    R.id.popSil -> {

                        if (saticiYetki == "Yönetici") {

                            var alert = AlertDialog.Builder(myContext).setTitle("Siparişi Sil").setMessage("Emin Misin ?").setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {

                                    refCorlu.child("Siparisler").child(siparisler[position].siparis_key.toString()).removeValue().addOnCompleteListener {
                                        Toast.makeText(myContext, "Sipariş Silindi...", Toast.LENGTH_LONG).show()
                                    }

                                    refCorlu.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(siparisler[position].siparis_key.toString()).removeValue()

                                    myContext.startActivity(Intent(myContext, SiparislerCorluActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))

                                }
                            })
                                .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        p0!!.dismiss()
                                    }
                                }).create()

                            alert.show()

                        } else {
                            Toast.makeText(myContext, "Yetkiniz yok. Yönetici ile iletişime geçin", Toast.LENGTH_LONG).show()

                        }


                    }
                }
                return@OnMenuItemClickListener true
            })
            popup.show()

            return@setOnLongClickListener true
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

            if (!siparisData.siparisi_giren.isNullOrEmpty()) {
                siparisGiren.text = siparisData.siparisi_giren.toString()
            } else {
                siparisGiren.visibility = View.GONE
            }

            siparisSayiGizle(siparisData)

            swSiparisPromosyon.setOnClickListener {
                if (swSiparisPromosyon.isChecked) {
                    refCorlu.child("Musteriler").child(siparisData.siparis_veren.toString()).child("promosyon_verildimi").setValue(true)
                } else {
                    refCorlu.child("Musteriler").child(siparisData.siparis_veren.toString()).child("promosyon_verildimi").setValue(false)

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


            try {
                tv3ltFiyat.text = siparisData.sut3lt_fiyat.toString()
                tv5ltFiyat.text = siparisData.sut5lt_fiyat.toString()
                tvYumurtaFiyat.text = siparisData.yumurta_fiyat.toString()
                var toplamFiyat = (sut3ltAdet * sut3ltFiyat!!) + (sut5ltAdet * sut5ltFiyat!!) + (yumurtaAdet * yumurtaFiyat!!)
                FirebaseDatabase.getInstance().reference.child("Corlu/Siparisler").child(siparisData.siparis_key.toString()).child("toplam_fiyat").setValue(toplamFiyat)
                tvFiyat.text = ((sut3ltAdet * sut3ltFiyat!!) + (sut5ltAdet * sut5ltFiyat!!) + (yumurtaAdet * yumurtaFiyat!!)).toString() + " tl"
            } catch (e: IOException) {
                Toast.makeText(myContext, "Bazı fiyatlar hatalı", Toast.LENGTH_LONG).show()
            }



            siparisTel.setOnClickListener {
                val arama =
                    Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                arama.data = Uri.parse("tel:" + siparisData.siparis_tel)
                myContext.startActivity(arama)
            }

            siparisAdres.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q= " + siparisData.siparis_adres))
                myContext.startActivity(intent)
            }


        }


        fun hataMesajiYazdir(s: String, isim: String) {
            refCorlu.child("Hatalar/SiparisAdapter").push().setValue(s)
            Toast.makeText(myContext, "Bu Kişinin Siparişi Hatalı Lütfen Sil $isim", Toast.LENGTH_LONG).show()
        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)
        }

        fun siparisSayiGizle(siparisData: SiparisData) {

            if (siparisData.sut3lt == "0") {
                tv3lt.visibility = View.VISIBLE
                tv3litre.visibility = View.VISIBLE
            }
            if (siparisData.sut5lt == "0") {
                tv5lt.visibility = View.VISIBLE
                tv5litre.visibility = View.VISIBLE
            }
            if (siparisData.yumurta == "0") {
                tvYumurta.visibility = View.VISIBLE
                tvYumurtaYazi.visibility = View.VISIBLE
            }


        }
    }


}