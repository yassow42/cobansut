package com.creativeoffice.cobansut.Adapter


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.SiparislerActivity
import com.creativeoffice.cobansut.TimeAgo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import kotlinx.android.synthetic.main.item_siparisler.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SiparisAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>) :
    RecyclerView.Adapter<SiparisAdapter.SiparisHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SiparisAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)


        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: SiparisAdapter.SiparisHolder, position: Int) {

        holder.setData(siparisler[position])


        holder.itemView.setOnLongClickListener {

            val popup = PopupMenu(myContext, holder.itemView)
            popup.inflate(R.menu.popup_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popTeslim -> {
                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Sipariş Teslim Edildi")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Onayla", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {


                                    var siparisData = SiparisData(
                                        siparisler[position].siparis_zamani,
                                        siparisler[position].siparis_teslim_zamani,
                                        siparisler[position].siparis_teslim_tarihi,
                                        siparisler[position].siparis_adres,
                                        siparisler[position].siparis_apartman,
                                        siparisler[position].siparis_tel,
                                        siparisler[position].siparis_veren,
                                        siparisler[position].siparis_mah,
                                        siparisler[position].siparis_notu,
                                        siparisler[position].siparis_key,
                                        siparisler[position].yumurta,
                                        siparisler[position].sut3lt,
                                        siparisler[position].sut5lt
                                    )

                                    FirebaseDatabase.getInstance().reference.child("Musteriler")
                                        .child(siparisler[position].siparis_veren.toString())
                                        .child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString())
                                        .setValue(siparisData)

                                    FirebaseDatabase.getInstance().reference.child("Teslim_siparisler")
                                        .child(siparisler[position].siparis_key.toString())
                                        .setValue(siparisData)
                                        .addOnCompleteListener {

                                            myContext.startActivity(Intent(myContext,SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                            Toast.makeText(myContext, "Sipariş Teslim Edildi", Toast.LENGTH_LONG).show()
                                            FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).removeValue()
                                            FirebaseDatabase.getInstance().reference.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                            FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                                        }


                                }
                            })
                            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    p0!!.dismiss()
                                }
                            }).create()

                        alert.show()

                    }
                    R.id.popDüzenle -> {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)

                        var view: View = View.inflate(myContext, R.layout.dialog_siparis_ekle, null)

                        builder.setTitle(siparisler[position].siparis_veren)
                        builder.setIcon(R.drawable.cow)
                     //   view.tvMusteriAdSoyad.setText(siparisler[position].siparis_veren)
                        view.et3lt.setText(siparisler[position].sut3lt)
                        view.et5lt.setText(siparisler[position].sut5lt)
                        view.etYumurta.setText(siparisler[position].yumurta)
                        view.etSiparisNotu.setText(siparisler[position].siparis_notu)
                        builder.setView(view)


                        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.dismiss()
                                }

                            })
                        builder.setPositiveButton("Güncelle", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {

                                    var sut3lt = "0"
                                    if (view.et3lt.text.isNotEmpty()) {
                                         sut3lt = view.et3lt.text.toString()
                                    }
                                    var sut5lt ="0"
                                    if (view.et5lt.text.isNotEmpty()) {
                                         sut5lt = view.et5lt.text.toString()
                                    }
                                    var yumurta = "0"
                                    if (view.etYumurta.text.isNotEmpty()) {
                                        yumurta = view.etYumurta.text.toString()
                                    }

                                    var not = view.etSiparisNotu.text.toString()
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).child("sut3lt").setValue(sut3lt)
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).child("sut5lt").setValue(sut5lt)
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).child("yumurta").setValue(yumurta)
                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).child("siparis_notu").setValue(not)

                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString()).child("sut3lt").setValue(sut3lt)
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString()).child("sut5lt").setValue(sut5lt)
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString()).child("yumurta").setValue(yumurta)
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString()).child("siparis_notu").setValue(not)
                                    var intent = Intent(myContext, SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                    myContext.startActivity(intent)


                                }
                            })

                        var dialog: Dialog = builder.create()

                        dialog.show()
                    }
                    R.id.popSil -> {

                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Siparişi Sil")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {

                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).removeValue().addOnCompleteListener {
                                            Toast.makeText(myContext, "Sipariş Silindi...", Toast.LENGTH_LONG).show()
                                        }

                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString()).removeValue()

                                    myContext.startActivity(Intent(myContext,SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))

                                }
                            })
                            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    p0!!.dismiss()
                                }
                            }).create()

                        alert.show()


                    }
                }
                return@OnMenuItemClickListener true
            })
            popup.show()

            return@setOnLongClickListener true
        }


    }

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tumLayout = itemView.tumLayout
        val siparisVeren = itemView.tvSiparisVeren
        val siparisAdres = itemView.tvSiparisAdres
        val siparisTel = itemView.tvSiparisTel
        val tv3lt = itemView.tv3lt
        val tv5lt = itemView.tv5lt
        val tvYumurta = itemView.tvYumurta
        val tvZaman = itemView.tvZaman
        val tvTeslimZaman = itemView.tvTeslimZamani
        val tvNot = itemView.tvNot
        val tvFiyat = itemView.tvFiyat

        val tv3litre = itemView.tv3litre
        val tv5litre = itemView.tv5litre
        val tvYumurtaYazi = itemView.tvYumurtaYazisi

        fun setData(siparisData: SiparisData) {

            siparisVeren.text = siparisData.siparis_veren
            siparisAdres.text =
                siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres + " " + siparisData.siparis_apartman
            siparisTel.text = siparisData.siparis_tel
            tvNot.text = siparisData.siparis_notu
            tv3lt.text = siparisData.sut3lt
            tv5lt.text = siparisData.sut5lt
            tvYumurta.text = siparisData.yumurta

            if (siparisData.sut3lt == "0"){
                tv3lt.visibility = View.INVISIBLE
                tv3litre.visibility = View.INVISIBLE
            }
            if (siparisData.sut5lt == "0"){
                tv5lt.visibility = View.INVISIBLE
                tv5litre.visibility = View.INVISIBLE
            }
            if (siparisData.yumurta == "0"){
                tvYumurta.visibility = View.INVISIBLE
                tvYumurtaYazi.visibility = View.INVISIBLE
            }


            tvFiyat.textSize = 19f

            tvZaman.text = TimeAgo.getTimeAgo(siparisData.siparis_zamani.toString().toLong())
            tvTeslimZaman.text = formatDate(siparisData.siparis_teslim_tarihi).toString()

            var sut3ltFiyat = siparisData.sut3lt.toString().toInt()
            var sut5ltFiyat = siparisData.sut5lt.toString().toInt()
            var yumurtaFiyat = siparisData.yumurta.toString().toInt()
            tvFiyat.text =
                ((sut3ltFiyat * 16) + (sut5ltFiyat * 22) + yumurtaFiyat).toString() + " tl"


            siparisTel.setOnClickListener {
                val arama =
                    Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                arama.data = Uri.parse("tel:" + siparisData.siparis_tel)
                myContext.startActivity(arama)
            }

            siparisAdres.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q= " + siparisData.siparis_adres)
                )
                myContext.startActivity(intent)
            }


        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)

        }

    }


}