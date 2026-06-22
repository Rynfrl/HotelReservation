# 🏨 Sistem Reservasi Hotel

Aplikasi Desktop **Sistem Reservasi Hotel** yang dirancang khusus untuk mempermudah operasional perhotelan sehari-hari. Dibangun menggunakan **Java Swing** dengan antarmuka yang telah dimodernisasi secara penuh menggunakan pustaka **FlatLaf**, aplikasi ini menawarkan pengalaman pengguna (UX) yang *clean*, cepat, dan intuitif.

## ✨ Fitur Utama

Aplikasi ini dibagi menjadi dua modul hak akses utama:

### 💼 Modul Admin (Manajemen & Analitik)
- **Dashboard Admin**: Ringkasan statistik performa hotel dalam tata letak kartu yang rapi.
- **Manajemen Kamar**: *Split-pane layout* untuk melihat daftar kamar dan mengedit/menambahkan kamar baru secara efisien.
- **Manajemen User (Pegawai)**: Kelola akses akun untuk Kasir dan Admin lainnya.
- **Laporan & Keuangan**: Tab panel terintegrasi untuk memantau pergerakan transaksi dan riwayat pembayaran harian.

### 🛎️ Modul Kasir (Operasional Front-Desk)
- **Visualisasi Denah Kamar Real-Time**: Status seluruh kamar (Tersedia, Terisi, Dipesan) ditampilkan melalui indikator warna yang otomatis diperbarui *(auto-refresh)*.
- **Manajemen Tamu & Reservasi**: Alur kerja pencatatan identitas tamu dan alokasi kamar *(auto-fill date)* yang sangat cepat.
- **Proses Check-In & Check-Out**: Sistem yang dirancang untuk dapat dioperasikan dengan meminimalisir jumlah klik.
- **Sistem Pembayaran Cerdas**: Fitur kalkulasi otomatis. Kembalian akan dihitung secara *real-time* saat kasir sedang mengetikkan nominal uang yang dibayar.

### 🏢 Fitur Skala Enterprise (Keamanan & Analitik)
- **Keamanan Kriptografi (SHA-256)**: Kata sandi pengguna tidak lagi disimpan dalam wujud teks murni, melainkan diacak menggunakan algoritma *hashing* satu arah standar industri, lengkap dengan fitur *auto-migrate* sandi lama.
- **Visualisasi Data Interaktif**: Tersedia diagram batang (*Bar Chart*) *native* pada halaman Admin yang menampilkan grafik tren pendapatan bulanan tanpa bantuan *library* tambahan.
- **Ekspor Laporan Otomatis**: Seluruh laporan (Reservasi maupun Keuangan) dapat dengan mudah diekspor menjadi format file **.csv** yang kompatibel penuh dengan **Microsoft Excel**.
- **Pencetak Struk Kasir**: Sistem akan merancang dan menyetak bukti pembayaran/struk secara otomatis ke format file lokal setiap kali pembayaran selesai, lengkap dengan rekaman nama Kasir yang bertugas.

## 🛠️ Teknologi yang Digunakan

- **Bahasa Pemrograman**: Java (JDK 8 / 21+)
- **GUI Framework**: Java Swing
- **Tema (Look and Feel)**: [FlatLaf](https://www.formdev.com/flatlaf/) (Flat Light Look and Feel)
- **IDE**: Apache NetBeans

## 🚀 Cara Menjalankan Project (NetBeans)

1. Lakukan *Clone* pada repository ini:
   ```bash
   git clone https://github.com/Rynfrl/HotelReservation.git
   ```
2. Buka aplikasi **Apache NetBeans**.
3. Pilih menu **File** > **Open Project...** lalu arahkan ke folder repository yang baru saja di-*clone*.
4. Klik kanan pada nama proyek di panel *Projects*, lalu pilih **Clean and Build**.
5. Setelah proses *build* selesai, klik kanan lagi dan pilih **Run**.

> **Catatan untuk Pengguna Java 21+**: 
> FlatLaf secara otomatis menggunakan akses *native* untuk mempercantik bar jendela. Untuk menghilangkan *warning* akses native di konsol, project ini telah dikonfigurasi dengan flag `--enable-native-access=ALL-UNNAMED` pada `run.jvmargs`.

## 🤝 Kontribusi
Jika Anda ingin berkontribusi pada proyek ini, silakan buat *Pull Request* atau ajukan *Issue* di repository GitHub ini. Semua masukan sangat dihargai!
