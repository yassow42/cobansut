package com.creativeoffice.cobansut.Adapter

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Activity.HesapActivity
import com.creativeoffice.cobansut.Datalar.AlinanParaData
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.Datalar.Users
import com.creativeoffice.cobansut.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_recyclerview.view.*
import kotlinx.android.synthetic.main.dialog_stok_ekle.view.*
import kotlinx.android.synthetic.main.item_hesap.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HesapAdapter(var myContext: Context, var userList: ArrayList<Users>,var teslimList:ArrayList<SiparisData>, var ileriZaman: Long?, var geriZaman: Long?) : RecyclerView.Adapter<HesapAdapter.ViewHolder>() {

    var ref = FirebaseDatabase.getInstance().reference
    var refUsers = FirebaseDatabase.getInstance().reference.child("users")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myView = LayoutInflater.from(myContext).inflate(R.layout.item_hesap, parent, false)

        return ViewHolder(myView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemUser = userList[position]

        holder.setData(itemUser)
        holder.userName.text = itemUser.user_name


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName = itemView.tvUserName
        var imgPlus = itemView.imgPlus
        var tvStok = itemView.tvStok
        var tvSatis = itemView.tvSatis
        var tvKalanBilgisi = itemView.tvKalanBilgisi
        var tvToplamFiyat = itemView.tvToplamFiyat
        var tvAlinanPara = itemView.tvAlinanPara
        var tvKalanPara = itemView.tvKalanPara


        fun setData(item: Users) {
            imgPlus(item)

            var satis3Lt = 0
            var satis5Lt = 0
            var satisDokme = 0
            var satisYumurta = 0

            var toplamPara = 0.0
            for (i in teslimList){

                if (i.siparisi_giren.equals(item.user_name)){
                    satis3Lt += i.sut3lt!!.toInt()
                    satis5Lt += i.sut5lt!!.toInt()
                    satisDokme += i.dokme_sut!!.toInt()
                    satisYumurta += i.yumurta!!.toInt()
                    toplamPara += i.toplam_fiyat!!.toDouble()
                }

            }
            tvSatis.text = "3lt:  $satis3Lt \n5lt:  $satis5Lt \nDökme:  $satisDokme \nYum:  $satisYumurta"
            tvToplamFiyat.text = "Toplam: $toplamPara"





            var alinanParaList = ArrayList<AlinanParaData>()
            ref.child("Depo_Alinan_Para").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        var alinanPara: Double = 0.0
                        for (ds in p0.children) {
                            var data = ds.getValue(AlinanParaData::class.java)!!

                            if (data.kimden_alindi == item.user_name) {
                                alinanParaList.add(data)
                                alinanPara += data.alinan_para!!.toDouble()
                                tvAlinanPara.text = "Alınan: " + alinanPara.toString() + " tl"
                                tvKalanPara.text = "Kalan: " + (toplamPara - alinanPara) + " tl"
                            }

                        }
                        alinanParaList.sortByDescending { it.alinma_zamani }
                        tvAlinanPara.setOnClickListener {
                            var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)
                            var dialogView = View.inflate(myContext, R.layout.dialog_recyclerview, null)


                            var gosterilenParaList = ArrayList<String>()
                            for (ds in alinanParaList) {

                                var kimdenAlindi = ds.kimden_alindi
                                var alinanPara = ds.alinan_para
                                var alinanParaZaman = ds.alinma_zamani

                                var tumListString = "Alınan Para: $alinanPara \n${formatDate(alinanParaZaman).toString()}"
                                if (item.user_name.equals(kimdenAlindi)) gosterilenParaList.add(tumListString)


                            }
                            val adapter = ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, gosterilenParaList)
                            dialogView.dialogRC.adapter = adapter

                            builder.setNegativeButton("Çık", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.dismiss()
                                }

                            })
                            builder.setView(dialogView)
                            var dialog: Dialog = builder.create()
                            dialog.show()

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        }
/*
        fun aracStokBilgisiGetir(item: Users) {
            var sut3ltSayisi = 0
            var sut5ltSayisi = 0
            var sutDokmeSayisi = 0
            var yumurtaSayisi = 0

            ref.child("Depo_Arac_Stok_Ekle").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p1: DataSnapshot) {
                    if (p1.hasChildren()) {
                        for (ds in p1.children) {
                            var data = ds.getValue(AracStokEkleData::class.java)!!
                            if (geriZaman!! < data.araca_stok_ekleme_zamani!! && data.araca_stok_ekleme_zamani!! < ileriZaman!!) {
                                if (data.eklenen_arac.equals(item.user_name)){
                                    sut3ltSayisi = sut3ltSayisi + data.sut3lt!!
                                    sut5ltSayisi = sut5ltSayisi + data.sut5lt!!
                                    sutDokmeSayisi = sutDokmeSayisi + data.dokme_sut!!
                                    yumurtaSayisi = yumurtaSayisi + data.yumurta!!
                                }

                            }
                        }
                        tvStok.text = "3lt: $sut3ltSayisi\n5lt: $sut5ltSayisi\nDökme: $sutDokmeSayisi\nYum: $yumurtaSayisi "


                        stokBilgisiAlveGuncelleAlinanParaList(item,sut3ltSayisi,sut5ltSayisi,sutDokmeSayisi,yumurtaSayisi)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

       fun stokBilgisiAlveGuncelleAlinanParaList(item: Users, sut3ltSayisi: Int, sut5ltSayisi: Int, sutDokmeSayisi: Int, yumurtaSayisi: Int) {

            refUsers.child(item.user_id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {


                    var satisSut3lt = p0.child("Satis").child("3lt").value.toString().toInt()
                    var satisSut5lt = p0.child("Satis").child("5lt").value.toString().toInt()
                    var satisDokme = p0.child("Satis").child("dokme_sut").value.toString().toInt()
                    var satisYumurta = p0.child("Satis").child("yumurta").value.toString().toInt()
                    var satisFiyat = p0.child("Satis").child("toplam_fiyat").value.toString().toDouble()


                    var kalan3lt = sut3ltSayisi - satisSut3lt
                    var kalan5lt = sut5ltSayisi - satisSut5lt
                    var kalanDokme = sutDokmeSayisi - satisDokme
                    var kalanYumurta = yumurtaSayisi - satisYumurta



                    tvSatis.text = "3lt:  $satisSut3lt \n5lt:  $satisSut5lt \nDökme:  $satisDokme \nYum:  $satisYumurta"
                    tvKalanBilgisi.text = "3lt:  $kalan3lt \n5lt:  $kalan5lt \nDökme:  $kalanDokme \nYum:  $kalanYumurta"
                    tvToplamFiyat.text = "Toplam: " + satisFiyat.toString() + " tl"

                    var alinanParaList = ArrayList<AlinanParaData>()
                    ref.child("Depo_Alinan_Para").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChildren()) {
                                var alinanPara: Double = 0.0
                                for (ds in p0.children) {
                                    var data = ds.getValue(AlinanParaData::class.java)!!

                                    if (data.kimden_alindi == item.user_name) {
                                        alinanParaList.add(data)
                                        alinanPara = alinanPara + data.alinan_para!!.toDouble()
                                        tvAlinanPara.text = "Alınan: " + alinanPara.toString() + " tl"
                                        tvKalanPara.text = "Kalan: " + (satisFiyat - alinanPara) + " tl"
                                    }

                                }
                                alinanParaList.sortByDescending { it.alinma_zamani }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


                    tvAlinanPara.setOnClickListener {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)
                        var dialogView = View.inflate(myContext, R.layout.dialog_recyclerview, null)


                        var gosterilenParaList = ArrayList<String>()
                        for (ds in alinanParaList) {

                            var kimdenAlindi = ds.kimden_alindi
                            var alinanPara = ds.alinan_para
                            var alinanParaZaman = ds.alinma_zamani

                            var tumListString = "Alınan Para: $alinanPara \n${formatDate(alinanParaZaman).toString()}"
                            if (item.user_name.equals(kimdenAlindi)) gosterilenParaList.add(tumListString)


                        }
                        val adapter = ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, gosterilenParaList)
                        dialogView.dialogRC.adapter = adapter






                        builder.setNegativeButton("Çık", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.dismiss()
                            }

                        })
                        builder.setView(dialogView)
                        var dialog: Dialog = builder.create()
                        dialog.show()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        }
*/
        fun imgPlus(item: Users) {
            imgPlus.setOnClickListener {
                var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)
                var dialogView = View.inflate(myContext, R.layout.dialog_stok_ekle, null)

                builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                    }

                })
                builder.setPositiveButton("Stok Ekle", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        var etsut3lt = 0
                        if (!dialogView.et3lt.text.isNullOrEmpty()) etsut3lt = dialogView.et3lt.text.toString().toInt()
                        var etsut5lt = 0
                        if (!dialogView.et5lt.text.isNullOrEmpty()) etsut5lt = dialogView.et5lt.text.toString().toInt()
                        var etdokme = 0
                        if (!dialogView.etDokmeSut.text.isNullOrEmpty()) etdokme = dialogView.etDokmeSut.text.toString().toInt()
                        var etyumurta = 0
                        if (!dialogView.etYumurta.text.isNullOrEmpty()) etyumurta = dialogView.etYumurta.text.toString().toInt()
                        var etAlininpara = 0.0
                        if (!dialogView.etAlinanPara.text.isNullOrEmpty()) etAlininpara = dialogView.etAlinanPara.text.toString().toDouble()


                        var key = ref.child("Depo_Arac_Stok_Ekle").push().key.toString()
                        ref.child("Depo_Arac_Stok_Ekle").child(key).child("eklenen_arac").setValue(item.user_name)
                        ref.child("Depo_Arac_Stok_Ekle").child(key).child("sut3lt").setValue(etsut3lt)
                        ref.child("Depo_Arac_Stok_Ekle").child(key).child("sut5lt").setValue(etsut5lt)
                        ref.child("Depo_Arac_Stok_Ekle").child(key).child("dokme_sut").setValue(etdokme)
                        ref.child("Depo_Arac_Stok_Ekle").child(key).child("yumurta").setValue(etyumurta)
                        ref.child("Depo_Arac_Stok_Ekle").child(key).child("araca_stok_ekleme_zamani").setValue(ServerValue.TIMESTAMP)

                        if (etAlininpara != 0.0) {
                            var key = ref.child("Depo_Alinan_Para").push().key.toString()
                            ref.child("Depo_Alinan_Para").child(key).child("kimden_alindi").setValue(item.user_name)
                            ref.child("Depo_Alinan_Para").child(key).child("alinan_para").setValue(etAlininpara)
                            ref.child("Depo_Alinan_Para").child(key).child("alinma_zamani").setValue(ServerValue.TIMESTAMP)
                            ref.child("Depo_Alinan_Para").child(key).child("key").setValue(key)
                        }


                        //   var guncelAlinanPara = alinanPara + etAlininpara
                        //        refStok.child("alinan_para").setValue(guncelAlinanPara)

                        myContext.startActivity(Intent(myContext, HesapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    }
                })
                builder.setTitle(item.user_name)
                builder.setIcon(R.drawable.cow)

                builder.setView(dialogView)
                var dialog: Dialog = builder.create()
                dialog.show()
            }
        }




        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("HH:mm - d MMM", Locale("tr"))
            return sdf.format(date)
        }

    }
}