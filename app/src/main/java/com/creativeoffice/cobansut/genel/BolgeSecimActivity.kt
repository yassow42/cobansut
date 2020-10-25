package com.creativeoffice.cobansut.genel

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.cobansut.Activity.SiparislerActivity
import com.creativeoffice.cobansut.BuildConfig
import com.creativeoffice.cobansut.CorluActivity.SiparislerCorluActivity
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.SiparisActivityCerkez
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_bolge_secim.*

class BolgeSecimActivity : AppCompatActivity() {
    val handler = Handler()
    lateinit var progressDialog: ProgressDialog

    lateinit var mAuth: FirebaseAuth

    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var versionCode: Int = 0
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bolge_secim)
        konumIzni()
        mAuth = FirebaseAuth.getInstance()
        //    mAuth.signOut()
        versionCode = BuildConfig.VERSION_CODE

        var user = mAuth.currentUser
        if (user == null) {

            startActivity(Intent(this@BolgeSecimActivity, LoginActivity::class.java))
        } else ref.child("users").child(mAuth.currentUser!!.uid).child("Version").setValue(versionCode)


        initMyAuthStateListener()
        setup()

        ref.addListenerForSingleValueEvent(versiyonveusername)

    }

    var versiyonveusername = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.child("versiyon").value != null) {
                var genelVersion = p0.child("versiyon").value.toString().toInt()
                if (genelVersion > versionCode) Toast.makeText(this@BolgeSecimActivity, "Eski Sürüm Kullanıyorsun. Lütfen Güncelle", Toast.LENGTH_LONG).show()
            }
            if (p0.child("users").child(mAuth.currentUser!!.uid).value != null) {
                val userName = p0.child("users").child(mAuth.currentUser!!.uid).child("user_name").value.toString()
                Toast.makeText(this@BolgeSecimActivity, "Hoşgeldin... $userName", Toast.LENGTH_SHORT).show()
            }


        }

        override fun onCancelled(p0: DatabaseError) {

        }

    }

    private fun setup() {


        ref.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                var gece3GelenZaman = p0.child("gece3").value.toString().toLong()
                var suankıZaman = System.currentTimeMillis()

                if (gece3GelenZaman < suankıZaman) {

                    var guncelGece3 = gece3GelenZaman + 86400000
                    FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3)
                }
            }
        })


        tvBurgaz.setOnClickListener {
            val intent = Intent(this, SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        tvCorlu.setOnClickListener {
            val intent = Intent(this, SiparislerCorluActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        tvCerkez.setOnClickListener {
            val intent = Intent(this, SiparisActivityCerkez::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

    }

    private fun initMyAuthStateListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val kullaniciGirisi = p0.currentUser
                if (kullaniciGirisi != null) { //eğer kişi giriş yaptıysa nul gorunmez. giriş yapmadıysa null olur
                } else {
                    startActivity(Intent(this@BolgeSecimActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun konumIzni() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {


                    if (report!!.areAllPermissionsGranted()) {

                    }

                    if (report!!.isAnyPermissionPermanentlyDenied) {
                        FirebaseDatabase.getInstance().reference.child("Hatalar/İzin Hatası").push().setValue("İzin Yok")
                        Toast.makeText(this@BolgeSecimActivity, "İzinleri kontrol et", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {

                }


            }).check()
    }
}