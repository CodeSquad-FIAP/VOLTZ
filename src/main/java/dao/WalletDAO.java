package dao;

import db.OracleConnection;
import model.Wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletDAO {

    // INSERT
    public void insert(Wallet wallet) {
        String sql = "INSERT INTO wallet (id, user_id, name) VALUES (?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wallet.getId());
            stmt.setInt(2, wallet.getUserId());
            stmt.setString(3, wallet.getName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public void update(Wallet wallet) {
        String sql = "UPDATE wallet SET user_id = ?, name = ? WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wallet.getUserId());
            stmt.setString(2, wallet.getName());
            stmt.setInt(3, wallet.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void delete(int id) {
        String sql = "DELETE FROM wallet WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // SELECT by ID
    public Wallet findById(int id) {
        String sql = "SELECT * FROM wallet WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // SELECT all
    public List<Wallet> findAll() {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT * FROM wallet";

        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                wallets.add(new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wallets;
    }
}
