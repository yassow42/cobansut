package com.creativeoffice.cobansut.genel

import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.cobansut.Activity.HesapActivity
import com.creativeoffice.cobansut.BuildConfig
import com.creativeoffice.cobansut.EnYeni.SiparisActivity
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.utils.Utils
import com.google.android.material.snackbar.Snackbar
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
import java.text.SimpleDateFormat
import java.util.*

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
        zamanAyarı()
        konumIzni()

        mAuth = FirebaseAuth.getInstance()
        //    mAuth.signOut()
        versionCode = BuildConfig.VERSION_CODE

        var user = mAuth.currentUser

        if (user == null) {
            startActivity(Intent(this@BolgeSecimActivity, LoginActivity::class.java))
        } else {
            ref.child("users").child(mAuth.currentUser!!.uid).child("Version").setValue(versionCode)
            val time = convertLongToTime(System.currentTimeMillis())
            ref.child("users").child(mAuth.currentUser!!.uid).child("son_aktif").setValue(time)

        }


        initMyAuthStateListener()
        setup()

        FirebaseDatabase.getInstance().reference.child("versiyon").keepSynced(true)
        FirebaseDatabase.getInstance().reference.child("Zaman").keepSynced(true)
        FirebaseDatabase.getInstance().reference.child("users").keepSynced(true)

        ref.child("versiyon").addListenerForSingleValueEvent(versiyon)


    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm dd.MM")
        return format.format(date)
    }

    var versiyon = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.value != null) {
                var genelVersion = p0.value.toString().toInt()
                if (genelVersion > versionCode) {
                    Toast.makeText(this@BolgeSecimActivity, "Eski Sürüm Kullanıyorsun. Lütfen Güncelle", Toast.LENGTH_LONG).show()

                    // tvBurgaz.isEnabled = false
                    //   tvCorlu.isEnabled = false
                    //  tvCerkez.isEnabled = false
                }
            }
        }

        override fun onCancelled(p0: DatabaseError) {

        }

    }

    private fun setup() {

        Snackbar.make(tvBurgaz, Utils.secilenBolge, 2500).show()

        tvDuyuru.text = Utils.kullaniciAdi + " " + Utils.secilenBolge + " " + Utils.zaman

        // Toast.makeText(this,"selamün aleyküm",Toast.LENGTH_LONG).show()

        ref.child("users").child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var kullaniciAdi = p0.child("user_name").value.toString()
                var yetki = p0.child("yetki").value.toString()
                Utils.kullaniciAdi = kullaniciAdi
                Utils.yetki = yetki

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        tvBurgaz.setOnClickListener {
            val intent = Intent(this, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            Utils.secilenBolge = "Burgaz"
            startActivity(intent)
        }

        tvCorlu.setOnClickListener {
            val intent = Intent(this, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            Utils.secilenBolge = "Corlu"
            startActivity(intent)
        }
        tvCerkez.setOnClickListener {
            val intent = Intent(this, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            Utils.secilenBolge = "Cerkez"
            startActivity(intent)
        }
        tvHesap.setOnClickListener {
            val intent = Intent(this, HesapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        refreshData.setOnClickListener {
            progressDialog = ProgressDialog(this@BolgeSecimActivity)
            progressDialog.setCancelable(false)

            val timer = object : CountDownTimer(4000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progressDialog.setMessage("Sistem kapatılıyor...Programı 2 kere başlatman gerekecek.!!\n ${millisUntilFinished / 1000}")

                    progressDialog.show()


                }

                override fun onFinish() {
                    (this@BolgeSecimActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                    //   (this@BolgeSecimActivity.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()

                }
            }
            timer.start()


        }
    }

    private fun initMyAuthStateListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                if (p0.currentUser != null) { //eğer kişi giriş yaptıysa nul gorunmez. giriş yapmadıysa null olur
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


    private fun zamanAyarı() {
        ref.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                var gece3GelenZaman = p0.child("gece3").value.toString().toLong()
                var suankıZaman = System.currentTimeMillis()


                var duyuru = p0.child("duyuru").value.toString()
                //   tvDuyuru.text = "Duyuru: \n" + duyuru

                if (gece3GelenZaman < suankıZaman) {
                    var guncelGece3 = gece3GelenZaman + 86400000
                    FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3)
                }
                Utils.zaman = gece3GelenZaman

            }
        })

    }
}