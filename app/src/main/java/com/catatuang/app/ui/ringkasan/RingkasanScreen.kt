package com.catatuang.app.ui.ringkasan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catatuang.app.ui.AppViewModelProvider
import com.catatuang.app.ui.theme.MerahPengeluaran
import com.catatuang.app.util.formatBulan
import com.catatuang.app.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingkasanScreen(
    viewModel: RingkasanViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Ringkasan") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total pengeluaran ${formatBulan(System.currentTimeMillis())}", style = MaterialTheme.typography.labelLarge)
                    Text(
                        formatRupiah(state.totalPengeluaran),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MerahPengeluaran,
                    )
                    Text("${state.jumlahTransaksi} transaksi pengeluaran", style = MaterialTheme.typography.bodySmall)
                }
            }
            OutlinedCard(Modifier.fillMaxWidth()) {
                Text(
                    "Pengeluaran per kategori dan batas anggaran sedang dikembangkan.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
