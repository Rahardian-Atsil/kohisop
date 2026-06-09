import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class KitchenManager implements WarnaTerminal {
    public void prosesPesanan(List<OrderItem> semuaPesanan) {
        PriorityQueue<OrderItem> pqMakanan = new PriorityQueue<>(
            (a, b) -> Double.compare(b.getMenu().getHarga(), a.getMenu().getHarga())
        );
        Stack<OrderItem> stackMinuman = new Stack<>();

        for (OrderItem item : semuaPesanan) {
            if (item.getMenu() instanceof Makanan) {
                pqMakanan.offer(item);
            } else if (item.getMenu() instanceof Minuman) {
                stackMinuman.push(item);
            }
        }

        System.out.println(CYAN + "\n=====================================================" + RESET);
        System.out.printf(YELLOW + "%40s\n", "PROSES PESANAN TIM DAPUR" + RESET);
        System.out.println(CYAN + "=====================================================" + RESET);

        System.out.println("\n--- TIM MAKANAN (Prioritas Harga Termahal) ---");
        int no = 1;
        while (!pqMakanan.isEmpty()) {
            OrderItem item = pqMakanan.poll();
            System.out.printf("%d. [Memasak] %s (x%d) - Harga per item: %.2f\n",
                no++, item.getMenu().getNama(), item.getQty(), item.getMenu().getHarga());
        }

        System.out.println("\n--- TIM MINUMAN (Last-Ordered-First-Served) ---");
        no = 1;
        while (!stackMinuman.isEmpty()) {
            OrderItem item = stackMinuman.pop();
            System.out.printf("%d. [Meracik] %s (x%d) - Harga per item: %.2f\n",
                no++, item.getMenu().getNama(), item.getQty(), item.getMenu().getHarga());
        }
    }
}