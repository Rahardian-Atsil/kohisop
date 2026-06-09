public interface PaymentChannel {
    double hitungDiskon(double totalTagihan);
    double getAdminFee();
    String getNamaChannel();
}