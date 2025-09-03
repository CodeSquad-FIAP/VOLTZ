package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class OracleConnection {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");

    private static Connection connection;

    private OracleConnection() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Conexão Oracle estabelecida com sucesso!");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("❌ Driver Oracle não encontrado", e);
        }
        return connection;
    }

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
