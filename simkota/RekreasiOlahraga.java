public class RekreasiOlahraga extends Bangunan {
    private int bonusBahagia;
    private int tiketMasuk;

    public RekreasiOlahraga(String nama, String kode, int biaya, int rawat, int bahagia, int tiket) {
        super(nama, kode, biaya, rawat);
        this.bonusBahagia = bahagia;
        this.tiketMasuk = tiket;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(tiketMasuk - getBiayaPerawatan());
        kota.ubahKebahagiaan(bonusBahagia);
        kota.tambahKebutuhanAir(5);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[32m" + getKodePeta() + "\u001B[0m"; } // Hijau
}