package com.creativeoffice.cobansut.Activity

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.TeslimEdilenlerAdapter
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_teslim.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TeslimActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    lateinit var suankiTeslimList: ArrayList<SiparisData>
    lateinit var butunTeslimList: ArrayList<SiparisData>
    var ref = FirebaseDatabase.getInstance().reference
    lateinit var progressDialog: ProgressDialog
    val hndler = Handler()

    var yetki2 = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teslim)
        setupNavigationView()
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setupBtn()


        suankiTeslimList = ArrayList()
        butunTeslimList = ArrayList()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Lütfen bekleyin...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        ref.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid.toString()).addListenerForSingleValueEvent(userYetki)

        hndler.postDelayed(Runnable { progressDialog.dismiss() }, 10000)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    private fun setupVeri() {
        ref.child("Teslim_siparisler").keepSynced(true)
        ref.child("Burgaz").child("Teslim_siparisler").keepSynced(true)
        ref.child("Burgaz").child("Teslim_siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                if (data.hasChildren()) {
                    var sut3ltSayisi = 0
                    var sut5ltSayisi = 0
                    var yumurtaSayisi = 0
                    var dokumSutSayisi = 0
                    var toplamFiyatlar = 0.0

                    ref.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            var gece3GelenZaman = p0.child("gece3").value.toString().toLong()
                            var suankiZaman = System.currentTimeMillis()

                            veriTasima(gece3GelenZaman)

                            if (gece3GelenZaman < suankiZaman) {
                                var guncelGece3 = gece3GelenZaman + 86400000
                                FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3)
                            }

                            for (ds in data.children) {
                                var gelenData = ds.getValue(SiparisData::class.java)!!
                                butunTeslimList.add(gelenData)

                                if (gelenData.siparis_teslim_zamani.toString() != "null") {

                                    if (gece3GelenZaman - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < gece3GelenZaman) {
                                        suankiTeslimList.add(gelenData)
                                        sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                        sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                        yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi
                                        dokumSutSayisi = gelenData.dokme_sut!!.toInt() + dokumSutSayisi
                                        toplamFiyatlar = gelenData.toplam_fiyat!!.toDouble() + toplamFiyatlar

                                    }

                                    if (gelenData.siparis_teslim_zamani!! < gece3GelenZaman - 604800000) {
                                        Log.e("sad", gelenData.siparis_teslim_zamani.toString() + "  " + (gece3GelenZaman - 2592000000))
                                        ref.child("Eski_veriler").child("Burgaz").child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(gelenData)
                                        ref.child("Burgaz").child("Teslim_siparisler").child(gelenData.siparis_key.toString()).removeValue()
                                        ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).removeValue()
                                    }
                                }
                            }


                            suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                            tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                            tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                            tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                            tvDokumSutTeslim.text = "Dökme: " + dokumSutSayisi.toString()


                            try {
                                tvFiyatGenelTeslim.setText(toplamFiyatlar.toDouble().toString() + " TL")
                                setupRecyclerView()
                            } catch (e: IOException) {
                                Log.e("teslim activity", "hatalar ${e.message.toString()}")

                            }
                            progressDialog.dismiss()
                        }
                    })

                } else {
                    progressDialog.setMessage("Veri Alınamıyor...")
                    hndler.postDelayed(Runnable { progressDialog.dismiss() }, 8000)

                }
            }
        })


    }

    private fun veriTasima(gece3GelenZaman: Long) {
        ref.child("Teslim_siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for (ds in p0.children) {
                    var gelenData = ds.getValue(SiparisData::class.java)!!

                    if (gelenData.siparis_teslim_zamani.toString() != "null") {
                        if (gelenData.siparis_teslim_zamani!! > gece3GelenZaman - 5184000000) {
                            ref.child("Burgaz").child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(gelenData)
                        } else {
                            ref.child("Eski_veriler").child("Burgaz").child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(gelenData)
                        }
                    } else {
                        Log.e("silinneler", gelenData.siparis_key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setupBtn() {

        imgEskiVerileriGetir.setOnClickListener {
            suankiTeslimList.clear()
            ref.child("Eski_veriler").child("Burgaz").child("Teslim_siparisler").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {

                    for (ds in p0.children){
                        var gelenData = ds.getValue(SiparisData::class.java)!!
                        butunTeslimList.add(gelenData)
                    }
                    Log.e("eski veri getir",butunTeslimList.size.toString())
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

        /*     val timeSetListenerZamandan = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                 calZamandan.set(Calendar.HOUR_OF_DAY, hourOfDay)
                 calZamandan.set(Calendar.MINUTE, minute)
             }*/

        var calZamana = Calendar.getInstance()
        val dateSetListenerZamana = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calZamana.set(Calendar.YEAR, year)
            calZamana.set(Calendar.MONTH, monthOfYear)
            calZamana.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
            tvZamana.text = sdf.format(calZamana.time)
        }

        /* val timeSetListenerZamana = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
             calZamana.set(Calendar.HOUR_OF_DAY, hourOfDay)
             calZamana.set(Calendar.MINUTE, minute)
         }*/



        tvZamandan.setOnClickListener {
            DatePickerDialog(this, dateSetListenerZamandan, calZamandan.get(Calendar.YEAR), calZamandan.get(Calendar.MONTH), calZamandan.get(Calendar.DAY_OF_MONTH)).show()
            //  TimePickerDialog(this, timeSetListenerZamandan, calZamandan.get(Calendar.HOUR_OF_DAY), calZamandan.get(Calendar.MINUTE), true).show()

        }
        tvZamana.setOnClickListener {
            DatePickerDialog(this, dateSetListenerZamana, calZamana.get(Calendar.YEAR), calZamana.get(Calendar.MONTH), calZamana.get(Calendar.DAY_OF_MONTH)).show()
            // TimePickerDialog(this, timeSetListenerZamana, calZamana.get(Calendar.HOUR_OF_DAY), calZamana.get(Calendar.MINUTE), true).show()
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
                                    Log.e("saddd", toplamFiyat.toString())
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


    private fun setupRecyclerView() {
        rcTeslimEdilenler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = TeslimEdilenlerAdapter(this, suankiTeslimList, "Burgaz")
        rcTeslimEdilenler.adapter = Adapter
        rcTeslimEdilenler.setHasFixedSize(true)
    }

    fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


    var userYetki = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.value != null) {

                yetki2 = p0.child("yetki2").value.toString()
                if (yetki2 != "Patron") {
                    setupVeri()
                    imgTarih.visibility = View.VISIBLE
                } else {
                    setupVeri()
                }

            }


        }

        override fun onCancelled(error: DatabaseError) {

        }

    }
}
