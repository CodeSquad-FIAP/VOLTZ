import java.util.List;

public class Report {
    public void generateWalletReport(Wallet wallet) {
        System.out.println("=== WALLET REPORT ===");
        List<CryptoAsset> assets = wallet.getAssets();
        double total = 0;

        for (CryptoAsset asset : assets) {
            double value = asset.getTotalValue();
            System.out.println("- " + asset.getName() + " (" + asset.getSymbol() + "): " + asset.getQuantity()
                    + " units @ $" + asset.getPrice() + " = $" + value);
            total += value;
        }

        System.out.println("Total wallet value: $" + total);
    }

    public void generateTransactionReport(List<Transaction> transactions) {
        System.out.println("=== TRANSACTION REPORT ===");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction t : transactions) {
                t.showTransaction();
            }
        }
    }

    public void generateCompanyReport(List<Company> companies) {
        System.out.println("=== COMPANY ALLOCATION REPORT ===");

        for (Company company : companies) {
            System.out.println("Company: " + company.getName() + " (" + company.getIdentifier() + ")");
            company.showAllocatedAssets();
        }
    }
}
