package com.catatuang.app.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val localeId: Locale = Locale.forLanguageTag("id-ID")

fun formatRupiah(nominal: Long): String {
    val nf = NumberFormat.getNumberInstance(localeId)
    val teks = "Rp" + nf.format(kotlin.math.abs(nominal))
    return if (nominal < 0) "-$teks" else teks
}

fun formatTanggal(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", localeId).format(Date(millis))

fun formatTanggalPanjang(millis: Long): String =
    SimpleDateFormat("EEEE, d MMMM yyyy", localeId).format(Date(millis))

fun formatBulan(millis: Long): String =
    SimpleDateFormat("MMMM yyyy", localeId).format(Date(millis))

/** Awal dan akhir bulan (epoch millis) untuk bulan yang memuat [millis]. */
fun rentangBulan(millis: Long = System.currentTimeMillis()): Pair<Long, Long> {
    val cal = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val awal = cal.timeInMillis
    cal.add(Calendar.MONTH, 1)
    return awal to cal.timeInMillis - 1
}

/** Millis yang sama digeser [jumlah] bulan (negatif = mundur). */
fun geserBulan(millis: Long, jumlah: Int): Long = Calendar.getInstance().apply {
    timeInMillis = rentangBulan(millis).first
    add(Calendar.MONTH, jumlah)
}.timeInMillis

/** Mengubah millis menjadi awal hari, untuk mengelompokkan transaksi per tanggal. */
fun awalHari(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis
