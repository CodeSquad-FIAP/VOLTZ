package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnection {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String USER = "rm557413";
    private static final String PASS = "110805";

    private static Connection connection;

    // Construtor privado para evitar instância
    private OracleConnection() {
    }

    // Retorna a conexão única (singleton)
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Carrega o driver Oracle (não estritamente necessário em versões modernas)
                Class.forName("oracle.jdbc.driver.OracleDriver");

                // Cria a conexão
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Conexão Oracle estabelecida com sucesso!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver Oracle não encontrado: " + e.getMessage());
            throw new SQLException(e);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar no Oracle: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    // Método para fechar a conexão manualmente
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔒 Conexão Oracle fechada com sucesso!");
            } catch (SQLException e) {
                System.err.println("❌ Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}
