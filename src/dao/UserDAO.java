package dao;

import model.User;
import db.OracleConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ---------------------------
    // INSERT
    // ---------------------------
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Usuário inserido com sucesso!" : "Falha ao inserir usuário.");
        }
    }

    // ---------------------------
    // UPDATE
    // ---------------------------
    public void update(User user, int id) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, id);
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Usuário atualizado!" : "Usuário não encontrado.");
        }
    }

    // ---------------------------
    // DELETE
    // ---------------------------
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Usuário removido!" : "Usuário não encontrado.");
        }
    }

    // ---------------------------
    // SELECT ALL
    // ---------------------------
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, password FROM users";

        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getInt("id"),       // Corrigido: adicionando ID
                        rs.getString("email"),
                        rs.getString("password")
                );
                users.add(user);
            }
        }
        return users;
    }

    // ---------------------------
    // SELECT BY ID
    // ---------------------------
    public User getById(int id) throws SQLException {
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
        }
        return null; // usuário não encontrado
    }
}
