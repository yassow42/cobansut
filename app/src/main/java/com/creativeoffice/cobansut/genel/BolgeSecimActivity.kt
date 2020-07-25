package com.creativeoffice.cobansut.genel

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.creativeoffice.cobansut.Activity.SiparislerActivity
import com.creativeoffice.cobansut.CorluActivity.SiparislerCorluActivity
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.SiparisActivityCerkez
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bolge_secim)
        konumIzni()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(" Bölgeler Yükleniyor... Lütfen bekleyin...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        handler.postDelayed(Runnable { progressDialog.dismiss() }, 350)

        mAuth = FirebaseAuth.getInstance()
        //    mAuth.signOut()
        initMyAuthStateListener()

        setupButon()

    }

    private fun setupButon() {

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