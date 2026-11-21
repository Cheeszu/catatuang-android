package com.catatuang.app.ui.ringkasan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.AnggaranRepository
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.data.Kategori
import com.catatuang.app.data.StatusAnggaran
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.TransaksiRepository
import com.catatuang.app.data.statusAnggaran
import com.catatuang.app.util.geserBulan
import com.catatuang.app.util.rentangBulan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PengeluaranKategori(val kategori: Kategori, val total: Long, val persen: Int)

data class RingkasanUiState(
    val bulan: Long = System.currentTimeMillis(),
    val totalPengeluaran: Long = 0,
    val jumlahTransaksi: Int = 0,
    val perKategori: List<PengeluaranKategori> = emptyList(),
    val batasAnggaran: Long = 0,
    val statusAnggaran: StatusAnggaran = StatusAnggaran.TANPA_BATAS,
)

/** Total pengeluaran per kategori, terbesar dulu. Persen dibulatkan ke bawah. */
fun hitungPerKategori(transaksi: List<Transaksi>): List<PengeluaranKategori> {
    val keluar = transaksi.filter { it.jenis == JenisTransaksi.PENGELUARAN }
    val total = keluar.sumOf { it.nominal }
    if (total == 0L) return emptyList()
    return keluar.groupBy { it.kategori }
        .map { (k, daftar) -> daftar.sumOf { it.nominal }.let { PengeluaranKategori(k, it, (it * 100 / total).toInt()) } }
        .sortedByDescending { it.total }
}

class RingkasanViewModel(repository: TransaksiRepository, private val anggaran: AnggaranRepository) : ViewModel() {

    private val bulan = MutableStateFlow(System.currentTimeMillis())

    val uiState: StateFlow<RingkasanUiState> = combine(repository.getSemua(), bulan, anggaran.batas) { semua, bln, batas ->
        val (awal, akhir) = rentangBulan(bln)
        val keluar = semua.filter { it.tanggal in awal..akhir && it.jenis == JenisTransaksi.PENGELUARAN }
        val total = keluar.sumOf { it.nominal }
        RingkasanUiState(bln, total, keluar.size, hitungPerKategori(keluar), batas, statusAnggaran(total, batas))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RingkasanUiState())

    fun ubahBulan(jumlah: Int) = bulan.update { geserBulan(it, jumlah) }

    fun simpanBatas(batas: Long) {
        viewModelScope.launch { anggaran.simpan(batas) }
    }
}
