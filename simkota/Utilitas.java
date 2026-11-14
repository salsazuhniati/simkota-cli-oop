public class Utilitas extends Bangunan {
    private int suplaiListrik;
    private int suplaiAir;
    private int bonusKas;
    private int efekBahagia;

    public Utilitas(String nama, String kode, int biaya, int rawat, int listrik, int air, int kas, int bahagia) {
        super(nama, kode, biaya, rawat);
        this.suplaiListrik = listrik;
        this.suplaiAir = air;
        this.bonusKas = kas;
        this.efekBahagia = bahagia;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(bonusKas - getBiayaPerawatan());
        kota.tambahSuplaiListrik(suplaiListrik);
        kota.tambahSuplaiAir(suplaiAir);
        kota.ubahKebahagiaan(efekBahagia);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[33m" + getKodePeta() + "\u001B[0m"; } // Kuning
}