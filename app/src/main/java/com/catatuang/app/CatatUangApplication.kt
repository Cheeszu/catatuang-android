package com.catatuang.app

import android.app.Application
import com.catatuang.app.data.AppDatabase
import com.catatuang.app.data.TransaksiRepository

class CatatUangApplication : Application() {
    val repository: TransaksiRepository by lazy {
        TransaksiRepository(AppDatabase.getInstance(this).transaksiDao())
    }
}
