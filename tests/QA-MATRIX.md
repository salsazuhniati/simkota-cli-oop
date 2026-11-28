# Hasil QA SimKota CLI — TC-01 sampai TC-08

**Kurnia Hary Trisnandha (3125510055)** — QA & Test Case Engineer pada proyek kelompok SimKota CLI. Kedelapan skenario mengacu pada tabel pengujian kelompok dalam gambar `WhatsApp Image 2026-11-19 at 09.56.55.jpeg`. Diuji pada commit `e9ef0a6` (termasuk perbaikan input CLI dan bonus SDM Politeknik), OpenJDK 21.

## Cara menjalankan

Dari root proyek, jalankan `python tests/run_qa.py`. Runner mengompilasi Java di direktori sementara, menjalankan delapan skenario, dan menulis transkrip ke `tests/evidence/qa-test-results.txt`. Alur `Main` dengan input dari berkas diuji terpisah dan direkam di `tests/evidence/main-stdin-reproduction.txt`.

## Matriks berdasarkan tabel pengujian kelompok

| ID dan skenario | Input / aksi yang diuji | Hasil yang dibuktikan | Status |
|---|---|---|---|
| **TC-01 Inisialisasi Game** | Masukkan nama Wali Kota dan kota; tampilkan peta awal. | Peta 8×10, kas $4.000, populasi 100, pajak 10% per sektor, beban/kapasitas sampah 100/250, ronde 1/15. | **Lulus** |
| **TC-02 Pembangunan Infrastruktur** | Pilih Pendidikan → Politeknik [PLT] lewat menu, bangun di (2,2). | Biaya $650, PLT muncul pada peta; SDM **+28** setelah ronde (10 → 38). | **Lulus** |
| **TC-03 Validasi Lokasi Pelabuhan** | Pelabuhan Maritim pada daratan (3,2), lalu laut (2,10). | Daratan bukan pesisir ditolak; petak laut kolom 10 menerima Pelabuhan. | **Lulus** |
| **TC-04 Upgrade & Gusur** | Upgrade rumah pada (2,2) ke Lv.2; gusur rumah lain pada (2,3). | Rumah pertama tetap Lv.2, petak lain kosong, kas bertambah refund $75 (50% harga awal $150). | **Lulus** |
| **TC-05 Kebijakan Pajak Sektoral** | Via menu 4 ubah pajak hunian 5% dan industri 20%. | Tarif baru dan pemasukan dipisah per sektor; contoh satu ronde tanpa bangunan pada populasi awal 100 memberi pajak hunian **$11**, kebahagiaan tetap 75% (+2−2). | **Lulus** |
| **TC-06 Penalti Utilitas & Sampah TPA** | Bangun lima rumah sehingga populasi 275 dan kebutuhan utilitas meningkat. | Kebutuhan listrik/air melebihi suplai, beban sampah 275 > kapasitas 250; tiga peringatan tampil, kebahagiaan turun 75% → 62%. | **Lulus** |
| **TC-07 Kondisi Kalah (Game Over)** | (1) Bangun PLTN lalu lanjutkan hingga bahagia <20%; (2) pada game baru siapkan kas negatif, lewati satu giliran. | Kedua cabang menampilkan pesan Game Over masing-masing, berhenti sebelum ronde 15, lalu menunjukkan status kalah, gelar evaluasi, dan leaderboard. | **Lulus dua cabang** |
| **TC-08 Kondisi Menang & Leaderboard** | Bermain 15 ronde memakai aksi bangun hunian, listrik, air, TPA dan Politeknik, kemudian lewati sisa giliran. | Ronde 15 tercapai; populasi ≥1.000, kebahagiaan ≥65%, SDM ≥60; status MENANG, rekap, skor/gelar, leaderboard tampil. | **Lulus** |

**Ringkasan:** 8 skenario lulus, 0 gagal. Bonus Politeknik lewat menu kini +28 sesuai tabel. TC-07 menguji dua cabang kekalahan. Untuk cabang kas negatif, penguji menyiapkan saldo −$10.000 pada objek `Kota` milik sebuah `GameEngine` terpisah melalui refleksi, lalu menjalankan menu 6; saldo akhir tetap negatif, pesan kebangkrutan tampil dan game berhenti pada ronde 1. Ini pengujian kondisi internal, **bukan** klaim bahwa kas dapat dibuat negatif lewat menu bangun biasa (belanja ditolak jika kas kurang). Hasil akhir TC-08 berubah karena event acak sehingga suite memeriksa batas target dan status akhir. Lima kali pengulangan suite penuh semuanya lulus. TC-02/05/07/08 melewati menu; TC-03/04/06 memakai model langsung untuk memisahkan efek acak.

## Regresi CLI-INPUT-01

Entry point `Main` sekarang meneruskan `Scanner` yang sama ke `GameEngine`; balasan `y/n` dibaca sebagai token dan sisa baris dibersihkan agar nama permainan berikutnya terbaca utuh. Input yang dipipe (nama Wali Kota, nama kota, lima belas opsi `6`, lalu `n`) selesai dengan exit code 0; kasus bermain ulang `y` lalu kota kedua juga lulus. Lihat `tests/evidence/main-stdin-reproduction.txt`.

## Bukti terminal dan kelanjutan manual

Transkrip `.txt` mencatat output pengujian otomatis; itu bukan screenshot aplikasi. Untuk laporan akhir kelompok, ambil screenshot terminal saat skenario berjalan, terutama peta awal, penolakan Pelabuhan di darat, kedua pesan Game Over, serta status MENANG/leaderboard. Penggunaan terminal interaktif tetap dapat dicoba sebagai playtesting tambahan.
