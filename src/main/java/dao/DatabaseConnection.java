package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String URL = "jdbc:postgresql://localhost:5432/unithesis_db";
    private static String USER = "postgres";
    private static String PASSWORD = "postgres"; // Valore di default generico

    static {
        // Carica il driver PostgreSQL
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver PostgreSQL non trovato!");
        }

        // Tenta di leggere le credenziali locali dal file db.properties
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                URL = prop.getProperty("db.url", URL);
                USER = prop.getProperty("db.user", USER);
                PASSWORD = prop.getProperty("db.password", PASSWORD);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Impossibile caricare db.properties, verranno usati i valori di default.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ CONNESSIONE AL DATABASE RIUSCITA CON SUCCESSO!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore durante la connessione al database:");
            e.printStackTrace();
        }
    }
}
