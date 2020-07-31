package com.creativeoffice.cobansut.cerkez.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.cobansut.Datalar.SiparisData
import com.creativeoffice.cobansut.R

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.item_siparisler_mahalle.view.*
import kotlinx.android.synthetic.main.item_siparisler_mahalle.view.tvMahalle
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MahalleAdapter(val myContext: Context, val mahalleler: ArrayList<String>,val kullaniciAdi:String) : RecyclerView.Adapter<MahalleAdapter.SiparisHolder>() {
    var ref = FirebaseDatabase.getInstance().reference.child("Cerkez")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MahalleAdapter.SiparisHolder {
        val view =
            LayoutInflater.from(myContext).inflate(R.layout.item_siparisler_mahalle, parent, false)

        SiparisHolder(view).recycler.visibility = View.GONE


        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return mahalleler.size
    }

    override fun onBindViewHolder(holder: MahalleAdapter.SiparisHolder, position: Int) {

        holder.mahalle.text = mahalleler[position].toString()
        holder.setRc(mahalleler[position])

        holder.switch.setOnClickListener {

            if (holder.switch.isChecked) {

                holder.recycler.visibility = View.VISIBLE

            } else {

                holder.recycler.visibility = View.GONE
            }
        }

    }

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mahalle = itemView.tvMahalle
        val siparisSayisi = itemView.tvSiparisSayisi
        val recycler = itemView.rcitemMahalle
        val switch = itemView.switchRc

        fun setRc(mahalleler: String) {
           ref.child("Siparisler").child(mahalleler)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var siparisList = ArrayList<SiparisData>()
                        if (p0.hasChildren()) {
                            for (ds in p0.children) {
                                try {
                                    var gelenData = ds.getValue(SiparisData::class.java)!!
                                        siparisList.add(gelenData)


                                }catch (e:Exception){
                                    FirebaseDatabase.getInstance().reference.child("Hatalar/MahalleAdapterCerkez").push().setValue(e.message.toString())
                                }

                            }
                            recycler.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
                            val adapter = MahalleSiparisleriAdapter(myContext, siparisList,kullaniciAdi)
                            recycler.adapter = adapter
                            siparisSayisi.setText("(" + siparisList.size + ")")
                        }


                    }


                })

        }


        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)
        }


    }


}