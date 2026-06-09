import java.util.ArrayList;
import java.util.List;

public class MenuManager implements WarnaTerminal {
    private List<Menu> daftarMenu;
    public MenuManager() {
        this.daftarMenu = new ArrayList<>();
        inisialisasiMenu();
    }

    private void inisialisasiMenu() {
        
        daftarMenu.add(new Minuman("A1", "Caffe Latte", 46));
        daftarMenu.add(new Minuman("A2", "Cappuccino", 46));
        daftarMenu.add(new Minuman("E1", "Caffe Americano", 37));
        daftarMenu.add(new Minuman("E2", "Caffe Mocha", 55));
        daftarMenu.add(new Minuman("E3", "Caramel Macchiato", 59));
        daftarMenu.add(new Minuman("E4", "Asian Dolce Latte", 55));
        daftarMenu.add(new Minuman("E5", "Double Shots Iced Shaken Espresso", 50));
        daftarMenu.add(new Minuman("B1", "Freshly Brewed Coffee", 23));
        daftarMenu.add(new Minuman("B2", "Vanilla Sweet Cream Cold Brew", 50));
        daftarMenu.add(new Minuman("B3", "Cold Brew", 44));
        
        daftarMenu.add(new Makanan("M1", "Petemania Pizza", 112));
        daftarMenu.add(new Makanan("M2", "Mie Rebus Super Mario", 35));
        daftarMenu.add(new Makanan("M3", "Ayam Bakar Goreng Rebus Spesial", 72));
        daftarMenu.add(new Makanan("M4", "Soto Kambing Iga Guling", 124));
        daftarMenu.add(new Makanan("S1", "Singkong Bakar A La Carte", 37));
        daftarMenu.add(new Makanan("S2", "Ubi Cilembu Bakar Arang", 58));
        daftarMenu.add(new Makanan("S3", "Tempe Mendoan", 18));
        daftarMenu.add(new Makanan("S4", "Tahu Bakso Extra Telur", 28));
    }

    public void tampilkanTabelMenu() {
        String formatKotak = "%-55s"; 
        System.out.println();
        String judul = "              --- TABEL MENU KOHISOP ---";
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, judul) + RESET + "\n");
        String headerKolom = String.format("%-5s | %-35s | %-5s", "KODE", "NAMA MENU", "HARGA");
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, headerKolom) + RESET + "\n");
        String garis = "-------------------------------------------------------";
        System.out.printf(BG_MGM_YELLOW + BLACK_TEXT + String.format(formatKotak, garis) + RESET + "\n");
        
        for (Menu m : daftarMenu) {
            String baris = String.format("%-5s | %-35s | %.0f", m.getKode(), m.getNama(), m.getHarga());
            System.out.printf(BG_MGM_YELLOW + BLACK_TEXT + String.format(formatKotak, baris) + RESET + "\n");
        }
        System.out.printf(BG_MGM_YELLOW + BLACK_TEXT + String.format(formatKotak, garis) + RESET + "\n");
    }

    public Menu cariMenu(String kode) {
        for (Menu m : daftarMenu) {
            if (m.getKode().equalsIgnoreCase(kode)) return m;
        }
        return null;
    }
}