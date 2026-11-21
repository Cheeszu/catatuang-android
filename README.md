# CatatUang

Aplikasi pencatat keuangan sederhana untuk mahasiswa (offline, tanpa akun).
Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · Room · Repository.

## Menjalankan
Buka folder ini di Android Studio, tunggu Gradle sync, lalu Run `app` di emulator/HP (minSdk 24).

## Struktur
```
app/src/main/java/com/catatuang/app/
  data/        Transaksi (entity), TransaksiDao, AppDatabase, TransaksiRepository,
               AnggaranRepository (batas anggaran di DataStore)
  ui/beranda   Beranda: saldo, total bulan ini, peringatan anggaran, 5 transaksi terbaru
  ui/form      Form tambah/edit + validasi nominal
  ui/detail    Detail + hapus dengan konfirmasi
  ui/riwayat   Riwayat per tanggal dengan filter bulan dan kategori
  ui/ringkasan Ringkasan bulanan per kategori + atur batas anggaran
  ui/navigation NavHost + bottom navigation
```

## Tes
`./gradlew testDebugUnitTest` menguji status anggaran, persentase per kategori, dan pergeseran bulan.

## Status
- Selesai: semua fitur di proposal (catat transaksi, Beranda, Riwayat dengan filter, Ringkasan per kategori, batas anggaran).
- Bug yang diketahui: lihat bagian "Bug yang masih ada" di laporan Final Readiness.
