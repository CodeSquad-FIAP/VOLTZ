package app;

import db.OracleConnection;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SystemApp {
    public static void main(String[] args) {

        System.out.println("=== TESTE DE INTEGRAÇÃO COM BANCO DE DADOS - USUÁRIOS ===");

        // 1️⃣ Criar usuários
        User user1 = new User("Scrooge McDuck", 1, "scrooge@ducktales.com", "vault123");
        User user2 = new User("Donald Duck", 2, "donald@ducktales.com", "quackquack");
        User user3 = new User("Daisy Duck", 3, "daisy@ducktales.com", "flower123");

        // Inserir usuários no banco
        user1.insert();
        user2.insert();
        user3.insert();

        System.out.println("\n=== USUÁRIOS INSERIDOS ===");
        User.displayAllUsers();

        // 2️⃣ Atualizar usuário
        user2.setEmail("donald.duck@newemail.com");
        user2.setPassword("newpassword");
        user2.update();

        System.out.println("\n=== USUÁRIOS APÓS ATUALIZAÇÃO ===");
        User.displayAllUsers();

        // 3️⃣ Excluir usuário
        user3.delete();

        System.out.println("\n=== USUÁRIOS APÓS EXCLUSÃO ===");
        User.displayAllUsers();

        // 4️⃣ Recuperar todos os usuários
        List<User> allUsers = User.getAllUsers();
        System.out.println("\n=== LISTA FINAL DE USUÁRIOS ===");
        for (User u : allUsers) {
            u.showInfo();
        }

        // 5️⃣ Teste de conexão direta (opcional)
        try (Connection conn = OracleConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("\n✅ Conexão com Oracle ativa: " + conn.getMetaData().getURL());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro na conexão: " + e.getMessage());
        }

        // Fechar conexão
        OracleConnection.closeConnection();
    }
}
