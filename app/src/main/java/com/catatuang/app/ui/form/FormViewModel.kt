package com.catatuang.app.ui.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.data.Kategori
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.TransaksiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormUiState(
    val id: Long = 0,
    val jenis: JenisTransaksi = JenisTransaksi.PENGELUARAN,
    val nominalTeks: String = "",
    val kategori: Kategori = Kategori.MAKAN,
    val tanggal: Long = System.currentTimeMillis(),
    val catatan: String = "",
    val errorNominal: String? = null,
    val tersimpan: Boolean = false,
) {
    val modeEdit: Boolean get() = id != 0L
}

class FormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TransaksiRepository,
) : ViewModel() {

    private val idEdit: Long = savedStateHandle.get<Long>("id") ?: 0L

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        if (idEdit != 0L) {
            viewModelScope.launch {
                repository.getById(idEdit).first()?.let { t ->
                    _uiState.value = FormUiState(
                        id = t.id,
                        jenis = t.jenis,
                        nominalTeks = t.nominal.toString(),
                        kategori = t.kategori,
                        tanggal = t.tanggal,
                        catatan = t.catatan,
                    )
                }
            }
        }
    }

    fun ubahJenis(jenis: JenisTransaksi) = _uiState.update { it.copy(jenis = jenis) }

    fun ubahNominal(teks: String) {
        val angka = teks.filter { it.isDigit() }.take(12)
        _uiState.update { it.copy(nominalTeks = angka, errorNominal = null) }
    }

    fun ubahKategori(kategori: Kategori) = _uiState.update { it.copy(kategori = kategori) }

    fun ubahTanggal(millis: Long) = _uiState.update { it.copy(tanggal = millis) }

    fun ubahCatatan(teks: String) = _uiState.update { it.copy(catatan = teks.take(100)) }

    fun simpan() {
        val s = _uiState.value
        val nominal = s.nominalTeks.toLongOrNull()
        if (nominal == null) {
            _uiState.update { it.copy(errorNominal = "Nominal wajib diisi") }
            return
        }
        if (nominal <= 0) {
            _uiState.update { it.copy(errorNominal = "Nominal harus lebih dari nol") }
            return
        }
        viewModelScope.launch {
            repository.simpan(
                Transaksi(
                    id = s.id,
                    nominal = nominal,
                    jenis = s.jenis,
                    kategori = s.kategori,
                    tanggal = s.tanggal,
                    catatan = s.catatan.trim(),
                )
            )
            _uiState.update { it.copy(tersimpan = true) }
        }
    }
}
