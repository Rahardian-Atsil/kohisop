public class QRIS implements PaymentChannel {
    @Override
    public double hitungDiskon(double totalTagihan) { return totalTagihan * 0.05; }
    @Override
    public double getAdminFee() { return 0; }
    @Override
    public String getNamaChannel() { return "QRIS"; }
}