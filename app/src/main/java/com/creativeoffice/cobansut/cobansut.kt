package com.creativeoffice.cobansut

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class cobansut : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }
}