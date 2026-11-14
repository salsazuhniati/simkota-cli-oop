# 🏙️ SimKota CLI: Ultimate Metropolis Edition

**SimKota CLI** adalah game simulasi pembangunan dan manajemen kota (*city-builder*) berbasis teks (*console-based*) yang dikembangkan menggunakan bahasa pemrograman **Java** dengan menerapkan seluruh pilar **Object-Oriented Programming (OOP)**.

Pemain berperan sebagai seorang **Wali Kota** yang bertugas membangun, menata ruang, meningkatkan level infrastruktur, serta menyeimbangkan ekonomi dan kesejahteraan warga selama **15 ronde** di atas peta tata kota berukuran **8×10 petak (80 sel)**.

## 🎯 Fitur Utama Permainan

1. **Peta Tata Kota Interaktif 8×10 Berwarna (ANSI Grid Map)**

   * Memiliki 4 jenis medan alami: **Tanah Kosong (`.`)**, **Sungai (`~~~`)**, **Laut Maritim (`===`)**, dan **Bukit (`^^^`)**.

   * Validasi tata ruang otomatis: bangunan darat hanya bisa didirikan di atas tanah, sedangkan **Pelabuhan Maritim** hanya dapat dibangun di pesisir laut atau tepi perairan.

2. **86 Jenis Infrastruktur Unik dalam 9 Kategori**

   * **Hunian & Akomodasi (8):** Gubuk Pinggir Sawah, Kost-Kostan, Perumahan Subsidi, Perumahan Menengah, Perumahan Kaya Raya, Kawasan Villa, Apartemen Bertingkat, Hotel Bintang Lima.

   * **Utilitas & Kebersihan (4):** Pembangkit Listrik, Instalasi Air PDAM, Pom Bensin (SPBU), TPA Sampah Kota.

   * **Pelayanan Publik, Hukum & Militer (13):** Kantor Balai Desa, Kantor Pemerintahan, Kantor Pajak, Rumah Sakit, Klinik Kesehatan, Apotek, Pemadam Kebakaran, Kantor Polisi, Penjara / Lapas, Markas Militer, Lawfirm, Pengadilan Negeri, Area Makam.

   * **Pendidikan & Pengasuhan (8):** Daycare Anak, Sekolah PAUD & TK, Sekolah Dasar (SD), SMP, SMA, Universitas, Politeknik, Perpustakaan Kota.

   * **Komersial, Kuliner & Gaya Hidup (15):** Warung 24 Jam, Warteg, Kafe, Restoran, Supermarket, Shopping Mall, Toko Alat Sekolah & ATK, Toko Sepatu & Tas, Toko Baju & Kain, Toko Perhiasan, Toko Elektronik, Toko Alat Musik, Showcase Otomotif, Pet Shop, Toko Bangunan.

   * **Rekreasi, Seni, Sejarah & Olahraga (17):** Taman Kota, Taman Bermain Anak, Alun-Alun Kota, Monumen Kota, Museum Bersejarah, Tempat Karaoke, Panggung Musik, Dance Studio, Studio Foto, Studio Musik, Lapangan Sepak Bola, Lapangan Tenis, Lapangan Basket, Gedung Olahraga (GOR), Lapangan Golf, Wahana Air, Kolam Pemancingan.

   * **Industri & Agrikultur (5):** Lahan Sawah, Peternakan, Kawasan Industri, Pelabuhan Maritim, Kilang/Pabrik Pom Bensin.

   * **Transportasi & Infrastruktur Jalan (10):** Jalan Raya, Jalan Tol, Gerbang Tol, Pangkalan Gojek, Halte Bus, Pangkalan Taksi, Rel Kereta, Stasiun Kereta Api, Jalur Kereta Cepat, Bandara Internasional.

   * **Tempat Ibadah & Sosial (6):** Masjid Agung, Gereja, Klenteng, Wihara, Pura, Panti Asuhan.

3. **Sistem Upgrade & Penggusuran (Demolish)**

   * Setiap bangunan dapat di-*upgrade* dari **Level 1 hingga Level 3** untuk melipatgandakan kapasitas dan pendapatannya.

   * Pemain dapat menggusur bangunan yang sudah berdiri untuk menata ulang kota dengan pengembalian dana (*refund*) sebesar **50%**.

4. **Manajemen Sumber Daya, Pajak & Random Event**

   * Keseimbangan suplai **Listrik**, **Air PDAM**, **Kapasitas Sampah TPA**, serta tingkat **Keamanan Kota** yang memengaruhi dampak kejadian acak (*kebakaran, kriminalitas, wisuda mahasiswa Politeknik, hingga panen raya*).

   * Pengaturan tarif pajak bertingkat (**Rendah 5%**, **Normal 10%**, **Tinggi 20%**) serta papan peringkat skor (**Leaderboard**).

## 🏗️ Arsitektur OOP (13 Class)

Proyek ini diorganisasikan ke dalam **13 class** modular:

* `Main.java` — *Entry point* aplikasi dan pengelola siklus bermain ulang (*play again*).

* `GameEngine.java` — Pengendali alur permainan (*game loop* 15 ronde), menu 9 kategori, *random event*, dan *leaderboard*.

* `Kota.java` — Entitas utama penyimpan status sumber daya kota, matriks peta `Bangunan[8][10]`, validasi koordinat, serta kalkulasi ronde.

* `WaliKota.java` — Entitas pemain yang menghitung skor akhir dan menentukan predikat gelar kepemimpinan.

* `Bangunan.java` *(Abstract Class)* — Blueprint dasar seluruh infrastruktur yang mendefinisikan atribut umum, mekanisme `upgrade()`, serta *abstract method* `jalankanEfekRonde(Kota)` dan `getKodePetaBerwarna()`.

* **9 Subclass Turunan `Bangunan`:**

  1. `Hunian.java`

  2. `Utilitas.java`

  3. `PelayananPublik.java`

  4. `Pendidikan.java`

  5. `Komersial.java`

  6. `RekreasiOlahraga.java`

  7. `IndustriAgrikultur.java`

  8. `Transportasi.java`

  9. `TempatIbadahSosial.java`

### Ringkasan Penerapan Pilar OOP

* **Encapsulation:** Seluruh atribut menggunakan modifier `private` dengan akses melalui *getter* dan *mutator* tervalidasi (misal pembatasan nilai kebahagiaan `0–100%`).

* **Inheritance & Abstraction:** `Bangunan` dibuat `abstract` untuk mencegah instansiasi objek generik dan mewariskan perilaku dasar ke 9 subclass kategori.

* **Polymorphism:** Method `jalankanEfekRonde(Kota)` dan `getKodePetaBerwarna()` di-*override* oleh masing-masing subclass sehingga diproses secara dinamis melalui satu `ArrayList<Bangunan>`.

* **Composition & Association:** `GameEngine` mengelola komposisi `Kota` dan `WaliKota`, sedangkan `Kota` mengagregasi banyak objek `Bangunan`.

## 📂 Struktur Direktori Proyek

```
tugas-project-oop/
├── .gitignore
├── README.md
└── simkota/
    ├── Main.java
    ├── GameEngine.java
    ├── Kota.java
    ├── WaliKota.java
    ├── Bangunan.java
    ├── Hunian.java
    ├── Utilitas.java
    ├── PelayananPublik.java
    ├── Pendidikan.java
    ├── Komersial.java
    ├── RekreasiOlahraga.java
    ├── IndustriAgrikultur.java
    ├── Transportasi.java
    └── TempatIbadahSosial.java

```

## 🚀 Cara Menjalankan Program

1. Pastikan **Java Development Kit (JDK 17 atau lebih baru)** sudah terpasang.

2. Buka terminal dan masuk ke direktori folder `simkota`:

   ```
   cd simkota
   
   ```

3. Kompilasi seluruh file sumber Java:

   ```
   javac *.java
   
   ```

4. Jalankan program:

   ```
   java Main
   
   ```

## 🎮 Target Kemenangan & Kondisi Kalah

* **Syarat Menang (Bertahan hingga Ronde 15):**

  * Populasi Warga **≥ 1.000 jiwa**

  * Tingkat Kebahagiaan Warga **≥ 65%**

  * Skor Sumber Daya Manusia (SDM) **≥ 60**

* **Kondisi Kalah (*Game Over* Instan):**

  * Kas Kota habis (**< \$0** / Bangkrut)

  * Kebahagiaan Warga merosot tajam (**< 20%** / Demo besar-besaran)

## 👤 Identitas Pengembang

* **Nama:** Salsa Zuhniati

* **NRP:** 3125510059

* **Program Studi:** D3 Teknik Informatika — Politeknik Elektronika Negeri Surabaya (PENS)

* **Mata Kuliah:** Pemrograman Berorientasi Obyek (OOP) 