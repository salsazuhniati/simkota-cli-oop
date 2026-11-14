public class Komersial extends Bangunan {
    private int pendapatan;
    private int bonusBahagia;

    public Komersial(String nama, String kode, int biaya, int rawat, int pendapatan, int bahagia) {
        super(nama, kode, biaya, rawat);
        this.pendapatan = pendapatan;
        this.bonusBahagia = bahagia;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(pendapatan - getBiayaPerawatan());
        kota.ubahKebahagiaan(bonusBahagia);
        kota.tambahKebutuhanListrik(15);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[35m" + getKodePeta() + "\u001B[0m"; } // Ungu
}