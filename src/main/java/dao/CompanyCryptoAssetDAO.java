package dao;

import db.OracleConnection;
import model.CryptoAsset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyCryptoAssetDAO {

    public void addOrUpdateAssetForCompany(int companyId, int cryptoAssetId, double quantity) {
        String sql = "MERGE INTO company_cryptoAsset cca " +
                     "USING (SELECT ? AS company_id, ? AS crypto_asset_id, ? AS quantity FROM dual) src " +
                     "ON (cca.company_id = src.company_id AND cca.crypto_asset_id = src.crypto_asset_id) " +
                     "WHEN MATCHED THEN " +
                     "  UPDATE SET cca.quantity = cca.quantity + src.quantity " +
                     "WHEN NOT MATCHED THEN " +
                     "  INSERT (company_id, crypto_asset_id, quantity) VALUES (src.company_id, src.crypto_asset_id, src.quantity)";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            stmt.setInt(2, cryptoAssetId);
            stmt.setDouble(3, quantity);

            stmt.executeUpdate();
            System.out.println("✅ Ativo alocado/atualizado para a empresa com sucesso.");

        } catch (SQLException e) {
            System.err.println("❌ Erro ao alocar ativo para empresa: " + e.getMessage());
        }
    }

    public void removeAssetFromCompany(int companyId, int cryptoAssetId) {
        String sql = "DELETE FROM company_cryptoAsset WHERE company_id = ? AND crypto_asset_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            stmt.setInt(2, cryptoAssetId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Ativo removido da empresa com sucesso.");
            } else {
                System.out.println("⚠️ Associação não encontrada para remoção.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao remover ativo da empresa: " + e.getMessage());
        }
    }

    public List<CryptoAsset> getAssetsByCompanyId(int companyId) {
        List<CryptoAsset> assets = new ArrayList<>();
        String sql = "SELECT ca.id, ca.name, ca.symbol, ca.price, cca.quantity " +
                     "FROM cryptoAsset ca " +
                     "JOIN company_cryptoAsset cca ON ca.id = cca.crypto_asset_id " +
                     "WHERE cca.company_id = ?";

        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CryptoAsset asset = new CryptoAsset(
                            rs.getString("name"),
                            rs.getString("symbol"),
                            rs.getDouble("quantity"),
                            rs.getDouble("price")
                    );
                    assets.add(asset);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar ativos por empresa: " + e.getMessage());
        }
        return assets;
    }
}
