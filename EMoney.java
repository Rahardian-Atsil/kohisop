public class EMoney implements PaymentChannel {
    @Override
    public double hitungDiskon(double totalTagihan) { return totalTagihan * 0.07; }
    @Override
    public double getAdminFee() { return 20; }
    @Override
    public String getNamaChannel() { return "eMoney"; }
}