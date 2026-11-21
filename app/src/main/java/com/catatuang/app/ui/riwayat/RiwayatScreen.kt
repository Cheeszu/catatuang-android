package com.catatuang.app.ui.riwayat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catatuang.app.data.Kategori
import com.catatuang.app.ui.AppViewModelProvider
import com.catatuang.app.ui.components.KosongInfo
import com.catatuang.app.ui.components.PemilihBulan
import com.catatuang.app.ui.components.TransaksiItem
import com.catatuang.app.util.formatTanggalPanjang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    onBukaTransaksi: (Long) -> Unit,
    viewModel: RiwayatViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Riwayat") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PemilihBulan(state.bulan, onGeser = viewModel::ubahBulan)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(state.kategori == null, onClick = { viewModel.pilihKategori(null) }, label = { Text("Semua") })
                }
                items(Kategori.entries) { k ->
                    FilterChip(state.kategori == k, onClick = { viewModel.pilihKategori(k) }, label = { Text(k.label) })
                }
            }
            LazyColumn(Modifier.fillMaxSize()) {
                if (!state.memuat && state.perTanggal.isEmpty()) {
                    item { KosongInfo("Tidak ada transaksi pada bulan dan kategori ini.") }
                }
                state.perTanggal.forEach { (hari, daftar) ->
                    stickyHeader(key = "h$hari") {
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                formatTanggalPanjang(hari),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                    items(daftar, key = { it.id }) { trx ->
                        TransaksiItem(trx, onClick = { onBukaTransaksi(trx.id) }, tampilkanTanggal = false)
                    }
                }
            }
        }
    }
}
