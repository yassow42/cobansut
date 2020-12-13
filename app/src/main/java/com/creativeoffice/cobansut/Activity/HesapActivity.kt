package com.creativeoffice.cobansut.Activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.HesapAdapter
import com.creativeoffice.cobansut.Datalar.AracStokEkleData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.Datalar.StokData
import com.creativeoffice.cobansut.Datalar.Users
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_hesap.*
import kotlinx.android.synthetic.main.dialog_recyclerview.view.*
import kotlinx.android.synthetic.main.dialog_stok_ekle.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HesapActivity : AppCompatActivity() {
    var ACTIVITY_NO = 3
    var ref = FirebaseDatabase.getInstance().reference
    var userID: String? = null
    lateinit var mAuth: FirebaseAuth

    var ileriZaman: Long? = null
    var geriZaman: Long? = null

    var stok3lt: Int = 0
    var stok5lt: Int = 0
    var stokDokmeSut: Int = 0
    var stokYumurta: Int = 0

    var teslimList = ArrayList<SiparisData>()
    var userList = ArrayList<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap)
        setupNavigationView()
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid


        if (userID != null) {
            ref.child("users").child(userID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var yetki2 = p0.child("yetki2").value.toString()
                    if (yetki2 != "Patron") {
                        startActivity(Intent(this@HesapActivity, BolgeSecimActivity::class.java))
                        finish()
                    }
                    ref.child("Zaman").addListenerForSingleValueEvent(zamanAl)


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


    }

    private fun veriler() {
        ref.child("users").keepSynced(true)
        ref.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                userList.clear()
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenData = ds.getValue(Users::class.java)!!

                        if (gelenData.user_name.toString() == "ibrahim39") userList.add(gelenData)
                        if (gelenData.user_name.toString() == "Corlumurat") userList.add(gelenData)

                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        ref.child("Teslim_siparisler").addListenerForSingleValueEvent(teslimEdilenler)
        ref.child("Cerkez").child("Teslim_siparisler").addListenerForSingleValueEvent(teslimEdilenler)
        ref.child("Corlu").child("Teslim_siparisler").addListenerForSingleValueEvent(teslimEdilenler)
/*
        var ibrahim39Satis3ltSayisi = 0
        var ibrahim39Satis5ltSayisi = 0
        var ibrahim39SatisDokmeSayisi = 0
        var ibrahim39SatisYumurtaSayisi = 0
        var ibrahim39SatisFiyat = 0.0

        var CorluMuratSatis3ltSayisi = 0
        var CorluMuratSatis5ltSayisi = 0
        var CorluMuratSatisDokmeSayisi = 0
        var CorluMuratSatisYumurtaSayisi = 0
        var CorluMuratSatisFiyat = 0.0

        var sut3ltSayisi = 0
        var sut5ltSayisi = 0
        var sutDokmeSayisi = 0
        var yumurtaSayisi = 0

        var sut3ltSayisiOnceki = 0
        var sut5ltSayisiOnceki = 0
        var sutDokmeSayisiOnceki = 0
        var yumurtaSayisiOnceki = 0
        if (ileriZaman != null) {
            ref.child("Corlu").child("Teslim_siparisler").keepSynced(true)
            ref.child("Cerkez").child("Teslim_siparisler").keepSynced(true)
            ref.child("Teslim_siparisler").keepSynced(true)
            ref.child("Corlu").child("Teslim_siparisler").orderByChild("siparis_teslim_tarihi").addListenerForSingleValueEvent(teslimEdilenler)
            ref.child("Cerkez").child("Teslim_siparisler").orderByChild("siparis_teslim_tarihi").addListenerForSingleValueEvent(teslimEdilenler)
            ref.child("Teslim_siparisler").orderByChild("siparis_teslim_tarihi").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {

                        for (ds in p0.children) {
                            var gelenData = ds.getValue(SiparisData::class.java)!!
                            if (gelenData.siparis_teslim_tarihi != null) {
                                if (ileriZaman!! - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < ileriZaman!!) {
                                    teslimList.add(gelenData)
                                }
                            }
                        }

                        for (item in teslimList) {
                            if (item.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {

                                if (item.siparisi_giren == "ibrahim39") {
                                    ibrahim39Satis3ltSayisi = ibrahim39Satis3ltSayisi + item.sut3lt.toString().toInt()
                                    ibrahim39Satis5ltSayisi = ibrahim39Satis5ltSayisi + item.sut5lt.toString().toInt()
                                    ibrahim39SatisDokmeSayisi = ibrahim39SatisDokmeSayisi + item.dokme_sut.toString().toInt()
                                    ibrahim39SatisYumurtaSayisi = ibrahim39SatisYumurtaSayisi + item.yumurta.toString().toInt()
                                    ibrahim39SatisFiyat = ibrahim39SatisFiyat + item.toplam_fiyat.toString().toDouble()

                                }

                                if (item.siparisi_giren == "Corlumurat") {
                                    CorluMuratSatis3ltSayisi = CorluMuratSatis3ltSayisi + item.sut3lt.toString().toInt()
                                    CorluMuratSatis5ltSayisi = CorluMuratSatis5ltSayisi + item.sut5lt.toString().toInt()
                                    CorluMuratSatisDokmeSayisi = CorluMuratSatisDokmeSayisi + item.dokme_sut.toString().toInt()
                                    CorluMuratSatisYumurtaSayisi = CorluMuratSatisYumurtaSayisi + item.yumurta.toString().toInt()
                                    CorluMuratSatisFiyat = CorluMuratSatisFiyat + item.toplam_fiyat.toString().toDouble()
                                }
                            }

                        }
                        ref.child("users").child("IwSSK7TVUFRqnex9weLnLhoTz3n2").child("Satis").child("3lt").setValue(ibrahim39Satis3ltSayisi)
                        ref.child("users").child("IwSSK7TVUFRqnex9weLnLhoTz3n2").child("Satis").child("5lt").setValue(ibrahim39Satis5ltSayisi)
                        ref.child("users").child("IwSSK7TVUFRqnex9weLnLhoTz3n2").child("Satis").child("dokme_sut").setValue(ibrahim39SatisDokmeSayisi)
                        ref.child("users").child("IwSSK7TVUFRqnex9weLnLhoTz3n2").child("Satis").child("yumurta").setValue(ibrahim39SatisYumurtaSayisi)
                        ref.child("users").child("IwSSK7TVUFRqnex9weLnLhoTz3n2").child("Satis").child("toplam_fiyat").setValue(ibrahim39SatisFiyat)

                        ref.child("users").child("8eQkYvU5rpNrrkVIiJCy39aGAdI3").child("Satis").child("3lt").setValue(CorluMuratSatis3ltSayisi)
                        ref.child("users").child("8eQkYvU5rpNrrkVIiJCy39aGAdI3").child("Satis").child("5lt").setValue(CorluMuratSatis5ltSayisi)
                        ref.child("users").child("8eQkYvU5rpNrrkVIiJCy39aGAdI3").child("Satis").child("dokme_sut").setValue(CorluMuratSatisDokmeSayisi)
                        ref.child("users").child("8eQkYvU5rpNrrkVIiJCy39aGAdI3").child("Satis").child("yumurta").setValue(CorluMuratSatisYumurtaSayisi)
                        ref.child("users").child("8eQkYvU5rpNrrkVIiJCy39aGAdI3").child("Satis").child("toplam_fiyat").setValue(CorluMuratSatisFiyat)

                        ref.child("users").child("U3EszZNMuuUQCQ0NrSjRu44v8PP2").child("Satis").child("3lt").setValue(0)
                        ref.child("users").child("U3EszZNMuuUQCQ0NrSjRu44v8PP2").child("Satis").child("5lt").setValue(0)
                        ref.child("users").child("U3EszZNMuuUQCQ0NrSjRu44v8PP2").child("Satis").child("dokme_sut").setValue(0)
                        ref.child("users").child("U3EszZNMuuUQCQ0NrSjRu44v8PP2").child("Satis").child("yumurta").setValue(0)
                        ref.child("users").child("U3EszZNMuuUQCQ0NrSjRu44v8PP2").child("Satis").child("toplam_fiyat").setValue(0.0)

                        ref.child("users").child("mxGcSHxcQncvNcvupGMC1Qg8JUa2").child("Satis").child("3lt").setValue(0)
                        ref.child("users").child("mxGcSHxcQncvNcvupGMC1Qg8JUa2").child("Satis").child("5lt").setValue(0)
                        ref.child("users").child("mxGcSHxcQncvNcvupGMC1Qg8JUa2").child("Satis").child("dokme_sut").setValue(0)
                        ref.child("users").child("mxGcSHxcQncvNcvupGMC1Qg8JUa2").child("Satis").child("yumurta").setValue(0)
                        ref.child("users").child("mxGcSHxcQncvNcvupGMC1Qg8JUa2").child("Satis").child("toplam_fiyat").setValue(0.0)

                        ref.child("users").child("sV2MFbOueZgU1gqNrRuB4Ab2GPW2").child("Satis").child("3lt").setValue(0)
                        ref.child("users").child("sV2MFbOueZgU1gqNrRuB4Ab2GPW2").child("Satis").child("5lt").setValue(0)
                        ref.child("users").child("sV2MFbOueZgU1gqNrRuB4Ab2GPW2").child("Satis").child("dokme_sut").setValue(0)
                        ref.child("users").child("sV2MFbOueZgU1gqNrRuB4Ab2GPW2").child("Satis").child("yumurta").setValue(0)
                        ref.child("users").child("sV2MFbOueZgU1gqNrRuB4Ab2GPW2").child("Satis").child("toplam_fiyat").setValue(0.0)

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


            ref.child("Depo_Log").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.hasChildren()) {
                        for (ds in p0.children) {
                            var data = ds.getValue(StokData::class.java)!!
                            if (geriZaman!! < data.stok_guncelleme_zamani!! && data.stok_guncelleme_zamani!! < ileriZaman!!) {
                                sut3ltSayisi = sut3ltSayisi + data.sut3lt!!
                                sut5ltSayisi = sut5ltSayisi + data.sut5lt!!
                                sutDokmeSayisi = sutDokmeSayisi + data.dokme_sut!!
                                yumurtaSayisi = yumurtaSayisi + data.yumurta!!
                            }
                            if (geriZaman!! - 86400000 < data.stok_guncelleme_zamani!! && data.stok_guncelleme_zamani!! < ileriZaman!! - 86400000) {
                                sut3ltSayisiOnceki = sut3ltSayisiOnceki + data.sut3lt!!
                                sut5ltSayisiOnceki = sut5ltSayisiOnceki + data.sut5lt!!
                                sutDokmeSayisiOnceki = sutDokmeSayisiOnceki + data.dokme_sut!!
                                yumurtaSayisiOnceki = yumurtaSayisiOnceki + data.yumurta!!
                            }

                        }
                    }
                    ref.child("Depo_Arac_Stok_Ekle").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p1: DataSnapshot) {
                            if (p1.hasChildren()) {
                                for (ds in p1.children) {
                                    var data = ds.getValue(AracStokEkleData::class.java)!!
                                    if (geriZaman!! < data.araca_stok_ekleme_zamani!! && data.araca_stok_ekleme_zamani!! < ileriZaman!!) {
                                        sut3ltSayisi = sut3ltSayisi - data.sut3lt!!
                                        sut5ltSayisi = sut5ltSayisi - data.sut5lt!!
                                        sutDokmeSayisi = sutDokmeSayisi - data.dokme_sut!!
                                        yumurtaSayisi = yumurtaSayisi - data.yumurta!!
                                    }
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                    tvStokSayisi.setText(
                        "Dün: 3lt: $sut3ltSayisiOnceki  5lt: $sut5ltSayisiOnceki Dökme: $sutDokmeSayisiOnceki Yum: $yumurtaSayisiOnceki\n"
                                + "Bugün: 3lt: $sut3ltSayisi  5lt: $sut5ltSayisi Dökme: $sutDokmeSayisi Yum: $yumurtaSayisi "
                    )
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

*/
    }


    fun tvStokEklePlus(view: View) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        var dialogView = View.inflate(this, R.layout.dialog_stok_ekle, null)

        dialogView.llAlinanPara.visibility = View.GONE
        dialogView.et3lt.setText("0")
        dialogView.et5lt.setText("0")
        dialogView.etDokmeSut.setText("0")
        dialogView.etYumurta.setText("0")
        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
            }

        })
        builder.setPositiveButton("Stok Ekle", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (dialogView.et3lt.text.isNotEmpty() && dialogView.et5lt.text.isNotEmpty() && dialogView.etDokmeSut.text.isNotEmpty() && dialogView.etYumurta.text.isNotEmpty()) {
                    var et3lt = dialogView.et3lt.text.toString().toInt()
                    var et5lt = dialogView.et5lt.text.toString().toInt()
                    var etDokmeSut = dialogView.etDokmeSut.text.toString().toInt()
                    var etYumurta = dialogView.etYumurta.text.toString().toInt()


                    var guncel3lt = et3lt + stok3lt
                    var guncel5lt = et5lt + stok5lt
                    var guncelDokmeSut = etDokmeSut + stokDokmeSut
                    var guncelYumurta = etYumurta + stokYumurta



                    ref.child("Depo").child("sut3lt").setValue(guncel3lt)
                    ref.child("Depo").child("sut5lt").setValue(guncel5lt)
                    ref.child("Depo").child("dokme_sut").setValue(guncelDokmeSut)
                    ref.child("Depo").child("yumurta").setValue(guncelYumurta).addOnSuccessListener {
                        val key = ref.child("Depo_Log").push().key.toString()
                        ref.child("Depo_Log").child(key).child("sut3lt").setValue(et3lt)
                        ref.child("Depo_Log").child(key).child("sut5lt").setValue(et5lt)
                        ref.child("Depo_Log").child(key).child("dokme_sut").setValue(etDokmeSut)
                        ref.child("Depo_Log").child(key).child("yumurta").setValue(etYumurta)
                        ref.child("Depo_Log").child(key).child("key").setValue(key)
                        ref.child("Depo_Log").child(key).child("stok_guncelleme_zamani").setValue(ServerValue.TIMESTAMP)
                        Snackbar.make(view, "Stok Güncellendi", 2000).show()
                    }
                } else Toast.makeText(this@HesapActivity, "Boşluklar var...!!! Doldur.!!", Toast.LENGTH_LONG)


            }
        })
        builder.setTitle("Stok Ekle")
        builder.setIcon(R.drawable.cow)

        builder.setView(dialogView)
        var dialog: Dialog = builder.create()
        dialog.show()

    }

    fun stokSayisi(view: View) {

        var builder: AlertDialog.Builder = AlertDialog.Builder(this@HesapActivity)
        var dialogView = View.inflate(this@HesapActivity, R.layout.dialog_recyclerview, null)
        var sut3ltSayisi = 0
        var sut5ltSayisi = 0
        var sutDokmeSayisi = 0
        var yumurtaSayisi = 0

        ref.child("Depo_Log").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    var stokList = ArrayList<String>()
                    for (ds in p0.children) {
                        var data = ds.getValue(StokData::class.java)!!
                        if (geriZaman!! < data.stok_guncelleme_zamani!! && data.stok_guncelleme_zamani!! < ileriZaman!!) {
                            sut3ltSayisi = sut3ltSayisi + data.sut3lt!!
                            sut5ltSayisi = sut5ltSayisi + data.sut5lt!!
                            sutDokmeSayisi = sutDokmeSayisi + data.dokme_sut!!
                            yumurtaSayisi = yumurtaSayisi + data.yumurta!!
                        }
                        var sut3lt = data.sut3lt
                        var sut5lt = data.sut5lt
                        var dokmeSut = data.dokme_sut
                        var yumurta = data.yumurta
                        var long = data.stok_guncelleme_zamani
                        var zaman = formatDate(long).toString()

                        var tumListString = "3lt: $sut3lt \n5lt: $sut5lt \nDökme Süt: $dokmeSut \nYumurta: $yumurta \n $zaman"
                        stokList.add(tumListString)
                    }


                    val adapter = ArrayAdapter<String>(this@HesapActivity, android.R.layout.simple_list_item_1, stokList)
                    dialogView.dialogRC.adapter = adapter


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        builder.setNegativeButton("Çık", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
            }

        })

        builder.setView(dialogView)
        var dialog: Dialog = builder.create()
        dialog.show()


    }

    var teslimEdilenler = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            for (ds in p0.children) {
                var gelenData = ds.getValue(SiparisData::class.java)!!
                if (gelenData.siparis_teslim_tarihi != null) {
                    if (ileriZaman!! - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < ileriZaman!!) {
                        teslimList.add(gelenData)
                        Log.e("HesapActTeslimListSayi",teslimList.size.toString())
                    }
                }

            }
            rcHesap.layoutManager = LinearLayoutManager(this@HesapActivity, LinearLayoutManager.VERTICAL, false)
            rcHesap.adapter = HesapAdapter(this@HesapActivity, userList,teslimList, ileriZaman, geriZaman)

        }

        override fun onCancelled(error: DatabaseError) {

        }

    }

    var zamanAl = object : ValueEventListener {


        override fun onDataChange(p0: DataSnapshot) {
            ileriZaman = p0.child("gece3").value.toString().toLong()
            geriZaman = p0.child("gerigece3").value.toString().toLong()
            if (ileriZaman != null) veriler()
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }


    fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("HH:mm - d MMM", Locale("tr"))
        return sdf.format(date)
    }

    override fun onBackPressed() {
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)

        super.onBackPressed()
    }
    private fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}