package model;

import db.OracleConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User extends Entity {
    private int id;
    private String email;
    private String password;

    public User(String name, int id, String email, String password) {
        super(name);
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public void showInfo() {
        System.out.println("User: " + name + ", Email: " + email + ", ID: " + id);
    }

    // ===================== MÉTODOS DE INTEGRAÇÃO COM DB =====================

    /**
     * Verifica se um usuário com o ID especificado já existe no banco
     */
    public static boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao verificar existência do usuário por ID: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica se um usuário com o email especificado já existe no banco
     */
    public static boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao verificar existência do usuário por email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gera o próximo ID disponível automaticamente
     */
    public static int getNextAvailableId() {
        String sql = "SELECT NVL(MAX(id), 0) + 1 FROM users";
        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar próximo ID: " + e.getMessage());
        }
        return 1; // Retorna 1 se houver erro
    }

    /**
     * Inserir usuário no banco com verificações de duplicidade
     */
    public void insert() {
        // Verificar se já existe usuário com mesmo ID
        if (existsById(this.id)) {
            System.err.println("⚠️ Usuário com ID " + this.id + " já existe. Gerando novo ID...");
            this.id = getNextAvailableId();
            System.out.println("ℹ️ Novo ID gerado: " + this.id);
        }

        // Verificar se já existe usuário com mesmo email
        if (existsByEmail(this.email)) {
            System.err.println("❌ Usuário com email '" + this.email + "' já existe. Inserção cancelada.");
            return;
        }

        String sql = "INSERT INTO users (id, name, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, password);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Usuário inserido com sucesso: " + name + " (ID: " + id + ")");
            } else {
                System.out.println("⚠️ Nenhuma linha foi inserida.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("❌ Erro de chave duplicada: " + e.getMessage());
                System.err.println("💡 Verifique se o ID (" + id + ") ou email ('" + email + "') já existem no banco.");
            } else {
                System.err.println("❌ Erro ao inserir usuário: " + e.getMessage());
            }
        }
    }

    /**
     * Inserir usuário com ID automático
     */
    public void insertWithAutoId() {
        this.id = getNextAvailableId();
        insert();
    }

    /**
     * Atualizar dados do usuário
     */
    public void update() {
        if (!existsById(this.id)) {
            System.err.println("❌ Usuário com ID " + this.id + " não existe. Não é possível atualizar.");
            return;
        }

        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setInt(4, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Usuário atualizado com sucesso: " + name + " (ID: " + id + ")");
            } else {
                System.out.println("⚠️ Nenhuma linha foi atualizada.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("❌ Erro: Email '" + email + "' já está sendo usado por outro usuário.");
            } else {
                System.err.println("❌ Erro ao atualizar usuário: " + e.getMessage());
            }
        }
    }

    /**
     * Excluir usuário do banco
     */
    public void delete() {
        if (!existsById(this.id)) {
            System.err.println("❌ Usuário com ID " + this.id + " não existe. Não é possível excluir.");
            return;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Usuário excluído com sucesso: " + name + " (ID: " + id + ")");
            } else {
                System.out.println("⚠️ Nenhuma linha foi excluída.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir usuário: " + e.getMessage());
        }
    }

    /**
     * Buscar usuário por ID
     */
    public static User findById(int id) {
        String sql = "SELECT id, name, email, password FROM users WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("name"),
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Buscar usuário por email
     */
    public static User findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE email = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("name"),
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar usuário por email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Recuperar todos os usuários do banco
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, password FROM users ORDER BY id";
        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao recuperar usuários: " + e.getMessage());
        }
        return users;
    }

    /**
     * Exibir todos os usuários no console
     */
    public static void displayAllUsers() {
        List<User> users = getAllUsers();
        System.out.println("=== LISTA DE USUÁRIOS (" + users.size() + " encontrado(s)) ===");
        if (users.isEmpty()) {
            System.out.println("Nenhum usuário encontrado no banco de dados.");
        } else {
            for (User u : users) {
                u.showInfo();
            }
        }
    }

    /**
     * Limpar todos os usuários da tabela (usar com cuidado!)
     */
    public static void deleteAllUsers() {
        String sql = "DELETE FROM users";
        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            System.out.println("✅ " + rows + " usuário(s) excluído(s) da tabela.");
        } catch (SQLException e) {
            System.err.println("❌ Erro ao limpar tabela de usuários: " + e.getMessage());
        }
    }
}