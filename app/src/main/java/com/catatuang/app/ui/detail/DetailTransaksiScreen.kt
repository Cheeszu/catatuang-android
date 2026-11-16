package com.catatuang.app.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.ui.AppViewModelProvider
import com.catatuang.app.ui.theme.HijauPemasukan
import com.catatuang.app.ui.theme.MerahPengeluaran
import com.catatuang.app.util.formatRupiah
import com.catatuang.app.util.formatTanggalPanjang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransaksiScreen(
    onEdit: (Long) -> Unit,
    onKembali: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val transaksi by viewModel.transaksi.collectAsStateWithLifecycle()
    var konfirmasiHapus by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onKembali) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        val t = transaksi ?: return@Scaffold
        val masuk = t.jenis == JenisTransaksi.PEMASUKAN
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        (if (masuk) "+" else "-") + formatRupiah(t.nominal),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (masuk) HijauPemasukan else MerahPengeluaran,
                    )
                    HorizontalDivider()
                    Baris("Jenis", if (masuk) "Pemasukan" else "Pengeluaran")
                    Baris("Kategori", t.kategori.label)
                    Baris("Tanggal", formatTanggalPanjang(t.tanggal))
                    Baris("Catatan", t.catatan.ifBlank { "-" })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { onEdit(t.id) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Text("  Edit")
                }
                Button(
                    onClick = { konfirmasiHapus = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Text("  Hapus")
                }
            }
        }
    }

    if (konfirmasiHapus) {
        AlertDialog(
            onDismissRequest = { konfirmasiHapus = false },
            title = { Text("Hapus transaksi?") },
            text = { Text("Transaksi yang dihapus tidak bisa dikembalikan.") },
            confirmButton = {
                TextButton(onClick = {
                    konfirmasiHapus = false
                    viewModel.hapus(onSelesai = onKembali)
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { konfirmasiHapus = false }) { Text("Batal") } },
        )
    }
}

@Composable
private fun Baris(label: String, nilai: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, Modifier.weight(0.35f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(nilai, Modifier.weight(0.65f))
    }
}
