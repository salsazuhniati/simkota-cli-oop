public class TempatIbadahSosial extends Bangunan {
    private int bonusBahagia;
    private int bonusKeamanan; // Harmoni & kerukunan warga

    public TempatIbadahSosial(String nama, String kode, int biaya, int rawat, int bahagia, int harmoni) {
        super(nama, kode, biaya, rawat);
        this.bonusBahagia = bahagia;
        this.bonusKeamanan = harmoni;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        int pengali = getLevel();
        kota.tambahKas(-getBiayaPerawatan());
        kota.ubahKebahagiaan(bonusBahagia * pengali);
        kota.tambahKeamanan(bonusKeamanan * pengali);
        kota.tambahKebutuhanListrik(5 * pengali);
    }

    @Override
    public String getKodePetaBerwarna() {
        return "\u001B[93m" + getKodePeta() + "\u001B[0m"; // Kuning Emas Terang
    }
}