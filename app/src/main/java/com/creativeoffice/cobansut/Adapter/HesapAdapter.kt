package com.creativeoffice.cobansut.Adapter

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Activity.HesapActivity
import com.creativeoffice.cobansut.Datalar.Users
import com.creativeoffice.cobansut.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_stok_ekle.view.*
import kotlinx.android.synthetic.main.item_hesap.view.*

class HesapAdapter(var myContext: Context, var userList: ArrayList<Users>) : RecyclerView.Adapter<HesapAdapter.ViewHolder>() {

    var ref = FirebaseDatabase.getInstance().reference
    var refUsers = FirebaseDatabase.getInstance().reference.child("users")

    var depoStok3lt: Int = 0
    var depoStok5lt: Int = 0
    var depoStokDokmeSut: Int = 0
    var depoStokYumurta: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myView = LayoutInflater.from(myContext).inflate(R.layout.item_hesap, parent, false)

        return ViewHolder(myView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = userList[position]

        holder.setData(item)
        holder.userName.text = item.user_name


        /*stok sıfırlama
        refUsers.child(item.user_id.toString()).child("Stok/3lt").setValue(0)
        refUsers.child(item.user_id.toString()).child("Stok/5lt").setValue(0)
        refUsers.child(item.user_id.toString()).child("Stok/dokme_sut").setValue(0)
        refUsers.child(item.user_id.toString()).child("Stok/yumurta").setValue(0)*/


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

            stokBilgisiAlveGuncelle(item)

        }

        fun stokBilgisiAlveGuncelle(item: Users) {
            var refStok = refUsers.child(item.user_id.toString()).child("Stok")
            refUsers.child(item.user_id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    ref.child("Depo").addValueEventListener(stokGetir)

                    var aracStokSut3lt = p0.child("Stok").child("3lt").value.toString().toInt()
                    var aracStokSut5lt = p0.child("Stok").child("5lt").value.toString().toInt()
                    var aracStokDokme = p0.child("Stok").child("dokme_sut").value.toString().toInt()
                    var aracStokYumurta = p0.child("Stok").child("yumurta").value.toString().toInt()
                    var aracStokAlinanPara = p0.child("Stok").child("alinan_para").value.toString().toDouble()

                    var satisSut3lt = p0.child("Satis").child("3lt").value.toString().toInt()
                    var satisSut5lt = p0.child("Satis").child("5lt").value.toString().toInt()
                    var satisDokme = p0.child("Satis").child("dokme_sut").value.toString().toInt()
                    var satisYumurta = p0.child("Satis").child("yumurta").value.toString().toInt()
                    var satisFiyat = p0.child("Satis").child("toplam_fiyat").value.toString().toDouble()


                    var kalan3lt = aracStokSut3lt - satisSut3lt
                    var kalan5lt = aracStokSut5lt - satisSut5lt
                    var kalanDokme = aracStokDokme - satisDokme
                    var kalanYumurta = aracStokYumurta - satisYumurta



                    tvStok.text = "3lt:  $aracStokSut3lt \n5lt:  $aracStokSut5lt \nDökme:  $aracStokDokme \nYum:  $aracStokYumurta"
                    tvSatis.text = "3lt:  $satisSut3lt \n5lt:  $satisSut5lt \nDökme:  $satisDokme \nYum:  $satisYumurta"
                    tvKalanBilgisi.text = "3lt:  $kalan3lt \n5lt:  $kalan5lt \nDökme:  $kalanDokme \nYum:  $kalanYumurta"
                    tvToplamFiyat.text = "Toplam: " + satisFiyat.toString() + " tl"
                    tvAlinanPara.text = "Alınan: " + aracStokAlinanPara.toString() + " tl"
                    tvKalanPara.text = "Kalan: " + (satisFiyat - aracStokAlinanPara) + " tl"


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

                                var depoGuncel3lt = depoStok3lt - etsut3lt
                                var depoGuncel5lt = depoStok5lt - etsut5lt
                                var depoGuncelDokme = depoStokDokmeSut - etdokme
                                var depoGuncelyumurta = depoStokYumurta - etyumurta

                                var refDepo = FirebaseDatabase.getInstance().reference.child("Depo")
                                refDepo.child("3lt").setValue(depoGuncel3lt)
                                refDepo.child("5lt").setValue(depoGuncel5lt)
                                refDepo.child("dokme_sut").setValue(depoGuncelDokme)
                                refDepo.child("yumurta").setValue(depoGuncelyumurta)
                                if (etAlininpara != 0.0) {
                                    var key = ref.child("Depo_Alinan_Para").push().key.toString()
                                    ref.child("Depo_Alinan_Para").child(key).child("kimden_alindi").setValue(item.user_name)
                                    ref.child("Depo_Alinan_Para").child(key).child("alinan_para").setValue(etAlininpara)
                                    ref.child("Depo_Alinan_Para").child(key).child("alinma_zamani").setValue(ServerValue.TIMESTAMP)
                                    ref.child("Depo_Alinan_Para").child(key).child("key").setValue(key)
                                }


                                var guncel3lt = aracStokSut3lt + etsut3lt
                                refStok.child("3lt").setValue(guncel3lt)

                                var guncel5lt = aracStokSut5lt + etsut5lt
                                refStok.child("5lt").setValue(guncel5lt)

                                var guncelDokme = aracStokDokme + etdokme
                                refStok.child("dokme_sut").setValue(guncelDokme)

                                var guncelYumurta = aracStokYumurta + etyumurta
                                refStok.child("yumurta").setValue(guncelYumurta)

                                var guncelAlinanPara = aracStokAlinanPara + etAlininpara
                                refStok.child("alinan_para").setValue(guncelAlinanPara)

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

                override fun onCancelled(error: DatabaseError) {

                }


            })
        }


        var stokGetir = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.child("3lt").value.toString().toInt().let {
                    depoStok3lt = it
                }
                p0.child("5lt").value.toString().toInt().let {
                    depoStok5lt = it
                }

                p0.child("dokme_sut").value.toString().toInt().let {
                    depoStokDokmeSut = it
                }
                p0.child("yumurta").value.toString().toInt().let {
                    depoStokYumurta = it
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }
}