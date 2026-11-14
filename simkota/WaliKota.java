public class WaliKota {
    private String nama;
    private int skorAkhir;

    public WaliKota(String nama) {
        this.nama = nama;
        this.skorAkhir = 0;
    }

    public void hitungSkor(Kota kota) {
        this.skorAkhir = (kota.getPopulasi() * 2) + (kota.getKebahagiaan() * 10) + kota.getKas();
    }

    // FITUR BARU: Evaluasi gelar berdasarkan pencapaian akhir kota
    public String tentukanGelar(Kota kota, boolean menang) {
        if (!menang) {
            if (kota.getKas() < 0) return "Wali Kota Terlilit Utang (Bangkrut)";
            if (kota.getKebahagiaan() < 20) return "Wali Kota Tiran (Dilengserkan Rakyat)";
            return "Wali Kota Kurang Ambisius (Target Gagal)";
        }

        if (kota.getKebahagiaan() >= 85 && kota.getKas() >= 1000) {
            return "Wali Kota Legendaris & Visioner";
        } else if (kota.getKebahagiaan() >= 85) {
            return "Wali Kota Teladan & Dicintai Rakyat";
        } else if (kota.getKas() >= 1500) {
            return "Wali Kota Konglomerat";
        } else {
            return "Wali Kota Pragmatis (Bertahan Hidup)";
        }
    }

    public String getNama() { return nama; }
    public int getSkorAkhir() { return skorAkhir; }
}