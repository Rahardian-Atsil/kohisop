import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KohiSopApp implements WarnaTerminal {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MenuManager menuManager = new MenuManager();
        MembershipManager membershipManager = new MembershipManager();
        CheckoutManager checkoutManager = new CheckoutManager();
        List<OrderItem> allKitchenOrders = new ArrayList<>();

        System.out.println(CYAN + "=====================================================" + RESET);
        System.out.printf(YELLOW + "%43s\n", "SELAMAT DATANG DI KOHISOP" + RESET);
        System.out.println(CYAN + "=====================================================" + RESET);

        for (int i = 1; i <= 3; i++) {
            System.out.println(CYAN + "\n=====================================================" + RESET);
            System.out.printf(YELLOW + "%36s\n", "PELANGGAN KE-" + i + RESET);
            System.out.println(CYAN + "=====================================================" + RESET);

            menuManager.tampilkanTabelMenu();
            OrderManager orderManager = new OrderManager(menuManager);
            orderManager.prosesPemesanan(sc);

            if (orderManager.isPesananKosong()) {
                System.out.println("Pesanan kosong. Melanjutkan ke pelanggan berikutnya.");
                continue;
            }

            Member currentMember = null;
            System.out.print("\nApakah ini pembelian pertama / ingin daftar member? (Y/N): ");
            String isFirst = sc.nextLine().trim();
            if (isFirst.equalsIgnoreCase("Y")) {
                System.out.print("Masukkan nama Anda: ");
                String nama = sc.nextLine().trim();
                currentMember = membershipManager.registerMember(nama);
                System.out.println(GREEN + "Member berhasil didaftarkan! Kode Anda: " + currentMember.getKode() + RESET);
            } else {
                System.out.print("Punya kode member? (Masukkan kode / ketik '-' jika tidak punya): ");
                String kodeInput = sc.nextLine().trim();
                if (!kodeInput.equals("-")) {
                    currentMember = membershipManager.findMember(kodeInput);
                    if (currentMember != null) {
                        System.out.println(GREEN + "Selamat datang kembali, " + currentMember.getNama() + "! Poin Anda saat ini: " + currentMember.getPoin() + RESET);
                    } else {
                        System.out.println(RED + "Member tidak ditemukan. Melanjutkan sebagai non-member." + RESET);
                    }
                }
            }

            checkoutManager.tampilkanRincianPesanan(orderManager.getPesananUser(), false, currentMember);

            checkoutManager.prosesTransaksiFinal(sc, orderManager.getPesananUser(), currentMember);

            allKitchenOrders.addAll(orderManager.getPesananUser());
        }

        KitchenManager kitchenManager = new KitchenManager();
        kitchenManager.prosesPesanan(allKitchenOrders);

        sc.close();
    }
}