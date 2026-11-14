public class PelayananPublik extends Bangunan {
    private int bonusBahagia;
    private int bonusKeamanan;

    public PelayananPublik(String nama, String kode, int biaya, int rawat, int bahagia, int aman) {
        super(nama, kode, biaya, rawat);
        this.bonusBahagia = bahagia;
        this.bonusKeamanan = aman;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(-getBiayaPerawatan());
        kota.ubahKebahagiaan(bonusBahagia);
        kota.tambahKeamanan(bonusKeamanan);
        kota.tambahKebutuhanListrik(5);
        kota.tambahKebutuhanAir(5);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[37m" + getKodePeta() + "\u001B[0m"; } // Putih Terang
}