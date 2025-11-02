package dao;

import db.OracleConnection;
import model.CryptoAsset;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletCryptoAssetDAO {

    public void addCryptoAssetToWallet(int walletId, int cryptoAssetId, double quantity) {
        String sql = "INSERT INTO wallet_cryptoAsset (wallet_id, crypto_asset_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setInt(2, cryptoAssetId);
            stmt.setDouble(3, quantity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Ativo adicionado à carteira com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao adicionar ativo à carteira: " + e.getMessage());
        }
    }

    public void removeCryptoAssetFromWallet(int walletId, int cryptoAssetId) {
        String sql = "DELETE FROM wallet_cryptoAsset WHERE wallet_id = ? AND crypto_asset_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setInt(2, cryptoAssetId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Ativo removido da carteira com sucesso.");
            } else {
                System.out.println("⚠️ Ativo não encontrado na carteira.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao remover ativo da carteira: " + e.getMessage());
        }
    }

    public void updateCryptoAssetQuantity(int walletId, int cryptoAssetId, double quantity) {
        String sql = "UPDATE wallet_cryptoAsset SET quantity = ? WHERE wallet_id = ? AND crypto_asset_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, quantity);
            stmt.setInt(2, walletId);
            stmt.setInt(3, cryptoAssetId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Quantidade do ativo atualizada com sucesso.");
            } else {
                System.out.println("⚠️ Ativo não encontrado na carteira.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar quantidade do ativo: " + e.getMessage());
        }
    }

    public List<CryptoAsset> findCryptoAssetsByWallet(int walletId) {
        List<CryptoAsset> assets = new ArrayList<>();
        String sql = "SELECT ca.id, ca.name, ca.symbol, ca.price, wca.quantity " +
                "FROM wallet_cryptoAsset wca " +
                "JOIN cryptoAsset ca ON ca.id = wca.crypto_asset_id " +
                "WHERE wca.wallet_id = ?";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CryptoAsset asset = new CryptoAsset(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("symbol"),
                            rs.getDouble("quantity"),
                            rs.getDouble("price")
                    );
                    assets.add(asset);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar ativos da carteira: " + e.getMessage());
        }
        return assets;
    }

    public CryptoAsset findByKeys(int walletId, int assetId) {
        String sql = "SELECT ca.name, ca.symbol, wca.quantity " +
                "FROM wallet_cryptoAsset wca " +
                "JOIN cryptoAsset ca ON wca.crypto_asset_id = ca.id " +
                "WHERE wca.wallet_id = ? AND wca.crypto_asset_id = ?";
        CryptoAsset asset = null;

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ps.setInt(2, assetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String symbol = rs.getString("symbol");
                    double quantity = rs.getDouble("quantity");

                    asset = new CryptoAsset(name, symbol, quantity, 0.0);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar ativo na carteira: " + e.getMessage());
        }
        return asset;
    }
}