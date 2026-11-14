public abstract class Bangunan {
    private String nama;
    private String kodePeta;
    private int biayaBangun;
    private int biayaPerawatan;
    private int level;

    public Bangunan(String nama, String kodePeta, int biayaBangun, int biayaPerawatan) {
        this.nama = nama;
        this.kodePeta = kodePeta;
        this.biayaBangun = biayaBangun;
        this.biayaPerawatan = biayaPerawatan;
        this.level = 1;
    }

    public abstract void jalankanEfekRonde(Kota kota);
    public abstract String getKodePetaBerwarna();

    // Method untuk Upgrade Level Bangunan (Biaya upgrade = 75% harga bangun awal)
    public int getBiayaUpgrade() {
        return (int) (biayaBangun * 0.75);
    }

    public boolean upgrade() {
        if (this.level >= 3) return false; // Maksimal Level 3
        this.level++;
        this.biayaPerawatan = (int) (this.biayaPerawatan * 1.3);
        return true;
    }

    public String getNama() { return nama + (level > 1 ? " (Lv." + level + ")" : ""); }
    public String getKodePeta() { return kodePeta; }
    public int getBiayaBangun() { return biayaBangun; }
    public int getBiayaPerawatan() { return biayaPerawatan; }
    public int getLevel() { return level; }
}