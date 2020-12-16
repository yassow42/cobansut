package com.creativeoffice.cobansut.EnYeni

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.MusteriAdapter
import com.creativeoffice.cobansut.Adapter.MusteriSiparisleriAdapter
import com.creativeoffice.cobansut.CorluActivity.AdresBulmaMapsCorluActivity
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperYeni
import com.creativeoffice.cobansut.utils.Datalar
import com.creativeoffice.cobansut.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_customer.*
import kotlinx.android.synthetic.main.activity_customer.bottomNavYeni
import kotlinx.android.synthetic.main.activity_customer.imgMusteriAra
import kotlinx.android.synthetic.main.activity_customer.imgMusteriEkle
import kotlinx.android.synthetic.main.activity_customer.rcMusteri
import kotlinx.android.synthetic.main.activity_customer.searchMs
import kotlinx.android.synthetic.main.activity_customer.spinner
import kotlinx.android.synthetic.main.activity_customer.tvBaslik
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*

import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.etApartman
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CustomerActivity : AppCompatActivity() {

    private var refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)
    var musteriList = ArrayList<MusteriData>()
    var musteriAdList = ArrayList<String>()

    //  val mahalleler = ArrayList<String>()
    lateinit var dialogViewSp: View
    lateinit var dialogMsDznle: Dialog

    lateinit var dialogViewSpArama: View
    lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)
        setupNavigationView()
        setupBtn()
        setupSpinner()
        setupVeri()


    }


    private fun setupVeri() {
        musteriList.clear()
        musteriAdList.clear()
        refBolge.child("Musteriler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenData = ds.getValue(MusteriData::class.java)!!
                        var musteriAdlari = gelenData.musteri_ad_soyad
                        musteriList.add(gelenData)
                        musteriAdList.add(musteriAdlari.toString())

                    }
                    var adapterSearch = ArrayAdapter<String>(this@CustomerActivity, android.R.layout.simple_expandable_list_item_1, musteriAdList)
                    searchMs.setAdapter(adapterSearch)
                    tvBaslik.text = "${Utils.secilenBolge} Müşterileri " + "(" + (musteriList.size) + ")"
                    setupRecyclerViewMusteriler()

                } else {
                    Toast.makeText(this@CustomerActivity, "Müşteri Bilgisi Alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun setupBtn() {
        imgMusteriEkle.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            dialogView = inflater.inflate(R.layout.dialog_musteri_ekle, null)
            var secilenMah = "Market"

            builder.setView(dialogView)
            builder.setTitle("Müşteri Ekle")

            val mahalleler = Datalar().mahalle()


            var adapterMah = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mahalleler)
            adapterMah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogView.spnMahler.adapter = adapterMah
            dialogView.spnMahler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(this@CustomerActivity, "Lütfen Mahalle Seç", Toast.LENGTH_LONG).show()
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)

                    secilenMah = dialogView.spnMahler.selectedItem.toString()

                }

            }




            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Ekle", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {


                    var musteriAdi = "Müşteri"
                    if (!dialogView.etMusteriAdSoyad.text.toString().isNullOrEmpty()) {
                        musteriAdi = dialogView.etMusteriAdSoyad.text.toString().trim().capitalize()
                    } else if (musteriAdi == "Müşteri") {
                        Toast.makeText(this@CustomerActivity, "Müşteri Adı Girmedin", Toast.LENGTH_LONG).show()
                    }
                    var musteriAdres = "Adres"
                    if (!dialogView.etAdres.text.toString().isNullOrEmpty()) {
                        musteriAdres = dialogView.etAdres.text.toString().trim()
                    } else if (musteriAdres == "Adres") {
                        Toast.makeText(this@CustomerActivity, "Adres Girmedin", Toast.LENGTH_LONG).show()
                    }

                    var musteriApt = "Apartman"
                    if (!dialogView.etApartman.text.toString().isNullOrEmpty()) {
                        musteriApt = dialogView.etApartman.text.toString().trim()
                    } else if (musteriAdres == "Adres") {
                        Toast.makeText(this@CustomerActivity, "Adres Girmedin", Toast.LENGTH_LONG).show()
                    }


                    var musteriTel = "Tel"
                    if (!dialogView.etTelefon.text.toString().isNullOrEmpty()) {
                        musteriTel = dialogView.etTelefon.text.toString()
                    } else if (musteriTel == "Tel") {
                        Toast.makeText(this@CustomerActivity, "Tel Girmedin", Toast.LENGTH_LONG).show()
                    }


                    var musteriBilgileri = MusteriData(musteriAdi, secilenMah, musteriAdres, musteriApt, musteriTel, System.currentTimeMillis(), false, false, null, null)

                    refBolge.child("Musteriler").child(musteriAdi).setValue(musteriBilgileri).addOnCompleteListener {
                        refBolge.child("Musteriler").child(musteriAdi).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                        setupVeri()
                    }

                }
            })

            var dialog: Dialog = builder.create()
            dialog.show()

        }

        imgMusteriAra.setOnClickListener {
            var arananMusteriAdi = searchMs.text.toString()
            val arananMusteriVarMi = musteriAdList.containsAll(listOf(arananMusteriAdi))

            if (arananMusteriVarMi) {
                /*        refBolge.child("Musteriler").child(arananMusteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if (p0.hasChildren()) {
                                    var musteriData = p0.getValue(MusteriData::class.java)!!

                                    var builder: AlertDialog.Builder = AlertDialog.Builder(this@CustomerActivity)
                                    dialogViewSpArama = View.inflate(this@CustomerActivity, R.layout.dialog_siparis_ekle, null)


                                    dialogViewSpArama.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
                                    var cal = Calendar.getInstance()
                                    val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                        cal.set(Calendar.YEAR, year)
                                        cal.set(Calendar.MONTH, monthOfYear)
                                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                                        val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                                        val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                                        dialogViewSpArama.tvZamanEkleDialog.text = sdf.format(cal.time)
                                    }
                                    val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        cal.set(Calendar.MINUTE, minute)
                                    }
                                    dialogViewSpArama.tvZamanEkleDialog.setOnClickListener {
                                        DatePickerDialog(this@CustomerActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                        TimePickerDialog(this@CustomerActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                                    }



                                    dialogViewSpArama.swPromosyon.setOnClickListener {

                                        if (dialogViewSpArama.swPromosyon.isChecked) {
                                            refBolge.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(true)
                                        } else {
                                            refBolge.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(false)

                                        }
                                    }

                                    dialogViewSpArama.swPromosyon.isChecked = musteriData.promosyon_verildimi.toString().toBoolean()



                                    builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog!!.dismiss()
                                        }

                                    })
                                    builder.setPositiveButton("Sipariş Ekle", object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {

                                            var sut3ltAdet = "0"
                                            if (dialogViewSpArama.et3lt.text.toString().isNotEmpty()) {
                                                sut3ltAdet = dialogViewSpArama.et3lt.text.toString()
                                            }
                                            var sut5ltAdet = "0"

                                            if (dialogViewSpArama.et5lt.text.toString().isNotEmpty()) {
                                                sut5ltAdet = dialogViewSpArama.et5lt.text.toString()
                                            }
                                            var yumurtaAdet = "0"
                                            if (dialogViewSpArama.etYumurta.text.toString().isNotEmpty()) {
                                                yumurtaAdet = dialogViewSpArama.etYumurta.text.toString()
                                            }

                                            var dokmeSutAdet = "0"
                                            if (dialogViewSpArama.etDokmeSut.text.toString().isNotEmpty()) {
                                                dokmeSutAdet = dialogViewSpArama.etDokmeSut.text.toString()
                                            }
                                            var sut3ltFiyat = dialogViewSpArama.et3ltFiyat.text.toString().toDouble()
                                            var sut5ltFiyat = dialogViewSpArama.et5ltFiyat.text.toString().toDouble()
                                            var yumurtaFiyat = dialogViewSpArama.etYumurtaFiyat.text.toString().toDouble()
                                            var dokmeSutFiyat = dialogViewSpArama.etDokmeSutFiyat.text.toString().toDouble()


                                            var siparisNotu = dialogViewSpArama.etSiparisNotu.text.toString()
                                            var siparisKey = FirebaseDatabase.getInstance().reference.child("Siparisler").push().key.toString()

                                            var toplamFiyat = (sut3ltAdet.toDouble() * sut3ltFiyat) + (sut5ltAdet.toDouble() * sut5ltFiyat) + (yumurtaAdet.toDouble() * yumurtaFiyat) + (dokmeSutAdet.toDouble() * dokmeSutFiyat)


                                            var siparisData = SiparisData(
                                                System.currentTimeMillis(),
                                                System.currentTimeMillis(),
                                                cal.timeInMillis,
                                                musteriData.musteri_adres,
                                                musteriData.musteri_apartman,
                                                musteriData.musteri_tel,
                                                musteriData.musteri_ad_soyad,
                                                musteriData.musteri_mah,
                                                siparisNotu,
                                                siparisKey,
                                                yumurtaAdet,
                                                yumurtaFiyat,
                                                sut3ltAdet,
                                                sut3ltFiyat,
                                                sut5ltAdet,
                                                sut5ltFiyat,
                                                dokmeSutAdet,
                                                dokmeSutFiyat,
                                                toplamFiyat,
                                                musteriData.musteri_zkonum,
                                                musteriData.promosyon_verildimi,
                                                musteriData.musteri_zlat,
                                                musteriData.musteri_zlong,
                                                Utils.kullaniciAdi
                                            )

                                            refBolge.child("Siparisler").child(musteriData.musteri_mah.toString()).child(siparisKey).setValue(siparisData)
                                            refBolge.child("Siparisler").child(musteriData.musteri_mah.toString()).child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                                            refBolge.child("Siparisler").child(musteriData.musteri_mah.toString()).child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)

                                            refBolge.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)

                                        }
                                    })

                                    builder.setTitle(musteriData.musteri_ad_soyad)
                                    builder.setIcon(R.drawable.cow)

                                    builder.setView(dialogViewSpArama)
                                    var dialog: Dialog = builder.create()
                                    dialog.show()

                                }


                            }

                        })*/
                val popup = PopupMenu(this, it)
                popup.inflate(R.menu.popup_menu_musteri)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.hizliSiparis -> {

                            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                            dialogViewSp = inflate(this, R.layout.dialog_siparis_ekle, null)


                            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.dismiss()
                                }

                            })
                            builder.setPositiveButton("Hızlı Sipariş Ekle", object : DialogInterface.OnClickListener {
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

                                    var sut3ltFiyat = dialogViewSp.et3ltFiyat.text.toString().toDouble()
                                    var sut5ltFiyat = dialogViewSp.et5ltFiyat.text.toString().toDouble()
                                    var yumurtaFiyat = dialogViewSp.etYumurtaFiyat.text.toString().toDouble()
                                    var dokmeSutFiyat = dialogViewSp.etDokmeSutFiyat.text.toString().toDouble()

                                    var siparisNotu = dialogViewSp.etSiparisNotu.text.toString()
                                    var siparisKey = refBolge.child("Teslim_siparisler").push().key.toString()

                                    var toplamFiyat = (sut3ltAdet.toDouble() * sut3ltFiyat) + (sut5ltAdet.toDouble() * sut5ltFiyat) + (yumurtaAdet.toDouble() * yumurtaFiyat) + (dokmeSutAdet.toDouble() * dokmeSutFiyat)
                                    refBolge.child("Musteriler").child(arananMusteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            var adres = p0.child("musteri_adres").value.toString()
                                            var telefon = p0.child("musteri_tel").value.toString()
                                            var konum = p0.child("musteri_zkonum").value.toString().toBoolean()
                                            var apartman = p0.child("musteri_apartman").value.toString()
                                            var mahalle = p0.child("musteri_mah").value.toString()

                                            var siparisData = SiparisData(
                                                System.currentTimeMillis(),
                                                System.currentTimeMillis(),
                                                System.currentTimeMillis(),
                                                adres,
                                                apartman,
                                                telefon,
                                                arananMusteriAdi,
                                                mahalle,
                                                siparisNotu,
                                                siparisKey,
                                                yumurtaAdet,
                                                yumurtaFiyat,
                                                sut3ltAdet,
                                                sut3ltFiyat,
                                                sut5ltAdet,
                                                sut5ltFiyat,
                                                dokmeSutAdet,
                                                dokmeSutFiyat,
                                                toplamFiyat,
                                                konum,
                                                true,
                                                null,
                                                null,
                                                Utils.kullaniciAdi
                                            )


                                            var referans = refBolge.child("Teslim_siparisler").child(siparisKey)
                                            referans.setValue(siparisData)
                                            refBolge.child("Musteriler").child(siparisData.siparis_veren.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)


                                        }


                                    })


                                }
                            })

                            builder.setTitle(arananMusteriAdi)
                            builder.setIcon(R.drawable.cow)

                            builder.setView(dialogViewSp)
                            var dialog: Dialog = builder.create()
                            dialog.show()

                        }
                        R.id.popDüzenle -> {

                            var musteriAdi = arananMusteriAdi
                            var builder: AlertDialog.Builder = AlertDialog.Builder(this)

                            var dialogView: View = inflate(this, R.layout.dialog_gidilen_musteri, null)
                            builder.setView(dialogView)


                            dialogMsDznle = builder.create()

                            dialogView.imgMaps.setOnClickListener {
                                var intent = Intent(this, AdresBulmaMapsCorluActivity::class.java)
                                intent.putExtra("musteri_konumu", Utils.secilenBolge)
                                intent.putExtra("musteriAdi", arananMusteriAdi)
                                this.startActivity(intent)
                            }
                            dialogView.swKonumKaydet.setOnClickListener {


                                if (dialogView.swKonumKaydet.isChecked) {
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_zkonum").setValue(true)
                                    //  holder.getLocation(musteriAdi)

                                } else {
                                    //  holder.locationManager.removeUpdates(holder.myLocationListener)
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_zkonum").setValue(false)
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_zlat").removeValue()
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_zlong").removeValue()

                                }

                            }

                            dialogView.imgCheck.setOnClickListener {

                                if (dialogView.etAdresGidilen.text.toString().isNotEmpty() && dialogView.etTelefonGidilen.text.toString().isNotEmpty()) {
                                    var mahalle = dialogView.tvMahalle.text.toString()
                                    var adres = dialogView.etAdresGidilen.text.toString()
                                    var telefon = dialogView.etTelefonGidilen.text.toString()
                                    var apartman = dialogView.etApartman.text.toString()


                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_mah").setValue(mahalle)
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_adres").setValue(adres)
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_apartman").setValue(apartman)
                                    refBolge.child("Musteriler").child(musteriAdi).child("musteri_tel").setValue(telefon).addOnCompleteListener {
///locationsu durduruyruz
                                        //    holder.locationManager.removeUpdates(holder.myLocationListener)
///
                                        dialogMsDznle.dismiss()

                                        Toast.makeText(this, "Müşteri Bilgileri Güncellendi", Toast.LENGTH_LONG).show()
                                    }.addOnFailureListener { Toast.makeText(this, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show() }
                                } else {
                                    Toast.makeText(this, "Bilgilerde boşluklar var", Toast.LENGTH_LONG).show()
                                }
                            }

                            dialogView.imgBack.setOnClickListener {
                                //   holder.locationManager.removeUpdates(holder.myLocationListener)
                                dialogMsDznle.dismiss()
                            }

                            dialogView.tvAdSoyad.text = arananMusteriAdi


                            refBolge.child("Musteriler").child(musteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    var adres = p0.child("musteri_adres").value.toString()
                                    var telefon = p0.child("musteri_tel").value.toString()
                                    var konum = p0.child("musteri_zkonum").value.toString().toBoolean()
                                    var apartman = p0.child("musteri_apartman").value.toString()
                                    var mahalle = p0.child("musteri_mah").value.toString()

                                    dialogView.swKonumKaydet.isChecked = konum
                                    dialogView.etAdresGidilen.setText(adres)
                                    dialogView.etTelefonGidilen.setText(telefon)
                                    dialogView.tvMahalle.setText(mahalle)
                                    dialogView.etApartman.setText(apartman)

                                    var list = ArrayList<SiparisData>()

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

                                        list.sortByDescending { it.siparis_teslim_zamani }
                                        dialogView.tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                                        dialogView.tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                                        dialogView.tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                                        dialogView.tvFiyatGenel.visibility = View.GONE
                                        //  dialogView.tvFiyatGenel.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"


                                        dialogView.rcSiparisGidilen.layoutManager = LinearLayoutManager(this@CustomerActivity, LinearLayoutManager.VERTICAL, false)
                                        //        dialogView.rcSiparisGidilen.layoutManager = StaggeredGridLayoutManager(myContext, LinearLayoutManager.VERTICAL, 2)
                                        val Adapter = MusteriSiparisleriAdapter(this@CustomerActivity, list, Utils.secilenBolge)
                                        dialogView.rcSiparisGidilen.adapter = Adapter
                                        dialogView.rcSiparisGidilen.setHasFixedSize(true)


                                    }
                                }


                            })
                            dialogMsDznle.setCancelable(false)
                            dialogMsDznle.show()

                        }
                        R.id.popSil -> {
                            refBolge.child("Hatalar/musterisilme").push().setValue(Utils.kullaniciAdi)

                            var alert = AlertDialog.Builder(this)
                                .setTitle("Müşteriyi Sil")
                                .setMessage("Emin Misin ?")
                                .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        refBolge.child("Musteriler").child(arananMusteriAdi).removeValue()
                                        refBolge.child("Musteriler").child(arananMusteriAdi).removeValue()

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


            } else {
                Toast.makeText(this, "Böyle Bir Müşteri Yok", Toast.LENGTH_SHORT).show()

            }


        }

    }

    private fun setupSpinner() {
        var siraList = ArrayList<String>()
        siraList.add("İsme A -> Z")
        siraList.add("İsme Z -> A")
        siraList.add("Zamana")
        siraList.add("Zamana ters")

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, siraList)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var secilenMarka = siraList[position]
                if (secilenMarka == "İsme A -> Z") {
                    musteriList.sortBy { it.musteri_ad_soyad }
                    setupRecyclerViewMusteriler()
                } else if (secilenMarka == "İsme Z -> A") {
                    musteriList.sortByDescending { it.musteri_ad_soyad }
                    setupRecyclerViewMusteriler()

                } else if (secilenMarka == "Zamana") {
                    musteriList.sortByDescending { it.siparis_son_zaman }
                    setupRecyclerViewMusteriler()
                } else if (secilenMarka == "Zamana ters") {
                    musteriList.sortBy { it.siparis_son_zaman }
                    setupRecyclerViewMusteriler()
                }
            }
        }
    }

    private fun setupRecyclerViewMusteriler() {
        rcMusteri.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcMusteri.adapter = MusteriAdapter(this, musteriList, Utils.kullaniciAdi, Utils.secilenBolge)
        rcMusteri.setHasFixedSize(false)
    }

    private fun setupNavigationView() {

        BottomNavigationViewHelperYeni.setupBottomNavigationView(bottomNavYeni)
        BottomNavigationViewHelperYeni.setupNavigation(this, bottomNavYeni) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavYeni.menu
        var menuItem = menu.getItem(3)
        menuItem.setChecked(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

}