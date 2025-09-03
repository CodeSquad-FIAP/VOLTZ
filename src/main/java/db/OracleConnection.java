package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class OracleConnection {

    // Carrega as variáveis do arquivo .env
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")  // Procura na raiz do projeto
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    // Configurações do banco usando .env
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");

    private static Connection connection;

    private OracleConnection() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                if (URL == null || USER == null || PASS == null) {
                    throw new SQLException("❌ Configurações do .env não foram carregadas corretamente. Verifique se o arquivo .env existe na raiz do projeto.");
                }

                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Conexão Oracle estabelecida com sucesso!");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("❌ Driver Oracle não encontrado. Verifique se ojdbc11.jar está no classpath.", e);
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