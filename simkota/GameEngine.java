import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameEngine {
    private Kota kota;
    private Scanner scanner;
    private int rondeSekarang;
    private final int MAX_RONDE = 15;
    private static ArrayList<String[]> leaderboard = new ArrayList<>();

    public GameEngine(String namaWaliKota, String namaKota) {
        this(namaWaliKota, namaKota, new Scanner(System.in));
    }

    public GameEngine(String namaWaliKota, String namaKota, Scanner scanner) {
        WaliKota waliKota = new WaliKota(namaWaliKota);
        this.kota = new Kota(namaKota, waliKota);
        this.scanner = scanner;
        this.rondeSekarang = 1;
    }

    public void mulaiGame() {
        System.out.println("\n=====================================================================");
        System.out.println("  SELAMAT DATANG DI SIMKOTA CLI: ULTIMATE METROPOLIS EDITION");
        System.out.println("  Wali Kota: " + kota.getWaliKota().getNama() + " | Kota: " + kota.getNamaKota());
        System.out.println("  Target Menang (Ronde 15): Populasi >= 1.000 | Bahagia >= 65% | SDM >= 60");
        System.out.println("=====================================================================");

        while (rondeSekarang <= MAX_RONDE) {
            tampilkanStatus();
            prosesMenuAksi();

            System.out.println("\n--- LAPORAN AKHIR RONDE " + rondeSekarang + " ---");
            kota.prosesRondeBangunan();
            picuRandomEvent();

            if (cekKondisiKalah()) {
                tampilkanHasilAkhir(false);
                return;
            }
            rondeSekarang++;
        }

        boolean menang = kota.getPopulasi() >= 1000 && kota.getKebahagiaan() >= 65 && kota.getSkorSDM() >= 60;
        tampilkanHasilAkhir(menang);
    }

    private String buatBar(int nilai, int maks) {
        int panjang = 12;
        int terisi = (maks <= 0) ? 0 : Math.min(panjang, Math.max(0, (nilai * panjang) / maks));
        return "[" + "#".repeat(terisi) + "-".repeat(panjang - terisi) + "]";
    }

    private void tampilkanStatus() {
        kota.gambarPetaKota();

        int persenListrik = (kota.getKebutuhanListrik() == 0) ? 100
                : Math.min(100, (kota.getSuplaiListrik() * 100) / kota.getKebutuhanListrik());
        int persenAir = (kota.getKebutuhanAir() == 0) ? 100
                : Math.min(100, (kota.getSuplaiAir() * 100) / kota.getKebutuhanAir());
        int persenSampah = (kota.getKapasitasSampah() == 0) ? 100
                : Math.min(100, (kota.getPopulasi() * 100) / kota.getKapasitasSampah());

        System.out.println("\n=== STATUS KOTA (RONDE " + rondeSekarang + "/" + MAX_RONDE + ") ===");
        System.out.printf("Kas Kota    : $%-10d | Populasi  : %d Warga%n", kota.getKas(), kota.getPopulasi());
        System.out.printf("Keamanan    : %-3d%%        | Skor SDM  : %d Poin%n", kota.getKeamanan(), kota.getSkorSDM());
        System.out.printf("Pajak Aktif : Hunian %d%% | Komersial %d%% | Industri %d%%%n",
                kota.getPajakHunian(), kota.getPajakKomersial(), kota.getPajakIndustri());
        System.out.printf("Kebahagiaan : %s %d%%%n", buatBar(kota.getKebahagiaan(), 100), kota.getKebahagiaan());
        System.out.printf("Listrik     : %s %d%% (Suplai %d / Butuh %d MW)%n",
                buatBar(persenListrik, 100), persenListrik, kota.getSuplaiListrik(), kota.getKebutuhanListrik());
        System.out.printf("Air PDAM    : %s %d%% (Suplai %d / Butuh %d m3)%n",
                buatBar(persenAir, 100), persenAir, kota.getSuplaiAir(), kota.getKebutuhanAir());
        System.out.printf("Sampah TPA  : %s %d%% (Beban %d / Kapasitas %d)%n",
                buatBar(persenSampah, 100), persenSampah, kota.getPopulasi(), kota.getKapasitasSampah());
    }

    private void prosesMenuAksi() {
        while (true) {
            System.out.println("\n--- MENU AKSI WALI KOTA ---");
            System.out.println("1. Bangun Infrastruktur Baru (109 Pilihan)");
            System.out.println("2. Upgrade Bangunan di Peta (Lv.1 -> Lv.3)");
            System.out.println("3. Gusur / Hancurkan Bangunan (Refund 50%)");
            System.out.println("4. Atur Kebijakan Pajak Sektoral (Hunian / Komersial / Industri)");
            System.out.println("5. Lihat Rekap Bangunan & Leaderboard");
            System.out.println("6. Lewati Giliran (Lanjut ke Ronde Berikutnya)");
            System.out.print("Pilih aksi (1-6): ");

            int pilihan = bacaAngka();
            switch (pilihan) {
                case 1 -> {
                    Bangunan b = pilihKatalogBangunan();
                    if (b != null) {
                        System.out.print("Masukkan Baris (1-8): ");
                        int r = bacaAngka() - 1;
                        System.out.print("Masukkan Kolom (1-10): ");
                        int c = bacaAngka() - 1;
                        if (kota.bangunDiKoordinat(b, r, c)) {
                            return; // Menghabiskan 1 giliran ronde
                        }
                    }
                }
                case 2 -> {
                    System.out.print("Masukkan Baris bangunan yang di-upgrade (1-8): ");
                    int r = bacaAngka() - 1;
                    System.out.print("Masukkan Kolom bangunan yang di-upgrade (1-10): ");
                    int c = bacaAngka() - 1;
                    if (kota.upgradeDiKoordinat(r, c)) {
                        return; // Menghabiskan 1 giliran ronde
                    }
                }
                case 3 -> {
                    System.out.print("Masukkan Baris bangunan yang digusur (1-8): ");
                    int r = bacaAngka() - 1;
                    System.out.print("Masukkan Kolom bangunan yang digusur (1-10): ");
                    int c = bacaAngka() - 1;
                    if (kota.gusurDiKoordinat(r, c)) {
                        return; // Menghabiskan 1 giliran ronde
                    }
                }
                case 4 -> ubahPajak(); // Tidak menghabiskan giliran
                case 5 -> {
                    kota.tampilkanRekapBangunan();
                    tampilkanLeaderboard();
                }
                case 6 -> {
                    System.out.println("Wali Kota memantau perkembangan kota pada ronde ini...");
                    return;
                }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private Bangunan pilihKatalogBangunan() {
        System.out.println("\n=== KATALOG 109 INFRASTRUKTUR (9 KATEGORI) ===");
        System.out.println("1. Hunian & Akomodasi (8 Varian)");
        System.out.println("2. Utilitas, Energi & Kebersihan (10 Varian)");
        System.out.println("3. Pelayanan Publik, Hukum & Militer (16 Varian)");
        System.out.println("4. Pendidikan & Riset (8 Varian)");
        System.out.println("5. Komersial, Keuangan & Gaya Hidup (19 Varian)");
        System.out.println("6. Rekreasi, Seni, Hiburan & Olahraga (25 Varian)");
        System.out.println("7. Industri, Agrikultur & Maritim (6 Varian)");
        System.out.println("8. Transportasi & Infrastruktur Jalan (11 Varian)");
        System.out.println("9. Tempat Ibadah (6 Varian)");
        System.out.println("0. Batal");
        System.out.print("Pilih kategori (0-9): ");

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
        System.out.println("1. Rumah Pedesaan ($150)       -> +35 Warga, +$25 Pajak");
        System.out.println("2. Kost-Kostan ($180)          -> +50 Warga, +$45 Pajak");
        System.out.println("3. Perumahan Subsidi ($250)    -> +80 Warga, +$40 Pajak");
        System.out.println("4. Perumahan Menengah ($400)   -> +70 Warga, +$110 Pajak");
        System.out.println("5. Perumahan Kaya Raya ($650)  -> +50 Warga, +$240 Pajak");
        System.out.println("6. Kawasan Villa ($800)        -> +25 Warga, +$300 Pajak, +5% Bahagia");
        System.out.println("7. Apartemen Bertingkat ($900) -> +50 Warga, +$280 Pajak");
        System.out.println("8. Hotel Bintang Lima ($950)   -> +40 Warga, +$360 Kas, +4% Bahagia");
        System.out.print("Pilih (1-8): ");
        return switch (bacaAngka()) {
            case 1 -> new Hunian("Rumah Pedesaan", "RPD", 150, 10, 35, 25, 5, 5);
            case 2 -> new Hunian("Kost-Kostan", "KST", 180, 12, 50, 45, 8, 8);
            case 3 -> new Hunian("Perumahan Subsidi", "HSB", 250, 18, 80, 40, 12, 12);
            case 4 -> new Hunian("Perumahan Menengah", "HMN", 400, 30, 70, 110, 16, 16);
            case 5 -> new Hunian("Perumahan Kaya Raya", "HKY", 650, 50, 50, 240, 25, 20);
            case 6 -> new Hunian("Kawasan Villa", "VLA", 800, 60, 25, 300, 20, 25);
            case 7 -> new Hunian("Apartemen Bertingkat", "APR", 900, 75, 50, 280, 35, 35);
            case 8 -> new Hunian("Hotel Bintang Lima", "HTL", 950, 80, 40, 360, 30, 30);
            default -> null;
        };
    }

    private Bangunan menuUtilitas() {
        System.out.println("\n-- 2. UTILITAS, ENERGI & KEBERSIHAN --");
        System.out.println("1. Pembangkit Listrik Dasar ($400)     -> +60 MW Listrik, -2% Bahagia");
        System.out.println("2. Turbin Kincir Angin ($320)           -> +40 MW Listrik, +1% Bahagia");
        System.out.println("3. Pembangkit Tenaga Air/PLTA ($550)    -> +95 MW Listrik, +2% Bahagia");
        System.out.println("4. Pembangkit Nuklir/PLTN ($1100)      -> +250 MW Listrik, -6% Bahagia");
        System.out.println("5. Pompa Air ($220)                    -> +35 m3 Air Bersih, +$10 Kas");
        System.out.println("6. Instalasi Air PDAM ($350)           -> +60 m3 Air Bersih, +2% Bahagia");
        System.out.println("7. Instalasi Pengolahan Air/WTP ($750) -> +160 m3 Air Bersih, +3% Bahagia");
        System.out.println("8. Pom Bensin / SPBU ($300)            -> +$100 Kas/ronde");
        System.out.println("9. TPA Pembuangan Akhir ($350)         -> +1000 Kapasitas Sampah, -2% Bahagia");
        System.out.println("10. Air Purifier Kota ($300)           -> +5% Bahagia (Udara Bersih)");
        System.out.print("Pilih (1-10): ");
        return switch (bacaAngka()) {
            case 1 -> new Utilitas("Pembangkit Listrik", "PLK", 400, 35, 60, 0, 0, -2);
            case 2 -> new Utilitas("Turbin Kincir Angin", "PLA", 320, 22, 40, 0, 0, 1);
            case 3 -> new Utilitas("Pembangkit Tenaga Air", "PTA", 550, 42, 95, 0, 0, 2);
            case 4 -> new Utilitas("Pembangkit Nuklir", "PTN", 1100, 95, 250, 0, 0, -6);
            case 5 -> new Utilitas("Pompa Air", "PMP", 220, 15, 0, 35, 10, 0);
            case 6 -> new Utilitas("Instalasi Air PDAM", "PDM", 350, 25, 0, 60, 25, 2);
            case 7 -> new Utilitas("Instalasi Pengolahan Air", "WTP", 750, 55, 0, 160, 0, 3);
            case 8 -> new Utilitas("Pom Bensin", "SPB", 300, 20, 0, 0, 100, 0);
            case 9 -> new Utilitas("TPA Sampah Kota", "TPA", 350, 30, 0, 0, 15, -2);
            case 10 -> new Utilitas("Air Purifier Kota", "APF", 300, 25, 0, 0, 0, 5);
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
        System.out.println("\n-- 4. PENDIDIKAN & RISET --");
        System.out.println("1. Daycare Anak ($180)         -> +2 SDM, +4% Bahagia");
        System.out.println("2. Sekolah PAUD & TK ($200)    -> +2 SDM, +4% Bahagia");
        System.out.println("3. Sekolah Dasar / SD ($250)   -> +4 SDM, +3% Bahagia");
        System.out.println("4. SMP ($300)                  -> +5 SDM, +3% Bahagia");
        System.out.println("5. SMA ($350)                  -> +6 SDM, +4% Bahagia");
        System.out.println("6. Universitas ($700)          -> +10 SDM, +6% Bahagia");
        System.out.println("7. Politeknik ($650)           -> +28 SDM, +5% Bahagia");
        System.out.println("8. Perpustakaan Kota ($280)    -> +4 SDM, +5% Bahagia");
        System.out.print("Pilih (1-8): ");
        return switch (bacaAngka()) {
            case 1 -> new Pendidikan("Daycare Anak", "DYC", 180, 12, 2, 4);
            case 2 -> new Pendidikan("Sekolah PAUD & TK", "PUD", 200, 15, 2, 4);
            case 3 -> new Pendidikan("Sekolah Dasar", "SDN", 250, 18, 4, 3);
            case 4 -> new Pendidikan("Sekolah Menengah Pertama", "SMP", 300, 22, 5, 3);
            case 5 -> new Pendidikan("Sekolah Menengah Atas", "SMA", 350, 26, 6, 4);
            case 6 -> new Pendidikan("Universitas", "UNV", 700, 55, 10, 6);
            case 7 -> new Pendidikan("Politeknik", "PLT", 650, 50, 28, 5);
            case 8 -> new Pendidikan("Perpustakaan Kota", "PRP", 280, 20, 4, 5);
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
        System.out.println("25. Gubuk Pinggir Sawah ($100) -> +4% Bahagia (Tempat Istirahat Petani)");
        System.out.print("Pilih (1-25): ");
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
            case 14 -> new RekreasiOlahraga("Tempat Pilates", "PIL", 280, 20, 5, 75);
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
            case 25 -> new RekreasiOlahraga("Gubuk Pinggir Sawah", "GBK", 100, 5, 4, 0);
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
        System.out.println("1. Jalan Raya ($120)          -> +10 Warga, +$20 Tiket/Pajak");
        System.out.println("2. Jalan Tol ($350)           -> +25 Warga, +$90 Tiket");
        System.out.println("3. Gerbang Tol ($280)         -> +10 Warga, +$110 Tiket");
        System.out.println("4. Pangkalan Gojek ($160)     -> +15 Warga, +$45 Tiket");
        System.out.println("5. Halte Bus ($180)           -> +20 Warga, +$50 Tiket");
        System.out.println("6. Terminal Bus ($380)        -> +45 Warga, +$115 Tiket");
        System.out.println("7. Pangkalan Taksi ($220)     -> +25 Warga, +$65 Tiket");
        System.out.println("8. Rel Kereta ($300)          -> +30 Warga, +$75 Tiket");
        System.out.println("9. Stasiun Kereta Api ($550)  -> +15 Warga, +$170 Tiket");
        System.out.println("10. Jalur Kereta Cepat ($800) -> +25 Warga, +$260 Tiket");
        System.out.println("11. Bandara ($1000)           -> +35 Warga, +$380 Tiket");
        System.out.print("Pilih (1-11): ");
        return switch (bacaAngka()) {
            case 1 -> new Transportasi("Jalan Raya", "JLN", 120, 8, 10, 20);
            case 2 -> new Transportasi("Jalan Tol", "TOL", 350, 25, 25, 90);
            case 3 -> new Transportasi("Gerbang Tol", "GTL", 280, 20, 10, 110);
            case 4 -> new Transportasi("Pangkalan Gojek", "GJK", 160, 12, 15, 45);
            case 5 -> new Transportasi("Halte Bus", "HLT", 180, 14, 20, 50);
            case 6 -> new Transportasi("Terminal Bus", "TRM", 380, 28, 45, 115);
            case 7 -> new Transportasi("Pangkalan Taksi", "TKS", 220, 16, 25, 65);
            case 8 -> new Transportasi("Rel Kereta", "REL", 300, 22, 30, 75);
            case 9 -> new Transportasi("Stasiun Kereta Api", "STS", 550, 45, 15, 170);
            case 10 -> new Transportasi("Jalur Kereta Cepat", "KCP", 800, 65, 25, 260);
            case 11 -> new Transportasi("Bandara Internasional", "BDR", 1000, 90, 35, 380);
            default -> null;
        };
    }

    private Bangunan menuIbadahSosial() {
        System.out.println("\n-- 9. TEMPAT IBADAH --");
        System.out.println("1. Musholla ($150)      -> +4% Bahagia, +4% Harmoni");
        System.out.println("2. Masjid Agung ($250)  -> +6% Bahagia, +6% Harmoni");
        System.out.println("3. Gereja ($250)        -> +6% Bahagia, +6% Harmoni");
        System.out.println("4. Klenteng ($250)      -> +6% Bahagia, +6% Harmoni");
        System.out.println("5. Wihara ($250)        -> +6% Bahagia, +6% Harmoni");
        System.out.println("6. Pura ($250)          -> +6% Bahagia, +6% Harmoni");
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
        System.out.println("\n=== KEBIJAKAN PAJAK SEKTORAL ===");
        System.out.printf("1. Pajak Hunian    (Saat ini: %s)%n", Kota.getLabelTarif(kota.getPajakHunian()));
        System.out.printf("2. Pajak Komersial (Saat ini: %s)%n", Kota.getLabelTarif(kota.getPajakKomersial()));
        System.out.printf("3. Pajak Industri  (Saat ini: %s)%n", Kota.getLabelTarif(kota.getPajakIndustri()));
        System.out.println("0. Kembali");
        System.out.print("Pilih sektor yang ingin diatur (0-3): ");

        int sektor = bacaAngka();
        if (sektor < 1 || sektor > 3) return;

        System.out.println("Pilih Tarif Baru: [1] Rendah (5%)  [2] Normal (10%)  [3] Tinggi (20%)");
        System.out.print("Pilihan tarif (1-3): ");
        int tarif = switch (bacaAngka()) {
            case 1 -> 5;
            case 2 -> 10;
            case 3 -> 20;
            default -> -1;
        };

        if (tarif == -1) {
            System.out.println("Pilihan tarif tidak valid.");
            return;
        }

        if (sektor == 1) kota.setPajakHunian(tarif);
        else if (sektor == 2) kota.setPajakKomersial(tarif);
        else kota.setPajakIndustri(tarif);

        System.out.println("\u001B[32m[✓] Tarif pajak sektor berhasil diperbarui menjadi " + Kota.getLabelTarif(tarif) + "!\u001B[0m");
    }

    private int bacaAngka() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Masukkan angka yang valid: ");
        }
        return scanner.nextInt();
    }

    private void picuRandomEvent() {
        Random rand = new Random();
        int peluang = rand.nextInt(100);
        if (peluang < 20) {
            System.out.println("\u001B[36m[EVENT] Festival Budaya & Wisata Kota! Kas +$250, Kebahagiaan +4%.\u001B[0m");
            kota.tambahKas(250);
            kota.ubahKebahagiaan(4);
        } else if (peluang < 35) {
            System.out.println("\u001B[33m[EVENT] Hujan Lebat & Perbaikan Drainase! Biaya darurat -$150.\u001B[0m");
            kota.tambahKas(-150);
        } else if (peluang < 45) {
            System.out.println("\u001B[32m[EVENT] Investor & Wisatawan Berdatangan! Populasi +45 Warga.\u001B[0m");
            kota.ubahPopulasi(45);
        }
    }

    private boolean cekKondisiKalah() {
        if (kota.getKas() < 0) {
            System.out.println("\n\u001B[31m[GAME OVER] Kota mengalami kebangkrutan (Kas < $0)!\u001B[0m");
            return true;
        }
        if (kota.getKebahagiaan() < 20) {
            System.out.println("\n\u001B[31m[GAME OVER] Warga melakukan demonstrasi besar karena kebahagiaan < 20%!\u001B[0m");
            return true;
        }
        return false;
    }

    private void tampilkanHasilAkhir(boolean menang) {
        WaliKota wk = kota.getWaliKota();
        wk.hitungSkor(kota);
        String gelar = wk.tentukanGelar(kota, menang);

        System.out.println("\n=====================================================================");
        System.out.println("                      EVALUASI AKHIR KEPEMIMPINAN                    ");
        System.out.println("=====================================================================");
        kota.tampilkanRekapBangunan();
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Status Akhir  : " + (menang ? "\u001B[32mMENANG (METROPOLIS SUKSES)\u001B[0m" : "\u001B[31mKALAH / GAGAL TARGET\u001B[0m"));
        System.out.println("Wali Kota     : " + wk.getNama() + " (" + kota.getNamaKota() + ")");
        System.out.println("Statistik     : Populasi " + kota.getPopulasi() + " | Bahagia " + kota.getKebahagiaan() + "% | SDM " + kota.getSkorSDM() + " | Kas $" + kota.getKas());
        System.out.println("Skor Akhir    : " + wk.getSkorAkhir() + " Poin");
        System.out.println("Gelar         : " + gelar);
        System.out.println("=====================================================================");

        simpanKeLeaderboard(gelar);
        tampilkanLeaderboard();
    }

    private void simpanKeLeaderboard(String gelar) {
        WaliKota wk = kota.getWaliKota();
        leaderboard.add(new String[]{
                wk.getNama(),
                kota.getNamaKota(),
                String.valueOf(wk.getSkorAkhir()),
                gelar
        });
        leaderboard.sort((a, b) -> Integer.compare(Integer.parseInt(b[2]), Integer.parseInt(a[2])));
    }

    private void tampilkanLeaderboard() {
        System.out.println("\n=== PAPAN PERINGKAT (LEADERBOARD) WALI KOTA ===");
        if (leaderboard.isEmpty()) {
            System.out.println("Belum ada catatan skor pada sesi ini.");
            return;
        }
        for (int i = 0; i < leaderboard.size(); i++) {
            String[] data = leaderboard.get(i);
            System.out.printf("#%d | %-15s | Kota: %-14s | Skor: %6s | %s%n",
                    i + 1, data[0], data[1], data[2], data[3]);
        }
    }
}
