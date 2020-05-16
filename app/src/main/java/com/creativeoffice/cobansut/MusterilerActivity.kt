package com.creativeoffice.cobansut

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.MusteriAdapter
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_musteriler.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MusterilerActivity : AppCompatActivity() {


    private val ACTIVITY_NO = 3
    var secilenMah: String? = null
    lateinit var musteriList: ArrayList<MusteriData>
    lateinit var musteriAdList: ArrayList<String>
    lateinit var dialogViewSpArama: View

    lateinit var dialogView: View


    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
     var kullaniciAdi: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteriler)
        setupNavigationView()
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        setupKullaniciAdi()
        musteriList = ArrayList()
        musteriAdList = ArrayList()


        setupBtn()
        setupSpinner()





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

    private fun setupVeri() {
        musteriList.clear()
        FirebaseDatabase.getInstance().reference.child("Musteriler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        try {
                            var gelenData = ds.getValue(MusteriData::class.java)!!
                            var musteriAdlari = gelenData.musteri_ad_soyad
                            musteriList.add(gelenData)
                            musteriAdList.add(musteriAdlari.toString())
                        } catch (e: Exception) {
                            FirebaseDatabase.getInstance().reference.child("Hatalar/musteriDataHata").push().setValue(e.message.toString())
                        }
                    }
                    var adapterSearch = ArrayAdapter<String>(this@MusterilerActivity, android.R.layout.simple_expandable_list_item_1, musteriAdList)
                    searchMs.setAdapter(adapterSearch)
                    tvMusteri.text = "Müşteriler " + "(" + (musteriList.size + 62) + ")"
                    setupRecyclerViewMusteriler()
                } else {
                    Toast.makeText(this@MusterilerActivity, "Müşteri Bilgisi Alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun setupSpinner() {
        var siraList = ArrayList<String>()
        siraList.add("İsme A -> Z")
        siraList.add("İsme Z -> A")
        siraList.add("Zamana")
        siraList.add("Zamana ters")



        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, siraList)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var secilenMarka = siraList[position]
                if (secilenMarka == "İsme A -> Z") {
                    musteriList.sortBy { it.musteri_ad_soyad }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                } else if (secilenMarka == "İsme Z -> A") {
                    musteriList.sortByDescending { it.musteri_ad_soyad }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)

                } else if (secilenMarka == "Zamana") {
                    musteriList.sortByDescending { it.siparis_son_zaman }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                } else if (secilenMarka == "Zamana ters") {
                    musteriList.sortBy { it.siparis_son_zaman }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                }
            }
        }
    }

    private fun setupRecyclerViewMusteriler() {
        rcMusteri.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = MusteriAdapter(this, musteriList, kullaniciAdi)
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
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)

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
                        musteriAdi = dialogView.etMusteriAdSoyad.text.toString().trim().capitalize()
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


                    var musteriBilgileri = MusteriData(musteriAdi, secilenMah, musteriAdres, musteriApt, musteriTel, null, false, null, null)

                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi.toString()).setValue(musteriBilgileri).addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                        setupVeri()
                    }

                }
            })

            var dialog: Dialog = builder.create()
            dialog.show()

        }

        imgMusteriAra.setOnClickListener {
            var arananMusteriAdi = searchMs.text.toString()
            //  Log.e("ass1",arananMusteriAdi)
            val arananMusteriVarMi = musteriAdList.containsAll(listOf(arananMusteriAdi))

            if (arananMusteriVarMi) {
                var ref = FirebaseDatabase.getInstance().reference
                ref.child("Musteriler").child(arananMusteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.hasChildren()) {
                            var musteriData = p0.getValue(MusteriData::class.java)!!

                            //    Log.e("ass", musteriData.musteri_apartman)


                            var builder: AlertDialog.Builder = AlertDialog.Builder(this@MusterilerActivity)
                            dialogViewSpArama = inflate(this@MusterilerActivity, R.layout.dialog_siparis_ekle, null)


                            dialogViewSpArama.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
                            var cal = Calendar.getInstance()
                            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, monthOfYear)
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                                val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                                val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                                dialogViewSpArama.tvZamanEkleDialog.text = sdf.format(cal.time)
                            }
                            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                cal.set(Calendar.MINUTE, minute)
                            }
                            dialogViewSpArama.tvZamanEkleDialog.setOnClickListener {
                                DatePickerDialog(this@MusterilerActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                TimePickerDialog(this@MusterilerActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                            }

                            dialogViewSpArama.et3lt.addTextChangedListener(watcherFiyat3lt)
                            dialogViewSpArama.et5lt.addTextChangedListener(watcherFiyat5lt)
                            dialogViewSpArama.etYumurta.addTextChangedListener(watcherFiyatYumurta)
                            dialogViewSpArama.imgFiyatRefresh.setOnClickListener {
                                var fiyat3 = 0
                                if (dialogViewSpArama.et3lt.text.isNotEmpty()) {
                                    fiyat3 = dialogViewSpArama.et3lt.text.toString().toInt() * 16
                                }

                                var fiyat5 = 0
                                if (dialogViewSpArama.et5lt.text.isNotEmpty()) {
                                    fiyat5 = dialogViewSpArama.et5lt.text.toString().toInt() * 22
                                }
                                var fiyatYum = 0
                                if (dialogViewSpArama.etYumurta.text.isNotEmpty()) {
                                    fiyatYum = dialogViewSpArama.etYumurta.text.toString().toInt() * 1
                                }
                                dialogViewSpArama.tvFiyatSp.text = (fiyat3 + fiyat5 + fiyatYum).toString()
                            }

                            dialogViewSpArama.swPromosyon.setOnClickListener {

                                if (dialogViewSpArama.swPromosyon.isChecked) {
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(true)
                                } else {
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(false)

                                }
                            }

                            dialogViewSpArama.swPromosyon.isChecked = musteriData.promosyon_verildimi.toString().toBoolean()



                            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.dismiss()
                                }

                            })
                            builder.setPositiveButton("Sipariş Ekle", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {

                                    var sut3lt = "0"
                                    if (dialogViewSpArama.et3lt.text.toString().isNotEmpty()) {
                                        sut3lt = dialogViewSpArama.et3lt.text.toString()
                                    }
                                    var sut5lt = "0"

                                    if (dialogViewSpArama.et5lt.text.toString().isNotEmpty()) {
                                        sut5lt = dialogViewSpArama.et5lt.text.toString()
                                    }
                                    var yumurta = "0"
                                    if (dialogViewSpArama.etYumurta.text.toString().isNotEmpty()) {
                                        yumurta = dialogViewSpArama.etYumurta.text.toString()
                                    }

                                    var siparisNotu = dialogViewSpArama.etSiparisNotu.text.toString()

                                    var siparisKey = FirebaseDatabase.getInstance().reference.child("Siparisler").push().key.toString()


                                    var siparisData = SiparisData(
                                        null,
                                        null,
                                        cal.timeInMillis,
                                        musteriData.musteri_adres,
                                        musteriData.musteri_apartman,
                                        musteriData.musteri_tel,
                                        musteriData.musteri_ad_soyad,
                                        musteriData.musteri_mah,
                                        siparisNotu,
                                        siparisKey,
                                        yumurta,
                                        sut3lt,
                                        sut5lt,
                                        musteriData.musteri_zkonum,
                                        musteriData.promosyon_verildimi,
                                        musteriData.musteri_zlat,
                                        musteriData.musteri_zlong,
                                        kullaniciAdi
                                    )
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisKey).setValue(siparisData)
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey)
                                        .child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey)
                                        .child("siparis_zamani").setValue(ServerValue.TIMESTAMP)


                                }
                            })

                            builder.setTitle(musteriData.musteri_ad_soyad)
                            builder.setIcon(R.drawable.cow)

                            builder.setView(dialogViewSpArama)
                            var dialog: Dialog = builder.create()
                            dialog.show()

                        }


                    }

                })


                //  Log.e("ass2","true")
            } else {
                Toast.makeText(this, "Böyle Bir Müşteri Yok", Toast.LENGTH_SHORT).show()
                //  Log.e("ass2", "Böyle Bir Müşteri Yok")
            }


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


    var watcherFiyat3lt = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 0) {

                dialogViewSpArama.tvFiyatSp.text = (dialogViewSpArama.tvFiyatSp.text.toString().toInt() + (s.toString().toInt() * 16)).toString()

            } else {

                dialogViewSpArama.tvFiyatSp.text = dialogViewSpArama.tvFiyatSp.text.toString()
            }
        }

    }

    var watcherFiyat5lt = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 0) {

                dialogViewSpArama.tvFiyatSp.text = (dialogViewSpArama.tvFiyatSp.text.toString().toInt() + (s.toString().toInt() * 22)).toString()

            } else {

                dialogViewSpArama.tvFiyatSp.text = dialogViewSpArama.tvFiyatSp.text.toString()
            }
        }

    }

    var watcherFiyatYumurta = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 0) {

                dialogViewSpArama.tvFiyatSp.text = (dialogViewSpArama.tvFiyatSp.text.toString().toInt() + (s.toString().toInt() * 1)).toString()

            } else {
                dialogViewSpArama.tvFiyatSp.text = dialogViewSpArama.tvFiyatSp.text.toString()
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
