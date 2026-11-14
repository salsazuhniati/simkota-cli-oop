import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class GameEngine {
    private Kota kota;
    private Scanner scanner;
    private int rondeSekarang;
    private final int MAX_RONDE = 15;

    // Penyimpan riwayat skor & peringkat selama sesi aplikasi berjalan
    private static final ArrayList<String[]> leaderboard = new ArrayList<>();

    public GameEngine(String namaWaliKota, String namaKota) {
        WaliKota player = new WaliKota(namaWaliKota);
        this.kota = new Kota(namaKota, player);
        this.scanner = new Scanner(System.in);
        this.rondeSekarang = 1;
    }

    public void mulaiGame() {
        System.out.println("=== SELAMAT DATANG DI SIMKOTA CLI: ULTIMATE METROPOLIS (80+ BANGUNAN) ===");
        System.out.println("Target Menang (Ronde " + MAX_RONDE + "): Populasi >= 1000, Kebahagiaan >= 65%, Skor SDM >= 60\n");

        while (rondeSekarang <= MAX_RONDE) {
            tampilkanStatus();
            prosesMenuAksi();
            kota.prosesRondeBangunan();
            picuRandomEvent();

            if (cekKondisiKalah()) return;
            rondeSekarang++;
        }
        tampilkanHasilAkhir();
    }

    private String buatBar(int nilai, int maks) {
        int blok = (int) Math.round(Math.min(1.0, Math.max(0.0, (double) nilai / maks)) * 8);
        return "[" + "#".repeat(blok) + "-".repeat(8 - blok) + "]";
    }

    private void tampilkanStatus() {
        kota.gambarPetaKota();
        System.out.printf("RONDE %d/%d | Wali Kota: %s | Pajak: %s\n",
                rondeSekarang, MAX_RONDE, kota.getWaliKota().getNama(), kota.getLabelPajak());
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("Kas: $%-6d | Populasi: %-4d/1000 | Keamanan: %d%% | Skor SDM: %d\n",
                kota.getKas(), kota.getPopulasi(), kota.getKeamanan(), kota.getSkorSDM());
        System.out.printf("Bahagia: %s %d%% | Listrik: %s %d/%d | Air: %s %d/%d | TPA: %d/%d\n",
                buatBar(kota.getKebahagiaan(), 100), kota.getKebahagiaan(),
                buatBar(kota.getKebutuhanListrik(), kota.getSuplaiListrik()), kota.getKebutuhanListrik(), kota.getSuplaiListrik(),
                buatBar(kota.getKebutuhanAir(), kota.getSuplaiAir()), kota.getKebutuhanAir(), kota.getSuplaiAir(),
                kota.getPopulasi(), kota.getKapasitasSampah());
        System.out.println("===========================================================================");
    }

    private void prosesMenuAksi() {
        boolean selesaiAksi = false;

        while (!selesaiAksi) {
            System.out.println("\n--- MENU UTAMA WALIKOTA ---");
            System.out.println("1. Bangun Infrastruktur Baru (9 Kategori / 80+ Jenis Bangunan)");
            System.out.println("2. Upgrade Bangunan Berdiri (Lv.1 -> Lv.3)");
            System.out.println("3. Gusur / Hancurkan Bangunan (Refund 50%)");
            System.out.println("4. Atur Kebijakan Pajak");
            System.out.println("5. Lihat Rekap Bangunan & Peringkat Skor");
            System.out.println("6. Lewati Giliran (Lanjut Ronde)");
            System.out.print("Pilihan (1-6): ");

            int pilih = bacaAngka();
            switch (pilih) {
                case 1 -> {
                    Bangunan b = pilihKatalogBangunan();
                    if (b != null) {
                        System.out.print("Masukkan Baris (1-" + Kota.BARIS + "): ");
                        int r = bacaAngka() - 1;
                        System.out.print("Masukkan Kolom (1-" + Kota.KOLOM + "): ");
                        int c = bacaAngka() - 1;
                        if (kota.bangunDiKoordinat(b, r, c)) {
                            System.out.println(">> Sukses membangun " + b.getNama() + " [" + b.getKodePeta() + "]!");
                            selesaiAksi = true;
                        }
                    }
                }
                case 2 -> {
                    System.out.print("Baris bangunan yang di-upgrade (1-" + Kota.BARIS + "): ");
                    int r = bacaAngka() - 1;
                    System.out.print("Kolom bangunan yang di-upgrade (1-" + Kota.KOLOM + "): ");
                    int c = bacaAngka() - 1;
                    if (kota.upgradeDiKoordinat(r, c)) selesaiAksi = true;
                }
                case 3 -> {
                    System.out.print("Baris bangunan yang digusur (1-" + Kota.BARIS + "): ");
                    int r = bacaAngka() - 1;
                    System.out.print("Kolom bangunan yang digusur (1-" + Kota.KOLOM + "): ");
                    int c = bacaAngka() - 1;
                    if (kota.gusurDiKoordinat(r, c)) selesaiAksi = true;
                }
                case 4 -> ubahPajak();
                case 5 -> {
                    kota.tampilkanRekapBangunan();
                    tampilkanLeaderboard();
                }
                case 6 -> {
                    System.out.println(">> Melanjutkan ke ronde berikutnya...");
                    selesaiAksi = true;
                }
                default -> System.out.println(">> Pilihan tidak valid.");
            }
        }
    }

    private Bangunan pilihKatalogBangunan() {
        System.out.println("\n=== KATALOG 9 KATEGORI INFRASTRUKTUR ===");
        System.out.println("1. Hunian & Akomodasi (Gubuk Sawah, Kost, Subsidi, Menengah, Kaya, Villa, Apartemen, Hotel)");
        System.out.println("2. Utilitas & Kebersihan (Listrik, Air PDAM, Pom Bensin, TPA Sampah)");
        System.out.println("3. Pelayanan Publik, Hukum & Militer (Balai Desa, Kantor Pemerintahan/Pajak, RS, Klinik, Apotek, Damkar, Polisi, Penjara, Markas Militer, Lawfirm, Pengadilan Negeri, Makam)");
        System.out.println("4. Pendidikan & Pengasuhan (Daycare, PAUD/TK, SD, SMP, SMA, Universitas, Politeknik)");
        System.out.println("5. Komersial, Kuliner & Gaya Hidup (Warung 24 Jam, Warteg, Kafe, Restoran, Supermarket, Mall, Toko ATK/Sekolah, Sepatu/Tas, Baju/Kain, Perhiasan, Elektronik, Alat Musik, Showroom Otomotif, Pet Shop)");
        System.out.println("6. Rekreasi, Seni & Olahraga (Taman Kota, Taman Bermain Anak, Alun-alun, Monumen, Karaoke, Panggung Musik, Dance/Foto/Musik Studio, Lap. Bola/Tenis/Basket/Golf, GOR, Wahana Air, Pemancingan)");
        System.out.println("7. Industri & Agrikultur (Sawah, Peternakan, Kawasan Industri, Pelabuhan Maritim, Pabrik Pom Bensin)");
        System.out.println("8. Transportasi & Infrastruktur Jalan (Jalan Raya, Jalan Tol, Gerbang Tol, Halte Bus, Pangkalan Taksi, Pangkalan Gojek, Stasiun Kereta, Rel, Kereta Cepat, Bandara)");
        System.out.println("9. Tempat Ibadah & Sosial (Masjid, Gereja, Klenteng, Wihara, Pura, Panti Asuhan)");
        System.out.println("0. Batal");
        System.out.print("Pilih Kategori (0-9): ");

        return switch (bacaAngka()) {
            case 1 -> menuHunian();
            case 2 -> menuUtilitas();
            case 3 -> menuPublik();
            case 4 -> menuPendidikan();
            case 5 -> menuKomersial();
            case 6 -> menuRekreasi();
            case 7 -> menuIndustri();
            case 8 -> menuTransportasi();
            case 9 -> menuIbadahSosial();
            default -> null;
        };
    }

    private Bangunan menuHunian() {
        System.out.println("\n-- 1. HUNIAN & AKOMODASI --");
        System.out.println("1. Gubuk Pinggir Sawah ($100)  -> +20 Warga, +$15 Pajak");
        System.out.println("2. Rumah Pedesaan ($150)       -> +35 Warga, +$25 Pajak");
        System.out.println("3. Kost-Kostan ($180)          -> +50 Warga, +$45 Pajak");
        System.out.println("4. Perumahan Subsidi ($250)    -> +80 Warga, +$40 Pajak");
        System.out.println("5. Perumahan Menengah ($400)   -> +70 Warga, +$110 Pajak");
        System.out.println("6. Perumahan Kaya Raya ($650)  -> +50 Warga, +$240 Pajak");
        System.out.println("7. Kawasan Villa ($800)        -> +25 Warga, +$300 Pajak, +5% Bahagia");
        System.out.println("8. Apartemen Bertingkat ($900) -> +180 Warga, +$280 Pajak");
        System.out.println("9. Hotel Bintang Lima ($950)   -> +40 Warga, +$360 Kas, +4% Bahagia");
        System.out.print("Pilih (1-9): ");
        return switch (bacaAngka()) {
            case 1 -> new Hunian("Gubuk Pinggir Sawah", "GBK", 100, 5, 20, 15, 2, 2);
            case 2 -> new Hunian("Rumah Pedesaan", "RPD", 150, 10, 35, 25, 5, 5);
            case 3 -> new Hunian("Kost-Kostan", "KST", 180, 12, 50, 45, 8, 8);
            case 4 -> new Hunian("Perumahan Subsidi", "HSB", 250, 18, 80, 40, 12, 12);
            case 5 -> new Hunian("Perumahan Menengah", "HMN", 400, 30, 70, 110, 16, 16);
            case 6 -> new Hunian("Perumahan Kaya Raya", "HKY", 650, 50, 50, 240, 25, 20);
            case 7 -> new Hunian("Kawasan Villa", "VLA", 800, 60, 25, 300, 20, 25);
            case 8 -> new Hunian("Apartemen Bertingkat", "APR", 900, 75, 180, 280, 35, 35);
            case 9 -> new Hunian("Hotel Bintang Lima", "HTL", 950, 80, 40, 360, 30, 30);
            default -> null;
        };
    }

    private Bangunan menuUtilitas() {
        System.out.println("\n-- 2. UTILITAS & KEBERSIHAN --");
        System.out.println("1. Pembangkit Listrik ($400)   -> +60 MW Listrik");
        System.out.println("2. Instalasi Air PDAM ($350)   -> +60 m3 Air Bersih");
        System.out.println("3. Pom Bensin / SPBU ($300)    -> +$100 Kas/ronde");
        System.out.println("4. TPA Pembuangan Akhir ($350) -> +500 Kapasitas Sampah, -2% Bahagia");
        System.out.println("5. Air Purifier Kota ($300)    -> +5% Bahagia (Udara Bersih)");
        System.out.print("Pilih (1-5): ");
        return switch (bacaAngka()) {
            case 1 -> new Utilitas("Pembangkit Listrik", "PLK", 400, 35, 60, 0, 0, -2);
            case 2 -> new Utilitas("Air PDAM", "PDM", 350, 25, 0, 60, 25, 2);
            case 3 -> new Utilitas("Pom Bensin", "SPB", 300, 20, 0, 0, 100, 0);
            case 4 -> new Utilitas("TPA Sampah Kota", "TPA", 350, 30, 0, 0, 15, -2);
            case 5 -> new Utilitas("Air Purifier Kota", "APF", 300, 25, 0, 0, 0, 5);
            default -> null;
        };
    }

    private Bangunan menuPublik() {
        System.out.println("\n-- 3. PELAYANAN PUBLIK, HUKUM & MILITER --");
        System.out.println("1. Kantor Balai Desa ($250)      -> +4% Bahagia, +5% Keamanan");
        System.out.println("2. Kantor Pemerintahan ($500)    -> +6% Bahagia, +8% Keamanan");
        System.out.println("3. Mall Pelayanan Publik ($550)  -> +8% Bahagia, +10% Keamanan");
        System.out.println("4. Kantor Pajak ($450)           -> +3% Bahagia, +5% Keamanan");
        System.out.println("5. Rumah Sakit ($600)            -> +10% Bahagia, +5% Keamanan");
        System.out.println("6. Klinik ($300) | 7. Apotek ($200)");
        System.out.println("8. Pemadam Kebakaran ($350) | 9. Kantor Polisi ($350) -> +15% Keamanan");
        System.out.println("10. Penjara / Lapas ($500)       -> +25% Keamanan, -2% Bahagia");
        System.out.println("11. Markas Militer/Tentara ($850)-> +40% Keamanan");
        System.out.println("12. Lawfirm ($400) | 13. Pengadilan Negeri ($550)");
        System.out.println("14. Area Makam ($150) | 15. Krematorium ($200) | 16. Panti Asuhan ($220)");
        System.out.print("Pilih (1-16): ");
        return switch (bacaAngka()) {
            case 1 -> new PelayananPublik("Kantor Balai Desa", "KBD", 250, 20, 4, 5);
            case 2 -> new PelayananPublik("Kantor Pemerintahan", "KPM", 500, 40, 6, 8);
            case 3 -> new PelayananPublik("Mall Pelayanan Publik", "MPP", 550, 45, 8, 10);
            case 4 -> new PelayananPublik("Kantor Pajak", "KPJ", 450, 35, 3, 5);
            case 5 -> new PelayananPublik("Rumah Sakit", "RSU", 600, 45, 10, 5);
            case 6 -> new PelayananPublik("Klinik Kesehatan", "KLN", 300, 20, 5, 0);
            case 7 -> new PelayananPublik("Apotek", "APT", 200, 12, 3, 0);
            case 8 -> new PelayananPublik("Pemadam Kebakaran", "PMK", 350, 25, 2, 15);
            case 9 -> new PelayananPublik("Kantor Polisi", "POL", 350, 25, 2, 15);
            case 10 -> new PelayananPublik("Penjara / Lapas", "PNJ", 500, 35, -2, 25);
            case 11 -> new PelayananPublik("Markas Militer", "MLT", 850, 70, 0, 40);
            case 12 -> new PelayananPublik("Lawfirm", "LAW", 400, 30, 4, 8);
            case 13 -> new PelayananPublik("Pengadilan Negeri", "PDL", 550, 45, 6, 14);
            case 14 -> new PelayananPublik("Area Makam", "MKM", 150, 10, 2, 0);
            case 15 -> new PelayananPublik("Krematorium", "KRM", 200, 15, 3, 0);
            case 16 -> new PelayananPublik("Panti Asuhan", "PNT", 220, 15, 7, 5);
            default -> null;
        };
    }

    private Bangunan menuPendidikan() {
        System.out.println("\n-- 4. PENDIDIKAN & PENGASUHAN --");
        System.out.println("1. Daycare Penitipan Anak ($180) -> +3 SDM, +4% Bahagia");
        System.out.println("2. Sekolah PAUD & TK ($200)     -> +4 SDM, +3% Bahagia");
        System.out.println("3. Sekolah Dasar / SD ($220)    -> +6 SDM, +2% Bahagia");
        System.out.println("4. SMP ($280)                   -> +9 SDM, +2% Bahagia");
        System.out.println("5. SMA ($350)                   -> +13 SDM, +3% Bahagia");
        System.out.println("6. Universitas ($600)           -> +22 SDM, +5% Bahagia");
        System.out.println("7. Politeknik ($650)            -> +28 SDM (Vokasi Unggulan!), +6% Bahagia");
        System.out.println("8. Perpustakaan Kota ($300)     -> +14 SDM, +5% Bahagia");
        System.out.print("Pilih (1-8): ");
        return switch (bacaAngka()) {
            case 1 -> new Pendidikan("Daycare Anak", "DYC", 180, 12, 3, 4);
            case 2 -> new Pendidikan("Sekolah PAUD & TK", "PUD", 200, 15, 4, 3);
            case 3 -> new Pendidikan("Sekolah Dasar (SD)", "SSD", 220, 15, 6, 2);
            case 4 -> new Pendidikan("SMP", "SMP", 280, 20, 9, 2);
            case 5 -> new Pendidikan("SMA", "SMA", 350, 25, 13, 3);
            case 6 -> new Pendidikan("Universitas", "UNV", 600, 45, 22, 5);
            case 7 -> new Pendidikan("Politeknik", "PLT", 650, 45, 28, 6);
            case 8 -> new Pendidikan("Perpustakaan Kota", "PRP", 300, 20, 14, 5);
            default -> null;
        };
    }

    private Bangunan menuKomersial() {
        System.out.println("\n-- 5. KOMERSIAL, KEUANGAN, KULINER & GAYA HIDUP --");
        System.out.println("1. Warung 24 Jam ($150) | 2. Warteg ($150) | 3. Toko Cake & Bakery ($240)");
        System.out.println("4. Kafe ($250) | 5. Restoran ($350) | 6. Pasar Tradisional ($220)");
        System.out.println("7. Supermarket ($450) | 8. Shopping Mall ($800) | 9. Bank ($650)");
        System.out.println("10. Toko Alat Sekolah & ATK ($220) | 11. Toko Sepatu & Tas ($250)");
        System.out.println("12. Toko Baju & Kain ($250) | 13. Toko Perhiasan ($550)");
        System.out.println("14. Toko Elektronik ($450) | 15. Toko Alat Musik ($300)");
        System.out.println("16. Showcase Mobil & Motor ($600) | 17. Pet Shop ($220)");
        System.out.println("18. Toko Bangunan ($400) | 19. Toko Perabotan Rumah Tangga ($320)");
        System.out.print("Pilih (1-19): ");
        return switch (bacaAngka()) {
            case 1 -> new Komersial("Warung 24 Jam", "WRG", 150, 10, 55, 2);
            case 2 -> new Komersial("Warteg", "WTG", 150, 10, 50, 3);
            case 3 -> new Komersial("Toko Cake & Bakery", "BKR", 240, 16, 80, 4);
            case 4 -> new Komersial("Kafe", "KFE", 250, 18, 80, 4);
            case 5 -> new Komersial("Restoran", "RST", 350, 25, 120, 4);
            case 6 -> new Komersial("Pasar Tradisional", "PSR", 220, 15, 85, 4);
            case 7 -> new Komersial("Supermarket", "SPM", 450, 35, 160, 3);
            case 8 -> new Komersial("Shopping Mall", "MAL", 800, 60, 310, 7);
            case 9 -> new Komersial("Bank", "BNK", 650, 50, 260, 4);
            case 10 -> new Komersial("Toko Alat Sekolah", "ATK", 220, 15, 75, 2);
            case 11 -> new Komersial("Toko Sepatu & Tas", "SPT", 250, 18, 85, 2);
            case 12 -> new Komersial("Toko Baju & Kain", "BJU", 250, 18, 85, 2);
            case 13 -> new Komersial("Toko Perhiasan", "PRH", 550, 40, 210, 4);
            case 14 -> new Komersial("Toko Elektronik", "ELK", 450, 35, 165, 3);
            case 15 -> new Komersial("Toko Alat Musik", "TAM", 300, 20, 95, 3);
            case 16 -> new Komersial("Showcase Otomotif", "OTO", 600, 45, 230, 4);
            case 17 -> new Komersial("Pet Shop", "PET", 220, 15, 70, 4);
            case 18 -> new Komersial("Toko Bangunan", "TBG", 400, 28, 150, 2);
            case 19 -> new Komersial("Toko Perabotan", "PRB", 320, 22, 115, 3);
            default -> null;
        };
    }

    private Bangunan menuRekreasi() {
        System.out.println("\n-- 6. REKREASI, SENI, HIBURAN & OLAHRAGA --");
        System.out.println("1. Taman Kota ($200) | 2. Taman Bermain Anak ($220) | 3. Alun-Alun Kota ($400)");
        System.out.println("4. Monumen Kota ($450) | 5. Museum Bersejarah ($500) | 6. Gedung Kesenian ($350)");
        System.out.println("7. Bioskop ($380) | 8. Rental PS ($160) | 9. Karaoke ($250) | 10. Panggung Musik ($300)");
        System.out.println("11. Dance Studio ($260) | 12. Studio Foto ($240) | 13. Studio Musik ($280)");
        System.out.println("14. Tempat Pilates ($280) | 15. SPA & Relaksasi ($340)");
        System.out.println("16. Lap. Sepak Bola ($350) | 17. Lap. Tenis ($250) | 18. Lap. Basket ($250)");
        System.out.println("19. Lapangan Padel ($320) | 20. Gedung Olahraga / GOR ($450) | 21. Stadion Utama ($850)");
        System.out.println("22. Lapangan Golf ($750) | 23. Wahana Air ($550) | 24. Kolam Pemancingan ($200)");
        System.out.print("Pilih (1-24): ");
        return switch (bacaAngka()) {
            case 1 -> new RekreasiOlahraga("Taman Kota", "TMN", 200, 15, 6, 0);
            case 2 -> new RekreasiOlahraga("Taman Bermain Anak", "TBA", 220, 15, 7, 15);
            case 3 -> new RekreasiOlahraga("Alun-Alun Kota", "ALN", 400, 25, 10, 30);
            case 4 -> new RekreasiOlahraga("Monumen Kota", "MNM", 450, 25, 11, 40);
            case 5 -> new RekreasiOlahraga("Museum Bersejarah", "MSM", 500, 35, 9, 85);
            case 6 -> new RekreasiOlahraga("Gedung Kesenian", "KSN", 350, 25, 7, 60);
            case 7 -> new RekreasiOlahraga("Bioskop", "BSK", 380, 28, 6, 120);
            case 8 -> new RekreasiOlahraga("Rental PS", "RPS", 160, 10, 4, 55);
            case 9 -> new RekreasiOlahraga("Tempat Karaoke", "KRK", 250, 20, 5, 65);
            case 10 -> new RekreasiOlahraga("Panggung Musik", "MSK", 300, 25, 7, 55);
            case 11 -> new RekreasiOlahraga("Dance Studio", "DNC", 260, 18, 5, 50);
            case 12 -> new RekreasiOlahraga("Studio Foto", "FTO", 240, 16, 4, 60);
            case 13 -> new RekreasiOlahraga("Studio Musik", "STM", 280, 20, 5, 65);
            case 14 -> new RekreasiOlahraga("Tempat Pilates", "PLT", 280, 20, 5, 75);
            case 15 -> new RekreasiOlahraga("SPA & Relaksasi", "SPA", 340, 24, 7, 95);
            case 16 -> new RekreasiOlahraga("Lapangan Sepak Bola", "LSB", 350, 25, 6, 40);
            case 17 -> new RekreasiOlahraga("Lapangan Tenis", "LTN", 250, 20, 4, 35);
            case 18 -> new RekreasiOlahraga("Lapangan Basket", "LBK", 250, 20, 4, 35);
            case 19 -> new RekreasiOlahraga("Lapangan Padel", "PAD", 320, 22, 5, 85);
            case 20 -> new RekreasiOlahraga("Gedung Olahraga", "GOR", 450, 35, 8, 75);
            case 21 -> new RekreasiOlahraga("Stadion Utama", "STD", 850, 70, 12, 260);
            case 22 -> new RekreasiOlahraga("Lapangan Golf", "GLF", 750, 60, 9, 240);
            case 23 -> new RekreasiOlahraga("Wahana Air", "WHA", 550, 45, 10, 130);
            case 24 -> new RekreasiOlahraga("Kolam Pemancingan", "PNC", 200, 15, 5, 45);
            default -> null;
        };
    }

    private Bangunan menuIndustri() {
        System.out.println("\n-- 7. INDUSTRI & AGRIKULTUR --");
        System.out.println("1. Lahan Sawah ($200)              -> +$85 Kas, +2% Bahagia");
        System.out.println("2. Perkebunan ($230)               -> +$100 Kas, +2% Bahagia");
        System.out.println("3. Peternakan ($250)               -> +$110 Kas");
        System.out.println("4. Kawasan Industri ($500)         -> +$240 Kas, -5% Bahagia");
        System.out.println("5. Pelabuhan Maritim ($700)        -> +$340 Kas (Tepi Laut/Sungai!)");
        System.out.println("6. Kilang/Pabrik Pom Bensin ($800) -> +$400 Kas, -7% Bahagia");
        System.out.print("Pilih (1-6): ");
        return switch (bacaAngka()) {
            case 1 -> new IndustriAgrikultur("Lahan Sawah", "SWH", 200, 10, 85, 2, false);
            case 2 -> new IndustriAgrikultur("Perkebunan", "PKB", 230, 12, 100, 2, false);
            case 3 -> new IndustriAgrikultur("Peternakan", "TRN", 250, 20, 110, 0, false);
            case 4 -> new IndustriAgrikultur("Kawasan Industri", "IND", 500, 50, 240, -5, false);
            case 5 -> new IndustriAgrikultur("Pelabuhan Maritim", "PLB", 700, 60, 340, -2, true);
            case 6 -> new IndustriAgrikultur("Pabrik Pom Bensin", "PPB", 800, 70, 400, -7, false);
            default -> null;
        };
    }

    private Bangunan menuTransportasi() {
        System.out.println("\n-- 8. TRANSPORTASI & INFRASTRUKTUR JALAN --");
        System.out.println("1. Jalan Raya ($100)           -> +15 Populasi, +$25 Kas");
        System.out.println("2. Jalan Tol ($300)            -> +35 Populasi, +$90 Kas");
        System.out.println("3. Gerbang Tol ($350)          -> +20 Populasi, +$140 Tarif Tol");
        System.out.println("4. Pangkalan Gojek ($160)      -> +30 Populasi, +$50 Kas");
        System.out.println("5. Halte Bus ($200)            -> +35 Populasi, +$45 Tiket");
        System.out.println("6. Pangkalan Taksi ($250)      -> +30 Populasi, +$65 Tiket");
        System.out.println("7. Rel Kereta ($150)           -> +20 Populasi, +$35 Kas");
        System.out.println("8. Stasiun Kereta Api ($650)   -> +100 Populasi, +$180 Tiket");
        System.out.println("9. Jalur Kereta Cepat ($850)   -> +130 Populasi, +$240 Tiket");
        System.out.println("10. Bandara ($950)             -> +150 Populasi, +$280 Tiket");
        System.out.print("Pilih (1-10): ");
        return switch (bacaAngka()) {
            case 1 -> new Transportasi("Jalan Raya", "JLN", 100, 8, 15, 25);
            case 2 -> new Transportasi("Jalan Tol", "TOL", 300, 20, 35, 90);
            case 3 -> new Transportasi("Gerbang Tol", "GTL", 350, 25, 20, 140);
            case 4 -> new Transportasi("Pangkalan Gojek", "GJK", 160, 10, 30, 50);
            case 5 -> new Transportasi("Halte Bus", "BUS", 200, 15, 35, 45);
            case 6 -> new Transportasi("Pangkalan Taksi", "TKS", 250, 20, 30, 65);
            case 7 -> new Transportasi("Rel Kereta", "REL", 150, 10, 20, 35);
            case 8 -> new Transportasi("Stasiun Kereta Api", "STA", 650, 50, 100, 180);
            case 9 -> new Transportasi("Jalur Kereta Cepat", "KCT", 850, 65, 130, 240);
            case 10 -> new Transportasi("Bandara Internasional", "BND", 950, 80, 150, 280);
            default -> null;
        };
    }

    private Bangunan menuIbadahSosial() {
        System.out.println("\n-- 9. TEMPAT IBADAH & SOSIAL --");
        System.out.println("1. Musholla ($150)      -> +4% Bahagia, +4% Keamanan/Harmoni");
        System.out.println("2. Masjid Agung ($250)  -> +6% Bahagia, +6% Keamanan/Harmoni");
        System.out.println("3. Gereja ($250)        -> +6% Bahagia, +6% Keamanan/Harmoni");
        System.out.println("4. Klenteng ($250)      -> +6% Bahagia, +6% Keamanan/Harmoni");
        System.out.println("5. Wihara ($250)        -> +6% Bahagia, +6% Keamanan/Harmoni");
        System.out.println("6. Pura ($250)          -> +6% Bahagia, +6% Keamanan/Harmoni");
        System.out.print("Pilih (1-6): ");
        return switch (bacaAngka()) {
            case 1 -> new TempatIbadahSosial("Musholla", "MSH", 150, 10, 4, 4);
            case 2 -> new TempatIbadahSosial("Masjid Agung", "MSJ", 250, 15, 6, 6);
            case 3 -> new TempatIbadahSosial("Gereja", "GRJ", 250, 15, 6, 6);
            case 4 -> new TempatIbadahSosial("Klenteng", "KLT", 250, 15, 6, 6);
            case 5 -> new TempatIbadahSosial("Wihara", "WHR", 250, 15, 6, 6);
            case 6 -> new TempatIbadahSosial("Pura", "PUR", 250, 15, 6, 6);
            default -> null;
        };
    }

    private void ubahPajak() {
        System.out.println("\n1. Rendah (5%) | 2. Normal (10%) | 3. Tinggi (20%)");
        System.out.print("Pilih tarif pajak: ");
        int p = bacaAngka();
        if (p >= 1 && p <= 3) kota.setTarifPajak(p);
    }

    private int bacaAngka() {
        if (scanner.hasNextInt()) return scanner.nextInt();
        scanner.next();
        return -1;
    }

    private void picuRandomEvent() {
        Random rand = new Random();
        int ev = rand.nextInt(7);
        if (ev == 0) {
            System.out.println("[EVENT] Wisuda Mahasiswa Politeknik! Skor SDM +12, Bahagia +4%.");
            kota.tambahSkorSDM(12);
            kota.ubahKebahagiaan(4);
        } else if (ev == 1) {
            int rugi = Math.max(20, 150 - kota.getKeamanan());
            System.out.println("[EVENT] Insiden Keamanan! Kerugian kas -$" + rugi + " (ditekan oleh keamanan kota).");
            kota.tambahKas(-rugi);
        } else if (ev == 2) {
            System.out.println("[EVENT] Lonjakan Penumpang Kereta Cepat & Tol! Kas +$220.");
            kota.tambahKas(220);
        } else if (ev == 3) {
            if (kota.getKeamanan() >= 75) {
                System.out.println("[EVENT] Markas Militer, Polisi & Penjara menjaga kota tetap kondusif!");
            } else {
                System.out.println("[EVENT] Kriminalitas meningkat karena keamanan rendah! Kas -$100, Bahagia -4%.");
                kota.tambahKas(-100);
                kota.ubahKebahagiaan(-4);
            }
        }
    }

    private boolean cekKondisiKalah() {
        if (kota.getKas() < 0 || kota.getKebahagiaan() < 20) {
            kota.gambarPetaKota();
            kota.getWaliKota().hitungSkor(kota);
            String gelar = kota.getWaliKota().tentukanGelar(kota, false);
            simpanKeLeaderboard(gelar);
            System.out.println("\n=======================================================");
            System.out.println("[GAME OVER] " + (kota.getKas() < 0 ? "Kota Bangkrut!" : "Warga Demo Besar-Besaran!"));
            System.out.println("Gelar Akhir: " + gelar);
            System.out.println("Skor Akhir : " + kota.getWaliKota().getSkorAkhir());
            System.out.println("=======================================================");
            tampilkanLeaderboard();
            return true;
        }
        return false;
    }

    private void tampilkanHasilAkhir() {
        boolean menang = (kota.getPopulasi() >= 1000 && kota.getKebahagiaan() >= 65 && kota.getSkorSDM() >= 60);
        kota.gambarPetaKota();
        kota.getWaliKota().hitungSkor(kota);
        kota.tampilkanRekapBangunan();
        String gelar = kota.getWaliKota().tentukanGelar(kota, menang);
        simpanKeLeaderboard(gelar);

        System.out.println("\n=======================================================");
        System.out.println(menang ? "STATUS: MENANG! Kota Mega Metropolis Terwujud!" : "STATUS: BELUM TERCAPAI. Target Populasi/Bahagia/SDM kurang.");
        System.out.println("Gelar Akhir: " + gelar);
        System.out.println("Skor Akhir : " + kota.getWaliKota().getSkorAkhir());
        System.out.println("=======================================================");
        tampilkanLeaderboard();
    }

    private void simpanKeLeaderboard(String gelar) {
        leaderboard.add(new String[]{
            kota.getWaliKota().getNama(),
            kota.getNamaKota(),
            String.valueOf(kota.getWaliKota().getSkorAkhir()),
            gelar
        });
        leaderboard.sort(Comparator.comparingInt((String[] a) -> Integer.parseInt(a[2])).reversed());
    }

    private void tampilkanLeaderboard() {
        System.out.println("\n=== RINGKASAN PERINGKAT WALI KOTA (LEADERBOARD) ===");
        if (leaderboard.isEmpty()) {
            System.out.println("Belum ada riwayat sesi selesai.");
            return;
        }
        for (int i = 0; i < leaderboard.size(); i++) {
            String[] data = leaderboard.get(i);
            System.out.printf("#%d | %s (Kota %s) - Skor: %s [%s]\n",
                    (i + 1), data[0], data[1], data[2], data[3]);
        }
    }
}