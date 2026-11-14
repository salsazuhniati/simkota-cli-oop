public class Pendidikan extends Bangunan {
    private int bonusSDM;
    private int bonusBahagia;

    public Pendidikan(String nama, String kode, int biaya, int rawat, int sdm, int bahagia) {
        super(nama, kode, biaya, rawat);
        this.bonusSDM = sdm;
        this.bonusBahagia = bahagia;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(-getBiayaPerawatan());
        kota.tambahSkorSDM(bonusSDM);
        kota.ubahKebahagiaan(bonusBahagia);
        kota.tambahKebutuhanListrik(10);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[34m" + getKodePeta() + "\u001B[0m"; } // Biru
}