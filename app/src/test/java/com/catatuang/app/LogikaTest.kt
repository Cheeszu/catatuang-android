package com.catatuang.app

import com.catatuang.app.data.JenisTransaksi.PEMASUKAN
import com.catatuang.app.data.JenisTransaksi.PENGELUARAN
import com.catatuang.app.data.Kategori
import com.catatuang.app.data.StatusAnggaran
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.statusAnggaran
import com.catatuang.app.ui.ringkasan.PengeluaranKategori
import com.catatuang.app.ui.ringkasan.hitungPerKategori
import com.catatuang.app.util.geserBulan
import com.catatuang.app.util.rentangBulan
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class LogikaTest {

    @Test
    fun statusAnggaran_batasDanAmbang80Persen() {
        assertEquals(StatusAnggaran.TANPA_BATAS, statusAnggaran(50_000, 0))
        assertEquals(StatusAnggaran.AMAN, statusAnggaran(79_999, 100_000))
        assertEquals(StatusAnggaran.MENDEKATI, statusAnggaran(80_000, 100_000))
        assertEquals(StatusAnggaran.MENDEKATI, statusAnggaran(100_000, 100_000))
        assertEquals(StatusAnggaran.MELEWATI, statusAnggaran(100_001, 100_000))
    }

    @Test
    fun hitungPerKategori_abaikanPemasukanDanUrutTerbesar() {
        fun t(n: Long, k: Kategori, j: com.catatuang.app.data.JenisTransaksi = PENGELUARAN) =
            Transaksi(nominal = n, jenis = j, kategori = k, tanggal = 0)
        val hasil = hitungPerKategori(
            listOf(t(30_000, Kategori.MAKAN), t(10_000, Kategori.TRANSPORTASI), t(20_000, Kategori.MAKAN), t(1_000_000, Kategori.LAINNYA, PEMASUKAN))
        )
        assertEquals(
            listOf(PengeluaranKategori(Kategori.MAKAN, 50_000, 83), PengeluaranKategori(Kategori.TRANSPORTASI, 10_000, 16)),
            hasil,
        )
        assertEquals(emptyList<PengeluaranKategori>(), hitungPerKategori(emptyList()))
    }

    @Test
    fun geserBulan_melewatiPergantianTahun() {
        val tgl31Jan = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 31, 10, 0) }.timeInMillis
        val (awalFeb, _) = rentangBulan(geserBulan(tgl31Jan, 1))
        val (awalDes, _) = rentangBulan(geserBulan(tgl31Jan, -1))
        assertEquals(Calendar.FEBRUARY, Calendar.getInstance().apply { timeInMillis = awalFeb }.get(Calendar.MONTH))
        assertEquals(2025, Calendar.getInstance().apply { timeInMillis = awalDes }.get(Calendar.YEAR))
    }
}
