package com.catatuang.app.ui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.data.Kategori
import com.catatuang.app.ui.AppViewModelProvider
import com.catatuang.app.ui.components.icon
import com.catatuang.app.util.formatTanggalPanjang
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormTransaksiScreen(
    onSelesai: () -> Unit,
    onKembali: () -> Unit,
    viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var tampilDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.tersimpan) {
        if (state.tersimpan) onSelesai()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.modeEdit) "Edit Transaksi" else "Tambah Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onKembali) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                val pilihan = listOf(JenisTransaksi.PENGELUARAN to "Pengeluaran", JenisTransaksi.PEMASUKAN to "Pemasukan")
                pilihan.forEachIndexed { i, (jenis, label) ->
                    SegmentedButton(
                        selected = state.jenis == jenis,
                        onClick = { viewModel.ubahJenis(jenis) },
                        shape = SegmentedButtonDefaults.itemShape(i, pilihan.size),
                    ) { Text(label) }
                }
            }

            OutlinedTextField(
                value = state.nominalTeks,
                onValueChange = viewModel::ubahNominal,
                label = { Text("Nominal (Rp)") },
                singleLine = true,
                isError = state.errorNominal != null,
                supportingText = state.errorNominal?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Kategori", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Kategori.entries.forEach { k ->
                    FilterChip(
                        selected = state.kategori == k,
                        onClick = { viewModel.ubahKategori(k) },
                        label = { Text(k.label) },
                        leadingIcon = { Icon(k.icon(), contentDescription = null) },
                    )
                }
            }

            OutlinedButton(onClick = { tampilDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Text("  " + formatTanggalPanjang(state.tanggal))
            }

            OutlinedTextField(
                value = state.catatan,
                onValueChange = viewModel::ubahCatatan,
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )

            Button(onClick = viewModel::simpan, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Simpan")
            }
        }
    }

    if (tampilDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = lokalKeUtc(state.tanggal))
        DatePickerDialog(
            onDismissRequest = { tampilDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { viewModel.ubahTanggal(utcKeLokal(it)) }
                    tampilDatePicker = false
                }) { Text("Pilih") }
            },
            dismissButton = { TextButton(onClick = { tampilDatePicker = false }) { Text("Batal") } },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

/** DatePicker bekerja dalam UTC tengah malam; dua fungsi ini mengonversi ke/dari zona waktu HP. */
private fun lokalKeUtc(millisLokal: Long): Long {
    val lokal = Calendar.getInstance().apply { timeInMillis = millisLokal }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(lokal.get(Calendar.YEAR), lokal.get(Calendar.MONTH), lokal.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun utcKeLokal(millisUtc: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millisUtc }
    val sekarang = Calendar.getInstance()
    return Calendar.getInstance().apply {
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH),
            sekarang.get(Calendar.HOUR_OF_DAY), sekarang.get(Calendar.MINUTE), 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
