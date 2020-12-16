package com.creativeoffice.cobansut.CorluAdapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Adapter.MusteriSiparisleriAdapter
import com.creativeoffice.cobansut.CorluActivity.AdresBulmaMapsCorluActivity
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et3lt
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et3ltFiyat
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et5lt
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et5ltFiyat
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.etSiparisNotu
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.etYumurta
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.etYumurtaFiyat
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.swPromosyon
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.tvZamanEkleDialog
import kotlinx.android.synthetic.main.item_musteri.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusteriCorluAdapter(val myContext: Context, val musteriler: ArrayList<MusteriData>, val kullaniciAdi: String?) : RecyclerView.Adapter<MusteriCorluAdapter.MusteriHolder>() {

    lateinit var dialogViewSp: View
    lateinit var dialogMsDznle: Dialog
    val ref = FirebaseDatabase.getInstance().reference
    val refCorlu = FirebaseDatabase.getInstance().reference.child("Corlu")

    var genelFiyat = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriCorluAdapter.MusteriHolder {
        val myView = LayoutInflater.from(myContext).inflate(R.layout.item_musteri, parent, false)

        return MusteriHolder(myView)
    }

    override fun getItemCount(): Int {
        return musteriler.size
    }

    override fun onBindViewHolder(holder: MusteriCorluAdapter.MusteriHolder, position: Int) {
        holder.setData(musteriler[position])

        holder.btnSiparisEkle.setOnClickListener {


            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)
            dialogViewSp = View.inflate(myContext, R.layout.dialog_siparis_ekle_corlu, null)


            dialogViewSp.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
            var cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                dialogViewSp.tvZamanEkleDialog.text = sdf.format(cal.time)
            }

            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
            }

            dialogViewSp.swPromosyon.setOnClickListener {

                if (dialogViewSp.swPromosyon.isChecked) {
                    refCorlu.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(true)
                } else {
                    refCorlu.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(false)

                }
            }

            //    dialogViewSp.swPromosyon.isChecked = musteriler[position].promosyon_verildimi.toString().toBoolean()

            dialogViewSp.tvZamanEkleDialog.setOnClickListener {
                DatePickerDialog(myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                TimePickerDialog(myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }


            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Sipariş Ekle", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    var sut3ltAdet = "0"
                    if (dialogViewSp.et3lt.text.toString().isNotEmpty()) {
                        sut3ltAdet = dialogViewSp.et3lt.text.toString()
                    }
                    var sut5ltAdet = "0"
                    if (dialogViewSp.et5lt.text.toString().isNotEmpty()) {
                        sut5ltAdet = dialogViewSp.et5lt.text.toString()
                    }
                    var yumurtaAdet = "0"
                    if (dialogViewSp.etYumurta.text.toString().isNotEmpty()) {
                        yumurtaAdet = dialogViewSp.etYumurta.text.toString()
                    }
                    var dokmeSutAdet = "0"
                    if (dialogViewSp.etDokmeSut.text.toString().isNotEmpty()) {
                        dokmeSutAdet = dialogViewSp.etDokmeSut.text.toString()
                    }


                    var dokmeSutFiyat = dialogViewSp.etDokmeSutFiyat.text.toString().toDouble()

                    var siparisNotu = dialogViewSp.etSiparisNotu.text.toString()
                    var siparisKey = FirebaseDatabase.getInstance().reference.child("Siparisler").push().key.toString()
                    var sut3ltFiyat = dialogViewSp.et3ltFiyat.text.toString().toDouble()
                    var sut5ltFiyat = dialogViewSp.et5ltFiyat.text.toString().toDouble()
                    var yumurtaFiyat = dialogViewSp.etYumurtaFiyat.text.toString().toDouble()

                    var toplamFiyat = (sut3ltAdet.toDouble() * sut3ltFiyat) + (sut5ltAdet.toDouble() * sut5ltFiyat) + (yumurtaAdet.toDouble() * yumurtaFiyat) + (dokmeSutAdet.toDouble() * dokmeSutFiyat)


                    var siparisData = SiparisData(
                        System.currentTimeMillis(), System.currentTimeMillis(), cal.timeInMillis, musteriler[position].musteri_adres, musteriler[position].musteri_apartman,
                        musteriler[position].musteri_tel, musteriler[position].musteri_ad_soyad, musteriler[position].musteri_mah, siparisNotu, siparisKey, yumurtaAdet, yumurtaFiyat, sut3ltAdet, sut3ltFiyat,
                        sut5ltAdet, sut5ltFiyat,dokmeSutAdet,dokmeSutFiyat, toplamFiyat, musteriler[position].musteri_zkonum, musteriler[position].promosyon_verildimi, musteriler[position].musteri_zlat,
                        musteriler[position].musteri_zlong, kullaniciAdi
                    )
                    refCorlu.child("Siparisler").child(musteriler[position].musteri_mah.toString()).child(siparisKey).child("toplam_fiyat").setValue(0.0)
                    refCorlu.child("Siparisler").child(musteriler[position].musteri_mah.toString()).child(siparisKey).setValue(siparisData)
                  //  refCorlu.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                 //   refCorlu.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                    refCorlu.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                    refCorlu.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                    refCorlu.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)


                }
            })

            builder.setTitle(musteriler[position].musteri_ad_soyad)
            builder.setIcon(R.drawable.cow)

            builder.setView(dialogViewSp)
            var dialog: Dialog = builder.create()
            dialog.show()

        }


        holder.itemView.setOnLongClickListener {

            val popup = PopupMenu(myContext, holder.itemView)
            popup.inflate(R.menu.popup_menu_musteri)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popDüzenle -> {

                        var musteriAdi = musteriler[position].musteri_ad_soyad.toString()
                        var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)

                        var dialogView: View = View.inflate(myContext, R.layout.dialog_gidilen_musteri, null)
                        builder.setView(dialogView)

                        dialogMsDznle = builder.create()

                        dialogView.imgMaps.setOnClickListener {
                            var intent = Intent(myContext, AdresBulmaMapsCorluActivity::class.java)
                            intent.putExtra("musteri_konumu", "Corlu")
                            intent.putExtra("musteriAdi", musteriler[position].musteri_ad_soyad)
                            myContext.startActivity(intent)
                        }

                        dialogView.swKonumKaydet.setOnClickListener {

                            if (dialogView.swKonumKaydet.isChecked) {
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_zkonum").setValue(true)
                                //  holder.getLocation(musteriAdi)

                            } else {
                                //     holder.locationManager.removeUpdates(holder.myLocationListener)
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_zkonum").setValue(false)
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_zlat").removeValue()
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_zlong").removeValue()

                            }

                        }

                        dialogView.imgCheck.setOnClickListener {

                            if (dialogView.etAdresGidilen.text.toString().isNotEmpty() && dialogView.etTelefonGidilen.text.toString().isNotEmpty()) {
                                var mahalle = dialogView.tvMahalle.text.toString()
                                var adres = dialogView.etAdresGidilen.text.toString()
                                var telefon = dialogView.etTelefonGidilen.text.toString()
                                var apartman = dialogView.etApartman.text.toString()


                                ref.child("Musteriler").child(musteriAdi).child("musteri_mah").setValue(mahalle)
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_adres").setValue(adres)
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_apartman").setValue(apartman)
                                refCorlu.child("Musteriler").child(musteriAdi).child("musteri_tel").setValue(telefon).addOnCompleteListener {
///locationsu durduruyruz
                                    //   holder.locationManager.removeUpdates(holder.myLocationListener)
///
                                    dialogMsDznle.dismiss()

                                    Toast.makeText(myContext, "Müşteri Bilgileri Güncellendi", Toast.LENGTH_LONG).show()
                                }.addOnFailureListener { Toast.makeText(myContext, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show() }
                            } else {
                                Toast.makeText(myContext, "Bilgilerde boşluklar var", Toast.LENGTH_LONG).show()
                            }
                        }

                        dialogView.imgBack.setOnClickListener {
                            //  holder.locationManager.removeUpdates(holder.myLocationListener)
                            dialogMsDznle.dismiss()
                        }

                        dialogView.tvAdSoyad.text = musteriler[position].musteri_ad_soyad.toString()
                        dialogView.tvMahalle.setText( musteriler[position].musteri_mah.toString())
                        dialogView.etApartman.setText(musteriler[position].musteri_apartman.toString())
                        refCorlu.child("Musteriler").child(musteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
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
                                    var yumurtaSayisi = 0

                                    for (ds in p0.child("siparisleri").children) {
                                        var gelenData = ds.getValue(SiparisData::class.java)!!
                                        list.add(gelenData)

                                        sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                        sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                        yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi

                                    }

                                    dialogView.tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                                    dialogView.tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                                    dialogView.tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                                    dialogView.tvFiyatGenel.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"


                                    dialogView.rcSiparisGidilen.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
                                    //        dialogView.rcSiparisGidilen.layoutManager = StaggeredGridLayoutManager(myContext, LinearLayoutManager.VERTICAL, 2)
                                    val Adapter = MusteriSiparisleriAdapter(myContext, list,"")
                                    dialogView.rcSiparisGidilen.adapter = Adapter
                                    dialogView.rcSiparisGidilen.setHasFixedSize(true)


                                }
                            }


                        })
                        dialogMsDznle.setCancelable(false)
                        dialogMsDznle.show()

                    }
                    R.id.popSil -> {

                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Müşteriyi Sil")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    refCorlu.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).removeValue()
                                   musteriler.remove(musteriler[position])
                                    notifyDataSetChanged()

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


    }

    inner class MusteriHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var musteriAdi = itemView.tvMusteriAdi
        var btnSiparisEkle = itemView.tvSiparisEkle
        var sonSiparisZamani = itemView.tvMusteriSonZaman


        var musteriAdiGnl = ""
        fun setData(musteriData: MusteriData) {
            musteriAdiGnl = musteriData.musteri_ad_soyad.toString()
            musteriAdi.text = musteriData.musteri_ad_soyad
            musteriData.siparis_son_zaman?.let {
                sonSiparisZamani.text = TimeAgo.getTimeAgo(it!!).toString()
            }

        }

    }


}
