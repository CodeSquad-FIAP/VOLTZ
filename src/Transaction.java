import java.time.LocalDateTime;

public class Transaction {
    private CryptoAsset asset;
    private double amount;
    private String type;
    private LocalDateTime timestamp;

    public Transaction(CryptoAsset asset, double amount, String type) {
        this.asset = asset;
        this.amount = amount;
        this.type = type.toUpperCase();
        this.timestamp = LocalDateTime.now();
    }

    public CryptoAsset getAsset() {
        return asset;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void showTransaction() {
        System.out.println("[" + timestamp + "] " + type + " " + amount + " of " + asset.getSymbol());
    }
}
