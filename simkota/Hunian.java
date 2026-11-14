public class Hunian extends Bangunan {
    private int kapasitasWarga;
    private int pajakDasar;
    private int bebanListrik;
    private int bebanAir;

    public Hunian(String nama, String kode, int biaya, int rawat, int warga, int pajak, int listrik, int air) {
        super(nama, kode, biaya, rawat);
        this.kapasitasWarga = warga;
        this.pajakDasar = pajak;
        this.bebanListrik = listrik;
        this.bebanAir = air;
    }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(pajakDasar - getBiayaPerawatan());
        kota.ubahPopulasi(kapasitasWarga);
        kota.tambahKebutuhanListrik(bebanListrik);
        kota.tambahKebutuhanAir(bebanAir);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[36m" + getKodePeta() + "\u001B[0m"; } // Cyan
}