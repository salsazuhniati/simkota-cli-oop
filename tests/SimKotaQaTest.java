import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Skenario TC-01 s.d. TC-08 sesuai tabel pengujian proyek kelompok. */
public class SimKotaQaTest {
    private static int passed, failed;

    private static void assertThat(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    private static Kota city() { return new Kota("Kota Uji", new WaliKota("QA")); }
    private static Hunian home() {
        return new Hunian("Rumah Pedesaan", "RPD", 150, 10, 35, 25, 5, 5);
    }

    private static String capture(Runnable action) {
        PrintStream previous = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(out);
            action.run();
        } finally {
            System.setOut(previous);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String play(String choices) {
        var previous = System.in;
        try {
            System.setIn(new ByteArrayInputStream(choices.getBytes(StandardCharsets.UTF_8)));
            return capture(() -> new GameEngine("QA", "Kota Uji").mulaiGame());
        } finally {
            System.setIn(previous);
        }
    }

    private static void run(String id, Runnable action) {
        try {
            action.run();
            passed++;
            System.out.println("PASS " + id);
        } catch (Throwable error) {
            failed++;
            System.out.println("FAIL " + id + " - " + error);
        }
    }

    private static void tc01() {
        Kota k = city();
        assertThat(Kota.BARIS == 8 && Kota.KOLOM == 10, "grid 8x10");
        assertThat(k.getKas() == 4000 && k.getPopulasi() == 100, "kondisi awal");
        assertThat(k.getPajakHunian() == 10 && k.getPajakKomersial() == 10
                && k.getPajakIndustri() == 10, "tiga pajak awal 10%");
        assertThat(k.getKapasitasSampah() == 250, "kapasitas TPA awal");
        String output = play("6\n".repeat(15));
        assertThat(output.contains("Kota: Kota Uji") && output.contains("RONDE 1/15"), "nama/ronde awal");
        assertThat(output.contains("(8x10)") && output.contains("Beban 100 / Kapasitas 250"),
                "peta awal dan TPA 100/250");
    }

    private static void tc02() {
        String output = play("1\n4\n7\n2\n2\n" + "6\n".repeat(14));
        assertThat(output.contains("Politeknik di koordinat (2,2)"), "Politeknik dibangun lewat menu");
        assertThat(output.contains("PLT"), "kode PLT muncul pada peta");
        Kota biaya = city();
        assertThat(biaya.bangunDiKoordinat(new Pendidikan("Politeknik", "PLT", 650, 50, 28, 5), 1, 1)
                && biaya.getKas() == 3350, "kas berkurang $650 saat pembangunan");
        Matcher sdms = Pattern.compile("Skor SDM\\s+: (\\d+) Poin").matcher(output);
        assertThat(sdms.find() && Integer.parseInt(sdms.group(1)) == 10, "SDM awal 10");
        assertThat(sdms.find(), "status ronde 2 tersedia");
        int kenaikan = Integer.parseInt(sdms.group(1)) - 10;
        assertThat(kenaikan == 28, "tabel mengharapkan +28 SDM, aktual dari menu +" + kenaikan);
    }

    private static void tc03() {
        Kota k = city();
        IndustriAgrikultur port = new IndustriAgrikultur("Pelabuhan Maritim", "PLB", 700, 60, 340, -2, true);
        assertThat(!k.bangunDiKoordinat(port, 2, 1), "darat tengah (3,2) ditolak");
        assertThat(k.bangunDiKoordinat(port, 1, 9), "laut (2,10) diterima");
        assertThat(k.getDaftarBangunan().size() == 1, "hanya satu pelabuhan");
    }

    private static void tc04() {
        Kota k = city();
        Hunian b = home();
        assertThat(k.bangunDiKoordinat(b, 1, 1), "rumah (2,2)");
        assertThat(k.bangunDiKoordinat(home(), 1, 2), "rumah lain (2,3)");
        assertThat(k.upgradeDiKoordinat(1, 1) && b.getLevel() == 2, "upgrade Lv.2");
        int sebelumGusur = k.getKas();
        assertThat(k.gusurDiKoordinat(1, 2), "gusur bangunan pada petak lain (2,3)");
        assertThat(k.getKas() == sebelumGusur + 75, "refund 50% dari harga $150");
        assertThat(k.getDaftarBangunan().size() == 1 && b.getLevel() == 2,
                "petak gusur kosong, rumah yang di-upgrade tetap Lv.2");
    }

    private static void tc05() {
        String actions = "4\n1\n1\n4\n3\n3\n6\n";
        String output = play(actions + "6\n".repeat(14));
        assertThat(output.contains("Hunian 5% | Komersial 10% | Industri 20%"), "tarif dari menu 4");
        assertThat(output.contains("Pemasukan Pajak Sektoral"), "pemasukan per sektor dicetak");
        Kota k = city();
        k.setPajakHunian(5);
        k.setPajakIndustri(20);
        k.prosesRondeBangunan();
        assertThat(k.getKas() == 4011 && k.getKebahagiaan() == 75,
                "pajak hunian $11, efek bahagia +2 dan -2");
    }

    private static void tc06() {
        Kota k = city();
        for (int i = 0; i < 5; i++) {
            assertThat(k.bangunDiKoordinat(home(), 0, i), "rumah " + i);
        }
        String output = capture(k::prosesRondeBangunan);
        assertThat(k.getPopulasi() == 275 && k.getKapasitasSampah() == 250, "beban sampah > 250");
        assertThat(k.getSuplaiListrik() < k.getKebutuhanListrik()
                && k.getSuplaiAir() < k.getKebutuhanAir(), "defisit utilitas");
        assertThat(output.contains("Pemadaman") && output.contains("Krisis air")
                && output.contains("Sampah menumpuk"), "tiga peringatan");
        assertThat(k.getKebahagiaan() == 62, "75 - 4 - 4 - 5 = 62");
    }

    private static void tc07() {
        String output = play("1\n2\n4\n1\n1\n6\n".repeat(15));
        assertThat(output.contains("[GAME OVER] Warga melakukan demonstrasi"),
                "cabang kebahagiaan < 20 harus Game Over");
        assertThat(!output.contains("RONDE 15/15"), "berhenti sebelum ronde 15");
        assertThat(output.contains("Status Akhir") && output.contains("Gelar")
                && output.contains("PAPAN PERINGKAT"), "evaluasi dan leaderboard");

        // Bangun biasa ditolak bila kas tidak cukup. Injeksi saldo untuk menguji
        // cabang kebangkrutan secara terisolasi tanpa mengubah kode produksi.
        String bankrupt;
        try {
            GameEngine game = new GameEngine("QA", "Kota Uji", new Scanner(
                    new ByteArrayInputStream("6\n".getBytes(StandardCharsets.UTF_8))));
            Field field = GameEngine.class.getDeclaredField("kota");
            field.setAccessible(true);
            Kota kota = (Kota) field.get(game);
            kota.tambahKas(-14000); // $4000 -> -$10000, tetap negatif meski event +$250.
            bankrupt = capture(game::mulaiGame);
        } catch (ReflectiveOperationException error) {
            throw new AssertionError("Tidak dapat menyiapkan kondisi kas negatif", error);
        }
        assertThat(bankrupt.contains("[GAME OVER] Kota mengalami kebangkrutan (Kas < $0)"),
                "kas negatif harus memicu Game Over kebangkrutan");
        assertThat(!bankrupt.contains("RONDE 2/15") && !bankrupt.contains("RONDE 15/15"),
                "kebangkrutan berhenti pada ronde 1");
        assertThat(bankrupt.contains("KALAH / GAGAL TARGET")
                && bankrupt.contains("Gelar")
                && bankrupt.contains("PAPAN PERINGKAT"), "evaluasi kalah dan leaderboard");
        Matcher kasAkhir = Pattern.compile("Statistik\\s+:.*Kas \\$(\\-?\\d+)").matcher(bankrupt);
        assertThat(kasAkhir.find() && Integer.parseInt(kasAkhir.group(1)) < 0,
                "saldo akhir tercatat negatif");
        System.out.println("  BUKTI TC-07: kebahagiaan < 20% dan kas "
                + kasAkhir.group(1) + " < 0; kedua cabang Game Over, evaluasi dan leaderboard muncul");
    }

    private static void tc08() {
        // Rencana ini menggunakan menu normal selama 15 ronde, tanpa mengubah state secara langsung.
        String[] actions = {
            "1\n1\n1\n1\n1\n", // rumah (1,1)
            "1\n1\n1\n1\n2\n", // rumah (1,2)
            "1\n2\n2\n1\n3\n", // turbin listrik
            "1\n2\n5\n1\n4\n", // pompa air
            "1\n2\n9\n1\n5\n", // TPA pertama
            "1\n4\n7\n1\n6\n", // Politeknik
            "1\n2\n6\n1\n7\n", // PDAM
            "1\n2\n9\n1\n8\n", // TPA kedua
        };
        StringBuilder input = new StringBuilder();
        for (String action : actions) input.append(action);
        input.append("6\n".repeat(15 - actions.length));
        String output = play(input.toString());
        assertThat(output.contains("RONDE 15/15"), "sampai ronde 15");
        assertThat(output.contains("MENANG (METROPOLIS SUKSES)"), "target menang tercapai");
        Matcher stats = Pattern.compile("Statistik\\s+: Populasi (\\d+) \\| Bahagia (\\d+)% \\| SDM (\\d+) \\| Kas \\$(\\d+)")
                .matcher(output);
        assertThat(stats.find(), "statistik akhir tampil");
        assertThat(Integer.parseInt(stats.group(1)) >= 1000
                && Integer.parseInt(stats.group(2)) >= 65
                && Integer.parseInt(stats.group(3)) >= 60, "ketiga target tercapai");
        assertThat(output.contains("REKAP INFRASTRUKTUR")
                && output.contains("PAPAN PERINGKAT"), "rekap dan leaderboard");
        System.out.println("  BUKTI TC-08: populasi=" + stats.group(1)
                + ", bahagia=" + stats.group(2) + "%, SDM=" + stats.group(3)
                + ", kas=$" + stats.group(4) + "; MENANG pada Ronde 15/15");
    }

    public static void main(String[] args) {
        run("TC-01 Inisialisasi Game", SimKotaQaTest::tc01);
        run("TC-02 Pembangunan Infrastruktur", SimKotaQaTest::tc02);
        run("TC-03 Validasi Lokasi Pelabuhan", SimKotaQaTest::tc03);
        run("TC-04 Upgrade & Gusur Bangunan", SimKotaQaTest::tc04);
        run("TC-05 Kebijakan Pajak Sektoral", SimKotaQaTest::tc05);
        run("TC-06 Penalti Utilitas & Sampah TPA", SimKotaQaTest::tc06);
        run("TC-07 Kondisi Kalah (Game Over)", SimKotaQaTest::tc07);
        run("TC-08 Kondisi Menang & Leaderboard", SimKotaQaTest::tc08);
        System.out.printf("QA RESULT: %d passed, %d failed%n", passed, failed);
        if (failed > 0) System.exit(1);
    }
}
