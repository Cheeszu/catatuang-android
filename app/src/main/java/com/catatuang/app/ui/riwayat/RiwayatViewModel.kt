package com.catatuang.app.ui.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.Kategori
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.TransaksiRepository
import com.catatuang.app.util.awalHari
import com.catatuang.app.util.geserBulan
import com.catatuang.app.util.rentangBulan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class RiwayatUiState(
    val bulan: Long = System.currentTimeMillis(),
    /** null = semua kategori. */
    val kategori: Kategori? = null,
    /** Kunci: awal hari (millis), nilai: transaksi pada hari itu. Urut dari terbaru. */
    val perTanggal: Map<Long, List<Transaksi>> = emptyMap(),
    val memuat: Boolean = true,
)

class RiwayatViewModel(repository: TransaksiRepository) : ViewModel() {

    private val bulan = MutableStateFlow(System.currentTimeMillis())
    private val kategori = MutableStateFlow<Kategori?>(null)

    val uiState: StateFlow<RiwayatUiState> = combine(repository.getSemua(), bulan, kategori) { semua, bln, kat ->
        val (awal, akhir) = rentangBulan(bln)
        val tersaring = semua.filter { it.tanggal in awal..akhir && (kat == null || it.kategori == kat) }
        RiwayatUiState(bln, kat, tersaring.groupBy { awalHari(it.tanggal) }, memuat = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RiwayatUiState())

    fun ubahBulan(jumlah: Int) = bulan.update { geserBulan(it, jumlah) }

    fun pilihKategori(k: Kategori?) { kategori.value = k }
}
