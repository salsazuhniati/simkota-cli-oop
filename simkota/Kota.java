import java.util.ArrayList;

public class Kota {
    private String namaKota;
    private int kas;
    private int populasi;
    private int kebahagiaan;
    private int suplaiListrik, kebutuhanListrik;
    private int suplaiAir, kebutuhanAir;
    private int kapasitasSampah; // Ditambah dari TPA
    private int keamanan;
    private int skorSDM;
    private int tarifPajak;
    private WaliKota waliKota;
    private ArrayList<Bangunan> daftarBangunan;

    public static final int BARIS = 8;
    public static final int KOLOM = 10;
    private Bangunan[][] petaBangunan;
    private String[][] medanDasar;

    public Kota(String namaKota, WaliKota waliKota) {
        this.namaKota = namaKota;
        this.waliKota = waliKota;
        this.kas = 4000; // Modal awal lebih besar untuk bangun kota mega metropolis
        this.populasi = 100;
        this.kebahagiaan = 75;
        this.suplaiListrik = 50;
        this.kebutuhanListrik = 10;
        this.suplaiAir = 50;
        this.kebutuhanAir = 10;
        this.kapasitasSampah = 250;
        this.keamanan = 50;
        this.skorSDM = 10;
        this.tarifPajak = 2;
        this.daftarBangunan = new ArrayList<>();
        this.petaBangunan = new Bangunan[BARIS][KOLOM];
        this.medanDasar = new String[BARIS][KOLOM];
        inisialisasiMedan();
    }

    private void inisialisasiMedan() {
        for (int r = 0; r < BARIS; r++) {
            for (int c = 0; c < KOLOM; c++) {
                medanDasar[r][c] = "TANAH";
            }
        }
        // Sungai di kolom 5 (indeks 4) dengan jembatan di baris 3 & 6
        for (int r = 0; r < BARIS; r++) {
            if (r != 2 && r != 5) medanDasar[r][4] = "SUNGAI";
        }
        // Laut maritim di kolom 10 (indeks 9)
        for (int r = 0; r < BARIS; r++) {
            medanDasar[r][9] = "LAUT";
        }
        // Bukit di pojok kiri atas
        medanDasar[0][0] = "BUKIT";
        medanDasar[0][1] = "BUKIT";
    }

    public boolean bangunDiKoordinat(Bangunan b, int r, int c) {
        if (r < 0 || r >= BARIS || c < 0 || c >= KOLOM) {
            System.out.println(">> Koordinat di luar batas peta (Baris 1-" + BARIS + ", Kolom 1-" + KOLOM + ")!");
            return false;
        }
        if (petaBangunan[r][c] != null) {
            System.out.println(">> Petak sudah terisi " + petaBangunan[r][c].getNama() + "!");
            return false;
        }
        if (b instanceof IndustriAgrikultur ind && ind.isButuhPesisir()) {
            if (!medanDasar[r][c].equals("LAUT") && !cekSebelahAir(r, c)) {
                System.out.println(">> Pelabuhan hanya bisa dibangun di petak LAUT (kolom 10) atau tepi air!");
                return false;
            }
        } else if (!medanDasar[r][c].equals("TANAH")) {
            System.out.println(">> Bangunan darat tidak bisa dibangun di atas " + medanDasar[r][c] + "!");
            return false;
        }
        if (this.kas < b.getBiayaBangun()) {
            System.out.println(">> Kas tidak cukup! Butuh $" + b.getBiayaBangun());
            return false;
        }

        this.kas -= b.getBiayaBangun();
        this.petaBangunan[r][c] = b;
        this.daftarBangunan.add(b);
        return true;
    }

    // FITUR BARU: Upgrade bangunan di koordinat tertentu
    public boolean upgradeDiKoordinat(int r, int c) {
        if (r < 0 || r >= BARIS || c < 0 || c >= KOLOM || petaBangunan[r][c] == null) {
            System.out.println(">> Tidak ada bangunan pada koordinat tersebut!");
            return false;
        }
        Bangunan b = petaBangunan[r][c];
        int biayaUp = b.getBiayaUpgrade();
        if (b.getLevel() >= 3) {
            System.out.println(">> " + b.getNama() + " sudah mencapai Level Maksimal (Lv.3)!");
            return false;
        }
        if (this.kas < biayaUp) {
            System.out.println(">> Kas tidak cukup untuk upgrade! Butuh $" + biayaUp);
            return false;
        }
        this.kas -= biayaUp;
        b.upgrade();
        System.out.println(">> Berhasil upgrade menjadi " + b.getNama() + " (Biaya: $" + biayaUp + ")!");
        return true;
    }

    // FITUR BARU: Gusur / Hancurkan bangunan di koordinat tertentu (Refund 50%)
    public boolean gusurDiKoordinat(int r, int c) {
        if (r < 0 || r >= BARIS || c < 0 || c >= KOLOM || petaBangunan[r][c] == null) {
            System.out.println(">> Tidak ada bangunan pada koordinat tersebut untuk digusur!");
            return false;
        }
        Bangunan b = petaBangunan[r][c];
        int refund = b.getBiayaBangun() / 2;
        this.kas += refund;
        this.daftarBangunan.remove(b);
        this.petaBangunan[r][c] = null;
        System.out.println(">> " + b.getNama() + " telah digusur! Dana kompensasi masuk: +$" + refund);
        return true;
    }

    private boolean cekSebelahAir(int r, int c) {
        int[][] arah = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : arah) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < BARIS && nc >= 0 && nc < KOLOM) {
                if (medanDasar[nr][nc].equals("LAUT") || medanDasar[nr][nc].equals("SUNGAI")) return true;
            }
        }
        return false;
    }

    public void gambarPetaKota() {
        System.out.println("\n+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+");
        System.out.printf("|                PETA TATA KOTA MEGA: %-27s |\n", namaKota.toUpperCase());
        System.out.println("+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+");
        System.out.println("| B\\K |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  | 10  |");
        System.out.println("+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+");

        for (int r = 0; r < BARIS; r++) {
            System.out.printf("|  %d  |", (r + 1));
            for (int c = 0; c < KOLOM; c++) {
                String sel;
                if (petaBangunan[r][c] != null) {
                    sel = " " + petaBangunan[r][c].getKodePetaBerwarna() + " ";
                } else if (medanDasar[r][c].equals("SUNGAI")) {
                    sel = " \u001B[34m~~~\u001B[0m ";
                } else if (medanDasar[r][c].equals("LAUT")) {
                    sel = " \u001B[36m===\u001B[0m ";
                } else if (medanDasar[r][c].equals("BUKIT")) {
                    sel = " \u001B[90m^^^\u001B[0m ";
                } else {
                    sel = "  .  ";
                }
                System.out.print(sel + "|");
            }
            System.out.println("\n+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+");
        }
        System.out.println("Medan: . Tanah | \u001B[34m~~~\u001B[0m Sungai | \u001B[36m===\u001B[0m Laut | \u001B[90m^^^\u001B[0m Bukit");
    }

    public void prosesRondeBangunan() {
        this.suplaiListrik = 50;
        this.kebutuhanListrik = 10;
        this.suplaiAir = 50;
        this.kebutuhanAir = 10;
        this.kapasitasSampah = 250;

        for (Bangunan b : daftarBangunan) {
            // Efek bangunan dikalikan level bangunan tersebut!
            for (int lv = 0; lv < b.getLevel(); lv++) {
                if (lv == 0) {
                    b.jalankanEfekRonde(this);
                } else {
                    // Bonus level tambahan tanpa memotong perawatan dobel
                    b.jalankanEfekRonde(this);
                    tambahKas(b.getBiayaPerawatan());
                }
            }
            if (b.getKodePeta().equals("TPA")) {
                this.kapasitasSampah += (500 * b.getLevel());
            }
        }

        prosesEfekPajak();

        if (kebutuhanListrik > suplaiListrik) {
            System.out.println("[!] DEFISIT LISTRIK! Kebahagiaan -8%.");
            ubahKebahagiaan(-8);
        }
        if (kebutuhanAir > suplaiAir) {
            System.out.println("[!] KRISIS AIR PDAM! Kebahagiaan -8%.");
            ubahKebahagiaan(-8);
        }
        if (populasi > kapasitasSampah) {
            System.out.println("[!] SAMPAH MENUMPUK (Bangun TPA)! Kebahagiaan -7%.");
            ubahKebahagiaan(-7);
        }
    }

    private void prosesEfekPajak() {
        int pendapatanPajak = switch (tarifPajak) {
            case 1 -> { ubahKebahagiaan(3); yield (int) (populasi * 0.25); }
            case 3 -> { ubahKebahagiaan(-6); yield (int) (populasi * 1.00); }
            default -> (int) (populasi * 0.50);
        };
        tambahKas(pendapatanPajak);
    }

    public void tampilkanRekapBangunan() {
        System.out.println("\n=== DAFTAR INFRASTRUKTUR BERDIRI (" + daftarBangunan.size() + " UNIT) ===");
        if (daftarBangunan.isEmpty()) {
            System.out.println("Belum ada bangunan.");
            return;
        }
        int totalRawat = 0;
        for (int i = 0; i < daftarBangunan.size(); i++) {
            Bangunan b = daftarBangunan.get(i);
            totalRawat += b.getBiayaPerawatan();
            System.out.printf("%d. [%s] %s | Rawat: $%d/ronde | Biaya Upgrade: $%d\n",
                    (i + 1), b.getKodePeta(), b.getNama(), b.getBiayaPerawatan(), b.getBiayaUpgrade());
        }
        System.out.println("Total Biaya Perawatan Kota: $" + totalRawat + "/ronde");
    }

    // Setter & Getter
    public void setTarifPajak(int p) { if (p >= 1 && p <= 3) this.tarifPajak = p; }
    public String getLabelPajak() { return switch (tarifPajak) { case 1 -> "Rendah (5%)"; case 3 -> "Tinggi (20%)"; default -> "Normal (10%)"; }; }
    public void tambahKas(int j) { this.kas += j; }
    public void ubahPopulasi(int j) { this.populasi = Math.max(0, this.populasi + j); }
    public void ubahKebahagiaan(int j) { this.kebahagiaan = Math.max(0, Math.min(100, this.kebahagiaan + j)); }
    public void tambahSuplaiListrik(int j) { this.suplaiListrik += j; }
    public void tambahKebutuhanListrik(int j) { this.kebutuhanListrik += j; }
    public void tambahSuplaiAir(int j) { this.suplaiAir += j; }
    public void tambahKebutuhanAir(int j) { this.kebutuhanAir += j; }
    public void tambahKeamanan(int j) { this.keamanan = Math.max(0, Math.min(100, this.keamanan + j)); }
    public void tambahSkorSDM(int j) { this.skorSDM += j; }

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
}