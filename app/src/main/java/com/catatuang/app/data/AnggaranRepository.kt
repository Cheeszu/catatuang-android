package com.catatuang.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pengaturan")
private val KUNCI_BATAS = longPreferencesKey("batas_anggaran_bulanan")

/** Batas pengeluaran per bulan, disimpan di DataStore. Nilai 0 berarti belum diatur. */
class AnggaranRepository(private val context: Context) {

    val batas: Flow<Long> = context.dataStore.data.map { it[KUNCI_BATAS] ?: 0L }

    suspend fun simpan(batas: Long) {
        context.dataStore.edit { it[KUNCI_BATAS] = batas }
    }
}

enum class StatusAnggaran { TANPA_BATAS, AMAN, MENDEKATI, MELEWATI }

/** Mendekati = sudah 80% atau lebih dari batas. */
fun statusAnggaran(pengeluaran: Long, batas: Long): StatusAnggaran = when {
    batas <= 0 -> StatusAnggaran.TANPA_BATAS
    pengeluaran > batas -> StatusAnggaran.MELEWATI
    pengeluaran * 100 >= batas * 80 -> StatusAnggaran.MENDEKATI
    else -> StatusAnggaran.AMAN
}
