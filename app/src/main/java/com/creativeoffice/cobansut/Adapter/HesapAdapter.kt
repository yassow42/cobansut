package com.creativeoffice.cobansut.Adapter

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.Users
import com.creativeoffice.cobansut.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_stok_ekle.view.*
import kotlinx.android.synthetic.main.item_hesap.view.*

class HesapAdapter(var myContext: Context, var userList: ArrayList<Users>) : RecyclerView.Adapter<HesapAdapter.ViewHolder>() {

    var ref = FirebaseDatabase.getInstance().reference
    var refUsers = FirebaseDatabase.getInstance().reference.child("users")

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
        var tvToplamFiyat = itemView.tvToplamFiyat

        fun setData(item: Users) {
            //   kullanicilariCikarma(item)
            stokBilgisiAlveGuncelle(item)
        }

        fun stokBilgisiAlveGuncelle(item: Users) {
            var refStok = refUsers.child(item.user_id.toString()).child("Stok")
            refUsers.child(item.user_id.toString()).child("Stok").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var gelenSut3lt = p0.child("3lt").value.toString().toInt()
                    var gelenSut5lt = p0.child("5lt").value.toString().toInt()
                    var gelenDokme = p0.child("dokme_sut").value.toString().toInt()
                    var gelenYumurta = p0.child("yumurta").value.toString().toInt()

                    tvStok.text = "3lt:  $gelenSut3lt \n5lt:  $gelenSut5lt \nDökme:  $gelenDokme \nYumurta:  $gelenYumurta"
                    //Log.e("stoklar", "${item.user_name} $gelenSut3lt  $gelenSut5lt $gelenDokme $gelenYumurta")

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

                                var guncel3lt = gelenSut3lt + etsut3lt
                                refStok.child("3lt").setValue(guncel3lt)

                                var guncel5lt = gelenSut5lt + etsut5lt
                                refStok.child("5lt").setValue(guncel5lt)

                                var guncelDokme = gelenDokme + etdokme
                                refStok.child("dokme_sut").setValue(guncelDokme)

                                var guncelYumurta = gelenYumurta + etyumurta
                                refStok.child("yumurta").setValue(guncelYumurta)

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
/*
        fun kullanicilariCikarma(item: Users) {
            var list = ArrayList<String>()
            list.add("Admin")
            list.add("Bingolbali")

            for (ds in list){
                if (ds == item.user_name){
                    userList.remove(item)
                    notifyDataSetChanged()
                }
            }
        }
*/

    }
}