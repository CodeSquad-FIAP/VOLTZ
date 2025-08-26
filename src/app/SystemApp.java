package app;

import dao.UserDAO;
import model.User;
import model.Company;
import model.CryptoAsset;
import model.Wallet;
import report.Report;
import db.OracleConnection;

import java.sql.SQLException;
import java.util.List;

public class SystemApp {

    public static void main(String[] args) {
        System.out.println("=== Sistema VOLTZ iniciado ===");

        // Teste de conexão com o banco Oracle
        try {
            OracleConnection.getConnection();
            System.out.println("Conexão com o banco Oracle realizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // Criar usuários e inserir no banco (com ID)
            User user1 = new User("Scrooge McDuck", 1, "scrooge@ducktales.com", "vault123");
            User user2 = new User("Donald Duck", 2, "donald@ducktales.com", "quackquack");
            User user3 = new User("Daisy Duck", 3, "daisy@ducktales.com", "flower123");
            User user4 = new User("Carol", 4, "carol@ducktales.com", "password");

            userDAO.insert(user1);
            userDAO.insert(user2);
            userDAO.insert(user3);
            userDAO.insert(user4);

            // Consultar e exibir todos os usuários do banco
            System.out.println("\n=== Usuários cadastrados no banco ===");
            List<User> users = userDAO.getAll();
            for (User u : users) {
                u.showInfo();
            }

            // Atualizar usuário
            user2.setName("Donald Updated");
            user2.setPassword("newpass");
            userDAO.update(user2, 2);

            // Excluir usuário
            userDAO.delete(4); // Exemplo: remover Carol

            System.out.println("\n=== Usuários após atualização/exclusão ===");
            users = userDAO.getAll();
            for (User u : users) {
                u.showInfo();
            }

            // --- Simulação de empresas e carteiras (em memória) ---
            Company company1 = new Company("DuckCorp", 1, "CNPJ-001");
            Company company2 = new Company("TreasureInc", 2, "CNPJ-002");

            Wallet wallet = new Wallet();
            CryptoAsset btc = new CryptoAsset("Bitcoin", "BTC", 2.0, 65000.0);
            CryptoAsset eth = new CryptoAsset("Ethereum", "ETH", 5.0, 3200.0);
            wallet.addAsset(btc);
            wallet.addAsset(eth);

            company1.allocateAsset(btc);
            company2.allocateAsset(eth, 1.0);

            Report report = new Report();
            System.out.println("\n=== Relatório da carteira ===");
            report.generateWalletReport(wallet);

            System.out.println("\n=== Ativos alocados nas empresas ===");
            company1.showAllocatedAssets();
            company2.showAllocatedAssets();

        } catch (SQLException e) {
            System.err.println("Erro na operação com o banco: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
