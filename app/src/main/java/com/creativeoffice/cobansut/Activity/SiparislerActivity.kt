package com.creativeoffice.cobansut.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Adapter.SiparisAdapter
import com.creativeoffice.cobansut.BolgeSecimActivity
import com.creativeoffice.cobansut.utils.BottomNavigationViewHelper
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.LoginActivity
import com.creativeoffice.cobansut.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_siparisler.*

class SiparislerActivity : AppCompatActivity() {
    lateinit var marketlist: ArrayList<SiparisData>
    lateinit var kurtulusList: ArrayList<SiparisData>
    lateinit var genclikList: ArrayList<SiparisData>
    lateinit var kasim8List: ArrayList<SiparisData>
    lateinit var Ataturklist: ArrayList<SiparisData>
    lateinit var barislist: ArrayList<SiparisData>
    lateinit var cumhuriyetlist: ArrayList<SiparisData>
    lateinit var derelist: ArrayList<SiparisData>
    lateinit var duraklist: ArrayList<SiparisData>
    lateinit var fatihlist: ArrayList<SiparisData>
    lateinit var gunlist: ArrayList<SiparisData>
    lateinit var guneslist: ArrayList<SiparisData>
    lateinit var hurriyetlist: ArrayList<SiparisData>
    lateinit var inonulist: ArrayList<SiparisData>
    lateinit var istiklallist: ArrayList<SiparisData>
    lateinit var kocasinanlist: ArrayList<SiparisData>
    lateinit var ozerlerlist: ArrayList<SiparisData>
    lateinit var sevgilist: ArrayList<SiparisData>
    lateinit var sitelerlist: ArrayList<SiparisData>
    lateinit var yenilist: ArrayList<SiparisData>
    lateinit var yildirimlist: ArrayList<SiparisData>
    lateinit var yildizlist: ArrayList<SiparisData>
    lateinit var yilmazlist: ArrayList<SiparisData>
    lateinit var zaferlist: ArrayList<SiparisData>
    lateinit var ilerilist: ArrayList<SiparisData>

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
    lateinit var kullaniciAdi: String
    private val ACTIVITY_NO = 0

    lateinit var progressDialog: ProgressDialog
    var hndler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siparisler)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
    //    mAuth.signOut()
        initMyAuthStateListener()
        userID = mAuth.currentUser!!.uid
        setupKullaniciAdi()
        setupListeler()
        setupNavigationView()
        setupBtn()
        zamanAyarı()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        
        hndler.postDelayed(Runnable { setupVeri() }, 750)
        hndler.postDelayed(Runnable { progressDialog.dismiss() }, 5000)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,BolgeSecimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
    private fun zamanAyarı() {
        FirebaseMessaging.getInstance().subscribeToTopic("msgNotification");
        FirebaseDatabase.getInstance().reference.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                var gece3 = p0.child("gece3").value.toString().toLong()
                var suankıZaman = System.currentTimeMillis()

                if (gece3 < suankıZaman) {

                    var guncelGece3 = gece3 + 86400000

                    FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3).addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("Zaman").child("gerigece3").setValue(gece3)
                    }

                }
            }
        })
    }

    private fun setupVeri() {

        var sut3ltSayisi = 0
        var sut5ltSayisi = 0
        var yumurtaSayisi = 0
        var toplamFiyatlar = 0.0

        var ref = FirebaseDatabase.getInstance().reference
        ref.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        try {
                            var gelenData = ds.getValue(SiparisData::class.java)!!
                            if (gelenData.sut3lt != null && gelenData.sut5lt != null && gelenData.yumurta != null) {
                                sut3ltSayisi = gelenData.sut3lt!!.toInt() + sut3ltSayisi
                                sut5ltSayisi = gelenData.sut5lt!!.toInt() + sut5ltSayisi
                                yumurtaSayisi = gelenData.yumurta!!.toInt() + yumurtaSayisi
                                toplamFiyatlar = gelenData.toplam_fiyat!!.toDouble() + toplamFiyatlar
                            }


                            if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {

                                if (gelenData.siparis_mah == "Market") {
                                    marketlist.add(gelenData)
                                    marketlist.sortBy { it.siparis_adres }
                                    tvMarketSayi.text = marketlist.size.toString() + " Sipariş"
                                    recyclerView(rcMarket, marketlist)

                                    if (marketlist.size > 0) {
                                        clMarket.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                }
                                if (gelenData.siparis_mah == "Kurtuluş") {
                                    kurtulusList.add(gelenData)
                                    kurtulusList.sortBy { it.siparis_adres }
                                    tvKurtulusSayi.text = kurtulusList.size.toString() + " Sipariş"
                                    if (kurtulusList.size > 0) {
                                        clKurtulus.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcKurtulus, kurtulusList)
                                }
                                if (gelenData.siparis_mah == "Gençlik") {
                                    genclikList.add(gelenData)
                                    tvGenclikSayi.text = genclikList.size.toString() + " Sipariş"

                                    if (genclikList.size > 0) {
                                        clGenclik.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }

                                    recyclerView(rcGenclik, genclikList)
                                }
                                if (gelenData.siparis_mah == "8 Kasım") {
                                    kasim8List.add(gelenData)
                                    tvKasimSayi.text = kasim8List.size.toString() + " Sipariş"
                                    if (kasim8List.size > 0) {
                                        cl8Kasim.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rc8Kasim, kasim8List)
                                }
                                if (gelenData.siparis_mah == "Atatürk") {
                                    Ataturklist.add(gelenData)
                                    tvAtaturkSayi.text = Ataturklist.size.toString() + " Sipariş"
                                    if (Ataturklist.size > 0) {
                                        clAtaturk.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcAtaturk, Ataturklist)
                                }
                                if (gelenData.siparis_mah == "Barış") {
                                    barislist.add(gelenData)
                                    tvBarisSayi.text = barislist.size.toString() + " Sipariş"
                                    if (barislist.size > 0) {
                                        clBaris.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcBaris, barislist)
                                }
                                if (gelenData.siparis_mah == "Cumhuriyet") {
                                    cumhuriyetlist.add(gelenData)
                                    tvCumhuriyetSayi.text = cumhuriyetlist.size.toString() + " Sipariş"
                                    if (cumhuriyetlist.size > 0) {
                                        clCumhuriyet.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcCumhuriyet, cumhuriyetlist)
                                }
                                if (gelenData.siparis_mah == "Dere") {
                                    derelist.add(gelenData)
                                    tvDereSayi.text = derelist.size.toString() + " Sipariş"
                                    if (derelist.size > 0) {
                                        clDere.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcDere, derelist)
                                }

                                if (gelenData.siparis_mah == "Durak") {
                                    duraklist.add(gelenData)
                                    tvDurakSayi.text = duraklist.size.toString() + " Sipariş"
                                    if (duraklist.size > 0) {
                                        clDurak.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcDurak, duraklist)
                                }
                                if (gelenData.siparis_mah == "Fatih") {
                                    fatihlist.add(gelenData)
                                    tvFatihSayi.text = fatihlist.size.toString() + " Sipariş"
                                    if (fatihlist.size > 0) {
                                        clFatih.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcFatih, fatihlist)
                                }

                                if (gelenData.siparis_mah == "Gündoğu") {
                                    gunlist.add(gelenData)
                                    tvGunSayi.text = gunlist.size.toString() + " Sipariş"
                                    if (gunlist.size > 0) {
                                        clGun.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcGun, gunlist)
                                }


                                if (gelenData.siparis_mah == "Güneş") {
                                    guneslist.add(gelenData)
                                    tvGunesSayi.text = guneslist.size.toString() + " Sipariş"
                                    if (guneslist.size > 0) {
                                        clGunes.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcGunes, guneslist)
                                }


                                if (gelenData.siparis_mah == "Hürriyet") {
                                    hurriyetlist.add(gelenData)
                                    tvHurriyetSayi.text = hurriyetlist.size.toString() + " Sipariş"
                                    if (hurriyetlist.size > 0) {
                                        clHurriyet.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcHurriyet, hurriyetlist)
                                }

                                if (gelenData.siparis_mah == "İnönü") {
                                    inonulist.add(gelenData)
                                    tvInonuSayi.text = inonulist.size.toString() + " Sipariş"
                                    if (inonulist.size > 0) {
                                        clinonu.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcInonu, inonulist)
                                }

                                if (gelenData.siparis_mah == "İstiklal") {
                                    istiklallist.add(gelenData)
                                    tvIstiklalSayi.text = istiklallist.size.toString() + " Sipariş"
                                    if (istiklallist.size > 0) {
                                        clIstıklal.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcIstiklal, istiklallist)
                                }

                                if (gelenData.siparis_mah == "Kocasinan") {
                                    kocasinanlist.add(gelenData)
                                    tvKocasinanSayi.text = kocasinanlist.size.toString() + " Sipariş"
                                    if (kocasinanlist.size > 0) {
                                        clKocasinan.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcKocasinan, kocasinanlist)
                                }
                                if (gelenData.siparis_mah == "Özerler") {
                                    ozerlerlist.add(gelenData)
                                    tvOzerlerSayi.text = ozerlerlist.size.toString() + " Sipariş"
                                    if (ozerlerlist.size > 0) {
                                        clOzerler.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcOzerler, ozerlerlist)
                                }
                                if (gelenData.siparis_mah == "Sevgi") {
                                    sevgilist.add(gelenData)
                                    tvSevgiSayi.text = sevgilist.size.toString() + " Sipariş"
                                    if (sevgilist.size > 0) {
                                        clSevgi.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcSevgi, sevgilist)
                                }
                                if (gelenData.siparis_mah == "Siteler") {
                                    sitelerlist.add(gelenData)
                                    tvSitelerSayi.text = sitelerlist.size.toString() + " Sipariş"
                                    if (sitelerlist.size > 0) {
                                        clSiteler.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcSiteler, sitelerlist)
                                }
                                if (gelenData.siparis_mah == "Yeni") {
                                    yenilist.add(gelenData)
                                    tvYeniSayi.text = yenilist.size.toString() + " Sipariş"
                                    if (yenilist.size > 0) {
                                        clYeni.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcYeni, yenilist)
                                }
                                if (gelenData.siparis_mah == "Yıldırım") {
                                    yildirimlist.add(gelenData)
                                    tvYildirimSayi.text = yildirimlist.size.toString() + " Sipariş"
                                    if (yildirimlist.size > 0) {
                                        clYildirim.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcYildirim, yildirimlist)
                                }
                                if (gelenData.siparis_mah == "Yıldız") {
                                    yildizlist.add(gelenData)
                                    tvYildizSayi.text = yildizlist.size.toString() + " Sipariş"
                                    if (yildizlist.size > 0) {
                                        clYildiz.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcYildiz, yildizlist)
                                }
                                if (gelenData.siparis_mah == "Yılmaz") {
                                    yilmazlist.add(gelenData)
                                    tvYilmazSayi.text = yilmazlist.size.toString() + " Sipariş"
                                    if (yilmazlist.size > 0) {
                                        clYilmaz.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcYilmaz, yilmazlist)
                                }
                                if (gelenData.siparis_mah == "Zafer") {
                                    zaferlist.add(gelenData)
                                    tvZaferSayi.text = zaferlist.size.toString() + " Sipariş"
                                    if (zaferlist.size > 0) {
                                        clZafer.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcZafer, zaferlist)
                                }


                            } else if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == 1) {
                                ilerilist.add(gelenData)
                                tvileriTarihliSayi.text = ilerilist.size.toString() + " Sipariş"

                                recyclerView(rcileriTarih,ilerilist)
                                //recyclerViewileriTarihli()
                            }
                        } catch (e: Exception) {
                            ref.child("Hatalar/siparisDataHata").push().setValue(e.message.toString())
                        }
                    }
                    progressDialog.dismiss()
                    tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                    tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                    tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()

                    tvFiyatGenel.text = toplamFiyatlar.toString() + " TL"

                } else {
                    progressDialog.setMessage("Sipariş yok :(")
                    hndler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
                    Toast.makeText(this@SiparislerActivity, "Sipariş yok :(", Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    private fun setupBtn() {

        imgCikis.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }




        imgMarketDown.setOnClickListener {
            imgMarketDown.visibility = View.GONE
            imgMarketUp.visibility = View.VISIBLE
            rcMarket.visibility = View.VISIBLE
        }
        imgMarketUp.setOnClickListener {
            imgMarketDown.visibility = View.VISIBLE
            imgMarketUp.visibility = View.GONE
            rcMarket.visibility = View.GONE
        }

        //////////////////////////
        imgKurtulusDown.setOnClickListener {
            imgKurtulusDown.visibility = View.GONE
            imgKurtulusUp.visibility = View.VISIBLE
            rcKurtulus.visibility = View.VISIBLE
        }
        imgKurtulusUp.setOnClickListener {
            imgKurtulusDown.visibility = View.VISIBLE
            imgKurtulusUp.visibility = View.GONE
            rcKurtulus.visibility = View.GONE
        }

        //////////////////////////
        imgGenclikDown.setOnClickListener {
            imgGenclikDown.visibility = View.GONE
            imgGenclikUp.visibility = View.VISIBLE
            rcGenclik.visibility = View.VISIBLE
        }
        imgGenclikUp.setOnClickListener {
            imgGenclikDown.visibility = View.VISIBLE
            imgGenclikUp.visibility = View.GONE
            rcGenclik.visibility = View.GONE
        }

        //////////////////////////
        imgKasimDown.setOnClickListener {
            imgKasimDown.visibility = View.GONE
            imgKasimUp.visibility = View.VISIBLE
            rc8Kasim.visibility = View.VISIBLE
        }
        imgKasimUp.setOnClickListener {
            imgKasimDown.visibility = View.VISIBLE
            imgKasimUp.visibility = View.GONE
            rc8Kasim.visibility = View.GONE
        }
        //////////////////////////
        imgAtaturkDown.setOnClickListener {
            imgAtaturkDown.visibility = View.GONE
            imgAtaturkUp.visibility = View.VISIBLE
            rcAtaturk.visibility = View.VISIBLE
        }
        imgAtaturkUp.setOnClickListener {
            imgAtaturkDown.visibility = View.VISIBLE
            imgAtaturkUp.visibility = View.GONE
            rcAtaturk.visibility = View.GONE
        }

        //////////////////////////
        imgileriDown.setOnClickListener {
            imgileriDown.visibility = View.GONE
            imgileriUp.visibility = View.VISIBLE
            rcileriTarih.visibility = View.VISIBLE
        }
        imgileriUp.setOnClickListener {
            imgileriDown.visibility = View.VISIBLE
            imgileriUp.visibility = View.GONE
            rcileriTarih.visibility = View.GONE
        }

        //////////////////////////

        imgBarisDown.setOnClickListener {
            imgBarisDown.visibility = View.GONE
            imgBarisUp.visibility = View.VISIBLE
            rcBaris.visibility = View.VISIBLE
        }
        imgBarisUp.setOnClickListener {
            imgBarisDown.visibility = View.VISIBLE
            imgBarisUp.visibility = View.GONE
            rcBaris.visibility = View.GONE
        }

        //////////////////////////

        imgCumhuriyetDown.setOnClickListener {
            imgCumhuriyetDown.visibility = View.GONE
            imgCumhuriyetUp.visibility = View.VISIBLE
            rcCumhuriyet.visibility = View.VISIBLE
        }
        imgCumhuriyetUp.setOnClickListener {
            imgCumhuriyetDown.visibility = View.VISIBLE
            imgCumhuriyetUp.visibility = View.GONE
            rcCumhuriyet.visibility = View.GONE
        }

        //////////////////////////

        imgDereDown.setOnClickListener {
            imgDereDown.visibility = View.GONE
            imgDereUp.visibility = View.VISIBLE
            rcDere.visibility = View.VISIBLE
        }
        imgDereUp.setOnClickListener {
            imgDereDown.visibility = View.VISIBLE
            imgDereUp.visibility = View.GONE
            rcDere.visibility = View.GONE
        }

        //////////////////////////
        imgDurakDown.setOnClickListener {
            imgDurakDown.visibility = View.GONE
            imgDurakUp.visibility = View.VISIBLE
            rcDurak.visibility = View.VISIBLE
        }
        imgDurakUp.setOnClickListener {
            imgDurakDown.visibility = View.VISIBLE
            imgDurakUp.visibility = View.GONE
            rcDurak.visibility = View.GONE
        }

        //////////////////////////

        imgFatihDown.setOnClickListener {
            imgFatihDown.visibility = View.GONE
            imgFatihUp.visibility = View.VISIBLE
            rcFatih.visibility = View.VISIBLE
        }
        imgFatihUp.setOnClickListener {
            imgFatihDown.visibility = View.VISIBLE
            imgFatihUp.visibility = View.GONE
            rcFatih.visibility = View.GONE
        }

        //////////////////////////

        imgGunDown.setOnClickListener {
            imgGunDown.visibility = View.GONE
            imgGunUp.visibility = View.VISIBLE
            rcGun.visibility = View.VISIBLE
        }
        imgGunUp.setOnClickListener {
            imgGunDown.visibility = View.VISIBLE
            imgGunUp.visibility = View.GONE
            rcGun.visibility = View.GONE
        }

        //////////////////////////

        imgGunesDown.setOnClickListener {
            imgGunesDown.visibility = View.GONE
            imgGunesUp.visibility = View.VISIBLE
            rcGunes.visibility = View.VISIBLE
        }
        imgGunesUp.setOnClickListener {
            imgGunesDown.visibility = View.VISIBLE
            imgGunesUp.visibility = View.GONE
            rcGunes.visibility = View.GONE
        }

        //////////////////////////

        imgGunDown.setOnClickListener {
            imgGunDown.visibility = View.GONE
            imgGunUp.visibility = View.VISIBLE
            rcGun.visibility = View.VISIBLE
        }
        imgGunUp.setOnClickListener {
            imgGunDown.visibility = View.VISIBLE
            imgGunUp.visibility = View.GONE
            rcGun.visibility = View.GONE
        }

        //////////////////////////

        imgHurriyetDown.setOnClickListener {
            imgHurriyetDown.visibility = View.GONE
            imgHurriyetUp.visibility = View.VISIBLE
            rcHurriyet.visibility = View.VISIBLE
        }
        imgHurriyetUp.setOnClickListener {
            imgHurriyetDown.visibility = View.VISIBLE
            imgHurriyetUp.visibility = View.GONE
            rcHurriyet.visibility = View.GONE
        }

        //////////////////////////

        imgInonuDown.setOnClickListener {
            imgInonuDown.visibility = View.GONE
            imgInonuUp.visibility = View.VISIBLE
            rcInonu.visibility = View.VISIBLE
        }
        imgInonuUp.setOnClickListener {
            imgInonuDown.visibility = View.VISIBLE
            imgInonuUp.visibility = View.GONE
            rcInonu.visibility = View.GONE
        }

        //////////////////////////


        imgIstiklalDown.setOnClickListener {
            imgIstiklalDown.visibility = View.GONE
            imgIstiklalUp.visibility = View.VISIBLE
            rcIstiklal.visibility = View.VISIBLE
        }
        imgIstiklalUp.setOnClickListener {
            imgIstiklalDown.visibility = View.VISIBLE
            imgIstiklalUp.visibility = View.GONE
            rcIstiklal.visibility = View.GONE
        }

        //////////////////////////


        imgKocasinanDown.setOnClickListener {
            imgKocasinanDown.visibility = View.GONE
            imgKocasinanUp.visibility = View.VISIBLE
            rcKocasinan.visibility = View.VISIBLE
        }
        imgKocasinanUp.setOnClickListener {
            imgKocasinanDown.visibility = View.VISIBLE
            imgKocasinanUp.visibility = View.GONE
            rcKocasinan.visibility = View.GONE
        }

        //////////////////////////
        imgOzerlerDown.setOnClickListener {
            imgOzerlerDown.visibility = View.GONE
            imgOzerlerUp.visibility = View.VISIBLE
            rcOzerler.visibility = View.VISIBLE
        }
        imgOzerlerUp.setOnClickListener {
            imgOzerlerDown.visibility = View.VISIBLE
            imgOzerlerUp.visibility = View.GONE
            rcOzerler.visibility = View.GONE
        }

        //////////////////////////

        imgSevgiDown.setOnClickListener {
            imgSevgiDown.visibility = View.GONE
            imgSevgiUp.visibility = View.VISIBLE
            rcSevgi.visibility = View.VISIBLE
        }
        imgSevgiUp.setOnClickListener {
            imgSevgiDown.visibility = View.VISIBLE
            imgSevgiUp.visibility = View.GONE
            rcSevgi.visibility = View.GONE
        }

        //////////////////////////
        imgSitelerDown.setOnClickListener {
            imgSitelerDown.visibility = View.GONE
            imgSitelerUp.visibility = View.VISIBLE
            rcSiteler.visibility = View.VISIBLE
        }
        imgSitelerUp.setOnClickListener {
            imgSitelerDown.visibility = View.VISIBLE
            imgSitelerUp.visibility = View.GONE
            rcSiteler.visibility = View.GONE
        }

        //////////////////////////


        imgYeniDown.setOnClickListener {
            imgYeniDown.visibility = View.GONE
            imgGunUp.visibility = View.VISIBLE
            rcYeni.visibility = View.VISIBLE
        }
        imgYeniUp.setOnClickListener {
            imgYeniDown.visibility = View.VISIBLE
            imgYeniUp.visibility = View.GONE
            rcYeni.visibility = View.GONE
        }

        //////////////////////////


        imgYeniDown.setOnClickListener {
            imgYeniDown.visibility = View.GONE
            imgYeniUp.visibility = View.VISIBLE
            rcYeni.visibility = View.VISIBLE
        }
        imgYeniUp.setOnClickListener {
            imgYeniDown.visibility = View.VISIBLE
            imgYeniUp.visibility = View.GONE
            rcYeni.visibility = View.GONE
        }

        //////////////////////////


        imgYildirimDown.setOnClickListener {
            imgYildirimDown.visibility = View.GONE
            imgYildirimUp.visibility = View.VISIBLE
            rcYildirim.visibility = View.VISIBLE
        }
        imgYildirimUp.setOnClickListener {
            imgYildirimDown.visibility = View.VISIBLE
            imgYildirimUp.visibility = View.GONE
            rcYildirim.visibility = View.GONE
        }

        //////////////////////////

        imgYildizDown.setOnClickListener {
            imgYildizDown.visibility = View.GONE
            imgYildizUp.visibility = View.VISIBLE
            rcYildiz.visibility = View.VISIBLE
        }
        imgYildizUp.setOnClickListener {
            imgYildizDown.visibility = View.VISIBLE
            imgYildizUp.visibility = View.GONE
            rcYildiz.visibility = View.GONE
        }

        //////////////////////////

        imgYilmazDown.setOnClickListener {
            imgYilmazDown.visibility = View.GONE
            imgYilmazUp.visibility = View.VISIBLE
            rcYilmaz.visibility = View.VISIBLE
        }
        imgYilmazUp.setOnClickListener {
            imgYilmazDown.visibility = View.VISIBLE
            imgYilmazUp.visibility = View.GONE
            rcYilmaz.visibility = View.GONE
        }

        //////////////////////////

        imgZaferDown.setOnClickListener {
            imgZaferDown.visibility = View.GONE
            imgZaferUp.visibility = View.VISIBLE
            rcZafer.visibility = View.VISIBLE
        }
        imgZaferUp.setOnClickListener {
            imgZaferDown.visibility = View.VISIBLE
            imgZaferUp.visibility = View.GONE
            rcZafer.visibility = View.GONE
        }

        //////////////////////////


    }

    private fun setupListeler() {


        marketlist = ArrayList()
        kurtulusList = ArrayList()
        genclikList = ArrayList()
        kasim8List = ArrayList()
        Ataturklist = ArrayList()
        barislist = ArrayList()
        cumhuriyetlist = ArrayList()
        derelist = ArrayList()
        duraklist = ArrayList()
        fatihlist = ArrayList()
        guneslist = ArrayList()
        gunlist = ArrayList()
        hurriyetlist = ArrayList()
        inonulist = ArrayList()
        istiklallist = ArrayList()
        kocasinanlist = ArrayList()
        ozerlerlist = ArrayList()
        sevgilist = ArrayList()
        sitelerlist = ArrayList()
        yenilist = ArrayList()
        yildirimlist = ArrayList()
        yildizlist = ArrayList()
        yilmazlist = ArrayList()
        zaferlist = ArrayList()
        ilerilist = ArrayList()

    }



    fun recyclerView(recyclerView: RecyclerView, siparisListesi: ArrayList<SiparisData>) {
        recyclerView.layoutManager = LinearLayoutManager(this@SiparislerActivity, LinearLayoutManager.VERTICAL, false)
        val Adapter = SiparisAdapter(this@SiparislerActivity, siparisListesi, kullaniciAdi)
        recyclerView.adapter = Adapter
        recyclerView.setHasFixedSize(true)
    }


    fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
    fun setupKullaniciAdi() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()
            }

        })
    }
    private fun initMyAuthStateListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val kullaniciGirisi = p0.currentUser
                if (kullaniciGirisi != null) { //eğer kişi giriş yaptıysa nul gorunmez. giriş yapmadıysa null olur
                } else {
                    startActivity(Intent(this@SiparislerActivity, LoginActivity::class.java))
                }
            }
        }
    }


}
