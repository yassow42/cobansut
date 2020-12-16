package com.creativeoffice.cobansut.EnYeni

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.TeslimEdilenlerAdapter
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.genel.BolgeSecimActivity

import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperYeni
import com.creativeoffice.cobansut.utils.Utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_siparis_teslim.*
import kotlinx.android.synthetic.main.activity_siparis_teslim.imgEskiVerileriGetir
import kotlinx.android.synthetic.main.activity_siparis_teslim.imgOptions
import kotlinx.android.synthetic.main.activity_siparis_teslim.imgTarih
import kotlinx.android.synthetic.main.activity_siparis_teslim.rcTeslimEdilenler
import kotlinx.android.synthetic.main.activity_siparis_teslim.tv3ltTeslim
import kotlinx.android.synthetic.main.activity_siparis_teslim.tv5ltTeslim
import kotlinx.android.synthetic.main.activity_siparis_teslim.tvDokumSutTeslim
import kotlinx.android.synthetic.main.activity_siparis_teslim.tvFiyatGenelTeslim
import kotlinx.android.synthetic.main.activity_siparis_teslim.tvYumurtaTeslim
import kotlinx.android.synthetic.main.activity_siparis_teslim.tvZamana
import kotlinx.android.synthetic.main.activity_siparis_teslim.tvZamandan
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SiparisTeslimActivity : AppCompatActivity() {
    var refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)

    var suankiTeslimList = ArrayList<SiparisData>()
    var butunTeslimList = ArrayList<SiparisData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siparis_teslim)
        setupNavigationView()
        setupBtn()

        tvBaslik.text = "${Utils.secilenBolge} Teslim Edilenleri"
        refBolge.child("Teslim_siparisler").keepSynced(true)
        refBolge.child("Teslim_siparisler").addListenerForSingleValueEvent(teslimEdilenler)
    }

    var teslimEdilenler = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(data: DataSnapshot) {
            butunTeslimList.clear()
            suankiTeslimList.clear()

            if (data.hasChildren()) {
                var sut3ltSayisi = 0
                var sut5ltSayisi = 0
                var yumurtaSayisi = 0
                var dokumSutSayisi = 0
                var toplamFiyatlar = 0.0

                for (ds in data.children) {
                    var gelenData = ds.getValue(SiparisData::class.java)!!
                    butunTeslimList.add(gelenData)

                    if (gelenData.siparis_teslim_zamani.toString() != "null") {

                        if (Utils.zaman - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < Utils.zaman
                        ) {
                            suankiTeslimList.add(gelenData)
                            sut3ltSayisi += gelenData.sut3lt!!.toInt()
                            sut5ltSayisi += gelenData.sut5lt!!.toInt()
                            yumurtaSayisi += gelenData.yumurta!!.toInt()
                            dokumSutSayisi += gelenData.dokme_sut!!.toInt()
                            toplamFiyatlar += gelenData.toplam_fiyat!!.toDouble()

                        }

/*
                            refBolge.child("Musteriler/${gelenData.siparis_veren.toString()}/siparisleri").child(gelenData.siparis_key.toString())
                                .child("dokum_sut").setValue("0")
                            refBolge.child("Musteriler/${gelenData.siparis_veren.toString()}/siparisleri").child(gelenData.siparis_key.toString())
                                .child("dokum_sut_fiyat").setValue(3.5)

                        refBolge.child("Musteriler/${gelenData.siparis_veren.toString()}/siparisleri").child(gelenData.siparis_key.toString())
                            .setValue(gelenData)
*/
                      /*  if (gelenData.siparisi_giren.toString()=="null"){
                            Log.e("sad",gelenData.siparis_veren.toString())
                            refBolge.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("siparisi_giren").setValue("Corlumurat")
                        }*/

                        /*   if (gelenData.siparis_teslim_zamani!! < gece3GelenZaman - 604800000) {
                               Log.e("sad", gelenData.siparis_teslim_zamani.toString() + "  " + (gece3GelenZaman - 2592000000))
                               ref.child("Eski_veriler").child("Burgaz").child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(gelenData)
                               ref.child("Burgaz").child("Teslim_siparisler").child(gelenData.siparis_key.toString()).removeValue()
                               ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).removeValue()
                           }*/
                    }
                }


                suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                tvDokumSutTeslim.text = "Dökme: " + dokumSutSayisi.toString()


                tvFiyatGenelTeslim.setText(toplamFiyatlar.toDouble().toString() + " TL")

                setupRecyclerView()
                veriTasima(Utils.zaman)
            }
        }

    }


    private fun setupBtn() {

        imgEskiVerileriGetir.setOnClickListener {
            suankiTeslimList.clear()
            FirebaseDatabase.getInstance().reference.child("Eski_veriler").child(Utils.secilenBolge).child("Teslim_siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    for (ds in p0.children) {
                        var gelenData = ds.getValue(SiparisData::class.java)!!
                        butunTeslimList.add(gelenData)
                    }
                    Log.e("eski veri getir", butunTeslimList.size.toString())
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        }


        tvZamandan.text = SimpleDateFormat("HH:mm  dd.MM.yyyy").format(System.currentTimeMillis() - (24 * 60 * 60 * 1000))
        tvZamana.text = SimpleDateFormat("HH:mm  dd.MM.yyyy").format(System.currentTimeMillis())

        var calZamandan = Calendar.getInstance()
        val dateSetListenerZamandan = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calZamandan.set(Calendar.YEAR, year)
            calZamandan.set(Calendar.MONTH, monthOfYear)
            calZamandan.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
            tvZamandan.text = sdf.format(calZamandan.time)
        }

             val timeSetListenerZamandan = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                 calZamandan.set(Calendar.HOUR_OF_DAY, hourOfDay)
                 calZamandan.set(Calendar.MINUTE, minute)
             }

        var calZamana = Calendar.getInstance()
        val dateSetListenerZamana = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calZamana.set(Calendar.YEAR, year)
            calZamana.set(Calendar.MONTH, monthOfYear)
            calZamana.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
            tvZamana.text = sdf.format(calZamana.time)
        }

         val timeSetListenerZamana = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
             calZamana.set(Calendar.HOUR_OF_DAY, hourOfDay)
             calZamana.set(Calendar.MINUTE, minute)
         }



        tvZamandan.setOnClickListener {
            DatePickerDialog(this, dateSetListenerZamandan, calZamandan.get(Calendar.YEAR), calZamandan.get(Calendar.MONTH), calZamandan.get(Calendar.DAY_OF_MONTH)).show()
             TimePickerDialog(this, timeSetListenerZamandan, calZamandan.get(Calendar.HOUR_OF_DAY), calZamandan.get(Calendar.MINUTE), true).show()

        }
        tvZamana.setOnClickListener {
            DatePickerDialog(this, dateSetListenerZamana, calZamana.get(Calendar.YEAR), calZamana.get(Calendar.MONTH), calZamana.get(Calendar.DAY_OF_MONTH)).show()
            TimePickerDialog(this, timeSetListenerZamana, calZamana.get(Calendar.HOUR_OF_DAY), calZamana.get(Calendar.MINUTE), true).show()
        }


        imgTarih.setOnClickListener {
            suankiTeslimList.clear()

            var sut3ltSayisi = 0
            var sut5ltSayisi = 0
            var yumurtaSayisi = 0
            var toplamFiyatlar = 0.0

            for (ds in butunTeslimList) {

                if (ds.siparis_teslim_zamani.toString() != "null") {
                    if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                        suankiTeslimList.add(ds)
                        sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                        sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                        yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                        toplamFiyatlar = ds.toplam_fiyat!!.toDouble() + toplamFiyatlar

                    }
                }

                suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()

                tvFiyatGenelTeslim.text = toplamFiyatlar.toString() + " TL"

                setupRecyclerView()

            }
        }

        imgOptions.setOnClickListener {

            val popup = PopupMenu(this, imgOptions)
            popup.inflate(R.menu.popup_menu_teslim)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {

                var sut3ltSayisi = 0
                var sut3ltFiyat = 0.0

                var sut5ltSayisi = 0
                var sut5ltFiyat = 0.0

                var sutDokmeSayisi = 0
                var sutDokmeFiyat = 0.0

                var yumurtaSayisi = 0
                var yumurtaFiyat = 0.0

                var toplamFiyat = 0.0
                when (it.itemId) {

                    R.id.MerkezCoban -> {
                        suankiTeslimList.clear()

                        for (ds in butunTeslimList) {
                            if (ds.siparisi_giren == "MerkezCoban") {
                                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                                    suankiTeslimList.add(ds)

                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut3ltFiyat = ds.sut3lt_fiyat!!.toInt() + sut3ltFiyat


                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    sut5ltFiyat = ds.sut5lt_fiyat!!.toInt() + sut5ltFiyat

                                    sutDokmeSayisi = ds.dokme_sut!!.toInt() + sutDokmeSayisi
                                    sutDokmeFiyat = ds.dokme_sut_fiyat!!.toInt() + sutDokmeFiyat

                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                    yumurtaFiyat = ds.yumurta_fiyat!!.toInt() + yumurtaFiyat

                                    toplamFiyat = ds.toplam_fiyat!! + toplamFiyat

                                }
                            }
                        }

                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvDokumSutTeslim.text = "Dokum: " + sutDokmeSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()



                        tvFiyatGenelTeslim.text = (toplamFiyat).toString() + " tl"
                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }

                    R.id.ibrahim -> {
                        suankiTeslimList.clear()


                        for (ds in butunTeslimList) {
                            if (ds.siparisi_giren == "ibrahim39") {
                                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                                    suankiTeslimList.add(ds)

                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut3ltFiyat = ds.sut3lt_fiyat!!.toInt() + sut3ltFiyat


                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    sut5ltFiyat = ds.sut5lt_fiyat!!.toInt() + sut5ltFiyat


                                    sutDokmeSayisi = ds.dokme_sut!!.toInt() + sutDokmeSayisi
                                    sutDokmeFiyat = ds.dokme_sut_fiyat!!.toInt() + sutDokmeFiyat


                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                    yumurtaFiyat = ds.yumurta_fiyat!!.toInt() + yumurtaFiyat

                                    toplamFiyat = ds.toplam_fiyat!! + toplamFiyat

                                }

                            }
                        }
                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvDokumSutTeslim.text = "Dokum: " + sutDokmeSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()



                        tvFiyatGenelTeslim.text = (toplamFiyat).toString() + " tl"
                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }

                    R.id.corlumurat -> {
                        suankiTeslimList.clear()

                        for (ds in butunTeslimList) {
                            if (ds.siparisi_giren == "Corlumurat") {
                                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                                    suankiTeslimList.add(ds)

                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut3ltFiyat = ds.sut3lt_fiyat!!.toInt() + sut3ltFiyat

                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    sut5ltFiyat = ds.sut5lt_fiyat!!.toInt() + sut5ltFiyat

                                    sutDokmeSayisi = ds.dokme_sut!!.toInt() + sutDokmeSayisi
                                    sutDokmeFiyat = ds.dokme_sut_fiyat!!.toInt() + sutDokmeFiyat

                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                    yumurtaFiyat = ds.yumurta_fiyat!!.toInt() + yumurtaFiyat

                                    toplamFiyat = ds.toplam_fiyat!! + toplamFiyat

                                }

                            }
                        }
                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvDokumSutTeslim.text = "Dokum: " + sutDokmeSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()

                        tvFiyatGenelTeslim.text = (toplamFiyat).toString() + " tl"

                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }


                    R.id.bingolbali -> {
                        suankiTeslimList.clear()

                        for (ds in butunTeslimList) {
                            if (ds.siparisi_giren == "Bingolbali") {
                                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                                    suankiTeslimList.add(ds)

                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut3ltFiyat = ds.sut3lt_fiyat!!.toInt() + sut3ltFiyat


                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    sut5ltFiyat = ds.sut5lt_fiyat!!.toInt() + sut5ltFiyat

                                    sutDokmeSayisi = ds.dokme_sut!!.toInt() + sutDokmeSayisi
                                    sutDokmeFiyat = ds.dokme_sut_fiyat!!.toInt() + sutDokmeFiyat

                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                    yumurtaFiyat = ds.yumurta_fiyat!!.toInt() + yumurtaFiyat

                                    toplamFiyat = ds.toplam_fiyat!! + toplamFiyat

                                }
                            }
                        }

                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvDokumSutTeslim.text = "Dokum: " + sutDokmeSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()



                        tvFiyatGenelTeslim.text = (toplamFiyat).toString() + " tl"
                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }

                }
                return@OnMenuItemClickListener true
            })
            popup.show()

        }


    }

    private fun veriTasima(gece3GelenZaman: Long) {

        FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").keepSynced(true)
        FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for (ds in p0.children) {
                    var gelenData = ds.getValue(SiparisData::class.java)!!
                    if (Utils.secilenBolge == "Burgaz") {
                        print("Burgazdaki veriler tasınıyor.")
                        if (gelenData.siparis_teslim_zamani.toString() != "null") {
                            if (gelenData.siparis_teslim_zamani!! > gece3GelenZaman - 5184000000) {
                                FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge).child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(gelenData)
                            } else {
                                FirebaseDatabase.getInstance().reference.child("Eski_veriler").child(Utils.secilenBolge).child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(gelenData)
                            }
                        } else {
                            Log.e("silinneler", gelenData.siparis_key.toString())
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setupRecyclerView() {
        rcTeslimEdilenler.layoutManager = LinearLayoutManager(this@SiparisTeslimActivity, LinearLayoutManager.VERTICAL, false)
        val Adapter = TeslimEdilenlerAdapter(this@SiparisTeslimActivity, suankiTeslimList, Utils.secilenBolge)
        rcTeslimEdilenler.adapter = Adapter
        rcTeslimEdilenler.setHasFixedSize(true)
    }

    private fun setupNavigationView() {
        BottomNavigationViewHelperYeni.setupBottomNavigationView(bottomNavYeni)
        BottomNavigationViewHelperYeni.setupNavigation(this, bottomNavYeni) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavYeni.menu
        var menuItem = menu.getItem(2)
        menuItem.setChecked(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

}