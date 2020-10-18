package com.creativeoffice.cobansut.CorluActivity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.genel.LoginActivity
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.adapter.MahalleAdapter
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperCorlu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_siparis_corlu.*
import kotlinx.android.synthetic.main.activity_siparis_corlu.bottomNav

class SiparislerCorluActivity : AppCompatActivity() {


    val handler = Handler()
    lateinit var progressDialog: ProgressDialog

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
    var kullaniciAdi: String = "yok"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siparis_corlu)
        setupNavigationView()
        mAuth = FirebaseAuth.getInstance()
        initMyAuthStateListener()
        userID = mAuth.currentUser!!.uid
        setupKullaniciAdi()
      //  setupBtn()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        handler.postDelayed(Runnable { progressDialog.dismiss() }, 4000)
    }

    private fun setupVeri() {


        var mahalleList = ArrayList<String>()

        var ref = FirebaseDatabase.getInstance().reference

        ref.child("Corlu").child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        mahalleList.add(ds.key.toString())
                    }

                    val adapter = MahalleAdapter(this@SiparislerCorluActivity, mahalleList, kullaniciAdi.toString(), "Corlu")
                    rcMahalleler.layoutManager = LinearLayoutManager(this@SiparislerCorluActivity, LinearLayoutManager.VERTICAL, false)
                    rcMahalleler.adapter = adapter
                    adapter.notifyDataSetChanged()

                    progressDialog.dismiss()



                } else {
                    progressDialog.setMessage("Sipariş yok :(")
                    handler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
                    Toast.makeText(this@SiparislerCorluActivity, "Sipariş yok :(", Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    fun setupNavigationView() {

        BottomNavigationViewHelperCorlu.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelperCorlu.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(0)
        menuItem.setChecked(true)
    }

    fun setupKullaniciAdi() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()
                setupVeri()
            }

        })
    }

    private fun initMyAuthStateListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val kullaniciGirisi = p0.currentUser
                if (kullaniciGirisi != null) { //eğer kişi giriş yaptıysa nul gorunmez. giriş yapmadıysa null olur
                } else {
                    startActivity(Intent(this@SiparislerCorluActivity, LoginActivity::class.java))
                }
            }
        }
    }


}