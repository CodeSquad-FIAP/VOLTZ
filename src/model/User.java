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

  public void setEmail(String email) { this.email = email; }
  public void setPassword(String password) { this.password = password; }

  @Override
  public void showInfo() {
    System.out.println("User: " + name + ", Email: " + email);
  }

  // ===================== MÉTODOS DE INTEGRAÇÃO COM DB =====================

  // Inserir usuário no banco
  public void insert() {
    String sql = "INSERT INTO users (id, name, email, password) VALUES (?, ?, ?, ?)";
    try (Connection conn = OracleConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      stmt.setString(2, name);
      stmt.setString(3, email);
      stmt.setString(4, password);
      stmt.executeUpdate();
      System.out.println("✅ Usuário inserido com sucesso: " + name);
    } catch (SQLException e) {
      System.err.println("❌ Erro ao inserir usuário: " + e.getMessage());
    }
  }

  // Atualizar dados do usuário
  public void update() {
    String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
    try (Connection conn = OracleConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      stmt.setString(2, email);
      stmt.setString(3, password);
      stmt.setInt(4, id);
      int rows = stmt.executeUpdate();
      if (rows > 0) System.out.println("✅ Usuário atualizado com sucesso: " + name);
      else System.out.println("⚠️ Usuário não encontrado para atualização: " + id);
    } catch (SQLException e) {
      System.err.println("❌ Erro ao atualizar usuário: " + e.getMessage());
    }
  }

  // Excluir usuário do banco
  public void delete() {
    String sql = "DELETE FROM users WHERE id = ?";
    try (Connection conn = OracleConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      int rows = stmt.executeUpdate();
      if (rows > 0) System.out.println("✅ Usuário excluído com sucesso: " + name);
      else System.out.println("⚠️ Usuário não encontrado para exclusão: " + id);
    } catch (SQLException e) {
      System.err.println("❌ Erro ao excluir usuário: " + e.getMessage());
    }
  }

  // Recuperar todos os usuários do banco
  public static List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT id, name, email, password FROM users";
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

  // Exibir todos os usuários no console
  public static void displayAllUsers() {
    List<User> users = getAllUsers();
    System.out.println("=== LISTA DE USUÁRIOS ===");
    for (User u : users) {
      u.showInfo();
    }
  }
}
