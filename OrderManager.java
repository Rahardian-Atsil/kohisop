import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderManager implements WarnaTerminal {
    private List<OrderItem> pesananUser;
    private MenuManager menuManager;

    public OrderManager(MenuManager menuManager) {
        this.pesananUser = new ArrayList<>();
        this.menuManager = menuManager;
    }

    public List<OrderItem> getPesananUser() {
        return pesananUser;
    }

    public boolean isPesananKosong() {
        return pesananUser.isEmpty();
    }

    public void prosesPemesanan(Scanner sc) {
        while (true) {
            int currentJenisMinuman = 0;
            int currentJenisMakanan = 0;
            for (OrderItem item : pesananUser) {
                if (item.getMenu() instanceof Minuman) currentJenisMinuman++;
                if (item.getMenu() instanceof Makanan) currentJenisMakanan++;
            }

            System.out.println(CYAN + "\n[REMINDER KUOTA JENIS MENU] " + RESET);
            System.out.println(CYAN + "- Minuman : Terpesan " + currentJenisMinuman + "/5 (Sisa " + (5 - currentJenisMinuman) + " jenis lagi)" + RESET);
            System.out.println(CYAN + "- Makanan : Terpesan " + currentJenisMakanan + "/5 (Sisa " + (5 - currentJenisMakanan) + " jenis lagi)" + RESET);

            System.out.print("\nMasukkan Kode Menu (ketik 'DONE' jika selesai, 'CC' batal): ");
            String inputKode = sc.nextLine().trim();
            
            if (inputKode.equalsIgnoreCase("CC")) {
                System.out.println(RED+"Pesanan Dibatalkan. Program Berhenti."+RESET);
                System.exit(0);
            }
            if (inputKode.equalsIgnoreCase("DONE")) break;
            
            Menu menuPilihan = menuManager.cariMenu(inputKode);
            if (menuPilihan == null) {
                System.out.println(RED+"Error: Kode menu tidak ditemukan!"+RESET);
                continue;
            }

            OrderItem itemExist = null;
            int countMinuman = 0;
            int countMakanan = 0;

            for (OrderItem item : pesananUser) {
                if (item.getMenu().getKode().equalsIgnoreCase(menuPilihan.getKode())) {
                    itemExist = item;
                }
                if (item.getMenu() instanceof Minuman) countMinuman++;
                if (item.getMenu() instanceof Makanan) countMakanan++;
            }

            if (itemExist == null) {
                if (menuPilihan instanceof Minuman && countMinuman >= 5) {
                    System.out.println(RED+"Error: Anda sudah memesan 5 jenis minuman yang berbeda! (Maks 5 jenis)"+RESET);
                    continue;
                }
                if (menuPilihan instanceof Makanan && countMakanan >= 5) {
                    System.out.println(RED+"Error: Anda sudah memesan 5 jenis makanan yang berbeda! (Maks 5 jenis)"+RESET);
                    continue;
                }
            }

            int currentQty = (itemExist != null) ? itemExist.getQty() : 0;
            int maxPorsi = (menuPilihan instanceof Minuman) ? 3 : 2;
            int sisaKuota = maxPorsi - currentQty;

            if (sisaKuota <= 0) {
                System.out.println(RED+"Error: Kuantitas untuk " + menuPilihan.getNama() + " sudah mencapai batas maksimal (" + maxPorsi + " porsi)."+RESET);
                continue;
            }

            int qty = 0;
            while (true) {
                System.out.print("Kuantitas untuk " + menuPilihan.getNama() + " (Maks " + sisaKuota + ") (Enter=1, 'S'/'0'=skip, 'CC'=batal): ");
                String qtyInput = sc.nextLine().trim();
                
                if (qtyInput.equalsIgnoreCase("CC")) {
                    System.out.println(RED+"Pesanan Dibatalkan. Program Berhenti."+RESET);
                    System.exit(0);
                }
                if (qtyInput.equalsIgnoreCase("S") || qtyInput.equals("0")) {
                    qty = 0; break;
                }
                if (qtyInput.isEmpty()) {
                    qty = 1;
                } else {
                    try {
                        qty = Integer.parseInt(qtyInput);
                    } catch (Exception e) {
                        System.out.println(RED+"Error: Input harus berupa angka!"+RESET);
                        continue;
                    }
                }
                
                if (qty < 0) { 
                    System.out.println(RED+"Error: Tidak boleh negatif."+RESET); 
                    continue; 
                }
                if (qty > sisaKuota) {
                    System.out.println(RED+"Error: Kuantitas melebihi batas! Sisa kuota Anda untuk menu ini adalah: " + sisaKuota+RESET);
                    continue;
                }
                break; 
            }
            
            if (qty > 0) {
                if (itemExist != null) {
                    itemExist.tambahQty(qty);
                } else {
                    pesananUser.add(new OrderItem(menuPilihan, qty));
                }
            }

            List<OrderItem> listMakanan = new ArrayList<>();
            List<OrderItem> listMinuman = new ArrayList<>();
            for (OrderItem item : pesananUser) {
                if (item.getMenu() instanceof Makanan) listMakanan.add(item);
                else listMinuman.add(item);
            }
            
            listMakanan.sort((a, b) -> Double.compare(b.getHargaNormalTotal(), a.getHargaNormalTotal()));
            listMinuman.sort((a, b) -> Double.compare(b.getHargaNormalTotal(), a.getHargaNormalTotal()));

            String formatKotak = "%-68s";
            System.out.println();
            String judulPesanan = "              --- PESANAN SEMENTARA SAAT INI ---" ;
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, judulPesanan) + RESET + "\n");
            
            String headerPesanan = String.format("%-5s | %-28s | %-5s | %-15s", "KODE", "NAMA", "QTY", "HARGA TOTAL");
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, headerPesanan) + RESET + "\n");
            
            if (!listMakanan.isEmpty()) {
                System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, " [MAKANAN]") + RESET + "\n");
                for (OrderItem item : listMakanan) {
                    String barisPesanan = String.format("%-5s | %-28s | %-5d | %.2f", item.getMenu().getKode(), item.getMenu().getNama(), item.getQty(), item.getHargaNormalTotal());
                    System.out.printf(BG_MGM_YELLOW + BLACK_TEXT + String.format(formatKotak, barisPesanan) + RESET + "\n");
                }
            }

            if (!listMinuman.isEmpty()) {
                System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, " [MINUMAN]") + RESET + "\n");
                for (OrderItem item : listMinuman) {
                    String barisPesanan = String.format("%-5s | %-28s | %-5d | %.2f", item.getMenu().getKode(), item.getMenu().getNama(), item.getQty(), item.getHargaNormalTotal());
                    System.out.printf(BG_MGM_YELLOW + BLACK_TEXT + String.format(formatKotak, barisPesanan) + RESET + "\n");
                }
            }
        }
    }
}