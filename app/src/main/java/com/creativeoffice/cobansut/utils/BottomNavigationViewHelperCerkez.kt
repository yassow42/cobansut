package com.creativeoffice.cobansut.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import com.creativeoffice.cobansut.R
import com.creativeoffice.cobansut.cerkez.MapsActivityCerkez
import com.creativeoffice.cobansut.cerkez.MusterilerActivityCerkez
import com.creativeoffice.cobansut.cerkez.SiparisActivityCerkez
import com.creativeoffice.cobansut.cerkez.TeslimEdilenlerActivityCerkez
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx


class BottomNavigationViewHelperCerkez {

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
                                val intent = Intent(context, SiparisActivityCerkez::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)

                            }

                            R.id.ic_teslimedildi -> {
                                val intent = Intent(context, TeslimEdilenlerActivityCerkez::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)

                            }
                            R.id.ic_musteri -> {
                                val intent = Intent(context, MusterilerActivityCerkez::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)

                            }
                            R.id.ic_maps -> {
                                val intent = Intent(context, MapsActivityCerkez::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                            }

                        }
                        return false
                    }
                }
        }
    }
}