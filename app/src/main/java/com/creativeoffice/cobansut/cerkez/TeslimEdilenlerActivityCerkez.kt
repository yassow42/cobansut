package com.creativeoffice.cobansut.cerkez

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.TeslimEdilenlerAdapter
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperCerkez
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_teslim_edilenler_cerkez.*

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TeslimEdilenlerActivityCerkez : AppCompatActivity() {

    private val ACTIVITY_NO = 2
    lateinit var suankiTeslimList: ArrayList<SiparisData>
    lateinit var butunTeslimList: ArrayList<SiparisData>
    var ref = FirebaseDatabase.getInstance().reference.child("Cerkez")
    lateinit var progressDialog: ProgressDialog
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teslim_edilenler_cerkez)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setupBtn()
        setupNavigationView()

        suankiTeslimList = ArrayList()
        butunTeslimList = ArrayList()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Lütfen bekleyin...")
        progressDialog.setCancelable(false)
        progressDialog.show()



        handler.postDelayed(Runnable { setupVeri() }, 2000)
        handler.postDelayed(Runnable { progressDialog.dismiss() }, 5000)


    }


    private fun setupVeri() {
        ref.child("Teslim_siparisler").keepSynced(true)
        ref.child("Teslim_siparisler").orderByChild("siparis_teslim_zamani").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                if (data.hasChildren()) {
                    var sut3ltSayisi = 0
                    var sut5ltSayisi = 0
                    var dokmeSayisi = 0

                    var yumurtaSayisi = 0
                    var toplamFiyatlar = 0.0

                    FirebaseDatabase.getInstance().reference.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            var gece3GelenZaman = p0.child("gece3").value.toString().toLong()
                            var suankıZaman = System.currentTimeMillis()

                            if (gece3GelenZaman < suankıZaman) {
                                var guncelGece3 = gece3GelenZaman + 86400000
                                FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3)
                            }
                            for (ds in data.children) {
                                var gelenData = ds.getValue(SiparisData::class.java)!!
                                if (gelenData.dokme_sut == null) {
                                    ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("dokme_sut").setValue("0")
                                    ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("dokme_sut_fiyat").setValue(3.5)
                                }
                                if (gelenData.dokme_sut_fiyat == null) {
                                    ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("dokme_sut").setValue("0")
                                    ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("dokme_sut_fiyat").setValue(3.5)
                                }
                                butunTeslimList.add(gelenData)

                                if (gece3GelenZaman - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < gece3GelenZaman) {
                                    suankiTeslimList.add(gelenData)
                                    sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                    sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                    dokmeSayisi += gelenData.dokme_sut!!.toInt()
                                    yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi

                                    try {
                                        toplamFiyatlar = gelenData.toplam_fiyat!!.toDouble() + toplamFiyatlar
                                    } catch (e: IOException) {
                                        Log.e("teslim activity", "hatalar ${e.message.toString()}")
                                    }


                                }

                                if ( gelenData.siparisi_giren == "CorluMurat") {
                                    ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("siparisi_giren").setValue("Corlumurat")
                                    ref.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("siparis_giren").removeValue()
                                }
                            }
                            progressDialog.dismiss()
                            suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                            tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                            tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                            tvDokme.text = "Dökme: " + dokmeSayisi.toString()
                            tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()

                            try {
                                tvFiyatGenel.setText(toplamFiyatlar.toString() + " TL")
                            } catch (e: IOException) {
                                Log.e("teslim activity", "hatalar ${e.message.toString()}")

                            }


                            setupRecyclerView()
                        }
                    })
                } else {
                    progressDialog.setMessage("Veri Alınamıyor...")
                    handler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)

                }
            }
        })
    }

    private fun setupBtn() {
        tvZamandan.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
        tvZamana.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())

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
            var dokmeSayisi = 0
            var yumurtaSayisi = 0
            var toplamFiyatlar = 0.0

            for (ds in butunTeslimList) {


                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                    suankiTeslimList.add(ds)
                    sut3ltSayisi += ds.sut3lt!!.toInt()
                    sut5ltSayisi += ds.sut5lt!!.toInt()
                    dokmeSayisi += ds.dokme_sut!!.toInt()
                    yumurtaSayisi += ds.yumurta!!.toInt()
                    toplamFiyatlar += ds.toplam_fiyat!!.toDouble()

                }

                suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                tvDokme.text = "Dökme: " + dokmeSayisi.toString()
                tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                tvFiyatGenel.text = toplamFiyatlar.toString() + " TL"

                setupRecyclerView()

            }
        }
        /*
        imgOptions.setOnClickListener {

            val popup = PopupMenu(this, imgOptions)
            popup.inflate(R.menu.popup_menu_teslim)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                var sut3ltSayisi = 0
                var sut5ltSayisi = 0
                var yumurtaSayisi = 0
                when (it.itemId) {
                    R.id.samet -> {
                        suankiTeslimList.clear()

                        for (ds in butunTeslimList) {
                            if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                                if (ds.siparisi_giren == "Samet") {
                                    suankiTeslimList.add(ds)
                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                }
                            }
                        }

                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                        tvFiyatGenelTeslim.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"
                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }
                    R.id.Engin -> {
                        suankiTeslimList.clear()

                        for (ds in butunTeslimList) {
                            if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {

                                if (ds.siparisi_giren == "Umit") {
                                    suankiTeslimList.add(ds)
                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                }

                            }
                        }

                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                        tvFiyatGenelTeslim.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"
                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }

                    R.id.nihat -> {
                        suankiTeslimList.clear()

                        for (ds in butunTeslimList) {
                            if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {

                                if (ds.siparisi_giren == "Nihat") {
                                    suankiTeslimList.add(ds)
                                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                                }

                            }
                        }

                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                        tvFiyatGenelTeslim.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"
                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        setupRecyclerView()
                    }


                }
                return@OnMenuItemClickListener true
            })
            popup.show()

        }
*/

    }


    private fun setupRecyclerView() {
        rcTeslimEdilenler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = TeslimEdilenlerAdapter(this, suankiTeslimList, "Cerkez")
        rcTeslimEdilenler.adapter = Adapter
        rcTeslimEdilenler.setHasFixedSize(true)
    }

    fun setupNavigationView() {

        BottomNavigationViewHelperCerkez.setupBottomNavigationView(bottomNavCerkez)
        BottomNavigationViewHelperCerkez.setupNavigation(this@TeslimEdilenlerActivityCerkez, bottomNavCerkez) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavCerkez.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}