# Kumpulan Activity Diagram Berdasarkan Use Case

Berikut adalah detail Activity Diagram untuk masing-masing sistem (Use Case) yang telah disesuaikan 100% dengan alur *source code* Java pada aplikasi HotelReservation Anda.

## 👨‍💼 Modul Admin

### UC1: Manajemen Kamar
Menjelaskan alur CRUD (Create, Read, Update, Delete) pada form `FormKamar.java`.
```mermaid
stateDiagram-v2
    [*] --> BukaFormKamar
    BukaFormKamar --> TampilDaftarKamar : loadTable()
    
    state PilihanAksi <<choice>>
    TampilDaftarKamar --> PilihanAksi : Admin memilih aksi
    
    PilihanAksi --> TambahKamar : Klik "Tambah Kamar"
    PilihanAksi --> EditKamar : Pilih baris & Klik "Edit"
    PilihanAksi --> HapusKamar : Pilih baris & Klik "Hapus"
    
    TambahKamar --> ValidasiInputKamar : Input data & Simpan
    EditKamar --> ValidasiInputKamar : Ubah data & Simpan
    
    state isValidKamar <<choice>>
    ValidasiInputKamar --> isValidKamar
    isValidKamar --> SimpanDBKamar : [Input Valid]
    isValidKamar --> ErrorKamar : [Input Kosong]
    
    HapusKamar --> KonfirmasiHapusKamar
    state isConfirmKamar <<choice>>
    KonfirmasiHapusKamar --> isConfirmKamar
    isConfirmKamar --> HapusDBKamar : [Yes]
    isConfirmKamar --> TampilDaftarKamar : [No]
    
    SimpanDBKamar --> TampilDaftarKamar : Sukses
    HapusDBKamar --> TampilDaftarKamar : Sukses
    ErrorKamar --> TampilDaftarKamar
```

### UC2: Manajemen User
Menjelaskan alur penambahan dan pengeditan User beserta enkripsi sandi pada `FormUser.java`.
```mermaid
stateDiagram-v2
    [*] --> BukaFormUser
    BukaFormUser --> TampilDaftarUser : loadTable()
    
    state PilihanUser <<choice>>
    TampilDaftarUser --> PilihanUser
    
    PilihanUser --> TambahUser : Input Data & Klik "Tambah"
    PilihanUser --> EditUser : Pilih baris, Input Data & Klik "Edit"
    PilihanUser --> HapusUser : Pilih baris & Klik "Hapus"
    
    TambahUser --> EnkripsiSandi : PasswordUtil.hashSHA256()
    EditUser --> EnkripsiSandi : Jika sandi diubah
    
    EnkripsiSandi --> SimpanDBUser : UserDAO.save() / update()
    SimpanDBUser --> TampilDaftarUser : Sukses
    
    HapusUser --> KonfirmasiHapusUser
    state isConfirmUser <<choice>>
    KonfirmasiHapusUser --> isConfirmUser
    isConfirmUser --> HapusDBUser : [Yes]
    isConfirmUser --> TampilDaftarUser : [No]
    
    HapusDBUser --> TampilDaftarUser
```

### UC3 & UC4: Melihat Laporan & Histori Pembayaran
Digabungkan karena keduanya berada dalam satu alur di `FormLaporan.java`.
```mermaid
stateDiagram-v2
    [*] --> BukaFormLaporan
    BukaFormLaporan --> InputRentangTanggal : Admin memilih Tanggal Mulai & Akhir
    
    InputRentangTanggal --> EksekusiFilter : Klik "Cari"
    EksekusiFilter --> TarikDataReservasi : ReservasiDAO.findByDateRange()
    TarikDataReservasi --> TarikDataPembayaran : PembayaranDAO.findByDateRange()
    
    TarikDataPembayaran --> TampilkanTabel : Perbarui tabel UI
    
    state OpsiEkspor <<choice>>
    TampilkanTabel --> OpsiEkspor : Admin ingin ekspor?
    
    OpsiEkspor --> PilihFolderEkspor : Klik "Export ke Excel"
    OpsiEkspor --> [*] : Selesai
    
    PilihFolderEkspor --> ProsesEkspor : CSVExporter.exportTableToCSV()
    ProsesEkspor --> TampilkanSukses : File tersimpan
    TampilkanSukses --> [*]
```

---

## 👩‍💻 Modul Kasir

### UC5: Kelola Data Tamu
Mirip dengan UC1, mengatur data tamu pada `FormTamu.java`.
```mermaid
stateDiagram-v2
    [*] --> BukaFormTamu
    BukaFormTamu --> TampilDaftarTamu : loadTable()
    
    state PilihanTamu <<choice>>
    TampilDaftarTamu --> PilihanTamu : Kasir memilih aksi
    
    PilihanTamu --> TambahTamu : Input data & Tambah
    PilihanTamu --> EditTamu : Pilih baris & Edit
    PilihanTamu --> HapusTamu : Pilih baris & Hapus
    
    TambahTamu --> ValidasiNoHp : Cek angka
    EditTamu --> ValidasiNoHp : Cek angka
    
    state isValidHp <<choice>>
    ValidasiNoHp --> isValidHp
    isValidHp --> SimpanDBTamu : [Valid]
    isValidHp --> ErrorHp : [Tidak Valid]
    ErrorHp --> TampilDaftarTamu
    
    SimpanDBTamu --> TampilDaftarTamu : Sukses
    HapusTamu --> HapusDBTamu : Konfirmasi -> Delete
    HapusDBTamu --> TampilDaftarTamu
```

### UC6: Reservasi Kamar
Penyewaan kamar baru di `FormReservasi.java` dan divalidasi oleh `ReservasiService.java`.
```mermaid
stateDiagram-v2
    [*] --> BukaFormReservasi
    BukaFormReservasi --> InputDataReservasi : Pilih Tamu, Kamar, Tgl Check-in & Check-out
    InputDataReservasi --> ProsesReservasi : Klik "Simpan Reservasi"
    
    ProsesReservasi --> ValidasiKamar : Cek status kamar di DB
    
    state isTersedia <<choice>>
    ValidasiKamar --> isTersedia
    
    isTersedia --> SimpanReservasiDB : [Kamar == TERSEDIA]
    isTersedia --> GagalkanReservasi : [Kamar != TERSEDIA]
    
    SimpanReservasiDB --> UpdateStatusKamar : Kamar di-set "DIPESAN"
    UpdateStatusKamar --> SuksesReservasi : Notifikasi berhasil
    SuksesReservasi --> [*]
    
    GagalkanReservasi --> [*] : Notifikasi Error
```

### UC7: Proses Check-In
Mengganti status saat tamu tiba di hotel (`FormCheckIn.java`).
```mermaid
stateDiagram-v2
    [*] --> BukaFormCheckIn
    BukaFormCheckIn --> TampilReservasiDipesan : Filter status == DIPESAN
    
    TampilReservasiDipesan --> PilihReservasiCI : Kasir memilih baris
    PilihReservasiCI --> EksekusiCI : Klik "Proses Check-In"
    
    EksekusiCI --> UpdateReservasiCI : Set status Reservasi -> CHECK_IN
    UpdateReservasiCI --> UpdateKamarCI : Set status Kamar -> TERISI
    
    UpdateKamarCI --> TampilReservasiDipesan : Refresh UI
    TampilReservasiDipesan --> [*] : Selesai
```

### UC8: Proses Check-Out
Mengganti status saat tamu meninggalkan kamar (`FormCheckOut.java`).
```mermaid
stateDiagram-v2
    [*] --> BukaFormCheckOut
    BukaFormCheckOut --> TampilReservasiCheckIn : Filter status == CHECK_IN
    
    TampilReservasiCheckIn --> PilihReservasiCO : Kasir memilih baris
    PilihReservasiCO --> EksekusiCO : Klik "Proses Check-Out"
    
    EksekusiCO --> UpdateReservasiCO : Set status Reservasi -> SELESAI
    UpdateReservasiCO --> UpdateKamarCO : Set status Kamar -> TERSEDIA
    
    UpdateKamarCO --> TampilReservasiCheckIn : Refresh UI
    TampilReservasiCheckIn --> [*] : Siap untuk Pembayaran
```

### UC9: Proses Pembayaran
Menyelesaikan pembayaran dan pembuatan struk (`FormPembayaran.java`).
```mermaid
stateDiagram-v2
    [*] --> BukaFormPembayaran
    BukaFormPembayaran --> TampilReservasiSelesai : Filter status == SELESAI & Belum Dibayar
    
    TampilReservasiSelesai --> PilihReservasiBayar : Kasir memilih baris
    PilihReservasiBayar --> KalkulasiTotal : Hitung = Lama Menginap * Harga Kamar
    KalkulasiTotal --> TampilTagihan : Tampilkan nominal Rp. di UI
    
    TampilTagihan --> InputUangKasir : Kasir mengetik nominal uang bayar
    InputUangKasir --> KalkulasiKembalian : (Uang Bayar - Total) secara Real-Time
    
    KalkulasiKembalian --> KlikBayar : Klik "Bayar & Cetak Struk"
    KlikBayar --> ValidasiUang
    
    state isUangCukup <<choice>>
    ValidasiUang --> isUangCukup
    
    isUangCukup --> SimpanPembayaranDB : [Bayar >= Total]
    isUangCukup --> ErrorUangKurang : [Bayar < Total]
    
    SimpanPembayaranDB --> GenerateStruk : ReceiptGenerator.generateReceipt()
    GenerateStruk --> CetakSelesai : Struk tersimpan di /struk/
    CetakSelesai --> TampilReservasiSelesai : Refresh tabel
    
    ErrorUangKurang --> InputUangKasir
```
