package com.catatuang.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catatuang.app.data.JenisTransaksi
import com.catatuang.app.data.Kategori
import com.catatuang.app.data.Transaksi
import com.catatuang.app.ui.theme.HijauPemasukan
import com.catatuang.app.ui.theme.MerahPengeluaran
import com.catatuang.app.util.formatRupiah
import com.catatuang.app.util.formatTanggal

fun Kategori.icon(): ImageVector = when (this) {
    Kategori.MAKAN -> Icons.Filled.Restaurant
    Kategori.TRANSPORTASI -> Icons.Filled.DirectionsBus
    Kategori.KOS -> Icons.Filled.Home
    Kategori.KULIAH -> Icons.Filled.School
    Kategori.HIBURAN -> Icons.Filled.Movie
    Kategori.LAINNYA -> Icons.Filled.MoreHoriz
}

@Composable
fun TransaksiItem(
    transaksi: Transaksi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tampilkanTanggal: Boolean = true,
) {
    val masuk = transaksi.jenis == JenisTransaksi.PEMASUKAN
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                transaksi.kategori.icon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(transaksi.kategori.label, style = MaterialTheme.typography.bodyLarge)
            val sub = buildList {
                if (tampilkanTanggal) add(formatTanggal(transaksi.tanggal))
                if (transaksi.catatan.isNotBlank()) add(transaksi.catatan)
            }.joinToString(" · ")
            if (sub.isNotEmpty()) {
                Text(
                    sub,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            (if (masuk) "+" else "-") + formatRupiah(transaksi.nominal),
            color = if (masuk) HijauPemasukan else MerahPengeluaran,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun KosongInfo(teks: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(teks, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}
