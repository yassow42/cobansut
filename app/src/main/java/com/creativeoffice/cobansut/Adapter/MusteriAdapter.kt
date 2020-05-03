package com.creativeoffice.cobansut.Adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.etAdresGidilen
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.etApartman
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.etTelefonGidilen
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.tv3litre
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.tv5litre
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.tvAdSoyad
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.tvFiyatGenel
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.tvYumurta
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import kotlinx.android.synthetic.main.item_musteri.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusteriAdapter(val myContext: Context, val musteriler: ArrayList<MusteriData>) : RecyclerView.Adapter<MusteriAdapter.MusteriHolder>() {

    lateinit var dialogViewSp: View
    var genelFiyat = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriAdapter.MusteriHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_musteri, parent, false)


        return MusteriHolder(view)

    }

    override fun getItemCount(): Int {
        return musteriler.size
    }

    override fun onBindViewHolder(holder: MusteriAdapter.MusteriHolder, position: Int) {
        holder.setData(musteriler[position])

        holder.btnSiparisEkle.setOnClickListener {


            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)
            dialogViewSp = inflate(myContext, R.layout.dialog_siparis_ekle, null)


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

            dialogViewSp.tvZamanEkleDialog.setOnClickListener {
                DatePickerDialog(myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                TimePickerDialog(myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }

            dialogViewSp.et3lt.addTextChangedListener(holder.watcherFiyat3lt)
            dialogViewSp.et5lt.addTextChangedListener(holder.watcherFiyat5lt)
            dialogViewSp.etYumurta.addTextChangedListener(holder.watcherFiyatYumurta)
            dialogViewSp.imgFiyatRefresh.setOnClickListener {
                var fiyat3 = 0
                if (dialogViewSp.et3lt.text.isNotEmpty()) {
                    fiyat3 = dialogViewSp.et3lt.text.toString().toInt() * 16
                }


                var fiyat5 = 0
                if (dialogViewSp.et5lt.text.isNotEmpty()) {
                    fiyat5 =dialogViewSp.et5lt.text.toString().toInt() * 22
                }
                var fiyatYum = 0
                if (dialogViewSp.etYumurta.text.isNotEmpty()) {
                    fiyatYum =dialogViewSp.etYumurta.text.toString().toInt() * 1
                }
                dialogViewSp.tvFiyatSp.text = (fiyat3 + fiyat5 + fiyatYum).toString()
            }

            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Sipariş Ekle", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    var sut3lt = "0"
                    if (dialogViewSp.et3lt.text.toString().isNotEmpty()) {
                        sut3lt = dialogViewSp.et3lt.text.toString()
                    }
                    var sut5lt = "0"

                    if (dialogViewSp.et5lt.text.toString().isNotEmpty()) {
                        sut5lt = dialogViewSp.et5lt.text.toString()
                    }
                    var yumurta = "0"
                    if (dialogViewSp.etYumurta.text.toString().isNotEmpty()) {
                        yumurta = dialogViewSp.etYumurta.text.toString()
                    }

                    var siparisNotu = dialogViewSp.etSiparisNotu.text.toString()

                    var siparisKey = FirebaseDatabase.getInstance().reference.child("Siparisler").push().key.toString()


                    var siparisData = SiparisData(
                        null, null, cal.timeInMillis, musteriler[position].musteri_adres, musteriler[position].musteri_apartman,
                        musteriler[position].musteri_tel, musteriler[position].musteri_ad_soyad, musteriler[position].musteri_mah, siparisNotu, siparisKey, yumurta, sut3lt, sut5lt
                    )
                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisKey).setValue(siparisData)
                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey)
                        .child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey)
                        .child("siparis_zamani").setValue(ServerValue.TIMESTAMP)


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

                        var ad = musteriler[position].musteri_ad_soyad.toString()
                        var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)

                        var dialogView: View = inflate(myContext, R.layout.dialog_gidilen_musteri, null)
                        builder.setView(dialogView)


                        var dialog: Dialog = builder.create()


                        dialogView.imgCheck.setOnClickListener {
                            if (dialogView.etAdresGidilen.text.toString().isNotEmpty() && dialogView.etTelefonGidilen.text.toString().isNotEmpty()) {
                                var adres = dialogView.etAdresGidilen.text.toString()
                                var telefon = dialogView.etTelefonGidilen.text.toString()
                                var apartman = dialogView.etApartman.text.toString()
                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).child("musteri_adres").setValue(adres)
                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).child("musteri_apartman").setValue(apartman)
                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).child("musteri_tel").setValue(telefon).addOnCompleteListener {
                                    Toast.makeText(myContext, "Müşteri Bilgileri Güncellendi", Toast.LENGTH_LONG).show()
                                    dialog.dismiss()
                                }.addOnFailureListener {
                                    Toast.makeText(myContext, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(myContext, "Boşluklar var", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                        }
                        dialogView.imgBack.setOnClickListener {
                            dialog.dismiss()
                        }



                        dialogView.tvAdSoyad.text = musteriler[position].musteri_ad_soyad.toString()
                        dialogView.tvMahalle.text = musteriler[position].musteri_mah.toString() + " Mahallesi"
                        dialogView.etApartman.setText(musteriler[position].musteri_apartman.toString())
                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                var adres = p0.child("musteri_adres").value.toString()
                                var telefon = p0.child("musteri_tel").value.toString()
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
                                    val Adapter = MusteriSiparisleriAdapter(myContext, list)
                                    dialogView.rcSiparisGidilen.adapter = Adapter
                                    dialogView.rcSiparisGidilen.setHasFixedSize(true)


                                }
                            }


                        })

                        dialog.show()


                    }
                    R.id.popSil -> {

                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Müşteriyi Sil")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).removeValue()

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



        fun setData(musteriData: MusteriData) {
            musteriAdi.text = musteriData.musteri_ad_soyad
            sonSiparisZamani.text = TimeAgo.getTimeAgo(musteriData.siparis_son_zaman!!).toString()
        }


        var watcherFiyat3lt = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 0) {

                    dialogViewSp.tvFiyatSp.text = (dialogViewSp.tvFiyatSp.text.toString().toInt() + (s.toString().toInt() * 16)).toString()

                } else {

                    dialogViewSp.tvFiyatSp.text = dialogViewSp.tvFiyatSp.text.toString()
                }
            }

        }

        var watcherFiyat5lt = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 0) {

                    dialogViewSp.tvFiyatSp.text = (dialogViewSp.tvFiyatSp.text.toString().toInt() + (s.toString().toInt() * 22)).toString()

                } else {

                    dialogViewSp.tvFiyatSp.text = dialogViewSp.tvFiyatSp.text.toString()
                }
            }

        }

        var watcherFiyatYumurta = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 0) {

                    dialogViewSp.tvFiyatSp.text = (dialogViewSp.tvFiyatSp.text.toString().toInt() + (s.toString().toInt() * 1)).toString()

                } else {
                    dialogViewSp.tvFiyatSp.text = dialogViewSp.tvFiyatSp.text.toString()
                }
            }

        }


    }


}
