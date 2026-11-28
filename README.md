# 🏙️ SimKota CLI: Ultimate Metropolis Edition

**SimKota CLI** adalah game simulasi pembangunan dan manajemen tata kota berbasis teks (*console-based*) yang dikembangkan menggunakan bahasa pemrograman **Java**. Proyek ini dirancang sebagai implementasi menyeluruh konsep **Pemrograman Berorientasi Obyek (OOP)** dengan arsitektur **13 Class**, **9 Kategori Infrastruktur**, serta **109 jenis bangunan unik**.

## 🌟 Fitur Utama Permainan

1. **Peta Tata Kota Interaktif (Grid 8×10 = 80 Petak):**
   * Menampilkan visualisasi peta berwarna (ANSI Colors) lengkap dengan variasi medan alami: **Tanah Kosong** (`.`), **Sungai** (`~~~`), **Laut Maritim** (`===`), dan **Bukit** (`^^^`).
   * Validasi lokasi otomatis (misalnya *Pelabuhan Maritim* wajib dibangun di perairan atau tepi laut/sungai).

2. **109 Jenis Infrastruktur dalam 9 Kategori:**
   * Mulai dari bangunan berskala kecil (*Gubuk Pinggir Sawah*, *Pompa Air*, *Warung 24 Jam*, *Musholla*, *Rental PS*) hingga fasilitas skala metropolis (*Pembangkit Nuklir/PLTN*, *Instalasi Pengolahan Air/WTP*, *Politeknik*, *Jalur Kereta Cepat*, *Stadion Utama*, *Markas Militer*, *Pelabuhan Maritim*).

3. **Kebijakan Pajak Sektoral Terpisah:**
   * Wali Kota dapat mengatur tarif pajak secara independen untuk tiga sektor utama: **Pajak Hunian**, **Pajak Komersial**, dan **Pajak Industri** (masing-masing dengan opsi **Rendah 5%**, **Normal 10%**, atau **Tinggi 20%**).

4. **Sistem Upgrade Bangunan (Level 1 → Level 3):**
   * Pemain dapat meningkatkan level bangunan yang sudah berdiri (biaya 75% dari harga bangun) untuk melipatgandakan output warga, pajak, SDM, keamanan, maupun pendapatan kas per ronde.

5. **Fitur Gusur / Demolish:**
   * Memungkinkan penataan ulang tata ruang kota dengan mengembalikan dana kas sebesar **50%** dari biaya bangun awal.

6. **Manajemen Sumber Daya, TPA Sampah & Leaderboard:**
   * Keseimbangan antara **Suplai Listrik**, **Air PDAM**, **Kapasitas Sampah TPA**, **Tingkat Keamanan**, **Skor SDM**, **Random Event** antar-ronde, serta **Papan Peringkat (Leaderboard)** Wali Kota.

## 🏗️ Arsitektur OOP & Struktur Proyek

Proyek ini menerapkan pilar utama OOP secara terstruktur:

* **Encapsulation:** Seluruh atribut bersifat `private` dengan kontrol akses melalui *getter* dan *mutator* tervalidasi.
* **Abstraction & Inheritance:** `Bangunan` dideklarasikan sebagai `abstract class` induk yang mewariskan atribut serta kontrak *method* ke **9 subclass kategori**.
* **Polymorphism:** Pemrosesan efek seluruh bangunan di akhir ronde dieksekusi secara dinamis melalui `ArrayList<Bangunan>` menggunakan method *override* `jalankanEfekRonde(Kota)`.
* **Composition & Aggregation:** `GameEngine` mengelola `Kota`, `Kota` mengomposisi `WaliKota`, serta mengagregasi objek `Bangunan` pada matriks grid 8×10.

### Struktur Direktori

```
tugas-project-oop/
├── simkota/
│   ├── Main.java                 # Entry point & loop sesi permainan
│   ├── GameEngine.java           # Pengendali alur 15 ronde, katalog 109 bangunan & leaderboard
│   ├── Kota.java                 # Pengelola kas, pajak sektoral, utilitas, TPA & peta 8x10
│   ├── WaliKota.java             # Perhitungan skor akhir & gelar kepemimpinan
│   ├── Bangunan.java             # Abstract class induk (atribut dasar & sistem level 1-3)
│   ├── Hunian.java               # Subclass 1: 8 varian hunian & akomodasi
│   ├── Utilitas.java             # Subclass 2: 10 varian utilitas, energi & kebersihan kota
│   ├── PelayananPublik.java      # Subclass 3: 16 varian pelayanan publik, hukum & sosial
│   ├── Pendidikan.java           # Subclass 4: 8 varian institusi pendidikan & literasi
│   ├── Komersial.java            # Subclass 5: 19 varian komersial, keuangan & kuliner
│   ├── RekreasiOlahraga.java     # Subclass 6: 25 varian rekreasi, seni, hiburan & olahraga
│   ├── IndustriAgrikultur.java   # Subclass 7: 6 varian industri, perkebunan & maritim
│   ├── Transportasi.java         # Subclass 8: 11 varian infrastruktur transportasi
│   └── TempatIbadahSosial.java   # Subclass 9: 6 varian tempat ibadah
├── .gitignore
└── README.md
```

## 🏙️ Katalog 109 Infrastruktur (9 Kategori)

| No | Subclass Kategori | Jumlah | Daftar Infrastruktur |
| :--- | :--- | :---: | :--- |
| 1 | **`Hunian`** | 8 | Rumah Pedesaan, Kost-Kostan, Perumahan Subsidi, Perumahan Menengah, Perumahan Kaya Raya, Kawasan Villa, Apartemen Bertingkat, Hotel Bintang Lima |
| 2 | **`Utilitas`** | 10 | Pembangkit Listrik Dasar, Turbin Kincir Angin, Pembangkit Tenaga Air (PLTA), Pembangkit Nuklir (PLTN), Pompa Air, Instalasi Air PDAM, Instalasi Pengolahan Air (WTP), Pom Bensin (SPBU), TPA Sampah Kota, Air Purifier Kota |
| 3 | **`PelayananPublik`** | 16 | Kantor Balai Desa, Kantor Pemerintahan, Mall Pelayanan Publik, Kantor Pajak, Rumah Sakit, Klinik Kesehatan, Apotek, Pemadam Kebakaran, Kantor Polisi, Penjara / Lapas, Markas Militer, Lawfirm, Pengadilan Negeri, Area Makam, Krematorium, Panti Asuhan |
| 4 | **`Pendidikan`** | 8 | Daycare Anak, Sekolah PAUD & TK, Sekolah Dasar (SD), SMP, SMA, Universitas, Politeknik, Perpustakaan Kota |
| 5 | **`Komersial`** | 19 | Warung 24 Jam, Warteg, Toko Cake & Bakery, Kafe, Restoran, Pasar Tradisional, Supermarket, Shopping Mall, Bank, Toko Alat Sekolah & ATK, Toko Sepatu & Tas, Toko Baju & Kain, Toko Perhiasan, Toko Elektronik, Toko Alat Musik, Showcase Otomotif, Pet Shop, Toko Bangunan, Toko Perabotan Rumah Tangga |
| 6 | **`RekreasiOlahraga`** | 25 | Taman Kota, Taman Bermain Anak, Alun-Alun Kota, Monumen Kota, Museum Bersejarah, Gedung Kesenian, Bioskop, Rental PS, Tempat Karaoke, Panggung Musik, Dance Studio, Studio Foto, Studio Musik, Tempat Pilates, SPA & Relaksasi, Lapangan Sepak Bola, Lapangan Tenis, Lapangan Basket, Lapangan Padel, Gedung Olahraga (GOR), Stadion Utama, Lapangan Golf, Wahana Air, Kolam Pemancingan, Gubuk Pinggir Sawah. |
| 7 | **`IndustriAgrikultur`** | 6 | Lahan Sawah, Perkebunan, Peternakan, Kawasan Industri, Pelabuhan Maritim, Kilang/Pabrik Pom Bensin |
| 8 | **`Transportasi`** | 11 | Jalan Raya, Jalan Tol, Gerbang Tol, Pangkalan Gojek, Halte Bus, Terminal Bus, Pangkalan Taksi, Rel Kereta, Stasiun Kereta Api, Jalur Kereta Cepat, Bandara Internasional |
| 9 | **`TempatIbadahSosial`** | 6 | Musholla, Masjid Agung, Gereja, Klenteng, Wihara, Pura |

## 🚀 Cara Menjalankan Program

1. Pastikan **Java Development Kit (JDK 17 atau lebih baru)** sudah terpasang di sistem Anda.
2. Buka terminal pada direktori utama proyek, lalu masuk ke folder `simkota`:
   ```powershell
   cd simkota
   ```
3. Kompilasi seluruh file `.java`:
   ```powershell
   javac *.java
   ```
4. Jalankan program utama:
   ```powershell
   java Main
   ```

## 🎯 Target Kemenangan (15 Ronde)

* **Kas Kota** tidak boleh defisit (`Kas >= $0`) dan **Kebahagiaan Warga** tidak boleh jatuh di bawah `20%`.
* Pada akhir **Ronde 15**, Wali Kota dinyatakan **MENANG** apabila berhasil mencapai:
  * **Populasi:** `>= 1.000` warga
  * **Kebahagiaan:** `>= 65%`
  * **Skor SDM:** `>= 60`

## 👥 Tim Pengembang

* **Salsa Zuhniati** (3125510059) — D3 PJJ Teknik Informatika, Politeknik Elektronika Negeri Surabaya (PENS)
* **Kurnia Hary Trisnandha** (3125510055) — D3 PJJ Teknik Informatika, Politeknik Elektronika Negeri Surabaya (PENS)
* **Lailatul Farrij** (3125510060) — D3 PJJ Teknik Informatika, Politeknik Elektronika Negeri Surabaya (PENS)
* **Sheilla Cantika Agne Kania** (3125510066) — D3 PJJ Teknik Informatika, Politeknik Elektronika Negeri Surabaya (PENS)