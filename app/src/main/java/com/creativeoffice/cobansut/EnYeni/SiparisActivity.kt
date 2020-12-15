package com.creativeoffice.cobansut.EnYeni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.SiparisAdapter
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.adapter.MahalleAdapter
import com.creativeoffice.cobansut.genel.BolgeSecimActivity

import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperYeni
import com.creativeoffice.cobansut.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_siparis.*
import kotlinx.android.synthetic.main.activity_siparis.imgileriDown
import kotlinx.android.synthetic.main.activity_siparis.imgileriUp
import kotlinx.android.synthetic.main.activity_siparis.rcileriTarih
import kotlinx.android.synthetic.main.activity_siparis.tv3litre
import kotlinx.android.synthetic.main.activity_siparis.tv5litre
import kotlinx.android.synthetic.main.activity_siparis.tvDokme
import kotlinx.android.synthetic.main.activity_siparis.tvFiyatGenel
import kotlinx.android.synthetic.main.activity_siparis.tvYumurta
import kotlinx.android.synthetic.main.activity_siparis.tvileriTarihliSayi
import kotlinx.android.synthetic.main.activity_siparisler.*


class SiparisActivity : AppCompatActivity() {


    var refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)

    val mahalleList = ArrayList<String>()
    val ileriTarihListesi = ArrayList<SiparisData>()

    var sut3ltSayisi = 0
    var sut5ltSayisi = 0
    var yumurtaSayisi = 0
    var dokmeSayisi = 0
    var toplamFiyatlar = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siparis)
        setupNavigationView()
        tvBaslik.text = "${Utils.secilenBolge} Siparişleri"
        tvKullanici.text = "${Utils.kullaniciAdi}"
        refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)
     //   Snackbar.make(bottomNavYeni, Utils.secilenBolge + " " + Utils.kullaniciAdi + " " + Utils.zaman, 2500).show()



        refBolge.keepSynced(true)
        refBolge.child("Siparisler").addListenerForSingleValueEvent(siparisleriGetir)
    }

    var siparisleriGetir = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            mahalleList.clear()
            ileriTarihListesi.clear()
            if (p0.hasChildren()) {

                for (ds in p0.children) {
                    var mahalle = ds.key.toString()
                    if (mahalle == "Market") {
                        mahalleList.add(0, mahalle)
                    } else {
                        mahalleList.add(mahalle)
                    }

                }

                for (mahalle in mahalleList) {

                    for (mahalleSiparisi in p0.child(mahalle).children) {
                        var gelenData = mahalleSiparisi.getValue(SiparisData::class.java)!!

                        if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {
                            sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                            sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                            dokmeSayisi = gelenData.dokme_sut!!.toInt() + dokmeSayisi
                            yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi

                            toplamFiyatlar = gelenData.toplam_fiyat!!.toDouble() + toplamFiyatlar

                        }
                        if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == 1) {
                            ileriTarihListesi.add(gelenData)
                            tvileriTarihliSayi.text = ileriTarihListesi.size.toString() + " Sipariş"

                            ileriTarihListesi.sortByDescending {
                                it.siparis_mah
                            }

                            rcileriTarih.layoutManager = LinearLayoutManager(this@SiparisActivity, LinearLayoutManager.VERTICAL, false)
                            val Adapter = SiparisAdapter(this@SiparisActivity, ileriTarihListesi, Utils.kullaniciAdi,Utils.secilenBolge)
                            rcileriTarih.adapter = Adapter
                            rcileriTarih.setHasFixedSize(true)

                        }


                        tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                        tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                        tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                        tvDokme.text = "Dökme: " + dokmeSayisi.toString()
                        tvFiyatGenel.text = toplamFiyatlar.toString() + " TL"

                        imgileriDown.setOnClickListener {
                            imgileriDown.visibility = View.GONE
                            imgileriUp.visibility = View.VISIBLE
                            rcileriTarih.visibility = View.VISIBLE
                        }
                        imgileriUp.setOnClickListener {
                            imgileriDown.visibility = View.VISIBLE
                            imgileriUp.visibility = View.GONE
                            rcileriTarih.visibility = View.GONE
                        }

                    }
                }


            }

            val adapter = MahalleAdapter(this@SiparisActivity, mahalleList, Utils.kullaniciAdi, Utils.secilenBolge)
            rcSiparisler.layoutManager = LinearLayoutManager(this@SiparisActivity, LinearLayoutManager.VERTICAL, false)
            rcSiparisler.adapter = adapter
            adapter.notifyDataSetChanged()

        }

        override fun onCancelled(error: DatabaseError) {

        }

    }


    private fun setupNavigationView() {

        BottomNavigationViewHelperYeni.setupBottomNavigationView(bottomNavYeni)
        BottomNavigationViewHelperYeni.setupNavigation(this, bottomNavYeni) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavYeni.menu
        var menuItem = menu.getItem(0)
        menuItem.setChecked(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }


}