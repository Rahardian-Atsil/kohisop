public class Makanan extends Menu {
    public Makanan(String kode, String nama, double harga) {
        super(kode, nama, harga);
    }

    @Override
    public double hitungPajak(Member member) {
        if (member != null && member.getKode().contains("A")) {
            return 0; 
        }
        if (harga > 50) return harga * 0.08;
        return harga * 0.11;
    }
}