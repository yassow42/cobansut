package com.creativeoffice.cobansut

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.MusteriAdapter
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_musteriler.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.tvAdresTam
import java.util.*
import kotlin.collections.ArrayList

class MusterilerActivity : AppCompatActivity() {


    private val ACTIVITY_NO = 3
    var secilenMah: String? = null
    lateinit var musteriList: ArrayList<MusteriData>

    lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteriler)
        setupNavigationView()
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        musteriList = ArrayList()

        setupVeri()
        setupBtn()
        setupSpinner()
    }


    private fun setupVeri() {


        musteriList.clear()
        FirebaseDatabase.getInstance().reference.child("Musteriler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {

                    for (ds in p0.children) {
                        var gelenData = ds.getValue(MusteriData::class.java)!!
                        musteriList.add(gelenData)
                    }
                    tvMusteri.text = "Müşteriler " + "(" + (musteriList.size + 49) + ")"
                    setupRecyclerViewMusteriler()
                } else {
                    Toast.makeText(this@MusterilerActivity, "Müşteri Bilgisi Alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }


        })


    }


    private fun setupSpinner() {
        var markaList = ArrayList<String>()
        markaList.add("İsme göre sırala A -> Z")
        markaList.add("İsme göre sırala Z -> A")
        markaList.add("Zamana göre sırala")
        markaList.add("Zamana göre ters sırala")



        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, markaList)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var secilenMarka = markaList[position]
                if (secilenMarka == "İsme göre sırala A -> Z") {

                    musteriList.sortBy { it.musteri_ad_soyad }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                } else if (secilenMarka == "İsme göre sırala Z -> A") {
                    musteriList.sortByDescending { it.musteri_ad_soyad }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)

                } else if (secilenMarka == "Zamana göre sırala") {
                    musteriList.sortByDescending { it.siparis_son_zaman }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                } else if (secilenMarka == "Zamana göre ters sırala") {
                    musteriList.sortBy { it.siparis_son_zaman }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                }
            }
        }
    }

    private fun setupRecyclerViewMusteriler() {
        rcMusteri.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = MusteriAdapter(this, musteriList)
        rcMusteri.adapter = Adapter
        rcMusteri.setHasFixedSize(true)
    }

    private fun setupBtn() {
        imgMusteriEkle.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            dialogView = inflater.inflate(R.layout.dialog_musteri_ekle, null)

            builder.setView(dialogView)
            builder.setTitle("Müşteri Ekle")
//////////////////////spinner

            val mahalleler = ArrayList<String>()


            mahalleler.add("Market")
            mahalleler.add("8 Kasım")
            mahalleler.add("Atatürk")
            mahalleler.add("Barış")
            mahalleler.add("Cumhuriyet")
            mahalleler.add("Dere")
            mahalleler.add("Durak")
            mahalleler.add("Fatih")
            mahalleler.add("Gençlik")
            mahalleler.add("Gündoğu")
            mahalleler.add("Güneş")
            mahalleler.add("Hürriyet")
            mahalleler.add("İnönü")
            mahalleler.add("İstiklal")
            mahalleler.add("Kocasinan")
            mahalleler.add("Kurtuluş")
            mahalleler.add("Özerler")
            mahalleler.add("Sevgi")
            mahalleler.add("Siteler")
            mahalleler.add("Yeni")
            mahalleler.add("Yıldırım")
            mahalleler.add("Yıldız")
            mahalleler.add("Yılmaz")
            mahalleler.add("Zafer")


            var adapterMah = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mahalleler)
            adapterMah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogView.spnMahler.adapter = adapterMah
            dialogView.spnMahler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(this@MusterilerActivity, "Lütfen Mahalle Seç", Toast.LENGTH_LONG).show()
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    secilenMah = dialogView.spnMahler.selectedItem.toString()

                }

            }

//////////////////////spinner

            dialogView.etAdres.addTextChangedListener(watcherAdres)

            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Ekle", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {


                    var musteriAdi = "Müşteri"
                    if (!dialogView.etMusteriAdSoyad.text.toString().isNullOrEmpty()) {
                        musteriAdi = dialogView.etMusteriAdSoyad.text.toString().trim()
                    } else if (musteriAdi == "Müşteri") {
                        Toast.makeText(this@MusterilerActivity, "Müşteri Adı Girmedin", Toast.LENGTH_LONG).show()
                    }
                    var musteriAdres = "Adres"
                    if (!dialogView.etAdres.text.toString().isNullOrEmpty()) {
                        musteriAdres = dialogView.etAdres.text.toString().trim()
                    } else if (musteriAdres == "Adres") {
                        Toast.makeText(this@MusterilerActivity, "Adres Girmedin", Toast.LENGTH_LONG).show()
                    }

                    var musteriApt = "Apartman"
                    if (!dialogView.etApartman.text.toString().isNullOrEmpty()) {
                        musteriApt = dialogView.etApartman.text.toString().trim()
                    } else if (musteriAdres == "Adres") {
                        Toast.makeText(this@MusterilerActivity, "Adres Girmedin", Toast.LENGTH_LONG).show()
                    }


                    var musteriTel = "Tel"
                    if (!dialogView.etTelefon.text.toString().isNullOrEmpty()) {
                        musteriTel = dialogView.etTelefon.text.toString()
                    } else if (musteriTel == "Tel") {
                        Toast.makeText(this@MusterilerActivity, "Tel Girmedin", Toast.LENGTH_LONG).show()
                    }


                    var musteriBilgileri = MusteriData(musteriAdi, secilenMah, musteriAdres, musteriApt, musteriTel, null)

                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi.toString()).setValue(musteriBilgileri).addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                        setupVeri()
                    }

                }
            })

            var dialog: Dialog = builder.create()
            dialog.show()

        }
    }


    var watcherAdres = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 0) {

                dialogView.tvAdresTam.text = secilenMah + " mahallesi " + s.toString()

            } else {

                dialogView.tvAdresTam.text = "Sadece Sokak ve No Girilecek  Örnek: Topuz Siteleri Sokak No 5 "
            }
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
