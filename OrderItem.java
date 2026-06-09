public class OrderItem {
    private Menu menu;
    private int qty;

    public OrderItem(Menu menu, int qty) {
        this.menu = menu;
        this.qty = qty;
    }

    public Menu getMenu() { return menu; }
    public int getQty() { return qty; }

    public void tambahQty(int tambahan) {
        this.qty += tambahan;
    }
    
    public double getHargaNormalTotal() { return menu.getHarga() * qty; }
    public double getPajakTotal(Member member) { return menu.hitungPajak(member) * qty; }
    public double getSubtotal(Member member) { return getHargaNormalTotal() + getPajakTotal(member); }
}