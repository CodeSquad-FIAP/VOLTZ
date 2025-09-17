package db;

import model.CryptoAsset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CryptoAssetDAO {

    // CREATE - Inserir novo crypto asset
    public boolean insert(CryptoAsset cryptoAsset) {
        String sql = "INSERT INTO cryptoAsset (name, symbol, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cryptoAsset.getName());
            stmt.setString(2, cryptoAsset.getSymbol());
            stmt.setDouble(3, cryptoAsset.getQuantity());
            stmt.setDouble(4, cryptoAsset.getPrice());
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                System.out.println("✅ CryptoAsset inserido com sucesso!");
            } else {
                System.out.println("❌ Erro ao inserir cryptoAsset");
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir cryptoAsset: " + e.getMessage());
            return false;
        }
    }

    // READ - Buscar crypto asset por ID
    public CryptoAsset findById(int id) {
        String sql = "SELECT * FROM cryptoAsset WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CryptoAsset(
                        rs.getString("name"),
                        rs.getString("symbol"),
                        rs.getDouble("quantity"),
                        rs.getDouble("price")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar cryptoAsset por ID: " + e.getMessage());
        }
        return null;
    }

    // READ - Listar todos os crypto assets
    public List<CryptoAsset> findAll() {
        List<CryptoAsset> cryptoAssets = new ArrayList<>();
        String sql = "SELECT * FROM cryptoAsset";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cryptoAssets.add(new CryptoAsset(
                    rs.getString("name"),
                    rs.getString("symbol"),
                    rs.getDouble("quantity"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar cryptoAssets: " + e.getMessage());
        }
        return cryptoAssets;
    }

    // UPDATE - Atualizar crypto asset
    public boolean update(CryptoAsset cryptoAsset, String originalSymbol) {
        String sql = "UPDATE cryptoAsset SET name = ?, symbol = ?, quantity = ?, price = ? WHERE symbol = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cryptoAsset.getName());
            stmt.setString(2, cryptoAsset.getSymbol());
            stmt.setDouble(3, cryptoAsset.getQuantity());
            stmt.setDouble(4, cryptoAsset.getPrice());
            stmt.setString(5, originalSymbol);
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                System.out.println("✅ CryptoAsset atualizado com sucesso!");
            } else {
                System.out.println("❌ CryptoAsset não encontrado para atualizar");
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar cryptoAsset: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Deletar crypto asset
    public boolean delete(String symbol) {
        String sql = "DELETE FROM cryptoAsset WHERE symbol = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, symbol);
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                System.out.println("✅ CryptoAsset deletado com sucesso!");
            } else {
                System.out.println("❌ CryptoAsset não encontrado para deletar");
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar cryptoAsset: " + e.getMessage());
            return false;
        }
    }
}