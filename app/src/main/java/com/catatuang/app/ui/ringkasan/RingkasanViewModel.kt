package com.catatuang.app.ui.ringkasan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.data.TransaksiRepository
import com.catatuang.app.util.rentangBulan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class RingkasanUiState(
    val totalPengeluaran: Long = 0,
    val jumlahTransaksi: Int = 0,
)

// TODO (berikutnya): pilihan bulan, persentase per kategori, batas anggaran (DataStore).
class RingkasanViewModel(repository: TransaksiRepository) : ViewModel() {

    val uiState: StateFlow<RingkasanUiState> = rentangBulan().let { (awal, akhir) ->
        repository.getAntara(awal, akhir)
    }.map { bulanIni ->
        val keluar = bulanIni.filter { it.jenis == JenisTransaksi.PENGELUARAN }
        RingkasanUiState(totalPengeluaran = keluar.sumOf { it.nominal }, jumlahTransaksi = keluar.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RingkasanUiState())
}
