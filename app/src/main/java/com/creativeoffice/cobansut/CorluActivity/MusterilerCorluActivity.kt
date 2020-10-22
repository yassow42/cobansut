package com.creativeoffice.cobansut.CorluActivity

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.MusteriAdapter
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.CorluAdapter.MusteriCorluAdapter
import com.creativeoffice.cobansut.Datalar.MusteriData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelperCorlu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_musteriler_corlu.imgMusteriAra
import kotlinx.android.synthetic.main.activity_musteriler_corlu.imgMusteriEkle
import kotlinx.android.synthetic.main.activity_musteriler_corlu.rcMusteri
import kotlinx.android.synthetic.main.activity_musteriler_corlu.searchMs
import kotlinx.android.synthetic.main.activity_musteriler_corlu.spinner
import kotlinx.android.synthetic.main.activity_musteriler_corlu.tvMusteri
import kotlinx.android.synthetic.main.activity_siparis_corlu.bottomNav
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et3lt
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et3ltFiyat
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et5lt
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.et5ltFiyat
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.etSiparisNotu
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.etYumurta
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.etYumurtaFiyat
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.swPromosyon
import kotlinx.android.synthetic.main.dialog_siparis_ekle_corlu.view.tvZamanEkleDialog
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusterilerCorluActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 4
    var secilenMah: String? = null
    lateinit var musteriList: ArrayList<MusteriData>
    lateinit var musteriAdList: ArrayList<String>
    lateinit var dialogViewSpArama: View
    lateinit var dialogView: View

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
    var kullaniciAdi: String? = null

    val ref = FirebaseDatabase.getInstance().reference
    val refCorlu = FirebaseDatabase.getInstance().reference.child("Corlu")

    val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteriler_corlu)
        setupNavigationView()
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        setupKullaniciAdi()
        musteriList = ArrayList()
        musteriAdList = ArrayList()

        handler.postDelayed({ setupVeri() }, 1000)
        setupBtn()
        setupSpinner()
        setupVeri()

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
            mahalleler.add("Alipaşa")
            mahalleler.add("Cemaliye")
            mahalleler.add("Çoban Çeşme")
            mahalleler.add("Cumhuriyet")
            mahalleler.add("Dere")
            mahalleler.add("Esentepe")
            mahalleler.add("Hatip")
            mahalleler.add("Havuzlar")
            mahalleler.add("Hıdırağa")
            mahalleler.add("Hürriyet")
            mahalleler.add("İnönü")
            mahalleler.add("Kazımiye")
            mahalleler.add("Kemalettin")
            mahalleler.add("Muhittin")
            mahalleler.add("Nusratiye")
            mahalleler.add("Reşadiye")
            mahalleler.add("Rumeli")
            mahalleler.add("Şeyh Sinan")
            mahalleler.add("Silahtarağa")
            mahalleler.add("Zafer")


            var adapterMah = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mahalleler)
            adapterMah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogView.spnMahler.adapter = adapterMah
            dialogView.spnMahler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(this@MusterilerCorluActivity, "Lütfen Mahalle Seç", Toast.LENGTH_LONG).show()
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
                    }
                    var musteriAdres = "Adres"
                    if (!dialogView.etAdres.text.toString().isNullOrEmpty()) {
                        musteriAdres = dialogView.etAdres.text.toString().trim()
                    }
                    var musteriApt = "Apartman"
                    if (!dialogView.etApartman.text.toString().isNullOrEmpty()) {
                        musteriApt = dialogView.etApartman.text.toString().trim()
                    }

                    var musteriTel = "Tel"
                    if (!dialogView.etTelefon.text.toString().isNullOrEmpty()) {
                        musteriTel = dialogView.etTelefon.text.toString()
                    }

                    var musteriBilgileri = MusteriData(musteriAdi, secilenMah, musteriAdres, musteriApt, musteriTel, null, false, null, null)

                    refCorlu.child("Musteriler").child(musteriAdi).setValue(musteriBilgileri).addOnCompleteListener {
                        refCorlu.child("Musteriler").child(musteriAdi).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                        setupVeri()
                    }

                }
            })

            var dialog: Dialog = builder.create()
            dialog.show()

        }

        imgMusteriAra.setOnClickListener {
            var arananMusteriAdi = searchMs.text.toString()
            val arananMusteriVarMi = musteriAdList.containsAll(listOf(arananMusteriAdi))

            if (arananMusteriVarMi) {
                refCorlu.child("Musteriler").child(arananMusteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.hasChildren()) {
                            var musteriData = p0.getValue(MusteriData::class.java)!!
                            var builder: AlertDialog.Builder = AlertDialog.Builder(this@MusterilerCorluActivity)
                            dialogViewSpArama = View.inflate(this@MusterilerCorluActivity, R.layout.dialog_siparis_ekle_corlu, null)
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
                                DatePickerDialog(this@MusterilerCorluActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                TimePickerDialog(this@MusterilerCorluActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                            }



                            dialogViewSpArama.swPromosyon.setOnClickListener {

                                if (dialogViewSpArama.swPromosyon.isChecked) {
                                    refCorlu.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(true)
                                } else {
                                    refCorlu.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("promosyon_verildimi").setValue(false)

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

                                    var sut3ltAdet = "0"
                                    if (dialogViewSpArama.et3lt.text.toString().isNotEmpty()) {
                                        sut3ltAdet = dialogViewSpArama.et3lt.text.toString()
                                    }
                                    var sut5ltAdet = "0"

                                    if (dialogViewSpArama.et5lt.text.toString().isNotEmpty()) {
                                        sut5ltAdet = dialogViewSpArama.et5lt.text.toString()
                                    }
                                    var yumurtaAdet = "0"
                                    if (dialogViewSpArama.etYumurta.text.toString().isNotEmpty()) {
                                        yumurtaAdet = dialogViewSpArama.etYumurta.text.toString()
                                    }

                                    var dokmeSutAdet = "0"
                                    if (dialogViewSpArama.etDokmeSut.text.toString().isNotEmpty()) {
                                        dokmeSutAdet = dialogViewSpArama.etDokmeSut.text.toString()
                                    }

                                    var siparisNotu = dialogViewSpArama.etSiparisNotu.text.toString()
                                    var siparisKey = refCorlu.child("Siparisler").push().key.toString()

                                    var sut3ltFiyat = dialogViewSpArama.et3ltFiyat.text.toString().toDouble()
                                    var sut5ltFiyat = dialogViewSpArama.et5ltFiyat.text.toString().toDouble()
                                    var yumurtaFiyat = dialogViewSpArama.etYumurtaFiyat.text.toString().toDouble()
                                    var dokmeSutFiyat = dialogViewSpArama.etDokmeSutFiyat.text.toString().toDouble()

                                    var toplamFiyat = (sut3ltAdet.toDouble() * sut3ltFiyat) + (sut5ltAdet.toDouble() * sut5ltFiyat) + (yumurtaAdet.toDouble() * yumurtaFiyat) + (dokmeSutAdet.toDouble() * dokmeSutFiyat)


                                    var siparisData = SiparisData(
                                        System.currentTimeMillis(), System.currentTimeMillis(), cal.timeInMillis, musteriData.musteri_adres, musteriData.musteri_apartman,
                                        musteriData.musteri_tel, musteriData.musteri_ad_soyad, musteriData.musteri_mah, siparisNotu, siparisKey, yumurtaAdet, yumurtaFiyat, sut3ltAdet, sut3ltFiyat,
                                        sut5ltAdet, sut5ltFiyat, dokmeSutAdet,dokmeSutFiyat, toplamFiyat, musteriData.musteri_zkonum, musteriData.promosyon_verildimi, musteriData.musteri_zlat,
                                        musteriData.musteri_zlong, kullaniciAdi
                                    )
                                    refCorlu.child("Siparisler").child(musteriData.musteri_mah.toString()).child(siparisKey).child("toplam_fiyat").setValue(0.0)
                                    refCorlu.child("Siparisler").child(musteriData.musteri_mah.toString()).child(siparisKey).setValue(siparisData)
                                //    refCorlu.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                                //    refCorlu.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                    refCorlu.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                                    refCorlu.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                    refCorlu.child("Musteriler").child(musteriData.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)


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

    private fun setupVeri() {
        musteriList.clear()
        refCorlu.child("Musteriler").addListenerForSingleValueEvent(object : ValueEventListener {
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
                            refCorlu.child("Hatalar/musteriDataHata").push().setValue(e.message.toString())
                        }
                    }
                    var adapterSearch = ArrayAdapter<String>(this@MusterilerCorluActivity, android.R.layout.simple_expandable_list_item_1, musteriAdList)
                    searchMs.setAdapter(adapterSearch)
                    tvMusteri.text = "Müşteriler " + "(" + (musteriList.size) + ")"
                    setupRecyclerViewMusteriler()
                } else {
                    Toast.makeText(this@MusterilerCorluActivity, "Müşteri Bilgisi Alınamadı.", Toast.LENGTH_SHORT).show()
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
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerCorluActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerCorluActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                } else if (secilenMarka == "İsme Z -> A") {
                    musteriList.sortByDescending { it.musteri_ad_soyad }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerCorluActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerCorluActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)

                } else if (secilenMarka == "Zamana") {
                    musteriList.sortByDescending { it.siparis_son_zaman }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerCorluActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerCorluActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                } else if (secilenMarka == "Zamana ters") {
                    musteriList.sortBy { it.siparis_son_zaman }
                    rcMusteri.layoutManager = LinearLayoutManager(this@MusterilerCorluActivity, LinearLayoutManager.VERTICAL, false)
                    val Adapter = MusteriAdapter(this@MusterilerCorluActivity, musteriList, kullaniciAdi)
                    rcMusteri.adapter = Adapter
                    rcMusteri.setHasFixedSize(true)
                }
            }
        }
    }

    private fun setupRecyclerViewMusteriler() {
        rcMusteri.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = MusteriCorluAdapter(this, musteriList, kullaniciAdi)
        rcMusteri.adapter = Adapter
        rcMusteri.setHasFixedSize(true)
    }

    fun setupNavigationView() {

        BottomNavigationViewHelperCorlu.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelperCorlu.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    fun setupKullaniciAdi() {
        ref.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.child("user_name").value.toString()?.let {
                    kullaniciAdi = it
                }
            }

        })
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


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
    }
}