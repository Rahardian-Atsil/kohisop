public interface CurrencyConverter {
    double convertFromIdr(double idrAmount);
    String getSymbol();
}

class USD implements CurrencyConverter {
    public double convertFromIdr(double idrAmount) { return idrAmount / 15.0; }
    public String getSymbol() { return "USD"; }
}

class JPY implements CurrencyConverter {
    public double convertFromIdr(double idrAmount) { return idrAmount * 10.0; }
    public String getSymbol() { return "JPY"; }
}

class MYR implements CurrencyConverter {
    public double convertFromIdr(double idrAmount) { return idrAmount / 4.0; }
    public String getSymbol() { return "MYR"; }
}

class EUR implements CurrencyConverter {
    public double convertFromIdr(double idrAmount) { return idrAmount / 14.0; }
    public String getSymbol() { return "EUR"; }
}

class IDR implements CurrencyConverter {
    public double convertFromIdr(double idrAmount) { return idrAmount; }
    public String getSymbol() { return "IDR"; }
}