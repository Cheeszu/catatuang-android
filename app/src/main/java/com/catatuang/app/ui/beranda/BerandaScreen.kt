package com.catatuang.app.ui.beranda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catatuang.app.data.StatusAnggaran
import com.catatuang.app.ui.AppViewModelProvider
import com.catatuang.app.ui.components.KosongInfo
import com.catatuang.app.ui.components.TransaksiItem
import com.catatuang.app.ui.theme.HijauPemasukan
import com.catatuang.app.ui.theme.MerahPengeluaran
import com.catatuang.app.util.formatBulan
import com.catatuang.app.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(
    onTambah: () -> Unit,
    onBukaTransaksi: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BerandaViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("CatatUang") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onTambah) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah transaksi")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Saldo saat ini", style = MaterialTheme.typography.labelLarge)
                        Text(
                            formatRupiah(state.saldo),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    KartuRingkas("Pemasukan", state.pemasukanBulanIni, HijauPemasukan, Modifier.weight(1f))
                    KartuRingkas("Pengeluaran", state.pengeluaranBulanIni, MerahPengeluaran, Modifier.weight(1f))
                }
                Text(
                    "Bulan ${formatBulan(System.currentTimeMillis())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                )
            }
            if (state.statusAnggaran == StatusAnggaran.MENDEKATI || state.statusAnggaran == StatusAnggaran.MELEWATI) {
                item { PeringatanAnggaran(state.statusAnggaran, state.pengeluaranBulanIni, state.batasAnggaran) }
            }
            item {
                Text(
                    "Transaksi terbaru",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                )
            }
            if (!state.memuat && state.terbaru.isEmpty()) {
                item { KosongInfo("Belum ada transaksi. Ketuk + untuk mencatat.") }
            }
            items(state.terbaru, key = { it.id }) { trx ->
                TransaksiItem(trx, onClick = { onBukaTransaksi(trx.id) })
            }
        }
    }
}

@Composable
private fun PeringatanAnggaran(status: StatusAnggaran, pengeluaran: Long, batas: Long) {
    val lewat = status == StatusAnggaran.MELEWATI
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lewat) MaterialTheme.colorScheme.errorContainer else Color(0xFFFFF3CD),
            contentColor = if (lewat) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF5C4400),
        ),
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Warning, contentDescription = null)
            Text(
                if (lewat) "Pengeluaran bulan ini ${formatRupiah(pengeluaran)} sudah melewati batas anggaran ${formatRupiah(batas)}."
                else "Pengeluaran bulan ini sudah ${pengeluaran * 100 / batas}% dari batas anggaran ${formatRupiah(batas)}.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun KartuRingkas(judul: String, nominal: Long, warna: Color, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(judul, style = MaterialTheme.typography.labelMedium)
            Text(
                formatRupiah(nominal),
                color = warna,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
