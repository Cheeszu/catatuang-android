# CatatUang

Aplikasi pencatat keuangan sederhana untuk mahasiswa (offline, tanpa akun).
Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · Room · Repository.

## Menjalankan
Buka folder ini di Android Studio, tunggu Gradle sync, lalu Run `app` di emulator/HP (minSdk 24).

## Struktur
```
app/src/main/java/com/catatuang/app/
  data/        Transaksi (entity), TransaksiDao, AppDatabase, TransaksiRepository
  ui/beranda   Beranda: saldo, total bulan ini, 5 transaksi terbaru
  ui/form      Form tambah/edit + validasi nominal
  ui/detail    Detail + hapus dengan konfirmasi
  ui/riwayat   Riwayat dikelompokkan per tanggal
  ui/ringkasan Ringkasan (sementara: total pengeluaran bulan ini)
  ui/navigation NavHost + bottom navigation
```

## Status MVP
- Selesai: tambah/edit/hapus transaksi tersimpan di Room, Beranda dengan saldo, bottom navigation, Detail, Riwayat dasar.
- Sedang dikembangkan: filter Riwayat per bulan & kategori, Ringkasan per kategori.
- Belum: batas anggaran (DataStore) dan peringatan di Beranda.
