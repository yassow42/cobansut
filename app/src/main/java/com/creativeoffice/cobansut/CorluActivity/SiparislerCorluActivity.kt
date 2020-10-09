package com.creativeoffice.cobansut.CorluActivity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.genel.BolgeSecimActivity
import com.creativeoffice.cobansut.CorluAdapter.SiparisCorluAdapter
import com.creativeoffice.cobansut.Datalar.SiparisData
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
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgCumhuriyetDown
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgCumhuriyetUp
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgDereDown
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgDereUp
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgHurriyetDown
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgHurriyetUp
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgMarketDown
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgMarketUp
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgZaferDown
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgZaferUp
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgileriDown
import kotlinx.android.synthetic.main.activity_siparis_corlu.imgileriUp
import kotlinx.android.synthetic.main.activity_siparis_corlu.rcCumhuriyet
import kotlinx.android.synthetic.main.activity_siparis_corlu.rcDere
import kotlinx.android.synthetic.main.activity_siparis_corlu.rcHurriyet
import kotlinx.android.synthetic.main.activity_siparis_corlu.rcMarket
import kotlinx.android.synthetic.main.activity_siparis_corlu.rcZafer
import kotlinx.android.synthetic.main.activity_siparis_corlu.rcileriTarih
import kotlinx.android.synthetic.main.activity_siparis_corlu.tv3litre
import kotlinx.android.synthetic.main.activity_siparis_corlu.tv5litre
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvCumhuriyetSayi
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvDereSayi
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvFiyatGenel
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvHurriyetSayi
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvMarketSayi
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvYumurta
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvZaferSayi
import kotlinx.android.synthetic.main.activity_siparis_corlu.tvileriTarihliSayi


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

        var sut3ltSayisi = 0
        var sut5ltSayisi = 0
        var yumurtaSayisi = 0
        var toplamFiyatlar = 0.0

        var ref = FirebaseDatabase.getInstance().reference

        ref.child("Corlu").child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        mahalleList.add(ds.key.toString())
                        /*
                           try {
                               var gelenData = ds.getValue(SiparisData::class.java)!!
                                   Log.e("Hata",gelenData.siparis_veren)
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
                                   if (gelenData.siparis_mah == "Alipaşa") {
                                       aliList.add(gelenData)
                                       aliList.sortBy { it.siparis_adres }
                                       tvAliSayi.text = aliList.size.toString() + " Sipariş"
                                       if (aliList.size > 0) {
                                           clAli.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcAli, aliList)
                                   }

                                   if (gelenData.siparis_mah == "Cemaliye") {
                                       cemaliyeList.add(gelenData)
                                       tvCemaliyeSayi.text = cemaliyeList.size.toString() + " Sipariş"

                                       if (cemaliyeList.size > 0) {
                                           clCemaliye.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }

                                       recyclerView(rcCemaliye, cemaliyeList)
                                   }

                                   if (gelenData.siparis_mah == "Çoban Çeşme") {
                                       cobanList.add(gelenData)
                                       tvCobanSayi.text = cobanList.size.toString() + " Sipariş"
                                       if (cobanList.size > 0) {
                                           clCoban.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcCoban, cobanList)
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

                                   if (gelenData.siparis_mah == "Esentepe") {
                                       esenList.add(gelenData)
                                       tvEsenSayi.text = esenList.size.toString() + " Sipariş"
                                       if (esenList.size > 0) {
                                           clEsen.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcEsen, esenList)
                                   }
                                   if (gelenData.siparis_mah == "Hatip") {
                                       hatipList.add(gelenData)
                                       tvHatipSayi.text = hatipList.size.toString() + " Sipariş"
                                       if (hatipList.size > 0) {
                                           clHatip.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcHatip, hatipList)
                                   }

                                   if (gelenData.siparis_mah == "Havuzlar") {
                                       havuzList.add(gelenData)
                                       tvHavuzSayi.text = havuzList.size.toString() + " Sipariş"
                                       if (havuzList.size > 0) {
                                           clHavuz.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcHavuz, havuzList)
                                   }
                                   if (gelenData.siparis_mah == "Hıdırağa") {
                                       hidirList.add(gelenData)
                                       tvHidirSayi.text = hidirList.size.toString() + " Sipariş"
                                       if (hidirList.size > 0) {
                                           clHidir.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcHidir, hidirList)
                                   }
                                   if (gelenData.siparis_mah == "Hürriyet") {
                                       hurriyetlist.add(gelenData)
                                       tvHurriyetSayi.text = hurriyetlist.size.toString() + " Sipariş"
                                       if (hurriyetlist.size > 0) {
                                           clHurriyet.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcHurriyet, hurriyetlist)
                                   }

                                   if (gelenData.siparis_mah == "Kazımiye") {
                                       kazimList.add(gelenData)
                                       tvKazimSayi.text = kazimList.size.toString() + " Sipariş"
                                       if (kazimList.size > 0) {
                                           clKazim.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcKazim, kazimList)
                                   }


                                   if (gelenData.siparis_mah == "Kemalettin") {
                                       kemalList.add(gelenData)
                                       tvKemalSayi.text = kemalList.size.toString() + " Sipariş"
                                       if (kemalList.size > 0) {
                                           clKemal.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcKemal, kemalList)
                                   }


                                   if (gelenData.siparis_mah == "Muhittin") {
                                       muhitList.add(gelenData)
                                       tvMuhitSayi.text = muhitList.size.toString() + " Sipariş"
                                       if (muhitList.size > 0) {
                                           clMuhit.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcMuhit, muhitList)
                                   }

                                   if (gelenData.siparis_mah == "Nusratiye") {
                                       nusratList.add(gelenData)
                                       tvNusratSayi.text = nusratList.size.toString() + " Sipariş"
                                       if (nusratList.size > 0) {
                                           clNusrat.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcNusrat, nusratList)
                                   }

                                   if (gelenData.siparis_mah == "Reşadiye") {
                                       resadList.add(gelenData)
                                       tvResatSayi.text = resadList.size.toString() + " Sipariş"
                                       if (resadList.size > 0) {
                                           clResat.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcResat, resadList)
                                   }

                                   if (gelenData.siparis_mah == "Rumeli") {
                                       rumeliList.add(gelenData)
                                       tvRumeliSayi.text = rumeliList.size.toString() + " Sipariş"
                                       if (rumeliList.size > 0) {
                                           clRumeli.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcRumeli, rumeliList)
                                   }
                                   if (gelenData.siparis_mah == "Şeyh Sinan") {
                                       seyhList.add(gelenData)
                                       Log.e("seylsit",seyhList.size.toString())
                                       tvSeyhSayi.text = seyhList.size.toString() + " Sipariş"
                                       if (seyhList.size > 0) {
                                           clSeyh.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcSeyh, seyhList)
                                   }
                                   if (gelenData.siparis_mah == "Silahtarağa") {
                                       silahList.add(gelenData)
                                       tvSilahSayi.text = silahList.size.toString() + " Sipariş"
                                       if (silahList.size > 0) {
                                           clSilah.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                       }
                                       recyclerView(rcSilah, silahList)
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
                                   recyclerView(rcileriTarih, ilerilist)

                               }
                           }
                           catch (e:Exception) {
                               ref.child("Hatalar/Acorlu/siparisDataHata").push().setValue(e.message.toString())
                               Log.e("HataCorluSİparis",e.message.toString())
                           }*/
                    }


                    val adapter = MahalleAdapter(this@SiparislerCorluActivity, mahalleList, kullaniciAdi.toString(), "Corlu")
                    rcMahalleler.layoutManager = LinearLayoutManager(this@SiparislerCorluActivity, LinearLayoutManager.VERTICAL, false)
                    rcMahalleler.adapter = adapter
                    adapter.notifyDataSetChanged()

                    progressDialog.dismiss()
                    tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                    tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                    tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()
                    tvFiyatGenel.text = toplamFiyatlar.toString() + " TL"


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
/*
    private fun setupBtn() {
/*
        imgCikis.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
*/
        lateinit var marketlist: ArrayList<SiparisData>
        lateinit var aliList: ArrayList<SiparisData>
        lateinit var cemaliyeList: ArrayList<SiparisData>
        lateinit var cobanList: ArrayList<SiparisData>
        lateinit var esenList: ArrayList<SiparisData>
        lateinit var hatipList: ArrayList<SiparisData>
        lateinit var cumhuriyetlist: ArrayList<SiparisData>
        lateinit var derelist: ArrayList<SiparisData>
        lateinit var havuzList: ArrayList<SiparisData>
        lateinit var hidirList: ArrayList<SiparisData>
        lateinit var kazimList: ArrayList<SiparisData>
        lateinit var kemalList: ArrayList<SiparisData>
        lateinit var hurriyetlist: ArrayList<SiparisData>
        lateinit var muhitList: ArrayList<SiparisData>
        lateinit var nusratList: ArrayList<SiparisData>
        lateinit var resadList: ArrayList<SiparisData>
        lateinit var rumeliList: ArrayList<SiparisData>
        lateinit var seyhList: ArrayList<SiparisData>
        lateinit var silahList: ArrayList<SiparisData>
        lateinit var yenilist: ArrayList<SiparisData>
        lateinit var yildirimlist: ArrayList<SiparisData>
        lateinit var yildizlist: ArrayList<SiparisData>
        lateinit var yilmazlist: ArrayList<SiparisData>
        lateinit var zaferlist: ArrayList<SiparisData>
        lateinit var ilerilist: ArrayList<SiparisData>


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
        imgAliDown.setOnClickListener {
            imgAliDown.visibility = View.GONE
            imgAliUp.visibility = View.VISIBLE
            rcAli.visibility = View.VISIBLE
        }
        imgAliUp.setOnClickListener {
            imgAliDown.visibility = View.VISIBLE
            imgAliUp.visibility = View.GONE
            rcAli.visibility = View.GONE
        }

        //////////////////////////
        imgCemaliyeDown.setOnClickListener {
            imgCemaliyeDown.visibility = View.GONE
            imgCemaliyeUp.visibility = View.VISIBLE
            rcCemaliye.visibility = View.VISIBLE
        }
        imgCemaliyeUp.setOnClickListener {
            imgCemaliyeDown.visibility = View.VISIBLE
            imgCemaliyeUp.visibility = View.GONE
            rcCemaliye.visibility = View.GONE
        }

        //////////////////////////
        imgCobanDown.setOnClickListener {
            imgCobanDown.visibility = View.GONE
            imgCobanUp.visibility = View.VISIBLE
            rcCoban.visibility = View.VISIBLE
        }
        imgCobanUp.setOnClickListener {
            imgCobanDown.visibility = View.VISIBLE
            imgCobanUp.visibility = View.GONE
            rcCoban.visibility = View.GONE
        }
        //////////////////////////
        imgEsenDown.setOnClickListener {
            imgEsenDown.visibility = View.GONE
            imgEsenUp.visibility = View.VISIBLE
            rcEsen.visibility = View.VISIBLE
        }
        imgEsenUp.setOnClickListener {
            imgEsenDown.visibility = View.VISIBLE
            imgEsenUp.visibility = View.GONE
            rcEsen.visibility = View.GONE
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

        imgHatipDown.setOnClickListener {
            imgHatipDown.visibility = View.GONE
            imgHatipUp.visibility = View.VISIBLE
            rcHatip.visibility = View.VISIBLE
        }
        imgHatipUp.setOnClickListener {
            imgHatipDown.visibility = View.VISIBLE
            imgHatipUp.visibility = View.GONE
            rcHatip.visibility = View.GONE
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
        imgHavuzDown.setOnClickListener {
            imgHavuzDown.visibility = View.GONE
            imgHavuzUp.visibility = View.VISIBLE
            rcHavuz.visibility = View.VISIBLE
        }
        imgHavuzUp.setOnClickListener {
            imgHavuzDown.visibility = View.VISIBLE
            imgHavuzUp.visibility = View.GONE
            rcHavuz.visibility = View.GONE
        }

        //////////////////////////

        imgHidirDown.setOnClickListener {
            imgHidirDown.visibility = View.GONE
            imgHidirUp.visibility = View.VISIBLE
            rcHidir.visibility = View.VISIBLE
        }
        imgHidirUp.setOnClickListener {
            imgHidirDown.visibility = View.VISIBLE
            imgHidirUp.visibility = View.GONE
            rcHidir.visibility = View.GONE
        }

        //////////////////////////

        imgKazimDown.setOnClickListener {
            imgKazimDown.visibility = View.GONE
            imgKazimUp.visibility = View.VISIBLE
            rcKazim.visibility = View.VISIBLE
        }
        imgKazimUp.setOnClickListener {
            imgKazimDown.visibility = View.VISIBLE
            imgKazimUp.visibility = View.GONE
            rcKazim.visibility = View.GONE
        }

        //////////////////////////

        imgKemalDown.setOnClickListener {
            imgKemalDown.visibility = View.GONE
            imgKemalUp.visibility = View.VISIBLE
            rcKemal.visibility = View.VISIBLE
        }
        imgKemalUp.setOnClickListener {
            imgKemalDown.visibility = View.VISIBLE
            imgKemalUp.visibility = View.GONE
            rcKemal.visibility = View.GONE
        }

        //////////////////////////

        imgMuhitDown.setOnClickListener {
            imgMuhitDown.visibility = View.GONE
            imgMuhitUp.visibility = View.VISIBLE
            rcMuhit.visibility = View.VISIBLE
        }
        imgMuhitUp.setOnClickListener {
            imgMuhitDown.visibility = View.VISIBLE
            imgMuhitUp.visibility = View.GONE
            rcMuhit.visibility = View.GONE
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

        imgNusratDown.setOnClickListener {
            imgNusratDown.visibility = View.GONE
            imgNusratUp.visibility = View.VISIBLE
            rcNusrat.visibility = View.VISIBLE
        }
        imgNusratUp.setOnClickListener {
            imgNusratDown.visibility = View.VISIBLE
            imgNusratUp.visibility = View.GONE
            rcNusrat.visibility = View.GONE
        }

        //////////////////////////


        imgResatDown.setOnClickListener {
            imgResatDown.visibility = View.GONE
            imgResatUp.visibility = View.VISIBLE
            rcResat.visibility = View.VISIBLE
        }
        imgResatUp.setOnClickListener {
            imgResatDown.visibility = View.VISIBLE
            imgResatUp.visibility = View.GONE
            rcResat.visibility = View.GONE
        }

        //////////////////////////


        imgRumeliDown.setOnClickListener {
            imgRumeliDown.visibility = View.GONE
            imgRumeliUp.visibility = View.VISIBLE
            rcRumeli.visibility = View.VISIBLE
        }
        imgRumeliUp.setOnClickListener {
            imgRumeliDown.visibility = View.VISIBLE
            imgRumeliUp.visibility = View.GONE
            rcRumeli.visibility = View.GONE
        }

        //////////////////////////
        imgSeyhDown.setOnClickListener {
            imgSeyhDown.visibility = View.GONE
            imgSeyhUp.visibility = View.VISIBLE
            rcSeyh.visibility = View.VISIBLE
        }
        imgSeyhUp.setOnClickListener {
            imgSeyhDown.visibility = View.VISIBLE
            imgSeyhUp.visibility = View.GONE
            rcSeyh.visibility = View.GONE
        }

        //////////////////////////

        imgSilahDown.setOnClickListener {
            imgSilahDown.visibility = View.GONE
            imgSilahUp.visibility = View.VISIBLE
            rcSilah.visibility = View.VISIBLE
        }
        imgSilahUp.setOnClickListener {
            imgSilahDown.visibility = View.VISIBLE
            imgSilahUp.visibility = View.GONE
            rcSilah.visibility = View.GONE
        }


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


    fun recyclerView(recyclerView: RecyclerView, siparisListesi: ArrayList<SiparisData>) {
        recyclerView.layoutManager = LinearLayoutManager(this@SiparislerCorluActivity, LinearLayoutManager.VERTICAL, false)
        val Adapter = SiparisCorluAdapter(this@SiparislerCorluActivity, siparisListesi, kullaniciAdi)
        recyclerView.adapter = Adapter
        recyclerView.setHasFixedSize(true)
    }
*/
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