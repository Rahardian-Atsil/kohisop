public class Tunai implements PaymentChannel {
    @Override
    public double hitungDiskon(double totalTagihan) { return 0; }
    @Override
    public double getAdminFee() { return 0; }
    @Override
    public String getNamaChannel() { return "Tunai"; }
}