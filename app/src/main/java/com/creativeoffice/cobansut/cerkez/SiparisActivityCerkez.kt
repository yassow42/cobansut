package com.creativeoffice.cobansut.cerkez

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.adapter.MahalleAdapter
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.genel.LoginActivity
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperCerkez
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_siparis_cerkez.*
import kotlinx.android.synthetic.main.activity_siparis_cerkez.bottomNav
import kotlinx.android.synthetic.main.activity_siparisler.*
import java.lang.Exception

class SiparisActivityCerkez : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var kullaniciAdi: String?=null
    private val ACTIVITY_NO = 0

    // lateinit var progressDialog: ProgressDialog
    var hndler = Handler()
    var ref = FirebaseDatabase.getInstance().reference.child("Cerkez")


    var loading: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siparis_cerkez)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        //    mAuth.signOut()
        initMyAuthStateListener()

        setupKullaniciAdi()
        setupNavigationView()

        zamanAyarı()

        hndler.postDelayed(Runnable { setupKullaniciAdi() }, 750)


    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    private fun veri() {
/*
        ref.child("Siparisler").child("2mahalle").push().setValue(
            SiparisData(
                1595721600000, 1595721600000, 1595721600000, "adr2es", "apart2man", "te2l"
                , "veren2", "mahall2e", "no2tu", "ke2yy", "21", 1.0, "22", 2.0, "23", 3.0, 25.0, false, false,
                null, null, "Ben2immm"
            )
        )

        ref.child("Musteriler").push().setValue(MusteriData("yassow","mah","adres","apartman","tel",
            1595721600000,false,false,null,null))
*/

        imgSighOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ref.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    val mahalleList = ArrayList<String>()
                    for (ds in p0.children) {
                        try {
                            mahalleList.add(ds.key.toString())
                        } catch (e: Exception) {
                            Log.e("hata", "siparişler activity ${e.message.toString()}")
                        }

                    }

                    val adapter = MahalleAdapter(this@SiparisActivityCerkez, mahalleList, kullaniciAdi.toString())
                    rcMahalleler.layoutManager = LinearLayoutManager(this@SiparisActivityCerkez, LinearLayoutManager.VERTICAL, false)
                    rcMahalleler.adapter = adapter
                }
            }
        })

    }

    private fun zamanAyarı() {

        FirebaseDatabase.getInstance().reference.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var gece3 = p0.child("gece3").value.toString().toLong()
                var suankıZaman = System.currentTimeMillis()

                if (gece3 < suankıZaman) {
                    var guncelGece3 = gece3 + 86400000
                    ref.child("Zaman").child("gece3").setValue(guncelGece3).addOnCompleteListener {
                        ref.child("Zaman").child("gerigece3").setValue(gece3)
                    }

                }
            }
        })
    }

    fun setupKullaniciAdi() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()

                veri()
            }

        })
    }

    private fun initMyAuthStateListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val kullaniciGirisi = p0.currentUser
                if (kullaniciGirisi != null) { //eğer kişi giriş yaptıysa nul gorunmez. giriş yapmadıysa null olur
                    ref.child("Girişler").push().setValue(kullaniciGirisi!!.uid)
                } else {
                    startActivity(Intent(this@SiparisActivityCerkez, LoginActivity::class.java))
                }
            }
        }
    }


    fun setupNavigationView() {

        BottomNavigationViewHelperCerkez.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelperCerkez.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


}