import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SystemApp {
    public static void main(String[] args) {
        try {
            Market market = new Market();
            market.updatePrices();
            market.showPrices();

            User user = new User("Scrooge McDuck", "scrooge@ducktales.com", "vault123");
            user.showInfo();

            Wallet wallet = new Wallet();
            CryptoAsset btc = new CryptoAsset("Bitcoin", "BTC", 2.0, market.getPrice("BTC"));
            CryptoAsset eth = new CryptoAsset("Ethereum", "ETH", 5.0, market.getPrice("ETH"));
            wallet.addAsset(btc);
            wallet.addAsset(eth);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new Transaction(btc, 2.0, "BUY"));
            transactions.add(new Transaction(eth, 5.0, "BUY"));

            Company duckCorp = new Company("DuckCorp", "CNPJ-001");
            Company treasureInc = new Company("TreasureInc", "CNPJ-002");

            duckCorp.allocateAsset(btc);
            treasureInc.allocateAsset(eth, 1.0); // overloading

            List<UserCompanyRelation> relations = new ArrayList<>();
            relations.add(new UserCompanyRelation(user, duckCorp, 50000.00, LocalDate.of(2025, 5, 1)));
            relations.add(new UserCompanyRelation(user, treasureInc, 35000.00, LocalDate.of(2025, 5, 2)));

            Report report = new Report();
            System.out.println();
            report.generateWalletReport(wallet);
            System.out.println();
            report.generateTransactionReport(transactions);
            System.out.println();
            report.generateCompanyReport(List.of(duckCorp, treasureInc));
            System.out.println();
            System.out.println("=== USER-COMPANY RELATIONS ===");
            for (UserCompanyRelation rel : relations) {
                rel.showRelation();
            }

        } catch (Exception e) {
            System.err.println("❌ Ocorreu um erro ao executar o sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

























































