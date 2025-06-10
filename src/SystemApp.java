import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.BufferedWriter;

public class SystemApp {
    public static void main(String[] args) {
        try {
            Market market = new Market();
            market.updatePrices();
            market.showPrices();

            User user = new User("Scrooge McDuck", 1, "scrooge@ducktales.com", "vault123");
            user.showInfo();

            Wallet wallet = new Wallet();
            CryptoAsset btc = new CryptoAsset("Bitcoin", "BTC", 2.0, market.getPrice("BTC"));
            CryptoAsset eth = new CryptoAsset("Ethereum", "ETH", 5.0, market.getPrice("ETH"));
            wallet.addAsset(btc);
            wallet.addAsset(eth);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new Transaction(btc, 2.0, "BUY"));
            transactions.add(new Transaction(eth, 5.0, "BUY"));

            Company duckCorp = new Company("DuckCorp", 1, "CNPJ-001");
            Company treasureInc = new Company("TreasureInc", 2, "CNPJ-002");

            duckCorp.allocateAsset(btc);
            treasureInc.allocateAsset(eth, 1.0); // sobrecarga

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

            // Uso de ArrayList com 2 classes (User e Company)

            System.out.println("\n=== TESTE ARRAYLIST USANDO USER E COMPANY ===");

            List<User> userList = new ArrayList<>();
            userList.add(new User("Donald Duck", 3, "donald@ducktales.com", "quackquack"));
            userList.add(new User("Daisy Duck", 4, "daisy@ducktales.com", "flower123"));

            List<Company> companyList = new ArrayList<>();
            companyList.add(new Company("Duck Enterprises", 1, "CNPJ-010"));
            companyList.add(new Company("Duck Investments", 2, "CNPJ-011"));
          
            Map<User,List<Company>> userCompanies = new HashMap<>();
            userCompanies.put(userList.get(0), companyList);
            userCompanies.put(userList.get(1), Collections.singletonList(companyList.get(1)));

            System.out.println("\n=== TESTE HASHMAP USANDO USER E COMPANY ===");
            for (var e : userCompanies.entrySet()) {
              System.out.printf("%s -> %s%n", 
                  e.getKey().getName(),
                  e.getValue().stream()
                    .map(Company::getName)
                    .collect(Collectors.joining(", "))
              );
            }

            Path file = Paths.get("data.txt");
            try (BufferedWriter w = Files.newBufferedWriter(file)) {
              for (User u : userList) {
                w.write(String.join(";", "USER", String.valueOf(u.getId()), u.getEmail(), u.getName()));
                w.newLine();
              }

              for (Map.Entry<User,List<Company>> e : userCompanies.entrySet()) {
                for (Company c : e.getValue()) {
                  w.write(String.join(";", "REL", String.valueOf(e.getKey().getId()), String.valueOf(c.getId())));
                  w.newLine();
                }
              }
            }

            List<String> lines = Files.readAllLines(file);

            lines.add("USER;5;Carol;carol@ducktales.com");
            Files.write(
                file,
                lines,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            // Mostrar usuários
            System.out.println("-- Lista de Usuários --");
            for (User u : userList) {
                u.showInfo();
            }

            // Mostrar empresas
            System.out.println("\n-- Lista de Empresas --");
            for (Company c : companyList) {
                c.showInfo();
            }

            // Criar e alocar ativos para a primeira empresa
            CryptoAsset ada = new CryptoAsset("Cardano", "ADA", 1000, market.getPrice("ADA"));
            companyList.get(0).allocateAsset(ada);

            System.out.println("\n-- Ativos alocados na primeira empresa --");
            companyList.get(0).showAllocatedAssets();

            System.out.println("\n=== FIM DO TESTE ARRAYLIST ===");

        } catch (Exception e) {
            System.err.println("❌ Ocorreu um erro ao executar o sistema: " + e.getMessage());
            System.err.println("Erro na classe: " + e.getClass().getName());
        }
    }
}
