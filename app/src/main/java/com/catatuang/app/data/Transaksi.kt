package com.catatuang.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class JenisTransaksi { PEMASUKAN, PENGELUARAN }

enum class Kategori(val label: String) {
    MAKAN("Makan"),
    TRANSPORTASI("Transportasi"),
    KOS("Kos"),
    KULIAH("Kuliah"),
    HIBURAN("Hiburan"),
    LAINNYA("Lainnya"),
}

/**
 * Satu baris catatan keuangan.
 * Nominal disimpan dalam rupiah (tanpa desimal), tanggal dalam epoch millis.
 */
@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nominal: Long,
    val jenis: JenisTransaksi,
    val kategori: Kategori,
    val tanggal: Long,
    val catatan: String = "",
)
