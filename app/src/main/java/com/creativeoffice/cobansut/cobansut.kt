package com.creativeoffice.cobansut

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class cobansut : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(1048576)
       FirebaseDatabase.getInstance().setPersistenceEnabled(false)


    }
}