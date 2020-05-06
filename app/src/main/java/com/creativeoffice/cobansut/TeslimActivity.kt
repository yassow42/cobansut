package com.creativeoffice.cobansut

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.TeslimEdilenlerAdapter
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_siparisler.bottomNav
import kotlinx.android.synthetic.main.activity_teslim.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TeslimActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    lateinit var suankiTeslimList: ArrayList<SiparisData>
    lateinit var butunTeslimList: ArrayList<SiparisData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teslim)
        setupNavigationView()
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        suankiTeslimList = ArrayList()
        butunTeslimList = ArrayList()
        setupVeri()
        setupBtn()


         FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").keepSynced(true)

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
            for (ds in butunTeslimList){
                var sut3ltSayisi = 0
                var sut5ltSayisi = 0
                var yumurtaSayisi = 0

                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                    suankiTeslimList.add(ds)
                    sut3ltSayisi = ds.sut3lt!!.toInt() + sut3ltSayisi
                    sut5ltSayisi = ds.sut5lt!!.toInt() + sut5ltSayisi
                    yumurtaSayisi = ds.yumurta!!.toInt() + yumurtaSayisi
                }

                suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                tvFiyatGenelTeslim.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"
                setupRecyclerView()

            }
    /*
            FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").orderByChild("siparis_teslim_zamani").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(data: DataSnapshot) {
                    if (data.hasChildren()) {
                        suankiTeslimList.clear()

                        var sut3ltSayisi = 0
                        var sut5ltSayisi = 0
                        var yumurtaSayisi = 0


                        for (ds in data.children) {
                            var gelenData = ds.getValue(SiparisData::class.java)!!


                            if (calZamandan.timeInMillis < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                                suankiTeslimList.add(gelenData)
                                sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi
                            }
                        }

                        suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                        tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                        tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                        tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                        tvFiyatGenelTeslim.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"
                        setupRecyclerView()


                    }

                }

            })
*/
        }


    }

    private fun setupVeri() {
        FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").orderByChild("siparis_teslim_zamani").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                if (data.hasChildren()) {


                    var sut3ltSayisi = 0
                    var sut5ltSayisi = 0
                    var yumurtaSayisi = 0


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

                                butunTeslimList.add(gelenData)

                                if (gece3GelenZaman - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < gece3GelenZaman) {
                                    suankiTeslimList.add(gelenData)
                                    sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                    sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                    yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi
                                }
                            }

                            suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                            tv3ltTeslim.text = "3lt: " + sut3ltSayisi.toString()
                            tv5ltTeslim.text = "5lt: " + sut5ltSayisi.toString()
                            tvYumurtaTeslim.text = "Yumurta: " + yumurtaSayisi.toString()
                            tvFiyatGenelTeslim.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"
                            setupRecyclerView()

                        }

                    })


                }

            }

        })
    }

    private fun setupRecyclerView() {
        rcTeslimEdilenler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = TeslimEdilenlerAdapter(this, suankiTeslimList)
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
}
