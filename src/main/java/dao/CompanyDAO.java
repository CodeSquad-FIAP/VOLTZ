package dao;

import db.OracleConnection;
import model.Company;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    // CREATE - Inserir nova empresa
    public boolean insert(Company company) {
        String sql = "INSERT INTO company (name, identifier) VALUES (?, ?)";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, company.getName());
            stmt.setString(2, company.getIdentifier());
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                System.out.println("✅ Company inserida com sucesso!");
            } else {
                System.out.println("❌ Erro ao inserir company");
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir company: " + e.getMessage());
            return false;
        }
    }

    // READ - Buscar empresa por ID
    public Company findById(int id) {
        String sql = "SELECT * FROM company WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Company(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getString("identifier")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar company por ID: " + e.getMessage());
        }
        return null;
    }

    // READ - Listar todas as empresas
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM company";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                companies.add(new Company(
                    rs.getString("name"),
                    rs.getInt("id"),
                    rs.getString("identifier")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar companies: " + e.getMessage());
        }
        return companies;
    }

    // UPDATE - Atualizar empresa
    public boolean update(Company company) {
        String sql = "UPDATE company SET name = ?, identifier = ? WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, company.getName());
            stmt.setString(2, company.getIdentifier());
            stmt.setInt(3, company.getId());
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                System.out.println("✅ Company atualizada com sucesso!");
            } else {
                System.out.println("❌ Company não encontrada para atualizar");
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar company: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Deletar empresa
    public boolean delete(int id) {
        String sql = "DELETE FROM company WHERE id = ?";
        try (Connection conn = OracleConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                System.out.println("✅ Company deletada com sucesso!");
            } else {
                System.out.println("❌ Company não encontrada para deletar");
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar company: " + e.getMessage());
            return false;
        }
    }
}