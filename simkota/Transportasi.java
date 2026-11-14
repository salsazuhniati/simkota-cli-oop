public class Transportasi extends Bangunan {
    private int bonusPopulasi;
    private int pendapatanTiket;

    public Transportasi(String nama, String kode, int biaya, int rawat, int populasi, int tiket) {
        super(nama, kode, biaya, rawat);
        this.bonusPopulasi = populasi;
        this.pendapatanTiket = tiket;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(pendapatanTiket - getBiayaPerawatan());
        kota.ubahPopulasi(bonusPopulasi);
        kota.tambahKebutuhanListrik(10);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[96m" + getKodePeta() + "\u001B[0m"; } // Cyan Terang
}