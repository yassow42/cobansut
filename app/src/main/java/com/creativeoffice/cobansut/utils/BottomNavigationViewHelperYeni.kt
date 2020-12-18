package com.creativeoffice.cobansut.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.creativeoffice.cobansut.EnYeni.CustomerActivity
import com.creativeoffice.cobansut.EnYeni.HaritaActivity
import com.creativeoffice.cobansut.EnYeni.SiparisActivity
import com.creativeoffice.cobansut.EnYeni.SiparisTeslimActivity
import com.creativeoffice.cobansut.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class BottomNavigationViewHelperYeni {


    companion object {

        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx) {


            bottomNavigationViewEx.enableAnimation(true)
            bottomNavigationViewEx.enableItemShiftingMode(false)
            bottomNavigationViewEx.enableShiftingMode(false)
            bottomNavigationViewEx.setTextVisibility(true)

        }

        fun setupNavigation(context: Context, bottomNavigationViewEx: BottomNavigationViewEx) {

            bottomNavigationViewEx.onNavigationItemSelectedListener =
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {

                        when (item.itemId) {

                            R.id.ic_siparisler -> {
                                val intent = Intent(context, SiparisActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)


                            }

                            R.id.ic_teslimedildi -> {
                                val intent = Intent(context, SiparisTeslimActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)

                            }
                            R.id.ic_musteri -> {
                                val intent = Intent(context, CustomerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)

                            }
                            R.id.ic_maps -> {
                                val intent = Intent(context, HaritaActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                            }



                        }
                        return false
                    }
                }
        }
    }
}