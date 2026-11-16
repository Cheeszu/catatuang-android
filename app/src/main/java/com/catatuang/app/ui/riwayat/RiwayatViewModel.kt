package com.catatuang.app.ui.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.TransaksiRepository
import com.catatuang.app.util.awalHari
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class RiwayatUiState(
    /** Kunci: awal hari (millis), nilai: transaksi pada hari itu. Urut dari terbaru. */
    val perTanggal: Map<Long, List<Transaksi>> = emptyMap(),
    val memuat: Boolean = true,
)

// TODO (berikutnya): filter berdasarkan bulan dan kategori.
class RiwayatViewModel(repository: TransaksiRepository) : ViewModel() {

    val uiState: StateFlow<RiwayatUiState> = repository.getSemua()
        .map { semua -> RiwayatUiState(perTanggal = semua.groupBy { awalHari(it.tanggal) }, memuat = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RiwayatUiState())
}
