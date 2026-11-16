package com.catatuang.app.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catatuang.app.data.Transaksi
import com.catatuang.app.data.TransaksiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TransaksiRepository,
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle.get<Long>("id"))

    val transaksi: StateFlow<Transaksi?> = repository.getById(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun hapus(onSelesai: () -> Unit) {
        val t = transaksi.value ?: return
        viewModelScope.launch {
            repository.hapus(t)
            onSelesai()
        }
    }
}
