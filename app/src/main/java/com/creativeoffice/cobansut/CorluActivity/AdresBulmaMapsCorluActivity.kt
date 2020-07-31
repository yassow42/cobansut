package com.creativeoffice.cobansut.CorluActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import com.creativeoffice.cobansut.MusterilerActivity
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.MusterilerActivityCerkez
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperCorlu

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_adres_bulma_maps_corlu.*
import kotlinx.android.synthetic.main.activity_maps_corlu.bottomNav

class AdresBulmaMapsCorluActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mRunnable: Runnable
    private val ACTIVITY_NO = 3
    var musteriAdi: String? = null
    lateinit var yer: String

    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adres_bulma_maps_corlu)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupNavigationView()

        musteriAdi = intent.getStringExtra("musteriAdi")
        yer = intent.getStringExtra("musteri_konumu")
        Log.e("sad", yer)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (yer != null) {
            konumKaydi()
            if (yer == "Corlu") {
                val corlu = LatLng(41.160780, 27.797565)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(corlu, 13.0f))
            } else if (yer == "Cerkez") {
                val cerkez = LatLng(41.282571, 28.000692)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cerkez, 13.0f))
            } else if (yer == "Burgaz") {
                val burgaz = LatLng(41.400897, 27.356983)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(burgaz, 13.7f))
            }
        }





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = false
        } else {
            mMap.isMyLocationEnabled = true
        }


    }

    private fun konumKaydi() {
        /*
        var hand = Handler()
        mRunnable = Runnable {

            hand.postDelayed(mRunnable, 1000)
        }

        hand.postDelayed(mRunnable, 5000)
*/
        tvKaydet.setOnClickListener {
            //  hand.removeCallbacks(mRunnable)
            val latLng: LatLng = mMap.getCameraPosition().target


            if (yer == "Corlu") {
                ref.child(yer).child("Musteriler").child(musteriAdi.toString()).child("musteri_zkonum").setValue(true)
                ref.child(yer).child("Musteriler").child(musteriAdi.toString()).child("musteri_zlat").setValue(latLng.latitude)
                ref.child(yer).child("Musteriler").child(musteriAdi.toString()).child("musteri_zlong").setValue(latLng.longitude)
                var intent = Intent(this, MusterilerCorluActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            } else if (yer == "Cerkez") {
                ref.child(yer).child("Musteriler").child(musteriAdi.toString()).child("musteri_zkonum").setValue(true)
                ref.child(yer).child("Musteriler").child(musteriAdi.toString()).child("musteri_zlat").setValue(latLng.latitude)
                ref.child(yer).child("Musteriler").child(musteriAdi.toString()).child("musteri_zlong").setValue(latLng.longitude)
                var intent = Intent(this, MusterilerActivityCerkez::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            } else if (yer == "Burgaz") {
                ref.child("Musteriler").child(musteriAdi.toString()).child("musteri_zkonum").setValue(true)
                ref.child("Musteriler").child(musteriAdi.toString()).child("musteri_zlat").setValue(latLng.latitude)
                ref.child("Musteriler").child(musteriAdi.toString()).child("musteri_zlong").setValue(latLng.longitude)
                var intent = Intent(this, MusterilerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            finish()
        }

    }

    fun setupNavigationView() {
        BottomNavigationViewHelperCorlu.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelperCorlu.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}