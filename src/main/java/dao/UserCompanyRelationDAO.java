package dao;

import db.OracleConnection;
import model.UserCompanyRelation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserCompanyRelationDAO {

    public void insert(int userId, int companyId, double investedAmount, LocalDate startDate) {
        String sql = "INSERT INTO userCompanyRelation (user_id, company_id, invested_amount, start_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, companyId);
            stmt.setDouble(3, investedAmount);
            stmt.setDate(4, Date.valueOf(startDate));

            stmt.executeUpdate();
            System.out.println("✅ Relação usuário-empresa criada com sucesso.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                System.err.println("❌ Erro: A relação entre o usuário ID " + userId + " e a empresa ID " + companyId + " já existe.");
            } else {
                System.err.println("❌ Erro ao inserir relação usuário-empresa: " + e.getMessage());
            }
        }
    }

    public void updateInvestedAmount(int userId, int companyId, double newInvestedAmount) {
        String sql = "UPDATE userCompanyRelation SET invested_amount = ? WHERE user_id = ? AND company_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newInvestedAmount);
            stmt.setInt(2, userId);
            stmt.setInt(3, companyId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Valor investido atualizado com sucesso.");
            } else {
                System.out.println("⚠️ Relação não encontrada para atualização.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar valor investido: " + e.getMessage());
        }
    }

    public void delete(int userId, int companyId) {
        String sql = "DELETE FROM userCompanyRelation WHERE user_id = ? AND company_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, companyId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Relação usuário-empresa excluída com sucesso.");
            } else {
                System.out.println("⚠️ Relação não encontrada para exclusão.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir relação: " + e.getMessage());
        }
    }

   public List<Integer> findUsersByCompanyId(int companyId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM userCompanyRelation WHERE company_id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar usuários por empresa: " + e.getMessage());
        }
        return userIds;
    }
}
