package dao;

import db.OracleConnection;
import model.CryptoAsset;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletCryptoAssetDAO {

    // Adiciona ativo à carteira
    public void addCryptoAssetToWallet(int walletId, int cryptoAssetId, double quantity) {
        String sql = "INSERT INTO wallet_cryptoAsset (wallet_id, cryptoAsset_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setInt(2, cryptoAssetId);
            stmt.setDouble(3, quantity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove ativo da carteira
    public void removeCryptoAssetFromWallet(int walletId, int cryptoAssetId) {
        String sql = "DELETE FROM wallet_cryptoAsset WHERE wallet_id = ? AND cryptoAsset_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setInt(2, cryptoAssetId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Atualiza quantidade de um ativo na carteira
    public void updateCryptoAssetQuantity(int walletId, int cryptoAssetId, double quantity) {
        String sql = "UPDATE wallet_cryptoAsset SET quantity = ? WHERE wallet_id = ? AND cryptoAsset_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, quantity);
            stmt.setInt(2, walletId);
            stmt.setInt(3, cryptoAssetId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Buscar todos os ativos de uma carteira
    public List<CryptoAsset> findCryptoAssetsByWallet(int walletId) {
        List<CryptoAsset> assets = new ArrayList<>();
        String sql = "SELECT ca.id, ca.name, ca.symbol, wca.quantity " +
                     "FROM wallet_cryptoAsset wca " +
                     "JOIN cryptoAsset ca ON ca.id = wca.cryptoAsset_id " +
                     "WHERE wca.wallet_id = ?";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CryptoAsset asset = new CryptoAsset(
                        rs.getString("name"),
                        rs.getString("symbol"),
                        rs.getDouble("quantity"),
                        0.0 // preço atualizado pode vir do Market
                );
                asset.setId(rs.getInt("id"));
                assets.add(asset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assets;
    }
}
