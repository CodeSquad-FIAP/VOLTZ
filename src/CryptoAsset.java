public class CryptoAsset {
    private String name;
    private String symbol;
    private double quantity;
    private double price;

    public CryptoAsset(String name, String symbol, double quantity, double price) {
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalValue() {
        return quantity * price;
    }
}
