import java.util.ArrayList;
import java.util.Random;

public class Kota {
    private String namaKota;
    private int kas;
    private int populasi;
    private int kebahagiaan;
    private int suplaiListrik;
    private int kebutuhanListrik;
    private int suplaiAir;
    private int kebutuhanAir;
    private int kapasitasSampah;
    private int keamanan;
    private int skorSDM;

    // Tarif Pajak Sektoral (Default 10% = Normal)
    private int pajakHunian;
    private int pajakKomersial;
    private int pajakIndustri;

    private WaliKota waliKota;
    private ArrayList<Bangunan> daftarBangunan;

    public static final int BARIS = 8;
    public static final int KOLOM = 10;
    private Bangunan[][] petaBangunan;
    private String[][] medanDasar;

    public Kota(String namaKota, WaliKota waliKota) {
        this.namaKota = namaKota;
        this.waliKota = waliKota;
        this.kas = 4000;
        this.populasi = 100;
        this.kebahagiaan = 75;
        this.suplaiListrik = 30;
        this.kebutuhanListrik = 15;
        this.suplaiAir = 30;
        this.kebutuhanAir = 15;
        this.kapasitasSampah = 250;
        this.keamanan = 50;
        this.skorSDM = 10;

        this.pajakHunian = 10;
        this.pajakKomersial = 10;
        this.pajakIndustri = 10;

        this.daftarBangunan = new ArrayList<>();
        this.petaBangunan = new Bangunan[BARIS][KOLOM];
        this.medanDasar = new String[BARIS][KOLOM];
        inisialisasiMedan();
    }

    private void inisialisasiMedan() {
        Random rand = new Random();
        for (int r = 0; r < BARIS; r++) {
            for (int c = 0; c < KOLOM; c++) {
                if (c == KOLOM - 1) {
                    medanDasar[r][c] = "LAUT";
                } else if (c == 4 && r >= 2 && r <= 5) {
                    medanDasar[r][c] = "SUNGAI";
                } else if (rand.nextInt(100) < 12) {
                    medanDasar[r][c] = "BUKIT";
                } else {
                    medanDasar[r][c] = "TANAH";
                }
            }
        }
    }

    public boolean bangunDiKoordinat(Bangunan b, int r, int c) {
        if (r < 0 || r >= BARIS || c < 0 || c >= KOLOM) {
            System.out.println("\u001B[31m[!] Koordinat di luar batas peta (Baris 1-8, Kolom 1-10)!\u001B[0m");
            return false;
        }
        if (petaBangunan[r][c] != null) {
            System.out.println("\u001B[31m[!] Petak tersebut sudah ditempati oleh " + petaBangunan[r][c].getNama() + "!\u001B[0m");
            return false;
        }
        if (this.kas < b.getBiayaBangun()) {
            System.out.println("\u001B[31m[!] Kas kota tidak mencukupi! Butuh $" + b.getBiayaBangun() + ".\u001B[0m");
            return false;
        }

        String medan = medanDasar[r][c];
        if (b instanceof IndustriAgrikultur ind && ind.isButuhPesisir()) {
            if (!medan.equals("LAUT") && !medan.equals("SUNGAI") && !cekSebelahAir(r, c)) {
                System.out.println("\u001B[31m[!] " + b.getNama() + " hanya dapat dibangun di perairan atau tepi laut/sungai!\u001B[0m");
                return false;
            }
        } else {
            if (medan.equals("LAUT") || medan.equals("SUNGAI")) {
                System.out.println("\u001B[31m[!] Bangunan darat tidak bisa dibangun di atas " + medan + "!\u001B[0m");
                return false;
            }
        }

        this.kas -= b.getBiayaBangun();
        this.petaBangunan[r][c] = b;
        this.daftarBangunan.add(b);
        System.out.println("\u001B[32m[✓] Berhasil membangun " + b.getNama() + " di koordinat (" + (r + 1) + "," + (c + 1) + ")!\u001B[0m");
        return true;
    }

    public boolean upgradeDiKoordinat(int r, int c) {
        if (r < 0 || r >= BARIS || c < 0 || c >= KOLOM) {
            System.out.println("\u001B[31m[!] Koordinat tidak valid!\u001B[0m");
            return false;
        }
        Bangunan b = petaBangunan[r][c];
        if (b == null) {
            System.out.println("\u001B[31m[!] Tidak ada bangunan di koordinat tersebut!\u001B[0m");
            return false;
        }
        if (b.getLevel() >= 3) {
            System.out.println("\u001B[33m[!] " + b.getNama() + " sudah mencapai level maksimal (Lv.3)!\u001B[0m");
            return false;
        }
        int biayaUp = b.getBiayaUpgrade();
        if (this.kas < biayaUp) {
            System.out.println("\u001B[31m[!] Kas tidak cukup untuk upgrade! Butuh $" + biayaUp + ".\u001B[0m");
            return false;
        }
        this.kas -= biayaUp;
        b.upgrade();
        System.out.println("\u001B[32m[✓] " + b.getNama() + " berhasil di-upgrade ke Level " + b.getLevel() + "!\u001B[0m");
        return true;
    }

    public boolean gusurDiKoordinat(int r, int c) {
        if (r < 0 || r >= BARIS || c < 0 || c >= KOLOM) {
            System.out.println("\u001B[31m[!] Koordinat tidak valid!\u001B[0m");
            return false;
        }
        Bangunan b = petaBangunan[r][c];
        if (b == null) {
            System.out.println("\u001B[31m[!] Petak tersebut sudah kosong!\u001B[0m");
            return false;
        }
        int refund = b.getBiayaBangun() / 2;
        this.kas += refund;
        this.daftarBangunan.remove(b);
        this.petaBangunan[r][c] = null;
        System.out.println("\u001B[33m[✓] " + b.getNama() + " digusur. Kompensasi dana +$" + refund + " masuk ke kas.\u001B[0m");
        return true;
    }

    private boolean cekSebelahAir(int r, int c) {
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < BARIS && nc >= 0 && nc < KOLOM) {
                if (medanDasar[nr][nc].equals("LAUT") || medanDasar[nr][nc].equals("SUNGAI")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void gambarPetaKota() {
        System.out.println("\n=== PETA TATA KOTA " + namaKota.toUpperCase() + " (8x10) ===");
        System.out.print("     ");
        for (int c = 1; c <= KOLOM; c++) {
            System.out.printf(" %-4d", c);
        }
        System.out.println();

        for (int r = 0; r < BARIS; r++) {
            System.out.printf("B%-2d |", r + 1);
            for (int c = 0; c < KOLOM; c++) {
                if (petaBangunan[r][c] != null) {
                    System.out.print(" " + petaBangunan[r][c].getKodePetaBerwarna() + " ");
                } else {
                    String simbol = switch (medanDasar[r][c]) {
                        case "LAUT" -> "\u001B[34m===\u001B[0m";
                        case "SUNGAI" -> "\u001B[36m~~~\u001B[0m";
                        case "BUKIT" -> "\u001B[33m^^^\u001B[0m";
                        default -> "\u001B[90m . \u001B[0m";
                    };
                    System.out.print(" " + simbol + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println("Legenda: [ . ] Tanah | [\u001B[36m~~~\u001B[0m] Sungai | [\u001B[34m===\u001B[0m] Laut | [\u001B[33m^^^\u001B[0m] Bukit");
    }

    public void prosesRondeBangunan() {
        // Reset perhitungan utilitas dinamis sebelum dievaluasi ulang
        this.suplaiListrik = 30;
        this.kebutuhanListrik = 15 + (this.populasi / 20);
        this.suplaiAir = 30;
        this.kebutuhanAir = 15 + (this.populasi / 20);
        this.kapasitasSampah = 250;

        // Eksekusi efek setiap bangunan secara polimorfik
        for (Bangunan b : daftarBangunan) {
            b.jalankanEfekRonde(this);
            if (b.getNama().contains("TPA")) {
                this.kapasitasSampah += (1000 * b.getLevel());
            }
        }

        // Hitung pemasukan & dampak pajak sektoral (Hunian, Komersial, Industri)
        prosesEfekPajak();

        // Cek penalti defisit Listrik, Air PDAM, dan Sampah TPA
        if (this.suplaiListrik < this.kebutuhanListrik) {
            System.out.println("\u001B[31m[!] PERINGATAN: Pemadaman bergilir! Suplai listrik kurang (-4% Bahagia).\u001B[0m");
            ubahKebahagiaan(-4);
        }
        if (this.suplaiAir < this.kebutuhanAir) {
            System.out.println("\u001B[31m[!] PERINGATAN: Krisis air bersih! Suplai air kurang (-4% Bahagia).\u001B[0m");
            ubahKebahagiaan(-4);
        }
        if (this.populasi > this.kapasitasSampah) {
            System.out.println("\u001B[31m[!] PERINGATAN: Sampah menumpuk melebihi kapasitas TPA (-5% Bahagia)!\u001B[0m");
            ubahKebahagiaan(-5);
        }
    }

    private void prosesEfekPajak() {
        int hasilHunian = (this.populasi * this.pajakHunian) / 45;
        int jumlahKomersial = 0;
        int jumlahIndustri = 0;

        for (Bangunan b : daftarBangunan) {
            if (b instanceof Komersial) {
                jumlahKomersial += b.getLevel();
            } else if (b instanceof IndustriAgrikultur) {
                jumlahIndustri += b.getLevel();
            }
        }

        int hasilKomersial = jumlahKomersial * (this.pajakKomersial * 5);
        int hasilIndustri = jumlahIndustri * (this.pajakIndustri * 4);
        int totalPajak = hasilHunian + hasilKomersial + hasilIndustri;

        this.tambahKas(totalPajak);

        // Efek kebahagiaan dari kebijakan tarif pajak masing-masing sektor
        if (pajakHunian >= 20) ubahKebahagiaan(-4);
        else if (pajakHunian <= 5) ubahKebahagiaan(2);

        if (pajakKomersial >= 20) ubahKebahagiaan(-2);
        else if (pajakKomersial <= 5 && jumlahKomersial > 0) ubahKebahagiaan(1);

        if (pajakIndustri >= 20) ubahKebahagiaan(-2);

        System.out.printf("\u001B[32m[$] Pemasukan Pajak Sektoral: +$%d\u001B[0m (Hunian %d%%: $%d | Komersial %d%%: $%d | Industri %d%%: $%d)%n",
                totalPajak, pajakHunian, hasilHunian, pajakKomersial, hasilKomersial, pajakIndustri, hasilIndustri);
    }

    public void tampilkanRekapBangunan() {
        System.out.println("\n=== REKAP INFRASTRUKTUR TERBANGUN (" + daftarBangunan.size() + " Unit) ===");
        if (daftarBangunan.isEmpty()) {
            System.out.println("Belum ada infrastruktur yang dibangun.");
            return;
        }
        int no = 1;
        for (int r = 0; r < BARIS; r++) {
            for (int c = 0; c < KOLOM; c++) {
                Bangunan b = petaBangunan[r][c];
                if (b != null) {
                    System.out.printf("%2d. [%s] %-28s | Lv.%d | Koordinat (%d,%d) | Perawatan: $%d/ronde%n",
                            no++, b.getKodePeta(), b.getNama(), b.getLevel(), r + 1, c + 1, b.getBiayaPerawatan());
                }
            }
        }
    }

    // Getter & Mutator Terkontrol (Encapsulation)
    public String getNamaKota() { return namaKota; }
    public int getKas() { return kas; }
    public int getPopulasi() { return populasi; }
    public int getKebahagiaan() { return kebahagiaan; }
    public int getSuplaiListrik() { return suplaiListrik; }
    public int getKebutuhanListrik() { return kebutuhanListrik; }
    public int getSuplaiAir() { return suplaiAir; }
    public int getKebutuhanAir() { return kebutuhanAir; }
    public int getKapasitasSampah() { return kapasitasSampah; }
    public int getKeamanan() { return keamanan; }
    public int getSkorSDM() { return skorSDM; }
    public WaliKota getWaliKota() { return waliKota; }
    public ArrayList<Bangunan> getDaftarBangunan() { return daftarBangunan; }

    public int getPajakHunian() { return pajakHunian; }
    public int getPajakKomersial() { return pajakKomersial; }
    public int getPajakIndustri() { return pajakIndustri; }

    public void setPajakHunian(int p) { this.pajakHunian = p; }
    public void setPajakKomersial(int p) { this.pajakKomersial = p; }
    public void setPajakIndustri(int p) { this.pajakIndustri = p; }

    public static String getLabelTarif(int tarif) {
        if (tarif <= 5) return "Rendah (5%)";
        if (tarif <= 10) return "Normal (10%)";
        return "Tinggi (20%)";
    }

    public void tambahKas(int jumlah) { this.kas += jumlah; }
    public void ubahPopulasi(int jumlah) { this.populasi = Math.max(0, this.populasi + jumlah); }
    public void ubahKebahagiaan(int jumlah) { this.kebahagiaan = Math.max(0, Math.min(100, this.kebahagiaan + jumlah)); }
    public void tambahSuplaiListrik(int jumlah) { this.suplaiListrik += jumlah; }
    public void tambahKebutuhanListrik(int jumlah) { this.kebutuhanListrik += jumlah; }
    public void tambahSuplaiAir(int jumlah) { this.suplaiAir += jumlah; }
    public void tambahKebutuhanAir(int jumlah) { this.kebutuhanAir += jumlah; }
    public void tambahKeamanan(int jumlah) { this.keamanan = Math.max(0, Math.min(100, this.keamanan + jumlah)); }
    public void tambahSkorSDM(int jumlah) { this.skorSDM = Math.max(0, this.skorSDM + jumlah); }
}