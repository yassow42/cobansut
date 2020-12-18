package com.creativeoffice.cobansut.DigerActivityler

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.creativeoffice.cobansut.Datalar.Users
import com.creativeoffice.cobansut.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_register.view.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    var ref =  FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupAuthListener()
        FirebaseDatabase.getInstance().reference.child("users").keepSynced(true)

        mAuth = FirebaseAuth.getInstance()


        btnRegister.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_register, null)

            builder.setView(view)
            var dialog: Dialog = builder.create()

            view.btnRegisterAlertDialog.setOnClickListener {


                var kullaniciAdi = view.etKullaniciAdiLoginAlertDialog.text.toString()
                var kullaniciAdiEmail = kullaniciAdi + "@gmail.com"
                var kullaniciSifre = view.etSifreLoginAlertDialog.text.toString()
                var userNameKullanimi = false


              ref .child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for (ds in p0.children) {
                            val gelenKullanicilar = ds.getValue(Users::class.java)!!

                            if (gelenKullanicilar.user_name.equals(kullaniciAdi)) {
                                userNameKullanimi = true
                                break
                            } else {
                                userNameKullanimi = false
                            }
                        }


                        if (userNameKullanimi == true) {
                            Toast.makeText(this@LoginActivity, "Kullanıcı Adı Kullanımdadır.", Toast.LENGTH_LONG).show()
                        } else {
                            mAuth.createUserWithEmailAndPassword(kullaniciAdiEmail, kullaniciSifre).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                override fun onComplete(p0: Task<AuthResult>) {
                                    if (p0.isSuccessful) {
                                        setupAuthListener()

                                        var userID = mAuth.currentUser!!.uid.toString()

                                        var kaydedilecekUsers = Users(kullaniciAdiEmail, kullaniciSifre, kullaniciAdi, userID)
                                        FirebaseDatabase.getInstance().reference.child("users").child(userID).setValue(kaydedilecekUsers)
                                        dialog.dismiss()

                                    } else {
                                        mAuth.currentUser!!.delete().addOnCompleteListener(object : OnCompleteListener<Void> {
                                            override fun onComplete(p0: Task<Void>) {
                                                if (p0.isSuccessful) {
                                                    Toast.makeText(this@LoginActivity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        })
                                    }
                                }
                            })
                        }
                    }
                })
            }



            dialog.show()
        }


        btnLogin.setOnClickListener {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            var kullaniciAdi = etKullaniciAdiLogin.text.toString()
            var kullaniciAdiEmail = kullaniciAdi + "@gmail.com"
            var kullaniciSifre = etSifreLogin.text.toString()

            mAuth.signInWithEmailAndPassword(kullaniciAdiEmail, kullaniciSifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0!!.isSuccessful) {

                            setupAuthListener()

                        } else {
                            Toast.makeText(this@LoginActivity, " Kullanıcı Adı/Sifre Hatalı :", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

        if (mAuth.currentUser!=null){
            Log.e("giris yapılan hesap",mAuth.currentUser!!.uid.toString())
        }else{
            Log.e("cıkıs","cıkıs yapılmıs uid yok")
        }

    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                   var intent = Intent(this@LoginActivity, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                } else {

                }
            }
        }
    }

    override fun onStart() {
        mAuth.addAuthStateListener(mAuthListener)
        super.onStart()
    }


    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
