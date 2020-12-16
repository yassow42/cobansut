package com.creativeoffice.cobansut.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.MusteriSiparisleriAdapter
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_gidilen_musteri.*

class GidilenMusteriActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gidilen_musteri)
        setupNavigationView()
        var ad = intent.getStringExtra("ad").toString()


        imgBack.setOnClickListener {
            onBackPressed()
        }

        setupVeri()

        imgCheck.setOnClickListener {
            if (etAdresGidilen.text.toString().isNotEmpty() && etTelefonGidilen.text.toString().isNotEmpty()) {
                var adres = etAdresGidilen.text.toString()
                var telefon = etTelefonGidilen.text.toString()
                FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).child("musteri_adres").setValue(adres)
                FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).child("musteri_tel").setValue(telefon).addOnCompleteListener{
                    onBackPressed()
                }.addOnFailureListener {
                    Toast.makeText(this, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Boşluklar var", Toast.LENGTH_LONG).show()
            }


        }
    }

    private fun setupVeri() {
        var ad = intent.getStringExtra("ad").toString()
        tvAdSoyad.text = ad

        FirebaseDatabase.getInstance().reference.child("Musteriler").child(ad).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var adres = p0.child("musteri_adres").value.toString()
                var telefon = p0.child("musteri_tel").value.toString()
                etAdresGidilen.setText(adres)
                etTelefonGidilen.setText(telefon)

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

                    tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                    tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                    tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                    tvFiyatGenel.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"

                  setupRecyclerView(list)

                }
            }


        })


    }


    fun setupRecyclerView(list: ArrayList<SiparisData>) {

        rcSiparisGidilen.layoutManager = LinearLayoutManager(this@GidilenMusteriActivity, LinearLayoutManager.VERTICAL, false)
        val Adapter = MusteriSiparisleriAdapter(this@GidilenMusteriActivity, list,Utils.secilenBolge)
        rcSiparisGidilen.adapter = Adapter
        rcSiparisGidilen.setHasFixedSize(true)

    }

    fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
