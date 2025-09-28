package dao;

import db.OracleConnection;
import model.Market;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MarketDAO {

   public void save(String symbol, double price) {
        String sql = "MERGE INTO market m " +
                     "USING (SELECT ? AS symbol, ? AS price FROM dual) src " +
                     "ON (m.symbol = src.symbol) " +
                     "WHEN MATCHED THEN " +
                     "  UPDATE SET m.price = src.price, m.last_updated = ? " +
                     "WHEN NOT MATCHED THEN " +
                     "  INSERT (symbol, price, last_updated) VALUES (src.symbol, src.price, ?)";
        
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            stmt.setString(1, symbol);
            stmt.setDouble(2, price);
            stmt.setTimestamp(3, now);
            stmt.setTimestamp(4, now);


            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Preço para o símbolo " + symbol + " salvo/atualizado com sucesso.");
            } else {
                System.out.println("⚠️ Nenhuma alteração no preço para o símbolo " + symbol);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar preço no mercado: " + e.getMessage());
        }
    }

   public Double getPrice(String symbol) {
        String sql = "SELECT price FROM market WHERE symbol = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, symbol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar preço por símbolo: " + e.getMessage());
        }
        return null;
    }

   public Map<String, Double> getAllPrices() {
        Map<String, Double> prices = new HashMap<>();
        String sql = "SELECT symbol, price FROM market";
        try (Connection conn = OracleConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prices.put(rs.getString("symbol"), rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao recuperar todos os preços do mercado: " + e.getMessage());
        }
        return prices;
    }

   public void delete(String symbol) {
        String sql = "DELETE FROM market WHERE symbol = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, symbol);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Símbolo " + symbol + " removido do mercado.");
            } else {
                System.out.println("⚠️ Símbolo " + symbol + " não encontrado para exclusão.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir símbolo do mercado: " + e.getMessage());
        }
    }
}
