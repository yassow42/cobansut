package com.creativeoffice.cobansut.CorluActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.creativeoffice.cobansut.R
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

    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adres_bulma_maps_corlu)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupNavigationView()

        musteriAdi = intent.getStringExtra("musteriAdi")

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val corlu = LatLng(41.160780, 27.797565)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(corlu, 13.0f))

        mMap.isMyLocationEnabled = true

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
            ref.child("Corlu/Musteriler").child(musteriAdi.toString()).child("musteri_zkonum").setValue(true)
            ref.child("Corlu/Musteriler").child(musteriAdi.toString()).child("musteri_zlat").setValue(latLng.latitude)
            ref.child("Corlu/Musteriler").child(musteriAdi.toString()).child("musteri_zlong").setValue(latLng.longitude)
            var intent = Intent(this, MusterilerCorluActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            Handler().postDelayed({ startActivity(intent) }, 250)
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