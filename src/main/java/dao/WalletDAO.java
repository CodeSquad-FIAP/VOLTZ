package dao;

import db.OracleConnection;
import model.Wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento de Wallets (Carteiras)
 * Versão corrigida - compatível com estrutura do banco
 */
public class WalletDAO {

    // INSERT
    public void insert(Wallet wallet) {
        // Usar apenas ID e USER_ID (colunas que existem)
        String sql = "INSERT INTO wallet (id, user_id) VALUES (?, ?)";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wallet.getId());
            stmt.setInt(2, wallet.getUserId());

            stmt.executeUpdate();
            System.out.println("✅ Carteira inserida com sucesso!");

        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir carteira: " + e.getMessage());
        }
    }

    // UPDATE
    public void update(Wallet wallet) {
        String sql = "UPDATE wallet SET user_id = ? WHERE id = ?";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wallet.getUserId());
            stmt.setInt(2, wallet.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Carteira atualizada com sucesso!");
            } else {
                System.out.println("⚠️ Carteira não encontrada para atualizar");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar carteira: " + e.getMessage());
        }
    }

    // DELETE
    public void delete(int id) {
        String sql = "DELETE FROM wallet WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Carteira deletada com sucesso!");
            } else {
                System.out.println("⚠️ Carteira não encontrada para deletar");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar carteira: " + e.getMessage());
        }
    }

    // SELECT by ID
    public Wallet findById(int id) {
        String sql = "SELECT id, user_id FROM wallet WHERE id = ?";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Criar nome padrão já que não temos coluna name
                String walletName = "Wallet " + rs.getInt("id");

                return new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        walletName
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar carteira: " + e.getMessage());
        }
        return null;
    }

    // SELECT all
    public List<Wallet> findAll() {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT id, user_id FROM wallet";

        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String walletName = "Wallet " + rs.getInt("id");

                wallets.add(new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        walletName
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar carteiras: " + e.getMessage());
        }

        return wallets;
    }
}