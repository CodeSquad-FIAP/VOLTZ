import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Market {
    private Map<String, Double> prices;

    public Market() {
        prices = new HashMap<>();
        initializePrices();
    }

    private void initializePrices() {
        prices.put("BTC", 65000.0);
        prices.put("ETH", 3200.0);
        prices.put("ADA", 0.45);
        prices.put("SOL", 150.0);
    }

    public void updatePrices() {
        Random rand = new Random();

        for (String symbol : prices.keySet()) {
            double variation = (rand.nextDouble() * 2 - 1) * 0.05;
            double currentPrice = prices.get(symbol);
            double newPrice = currentPrice + (currentPrice * variation);
            prices.put(symbol, Math.round(newPrice * 100.0) / 100.0);
        }
    }

    public double getPrice(String symbol) {
        return prices.getOrDefault(symbol, 0.0);
    }

    public void showPrices() {
        System.out.println("=== MARKET PRICES ===");
        for (String symbol : prices.keySet()) {
            System.out.println(symbol + ": $" + prices.get(symbol));
        }
    }
}
