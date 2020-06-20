package com.creativeoffice.cobansut.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.creativeoffice.cobansut.CorluActivity.MusterilerCorluActivity
import com.creativeoffice.cobansut.MusterilerActivity
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperCorlu

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_adres_bulma_maps.*
import kotlinx.android.synthetic.main.activity_adres_bulma_maps.bottomNav
import kotlinx.android.synthetic.main.activity_maps_corlu.*

class AdresBulmaMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var musteriAdi: String
    var ref = FirebaseDatabase.getInstance().reference
    private val ACTIVITY_NO = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adres_bulma_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupNavigationView()
        musteriAdi = intent.getStringExtra("musteriAdi")
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val burgaz = LatLng(41.400897, 27.356983)
        //   mMap.addMarker(MarkerOptions().position(burgaz).title("Lüleburgaz"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(burgaz, 13.7f))
        konumKaydi()

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
            ref.child("Musteriler").child(musteriAdi).child("musteri_zkonum").setValue(true)
            ref.child("Musteriler").child(musteriAdi).child("musteri_zlat").setValue(latLng.latitude)
            ref.child("Musteriler").child(musteriAdi).child("musteri_zlong").setValue(latLng.longitude)
            var intent = Intent(this, MusterilerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            Handler().postDelayed({ startActivity(intent) }, 250)


        }

    }

    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

}