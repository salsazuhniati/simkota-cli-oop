public class IndustriAgrikultur extends Bangunan {
    private int produksiKas;
    private int efekBahagia;
    private boolean butuhPesisir; // Khusus Pelabuhan harus di tepi laut/sungai!

    public IndustriAgrikultur(String nama, String kode, int biaya, int rawat, int kas, int bahagia, boolean butuhPesisir) {
        super(nama, kode, biaya, rawat);
        this.produksiKas = kas;
        this.efekBahagia = bahagia;
        this.butuhPesisir = butuhPesisir;
    }

    public boolean isButuhPesisir() { return butuhPesisir; }

    @Override
    public void jalankanEfekRonde(Kota kota) {
        kota.tambahKas(produksiKas - getBiayaPerawatan());
        kota.ubahKebahagiaan(efekBahagia);
        kota.tambahKebutuhanListrik(20);
        kota.tambahKebutuhanAir(10);
    }

    @Override
    public String getKodePetaBerwarna() { return "\u001B[31m" + getKodePeta() + "\u001B[0m"; } // Merah
}