package app;

import db.OracleConnection;
import model.*;
import report.Report;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SystemApp {
    public static void main(String[] args) {
        try {
            // Inicializando o mercado
            Market market = new Market();
            market.updatePrices();
            market.showPrices();

            // Criando usuários com IDs únicos
            User user1 = new User("Scrooge McDuck", 1, "scrooge@ducktales.com", "vault123");
            User user2 = new User("Donald Duck", 3, "donald@ducktales.com", "quackquack");
            User user3 = new User("Daisy Duck", 4, "daisy@ducktales.com", "flower123");
            User user4 = new User("Carol", 5, "carol@ducktales.com", "password");

            List<User> userList = List.of(user1, user2, user3, user4);

            // Criando empresas
            Company company1 = new Company("DuckCorp", 1, "CNPJ-001");
            Company company2 = new Company("TreasureInc", 2, "CNPJ-002");
            Company company3 = new Company("Duck Enterprises", 3, "CNPJ-010");
            Company company4 = new Company("Duck Investments", 4, "CNPJ-011");

            List<Company> companyList = List.of(company1, company2, company3, company4);

            // Criando carteira e ativos
            Wallet wallet = new Wallet();
            CryptoAsset btc = new CryptoAsset("Bitcoin", "BTC", 2.0, market.getPrice("BTC"));
            CryptoAsset eth = new CryptoAsset("Ethereum", "ETH", 5.0, market.getPrice("ETH"));
            CryptoAsset ada = new CryptoAsset("Cardano", "ADA", 1000, market.getPrice("ADA"));

            wallet.addAsset(btc);
            wallet.addAsset(eth);

            // Alocando ativos para empresas
            company1.allocateAsset(btc);
            company2.allocateAsset(eth, 1.0);
            company3.allocateAsset(ada);

            // Criando transações
            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new Transaction(btc, 2.0, "BUY"));
            transactions.add(new Transaction(eth, 5.0, "BUY"));

            // Criando relações usuário-empresa
            List<UserCompanyRelation> relations = new ArrayList<>();
            relations.add(new UserCompanyRelation(user1, company1, 50000.00, LocalDate.of(2025, 5, 1)));
            relations.add(new UserCompanyRelation(user1, company2, 35000.00, LocalDate.of(2025, 5, 2)));
            relations.add(new UserCompanyRelation(user2, company1, 20000.00, LocalDate.of(2025, 5, 3)));
            relations.add(new UserCompanyRelation(user3, company2, 25000.00, LocalDate.of(2025, 5, 4)));

            Map<User, List<Company>> userCompanies = new HashMap<>();
            userCompanies.put(user2, Arrays.asList(company3, company4));
            userCompanies.put(user3, Collections.singletonList(company4));

            // Gerando relatórios
            Report report = new Report();
            System.out.println();
            report.generateWalletReport(wallet);
            System.out.println();
            report.generateTransactionReport(transactions);
            System.out.println();
            report.generateCompanyReport(companyList);
            System.out.println();

            System.out.println("=== USER-COMPANY RELATIONS ===");
            for (UserCompanyRelation rel : relations) {
                rel.showRelation();
            }

            System.out.println("\n=== TESTE HASHMAP USANDO USER E COMPANY ===");
            for (var entry : userCompanies.entrySet()) {
                System.out.printf("%s -> %s%n",
                        entry.getKey().getName(),
                        entry.getValue().stream().map(Company::getName).collect(Collectors.joining(", "))
                );
            }

            // Salvando dados em arquivo
            Path file = Paths.get("data.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                for (User u : userList) {
                    writer.write(String.join(";", "USER", String.valueOf(u.getId()), u.getEmail(), u.getName()));
                    writer.newLine();
                }
                for (var e : userCompanies.entrySet()) {
                    for (Company c : e.getValue()) {
                        writer.write(String.join(";", "REL", String.valueOf(e.getKey().getId()), String.valueOf(c.getId())));
                        writer.newLine();
                    }
                }
            }

            List<String> lines = Files.readAllLines(file);
            lines.add("USER;5;Carol;carol@ducktales.com");
            Files.write(file, lines, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("-- Lista de Usuários --");
            for (User u : userList) {
                u.showInfo();
            }

            System.out.println("\n-- Lista de Empresas --");
            for (Company c : companyList) {
                c.showInfo();
            }

            System.out.println("\n-- Ativos alocados na primeira empresa --");
            company1.showAllocatedAssets();

            System.out.println("\n=== FIM DO TESTE ARRAYLIST ===");

        } catch (Exception e) {
            System.err.println("❌ Ocorreu um erro ao executar o sistema: " + e.getMessage());
            e.printStackTrace();
        }

        // Teste de conexão Oracle
        System.out.println("\n=== TESTE DE CONEXÃO COM ORACLE ===");
        testOracleConnection();

        // Teste de CRUD melhorado
        System.out.println("\n=== INICIANDO TESTE COMPLETO DE CRUD DE USUÁRIO ===");
        testUserCRUD();

        // Fechamento da conexão
        OracleConnection.closeConnection();
        System.out.println("Programa finalizado e conexão com o Oracle devidamente fechada.");
    }

    /**
     * Teste de conexão com Oracle
     */
    private static void testOracleConnection() {
        try {
            System.out.println("1. Testando conexão...");
            Connection conn = OracleConnection.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexão estabelecida com sucesso!");
                System.out.println();

                System.out.println("2. Testando execução de comando SQL...");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SYSDATE FROM DUAL");

                if (rs.next()) {
                    System.out.println("📅 Data/Hora do servidor Oracle: " + rs.getTimestamp(1));
                }

                rs.close();
                stmt.close();
                System.out.println("✅ Comando SQL executado com sucesso!");

                System.out.println("\n3. Testando fechamento de conexão...");
                System.out.println("✅ Teste de conexão finalizado!");

            } else {
                System.out.println("❌ Falha na conexão!");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL: " + e.getMessage());
            System.err.println("💡 Verifique: conexão de rede, credenciais e VPN da FIAP");
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }

        System.out.println("=== FIM DO TESTE DE CONEXÃO ===");
    }

    /**
     * Teste completo de CRUD para usuários
     */
    private static void testUserCRUD() {
        // 1. Ver estado inicial
        System.out.println("\n--- Etapa 1: Listando usuários ANTES da inserção ---");
        User.displayAllUsers();

        // 2. Criar usuário de teste com ID único
        int nextId = User.getNextAvailableId();
        User usuarioDeTeste = new User("Usuário Teste", nextId, "teste" + nextId + "@voltz.com", "senha123");
        System.out.println("\n--- Etapa 2: Inserindo '" + usuarioDeTeste.getName() + "' com ID " + nextId + " ---");
        usuarioDeTeste.insert();

        // 3. Verificar a criação
        System.out.println("\n--- Etapa 3: Verificando se o usuário foi inserido ---");
        User.displayAllUsers();

        // 4. Atualizar
        usuarioDeTeste.setName("Usuário Teste (Atualizado)");
        usuarioDeTeste.setEmail("usuario.teste.atualizado" + nextId + "@voltz.com");
        System.out.println("\n--- Etapa 4: Atualizando para '" + usuarioDeTeste.getName() + "' ---");
        usuarioDeTeste.update();

        // 5. Verificar a atualização
        System.out.println("\n--- Etapa 5: Verificando se o usuário foi atualizado ---");
        User.displayAllUsers();

        // 6. Teste de busca
        System.out.println("\n--- Etapa 6: Testando busca por ID ---");
        User usuarioEncontrado = User.findById(usuarioDeTeste.getId());
        if (usuarioEncontrado != null) {
            System.out.println("✅ Usuário encontrado:");
            usuarioEncontrado.showInfo();
        } else {
            System.out.println("❌ Usuário não encontrado");
        }

        // 7. Deletar
        System.out.println("\n--- Etapa 7: Excluindo o usuário de teste ---");
        usuarioDeTeste.delete();

        // 8. Verificar a exclusão
        System.out.println("\n--- Etapa 8: Verificando se o usuário foi excluído ---");
        User.displayAllUsers();

        // 9. Teste de inserção com ID automático
        System.out.println("\n--- Etapa 9: Testando inserção com ID automático ---");
        User usuarioAutoId = new User("Usuario Auto ID", 0, "auto@voltz.com", "senha456");
        usuarioAutoId.insertWithAutoId();
        System.out.println("🔄 ID gerado automaticamente: " + usuarioAutoId.getId());

        // 10. Limpeza final (comentado por segurança)
        // System.out.println("\n--- Etapa 10: Limpeza final ---");
        // User.deleteAllUsers(); // Descomente apenas se necessário

        System.out.println("\n=== FIM DO TESTE DE CRUD ===");
    }

    // ================== MÉTODOS DE TESTE (DEPRECATED) ==================
    // Mantidos para compatibilidade, mas recomendamos usar testUserCRUD()

    private static void testInsertUser(User user) {
        System.out.println("🔄 Tentando inserir: " + user.getName());
        user.insert();
    }

    private static void testUpdateUser(User user) {
        System.out.println("🔄 Tentando atualizar para: " + user.getName());
        user.update();
    }

    private static void testDeleteUser(User user) {
        System.out.println("🔄 Tentando excluir usuário com ID: " + user.getId());
        user.delete();
    }

    private static void testDisplayAllUsers() {
        System.out.println("📋 Exibindo todos os usuários no banco:");
        User.displayAllUsers();
    }
}