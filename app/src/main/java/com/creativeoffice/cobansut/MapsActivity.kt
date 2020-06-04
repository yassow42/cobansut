package com.creativeoffice.cobansut

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.cobansut.Activity.SiparislerActivity
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.dialog_map_item_siparisler.view.*
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var progressDialog: ProgressDialog
    private val ACTIVITY_NO = 1

    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var kullaniciAdi: String

    var konum: Boolean = false
    val hndler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid

        setupKullaniciAdi()
        setupNavigationView()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(" Harita Yükleniyor... Lütfen bekleyin...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        konum = sharedPreferences.getBoolean("Konum", false)
        swKonum.isChecked = konum

        Toast.makeText(this, "Bazı Siparişler Adres Bulunamadığından gösterilmeyebilir. Dikkatli Ol.!!!", Toast.LENGTH_LONG).show()


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //     mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.isMyLocationEnabled = konum
        // Add a marker in Sydney and move the camera
        val burgaz = LatLng(41.400897, 27.356983)
        //   mMap.addMarker(MarkerOptions().position(burgaz).title("Lüleburgaz"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(burgaz, 13.7f))


       hndler.postDelayed(Runnable { veriler() }, 2000)

    }


    fun veriler() {
        var gelenData: SiparisData

        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        try {
                            gelenData = ds.getValue(SiparisData::class.java)!!
                            if (gelenData.siparis_adres.toString() != "Adres") {
                                if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {
                                    var konumVarMi = gelenData.musteri_zkonum.toString().toBoolean()
                                    if (konumVarMi) {
                                        var lat = gelenData.musteri_zlat!!.toDouble()
                                        var long = gelenData.musteri_zlong!!.toDouble()
                                        val adres = LatLng(lat, long)
                                        var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren).snippet(gelenData.siparis_adres + " / " + gelenData.siparis_apartman))

                                        myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                        myMarker.tag = gelenData.siparis_key
                                    }
                                    else {
                                        var lat = convertAddressLat(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres + " Lüleburgaz 39750")!!.toDouble()
                                        var long = convertAddressLng(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres + " Lüleburgaz 39750")!!.toDouble()
                                        val adres = LatLng(lat, long)
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


                                                view.tv5lt.text = gelenData!!.sut5lt
                                                view.tv3lt.text = gelenData!!.sut3lt
                                                view.tvYumurta.text = gelenData!!.yumurta

                                                var sut3ltFiyat = gelenData.sut3lt.toString().toInt()
                                                var sut5ltFiyat = gelenData.sut5lt.toString().toInt()
                                                var yumurtaFiyat = gelenData.yumurta.toString().toInt()

                                                view.tvFiyat.text = ((sut3ltFiyat * 16) + (sut5ltFiyat * 22) + yumurtaFiyat).toString() + " tl"


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
                                                                    gelenData.sut3lt,
                                                                    gelenData.sut5lt,
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
                            Log.e("Harita Veri Hatası", ex.message.toString())
                        }
                    }
                    progressDialog.dismiss()
                }else{
                    hndler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
                }
            }
        })


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
