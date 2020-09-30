package com.creativeoffice.cobansut

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.creativeoffice.cobansut.Activity.SiparislerActivity
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.dialog_map_item_siparisler.view.*
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var progressDialog: ProgressDialog
    private val ACTIVITY_NO = 1


    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var kullaniciAdi: String

    var konum: Boolean = false
    val hndler = Handler()
    val ref = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        setupNavigationView()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(" Harita Yükleniyor... Lütfen bekleyin...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Toast.makeText(this, "Bazı Siparişler Adres Bulunamadığından gösterilmeyebilir. Dikkatli Ol.!!!", Toast.LENGTH_LONG).show()


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val burgaz = LatLng(41.400897, 27.356983)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(burgaz, 13.7f))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          //  aracTakip()
            setupKullaniciAdi()
            swKonum.setOnClickListener {
                val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                if (swKonum.isChecked) {
                    editor.putBoolean("Konum", true)
                    editor.apply()
                    mMap.isMyLocationEnabled = true
                    Toast.makeText(this, "Konum açıldı", Toast.LENGTH_SHORT).show()
                } else {
                    editor.putBoolean("Konum", false)
                    editor.apply()
                    mMap.isMyLocationEnabled = false
                    Toast.makeText(this, "Konum kapalı", Toast.LENGTH_SHORT).show()
                }
            }
            val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            konum = sharedPreferences.getBoolean("Konum", false)
            swKonum.isChecked = konum
            mMap.isMyLocationEnabled = konum

        } else {
            Toast.makeText(this, "İzinleri Kontrol et.", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1)
        }


    }


    fun aracTakip() {

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 1f, myLocationListener)


    }


    val myLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            mMap.clear()
            var lat = location!!.latitude
            var long = location!!.longitude


            ref.child("users").child(userID).child("lat").setValue(lat)
            ref.child("users").child(userID).child("long").setValue(long)


            var konum = LatLng(lat, long)
            Log.e("konum", lat.toString())
            Log.e("konum", long.toString())
            mMap.addMarker(MarkerOptions().position(konum).title("araç"))


        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("Not yet implemented")
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("Not yet implemented")
        }
    }

    fun veriler() {
        var gelenData: SiparisData
        ref.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        try {
                            gelenData = ds.getValue(SiparisData::class.java)!!
                            if (gelenData.siparis_teslim_tarihi != null) {
                                if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {
                                    var konumVarMi = gelenData.musteri_zkonum.toString().toBoolean()
                                    if (konumVarMi) {
                                        try {
                                            var lat = gelenData.musteri_zlat!!.toDouble()
                                            var long = gelenData.musteri_zlong!!.toDouble()
                                            val adres = LatLng(lat, long)
                                            var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren).snippet(gelenData.siparis_adres + " / " + gelenData.siparis_apartman))

                                            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                            myMarker.tag = gelenData.siparis_key
                                        } catch (e: Exception) {
                                            Toast.makeText(this@MapsActivity, gelenData.siparis_veren + " adlı müşterinin konumu hatalı. Lütfen ev konum switch'ini kapatın", Toast.LENGTH_LONG).show()
                                        }

                                    } else {
                                        var lat = convertAddressLat(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres + " Lüleburgaz 39750")!!.toDouble()
                                        var long = convertAddressLng(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres + " Lüleburgaz 39750")!!.toDouble()
                                        val adres = LatLng(lat, long)
                                        mMap.addCircle(CircleOptions().center(adres).radius(75.0).strokeWidth(6f).strokeColor(Color.BLUE).fillColor(ContextCompat.getColor(applicationContext, R.color.transpan)))
                                        var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren).snippet(gelenData.siparis_adres + " / " + gelenData.siparis_apartman))
                                        myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                        myMarker.tag = gelenData.siparis_key
                                    }
                                    mMap.setOnMarkerClickListener {
                                        it.tag

                                        var bottomSheetDialog = BottomSheetDialog(this@MapsActivity)

                                        var view = bottomSheetDialog.layoutInflater.inflate(R.layout.dialog_map_item_siparisler, null)
                                        bottomSheetDialog.setContentView(view)
                                        var ref = FirebaseDatabase.getInstance().reference
                                        ref.child("Siparisler").child(it.tag.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                var gelenData = p0.getValue(SiparisData::class.java)!!
                                                view.tvSiparisVeren.text = gelenData!!.siparis_veren
                                                view.tvSiparisAdres.text = gelenData!!.siparis_mah + " mah. " + gelenData!!.siparis_adres + " " + gelenData!!.siparis_apartman
                                                view.tvSiparisTel.text = gelenData.siparis_tel
                                                view.tvNot.text = gelenData.siparis_notu


                                                view.tv3lt.text = gelenData!!.sut3lt
                                                view.tv3ltFiyat.text = gelenData!!.sut3lt_fiyat.toString()
                                                view.tv5lt.text = gelenData!!.sut5lt
                                                view.tv5ltFiyat.text = gelenData!!.sut5lt_fiyat.toString()
                                                view.tvYumurta.text = gelenData!!.yumurta
                                                view.tvYumurtaFiyat.text = gelenData!!.yumurta_fiyat.toString()

                                                var sut3ltAdet = gelenData.sut3lt.toString().toInt()
                                                var sut3ltFiyat = gelenData.sut3lt_fiyat.toString().toDouble()
                                                var sut5ltAdet = gelenData.sut5lt.toString().toInt()
                                                var sut5ltFiyat = gelenData.sut5lt_fiyat.toString().toDouble()
                                                var yumurtaAdet = gelenData.yumurta.toString().toInt()
                                                var yumurtaFiyat = gelenData.yumurta_fiyat.toString().toDouble()

                                                view.tvFiyat.text = ((sut3ltAdet * sut3ltFiyat) + (sut5ltAdet * sut5ltFiyat) + (yumurtaAdet * yumurtaFiyat)).toString() + " tl"


                                                view.swMapPro.setOnClickListener {

                                                    if (view.swMapPro.isChecked) {
                                                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(gelenData.siparis_veren.toString()).child("promosyon_verildimi").setValue(true)
                                                    } else {
                                                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(gelenData.siparis_veren.toString()).child("promosyon_verildimi").setValue(false)

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
                                                    var alert = AlertDialog.Builder(this@MapsActivity)
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
                                                                    gelenData.toplam_fiyat,
                                                                    gelenData.musteri_zkonum,
                                                                    gelenData.promosyon_verildimi,
                                                                    gelenData.musteri_zlat,
                                                                    gelenData.musteri_zlong,
                                                                    kullaniciAdi
                                                                )

                                                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(gelenData.siparis_veren.toString()).child("siparisleri")
                                                                    .child(gelenData.siparis_key.toString()).setValue(siparisData)

                                                                FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).setValue(siparisData)
                                                                    .addOnCompleteListener {

                                                                        startActivity(Intent(this@MapsActivity, SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                                                        Toast.makeText(this@MapsActivity, "Sipariş Teslim Edildi", Toast.LENGTH_LONG).show()
                                                                        FirebaseDatabase.getInstance().reference.child("Siparisler").child(gelenData.siparis_key.toString()).removeValue()
                                                                        FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").child(gelenData.siparis_key.toString()).child("siparis_teslim_zamani")
                                                                            .setValue(ServerValue.TIMESTAMP)
                                                                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(gelenData.siparis_veren.toString()).child("siparis_son_zaman").setValue(
                                                                            ServerValue.TIMESTAMP
                                                                        )
                                                                    }
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
                                        })

                                        bottomSheetDialog.show()
                                        it.isVisible
                                    }
                                }
                            }


                        } catch (ex: IOException) {
                            Log.e("sad", ex.message.toString())
                        }
                    }
                    progressDialog.dismiss()
                } else {
                    hndler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
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


    fun setupKullaniciAdi() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()
                veriler()
            }
        })
    }

    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

}


