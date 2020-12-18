package com.creativeoffice.cobansut

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class cobansut : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(3048577)
       FirebaseDatabase.getInstance().setPersistenceEnabled(false)


    }
}