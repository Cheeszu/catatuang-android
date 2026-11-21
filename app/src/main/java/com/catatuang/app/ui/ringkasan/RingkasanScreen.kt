package com.catatuang.app.ui.ringkasan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catatuang.app.data.StatusAnggaran
import com.catatuang.app.ui.AppViewModelProvider
import com.catatuang.app.ui.components.PemilihBulan
import com.catatuang.app.ui.components.icon
import com.catatuang.app.ui.theme.HijauPemasukan
import com.catatuang.app.ui.theme.MerahPengeluaran
import com.catatuang.app.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingkasanScreen(
    viewModel: RingkasanViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var aturBatas by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Ringkasan") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PemilihBulan(state.bulan, onGeser = viewModel::ubahBulan)
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total pengeluaran", style = MaterialTheme.typography.labelLarge)
                    Text(
                        formatRupiah(state.totalPengeluaran),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MerahPengeluaran,
                    )
                    Text("${state.jumlahTransaksi} transaksi pengeluaran", style = MaterialTheme.typography.bodySmall)
                }
            }

            KartuAnggaran(state, onAtur = { aturBatas = true })

            Text("Per kategori", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
            if (state.perKategori.isEmpty()) {
                Text(
                    "Belum ada pengeluaran pada bulan ini.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            state.perKategori.forEach { pk ->
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(pk.kategori.icon(), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(pk.kategori.label, modifier = Modifier.weight(1f))
                        Text("${formatRupiah(pk.total)} · ${pk.persen}%", style = MaterialTheme.typography.bodyMedium)
                    }
                    LinearProgressIndicator(
                        progress = { pk.persen / 100f },
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        drawStopIndicator = {},
                    )
                }
            }
        }
    }

    if (aturBatas) {
        DialogBatas(
            awal = state.batasAnggaran,
            onSimpan = { viewModel.simpanBatas(it); aturBatas = false },
            onBatal = { aturBatas = false },
        )
    }
}

@Composable
private fun KartuAnggaran(state: RingkasanUiState, onAtur: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Batas anggaran bulanan", style = MaterialTheme.typography.titleSmall)
            if (state.batasAnggaran <= 0) {
                Text("Belum diatur.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                val warna = when (state.statusAnggaran) {
                    StatusAnggaran.MELEWATI -> MerahPengeluaran
                    StatusAnggaran.MENDEKATI -> Color(0xFFE0A000)
                    else -> HijauPemasukan
                }
                Text("${formatRupiah(state.totalPengeluaran)} dari ${formatRupiah(state.batasAnggaran)}")
                LinearProgressIndicator(
                    progress = { (state.totalPengeluaran.toFloat() / state.batasAnggaran).coerceAtMost(1f) },
                    color = warna,
                    modifier = Modifier.fillMaxWidth(),
                    drawStopIndicator = {},
                )
                Text(
                    when (state.statusAnggaran) {
                        StatusAnggaran.MELEWATI -> "Sudah melewati batas."
                        StatusAnggaran.MENDEKATI -> "Hampir mencapai batas."
                        else -> "Masih aman, sisa ${formatRupiah(state.batasAnggaran - state.totalPengeluaran)}."
                    },
                    color = warna,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            OutlinedButton(onClick = onAtur) { Text(if (state.batasAnggaran > 0) "Ubah batas" else "Atur batas") }
        }
    }
}

@Composable
private fun DialogBatas(awal: Long, onSimpan: (Long) -> Unit, onBatal: () -> Unit) {
    var teks by remember { mutableStateOf(if (awal > 0) awal.toString() else "") }
    AlertDialog(
        onDismissRequest = onBatal,
        title = { Text("Batas anggaran bulanan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Beranda akan menampilkan peringatan saat pengeluaran mencapai 80% dari batas. Kosongkan untuk menghapus batas.")
                OutlinedTextField(
                    value = teks,
                    onValueChange = { teks = it.filter(Char::isDigit).take(12) },
                    label = { Text("Batas (Rp)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSimpan(teks.toLongOrNull() ?: 0L) }) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onBatal) { Text("Batal") } },
    )
}
