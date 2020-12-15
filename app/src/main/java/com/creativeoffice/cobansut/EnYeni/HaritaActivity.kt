package com.creativeoffice.cobansut.EnYeni

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.SiparisActivityCerkez
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperYeni
import com.creativeoffice.cobansut.utils.Utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_harita.*
import kotlinx.android.synthetic.main.dialog_map_item_siparisler.view.*
import java.io.IOException


class HaritaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var refBolge = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_harita)
        setupNavigationView()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Utils.secilenBolge == "Burgaz") {
            refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)

            val burgaz = LatLng(41.400897, 27.356983)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(burgaz, 13.7f))
        } else if (Utils.secilenBolge == "Corlu") {
            refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)

            val corlu = LatLng(41.160780, 27.797565)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(corlu, 13.0f))

        } else if (Utils.secilenBolge == "Cerkez") {
            refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)

            val cerkez = LatLng(41.282571, 28.000692)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cerkez, 12.3f))
        }

        setupVeriler()
    }

    private fun setupVeriler() {
        var mahalleList = ArrayList<String>()

        refBolge.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        mahalleList.add(ds.key.toString())
                    }

                    for (imahalle in mahalleList) {
                        refBolge.child("Siparisler").child(imahalle).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {

                                if (p0.hasChildren()) {
                                    for (ds in p0.children) {
                                        try {
                                            var gelenData = ds.getValue(SiparisData::class.java)!!

                                            if (gelenData.siparis_teslim_tarihi != null) {
                                                if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {
                                                    var konumVarMi = gelenData.musteri_zkonum.toString().toBoolean()
                                                    if (konumVarMi) {
                                                        var lat = gelenData.musteri_zlat!!.toDouble()
                                                        var long = gelenData.musteri_zlong!!.toDouble()
                                                        val adres = LatLng(lat, long)
                                                        //  mMap.addCircle(CircleOptions().center(adres).radius(100.0).strokeWidth(2f).strokeColor(Color.BLUE).fillColor(ContextCompat.getColor(applicationContext, R.color.transpan)))
                                                        var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren))

                                                        myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                                        myMarker.tag = gelenData.siparis_key
                                                        myMarker.snippet = gelenData.siparis_mah
                                                    } else {
                                                        if (gelenData.siparis_mah != "Market") {
                                                            try {
                                                                var lat = convertAddressLat(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres)!!.toDouble()
                                                                var long = convertAddressLng(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres)!!.toDouble()
                                                                val adres = LatLng(lat, long)
                                                                mMap.addCircle(
                                                                    CircleOptions().center(adres).radius(150.0).strokeWidth(6f).strokeColor(Color.BLUE).fillColor(
                                                                        ContextCompat.getColor(
                                                                            applicationContext,
                                                                            R.color.transpan
                                                                        )
                                                                    )
                                                                )
                                                                var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren))

                                                                myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                                                myMarker.tag = gelenData.siparis_key
                                                                myMarker.snippet = gelenData.siparis_mah
                                                            } catch (e: Exception) {
                                                                Log.e("127 hata", e.message.toString())

                                                            }

                                                        }
                                                    }


                                                    mMap.setOnMarkerClickListener {
                                                        it.tag
                                                        it.snippet
                                                        Log.e("konum", it.tag.toString())
                                                        Log.e("konum", it.snippet.toString())
                                                        var bottomSheetDialog = BottomSheetDialog(this@HaritaActivity)
                                                        var view = bottomSheetDialog.layoutInflater.inflate(R.layout.dialog_map_item_siparisler, null)
                                                        bottomSheetDialog.setContentView(view)
                                                        refBolge.child("Siparisler").child(it.snippet.toString()).child(it.tag.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {
                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {
                                                                if (p0.hasChildren()) {
                                                                    var gelenData = p0.getValue(SiparisData::class.java)!!
                                                                    view.tvSiparisVeren.text = gelenData.siparis_veren
                                                                    view.tvSiparisAdres.text = gelenData.siparis_mah + " mah. " + gelenData!!.siparis_adres + " " + gelenData!!.siparis_apartman
                                                                    view.tvSiparisTel.text = gelenData.siparis_tel
                                                                    view.tvNot.text = gelenData.siparis_notu

                                                                    view.tv3lt.text = gelenData.sut3lt
                                                                    view.tv3ltFiyat.text = gelenData.sut3lt_fiyat.toString()
                                                                    view.tv5lt.text = gelenData.sut5lt
                                                                    view.tv5ltFiyat.text = gelenData.sut5lt_fiyat.toString()
                                                                    view.tvYumurta.text = gelenData.yumurta
                                                                    view.tvYumurtaFiyat.text = gelenData.yumurta_fiyat.toString()

                                                                    var sut3ltAdet = gelenData.sut3lt.toString().toInt()
                                                                    var sut3ltFiyat = gelenData.sut3lt_fiyat.toString().toDouble()
                                                                    var sut5ltAdet = gelenData.sut5lt.toString().toInt()
                                                                    var sut5ltFiyat = gelenData.sut5lt_fiyat.toString().toDouble()
                                                                    var yumurtaAdet = gelenData.yumurta.toString().toInt()
                                                                    var yumurtaFiyat = gelenData.yumurta_fiyat.toString().toDouble()
                                                                    view.tvFiyat.text = ((sut3ltAdet * sut3ltFiyat) + (sut5ltAdet * sut5ltFiyat) + (yumurtaAdet * yumurtaFiyat)).toString() + " tl"

                                                                    view.swMapPro.setOnClickListener {
                                                                        if (view.swMapPro.isChecked) {
                                                                            refBolge.child("Musteriler").child(gelenData.siparis_veren.toString()).child("promosyon_verildimi").setValue(true)
                                                                        } else {
                                                                            refBolge.child("Musteriler").child(gelenData.siparis_veren.toString()).child("promosyon_verildimi").setValue(false)

                                                                        }
                                                                    }
                                                                    view.swMapPro.isChecked = gelenData.promosyon_verildimi.toString().toBoolean()

                                                                    view.tvSiparisTel.setOnClickListener {
                                                                        val arama =
                                                                            Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                                                                        arama.data = Uri.parse("tel:" + gelenData.siparis_tel)
                                                                        startActivity(arama)
                                                                    }
                                                                    view.tvSiparisAdres.setOnClickListener {
                                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q= " + gelenData.siparis_mah + " " + gelenData.siparis_adres))
                                                                        startActivity(intent)
                                                                    }

                                                                    view.btnTeslim.setOnClickListener {
                                                                        var alert = AlertDialog.Builder(this@HaritaActivity)
                                                                            .setTitle("Sipariş Teslim Edildi")
                                                                            .setMessage("Emin Misin ?")
                                                                            .setPositiveButton("Onayla", object : DialogInterface.OnClickListener {
                                                                                override fun onClick(p0: DialogInterface?, p1: Int) {


                                                                                    var siparisData = SiparisData(
                                                                                        gelenData.siparis_zamani,
                                                                                        gelenData.siparis_teslim_zamani,
                                                                                        gelenData.siparis_teslim_tarihi,
                                                                                        gelenData.siparis_adres,
                                                                                        gelenData.siparis_apartman,
                                                                                        gelenData.siparis_tel,
                                                                                        gelenData.siparis_veren,
                                                                                        gelenData.siparis_mah,
                                                                                        gelenData.siparis_notu,
                                                                                        gelenData.siparis_key,
                                                                                        gelenData.yumurta,
                                                                                        gelenData.yumurta_fiyat,
                                                                                        gelenData.sut3lt,
                                                                                        gelenData.sut3lt_fiyat,
                                                                                        gelenData.sut5lt,
                                                                                        gelenData.sut5lt_fiyat,
                                                                                        gelenData.dokme_sut,
                                                                                        gelenData.dokme_sut_fiyat,
                                                                                        gelenData.toplam_fiyat,
                                                                                        gelenData.musteri_zkonum,
                                                                                        gelenData.promosyon_verildimi,
                                                                                        gelenData.musteri_zlat,
                                                                                        gelenData.musteri_zlong,
                                                                                        Utils.kullaniciAdi
                                                                                    )

                                                                                    refBolge.child("Musteriler").child(gelenData.siparis_veren.toString()).child("siparisleri")
                                                                                        .child(gelenData.siparis_key.toString()).setValue(siparisData)

                                                                                    refBolge.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(siparisData)


                                                                                    Toast.makeText(this@HaritaActivity, "Sipariş Teslim Edildi", Toast.LENGTH_LONG).show()
                                                                                    refBolge.child("Siparisler").child(gelenData.siparis_mah.toString()).child(gelenData.siparis_key.toString()).removeValue()
                                                                                    refBolge.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("siparis_teslim_zamani")
                                                                                        .setValue(ServerValue.TIMESTAMP)
                                                                                    refBolge.child("Musteriler").child(gelenData.siparis_veren.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
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


                                                            }
                                                        })

                                                        bottomSheetDialog.show()
                                                        it.isVisible
                                                    }
                                                }
                                            }


                                        } catch (ex: IOException) {
                                            Log.e("Harita Veri Hatası", ex.message.toString())
                                        }
                                    }


                                } else {


                                }
                            }
                        })
                    }
                }
            }
        })


    }

    fun convertAddressLat(adres: String): Double? {
        var geoCoder = Geocoder(this)
        try {
            val addressList: List<Address> =
                geoCoder.getFromLocationName(adres.toString(), 1)
            if (addressList != null && addressList.size > 0) {
                val lat: Double = addressList[0].getLatitude()
                val lng: Double = addressList[0].getLongitude()
                return lat
            }
        } catch (ex: Exception) {
            Log.e("Harita Verilerini Conve", ex.toString())
        }
        return null
    }

    fun convertAddressLng(adres: String): Double? {
        var geoCoder = Geocoder(this)
        try {
            val addressList: List<Address> =
                geoCoder.getFromLocationName(adres.toString(), 1)
            if (addressList != null && addressList.size > 0) {
                val lat: Double = addressList[0].getLatitude()
                val lng: Double = addressList[0].getLongitude()
                return lng
            }
        } catch (ex: Exception) {
            Log.e("Harita Verilerini Conve", ex.toString())
        }
        return null
    }

    private fun setupNavigationView() {

        BottomNavigationViewHelperYeni.setupBottomNavigationView(bottomNavYeni)
        BottomNavigationViewHelperYeni.setupNavigation(this, bottomNavYeni) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavYeni.menu
        var menuItem = menu.getItem(1)
        menuItem.setChecked(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}