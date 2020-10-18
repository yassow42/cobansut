package com.creativeoffice.cobansut.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.HesapAdapter
import com.creativeoffice.cobansut.Datalar.Users
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_hesap.*


class HesapActivity : AppCompatActivity() {
    var ACTIVITY_NO = 3
    var ref = FirebaseDatabase.getInstance().reference
    var userID: String? = null

    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap)
        setupNavigationView()
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        if (userID != null) {
            ref.child("users").child(userID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var yetki2 = p0.child("yetki2").value.toString()
                    if (yetki2 != "Patron") startActivity(Intent(this@HesapActivity, BolgeSecimActivity::class.java))
                    veriler()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


    }

    private fun veriler() {

        ref.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var userList = ArrayList<Users>()
                userList.clear()
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenData = ds.getValue(Users::class.java)!!
                        userList.add(gelenData)
                    }
                }
                rcHesap.layoutManager = LinearLayoutManager(this@HesapActivity, LinearLayoutManager.VERTICAL, false)
                rcHesap.adapter = HesapAdapter(this@HesapActivity, userList)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}