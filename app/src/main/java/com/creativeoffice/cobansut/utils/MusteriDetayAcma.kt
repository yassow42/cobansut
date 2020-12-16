package com.creativeoffice.cobansut.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.cobansut.Adapter.MusteriSiparisleriAdapter
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*

class MusteriDetayAcma(var myContext: Context, var text:TextView, var musteriAdi:String) {


  public  fun siparisVerenSiparisDetaylari(){
      var refBolge = FirebaseDatabase.getInstance().reference.child(Utils.secilenBolge)

      text.setOnClickListener {
          Log.e("sadd",musteriAdi)

          var dialogMsDznle: Dialog
          var builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(myContext)
          var dialogView: View = View.inflate(myContext, R.layout.dialog_gidilen_musteri, null)
          builder.setView(dialogView)
          dialogMsDznle = builder.create()

          dialogView.imgMaps.visibility = View.GONE

          dialogView.imgCheck.visibility = View.GONE

          /*   .setOnClickListener {

             if (dialogView.etAdresGidilen.text.toString().isNotEmpty() && dialogView.etTelefonGidilen.text.toString().isNotEmpty()) {
                 var mahalle = dialogView.tvMahalle.text.toString()
                 var adres = dialogView.etAdresGidilen.text.toString()
                 var telefon = dialogView.etTelefonGidilen.text.toString()
                 var apartman = dialogView.etApartman.text.toString()


                 ref.child("Musteriler").child(musteriAdi).child("musteri_mah").setValue(mahalle)
                 ref.child("Musteriler").child(musteriAdi).child("musteri_adres").setValue(adres)
                 ref.child("Musteriler").child(musteriAdi).child("musteri_apartman").setValue(apartman)
                 ref.child("Musteriler").child(musteriAdi).child("musteri_tel").setValue(telefon).addOnCompleteListener {
                     dialogMsDznle.dismiss()
                     Toast.makeText(myContext, "Müşteri Bilgileri Güncellendi", Toast.LENGTH_LONG).show()
                 }.addOnFailureListener { Toast.makeText(myContext, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show() }
             } else {
                 Toast.makeText(myContext, "Bilgilerde boşluklar var", Toast.LENGTH_LONG).show()
             }
         }*/

          dialogView.imgBack.setOnClickListener {
              dialogMsDznle.dismiss()
          }

          dialogView.etAdresGidilen.visibility = View.GONE //.text = musteriler[position].musteri_ad_soyad.toString()
          dialogView.swKonumKaydet.visibility = View.GONE //.text = musteriler[position].musteri_ad_soyad.toString()
          dialogView.etTelefonGidilen.visibility = View.GONE //.text = musteriler[position].musteri_ad_soyad.toString()
          dialogView.tvAdSoyad.visibility = View.GONE //.text = musteriler[position].musteri_ad_soyad.toString()
          dialogView.tvMahalle.visibility = View.GONE //.setText( musteriler[position].musteri_mah.toString())
          dialogView.etApartman.visibility = View.GONE //.setText(musteriler[position].musteri_apartman.toString())
          dialogView.tvFiyatGenel.visibility = View.GONE
          dialogView.view2.visibility = View.GONE

          refBolge.child("Musteriler").child(musteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
              override fun onCancelled(p0: DatabaseError) {

              }

              override fun onDataChange(p0: DataSnapshot) {
                  var adres = p0.child("musteri_adres").value.toString()
                  var telefon = p0.child("musteri_tel").value.toString()
                  var mahalle = p0.child("musteri_mah").value.toString()
                  var konum = p0.child("musteri_zkonum").value.toString().toBoolean()

                  dialogView.swKonumKaydet.isChecked = konum
                  dialogView.etAdresGidilen.setText(adres)
                  dialogView.etTelefonGidilen.setText(telefon)
                  dialogView.tvMahalle.setText(mahalle)

                  var list = ArrayList<SiparisData>()
                  if (p0.child("siparisleri").hasChildren()) {

                      var sut3ltSayisi = 0
                      var sut5ltSayisi = 0
                      var dokmeSayisi = 0
                      var yumurtaSayisi = 0

                      for (ds in p0.child("siparisleri").children) {
                          var gelenData = ds.getValue(SiparisData::class.java)!!
                          list.add(gelenData)

                          sut3ltSayisi += gelenData.sut3lt!!.toInt()
                          sut5ltSayisi += gelenData.sut5lt!!.toInt()
                          //   dokmeSayisi += gelenData.dokme_sut!!.toInt()
                          yumurtaSayisi += gelenData.yumurta!!.toInt()


                      }

                      list.sortByDescending { it.siparis_teslim_zamani }
                      dialogView.tv3litre.text = "3lt: " + sut3ltSayisi.toString()
                      dialogView.tv5litre.text = "5lt: " + sut5ltSayisi.toString()
                      dialogView.tvDokme.text = "Dökme: " + dokmeSayisi.toString()
                      dialogView.tvYumurta.text = "Yumurta: " + yumurtaSayisi.toString()

                      //  dialogView.tvFiyatGenel.text = ((sut3ltSayisi * 16) + (sut5ltSayisi * 22) + yumurtaSayisi).toString() + " tl"


                      dialogView.rcSiparisGidilen.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
                      //dialogView.rcSiparisGidilen.layoutManager = StaggeredGridLayoutManager(myContext, LinearLayoutManager.VERTICAL, 2)
                      val Adapter = MusteriSiparisleriAdapter(myContext, list,Utils.secilenBolge)
                      dialogView.rcSiparisGidilen.adapter = Adapter
                      dialogView.rcSiparisGidilen.setHasFixedSize(true)


                  }
              }


          })
          dialogMsDznle.setCancelable(false)
          dialogMsDznle.show()
      }


    }

}