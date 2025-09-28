package dao;

import db.OracleConnection;
import model.CryptoAsset;
import model.Transaction;
import model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void insert(Transaction transaction, int userId, int cryptoAssetId) {
        String sql = "INSERT INTO transaction (crypto_asset_id, amount, type, timestamp, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cryptoAssetId);
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getType());
            stmt.setTimestamp(4, Timestamp.valueOf(transaction.getTimestamp()));
            stmt.setInt(5, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Transação inserida com sucesso.");
            } else {
                System.out.println("⚠️ Nenhuma transação foi inserida.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir transação: " + e.getMessage());
        }
    }

    public Transaction findById(int id) {
        String sql = "SELECT t.id, t.amount, t.type, t.timestamp, ca.id as crypto_id, ca.name, ca.symbol, ca.price " +
                     "FROM transaction t " +
                     "JOIN cryptoAsset ca ON t.crypto_asset_id = ca.id " +
                     "WHERE t.id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CryptoAsset asset = new CryptoAsset(
                            rs.getString("name"),
                            rs.getString("symbol"),
                            0, // A quantidade na transação é o 'amount', não o total do ativo
                            rs.getDouble("price")
                    );

                    Transaction transaction = new Transaction(
                            asset,
                            rs.getDouble("amount"),
                            rs.getString("type")
                    );
                    return transaction;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar transação por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Transaction> findByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.id, t.amount, t.type, t.timestamp, ca.id as crypto_id, ca.name, ca.symbol, ca.price " +
                     "FROM transaction t " +
                     "JOIN cryptoAsset ca ON t.crypto_asset_id = ca.id " +
                     "WHERE t.user_id = ? ORDER BY t.timestamp DESC";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                     CryptoAsset asset = new CryptoAsset(
                            rs.getString("name"),
                            rs.getString("symbol"),
                            0,
                            rs.getDouble("price")
                    );
                    Transaction transaction = new Transaction(
                            asset,
                            rs.getDouble("amount"),
                            rs.getString("type")
                    );
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar transações por usuário: " + e.getMessage());
        }
        return transactions;
    }

    public void delete(int id) {
        String sql = "DELETE FROM transaction WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Transação com ID " + id + " excluída com sucesso.");
            } else {
                System.out.println("⚠️ Nenhuma transação encontrada com o ID " + id + " para excluir.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir transação: " + e.getMessage());
        }
    }
}
