package com.catatuang.app.ui.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.AnggaranRepository
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.data.StatusAnggaran
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.TransaksiRepository
import com.catatuang.app.data.statusAnggaran
import com.catatuang.app.util.rentangBulan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class BerandaUiState(
    val saldo: Long = 0,
    val pemasukanBulanIni: Long = 0,
    val pengeluaranBulanIni: Long = 0,
    val batasAnggaran: Long = 0,
    val statusAnggaran: StatusAnggaran = StatusAnggaran.TANPA_BATAS,
    val terbaru: List<Transaksi> = emptyList(),
    val memuat: Boolean = true,
)

class BerandaViewModel(repository: TransaksiRepository, anggaran: AnggaranRepository) : ViewModel() {

    val uiState: StateFlow<BerandaUiState> = combine(repository.getSemua(), anggaran.batas) { semua, batas ->
        val (awal, akhir) = rentangBulan()
        val bulanIni = semua.filter { it.tanggal in awal..akhir }
        val keluar = bulanIni.filter { it.jenis == JenisTransaksi.PENGELUARAN }.sumOf { it.nominal }
        BerandaUiState(
            saldo = semua.sumOf { if (it.jenis == JenisTransaksi.PEMASUKAN) it.nominal else -it.nominal },
            pemasukanBulanIni = bulanIni.filter { it.jenis == JenisTransaksi.PEMASUKAN }.sumOf { it.nominal },
            pengeluaranBulanIni = keluar,
            batasAnggaran = batas,
            statusAnggaran = statusAnggaran(keluar, batas),
            terbaru = semua.take(5),
            memuat = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BerandaUiState())
}
