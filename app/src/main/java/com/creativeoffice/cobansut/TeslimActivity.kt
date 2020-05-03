package com.creativeoffice.cobansut

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.TeslimEdilenlerAdapter
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_siparisler.bottomNav
import kotlinx.android.synthetic.main.activity_teslim.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.*

class TeslimActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    lateinit var teslimList: ArrayList<SiparisData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teslim)
        setupNavigationView()
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        teslimList = ArrayList()
        setupVeri()


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
                    // FirebaseDatabase.getInstance().reference.child("Zaman").child("simdi").setValue(ServerValue.TIMESTAMP)
                    //  FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(1587956400000)

                    FirebaseDatabase.getInstance().reference.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                         var   gece3GelenZaman = p0.child("gece3").value.toString().toLong()

                            var suankıZaman = System.currentTimeMillis()

                            if (gece3GelenZaman < suankıZaman) {
                                var guncelGece3 = gece3GelenZaman + 86400000
                                FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3)
                            }
                            for (ds in data.children) {
                                var gelenData = ds.getValue(SiparisData::class.java)!!

                                if (gece3GelenZaman -86400000  < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < gece3GelenZaman ) {

                                    Log.e("sad1", (gece3GelenZaman-86400000).toString() + " / " + gelenData.siparis_teslim_zamani.toString() + " / " + gece3GelenZaman.toString())
                                    teslimList.add(gelenData)
                                    sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                    sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                    yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi


                                }
                            }


                            teslimList.sortByDescending { it.siparis_teslim_zamani }

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
        val Adapter = TeslimEdilenlerAdapter(this, teslimList)
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
