import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String ulang;
        do {
            System.out.print("Masukkan Nama Wali Kota: ");
            String namaPlayer = input.nextLine();
            System.out.print("Masukkan Nama Kota: ");
            String namaKota = input.nextLine();

            GameEngine game = new GameEngine(namaPlayer, namaKota);
            game.mulaiGame();

            System.out.print("\nIngin bermain lagi dengan kota baru? (y/n): ");
            ulang = input.nextLine();
        } while (ulang.equalsIgnoreCase("y"));
        System.out.println("Terima kasih telah bermain SimKota CLI!");
    }
}