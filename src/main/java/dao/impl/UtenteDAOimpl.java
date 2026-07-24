package dao.impl;

import dao.DatabaseConnection;
import dao.UtenteDAO;
import model.Utente;

import java.sql.*;

public class UtenteDAOimpl implements UtenteDAO {

    @Override
    public Utente trovaPerEmail(String email) {
        String sql = "SELECT id_utente, email, password, ruolo, nome, cognome FROM utenti WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getInt("id_utente"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("ruolo"),
                            rs.getString("nome"),
                            rs.getString("cognome")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca dell'utente per email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean salva(Utente utente) {
        String sql = "INSERT INTO utenti (email, password, ruolo, nome, cognome) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, utente.getEmail());
            pstmt.setString(2, utente.getPassword());
            pstmt.setString(3, utente.getRuolo());
            pstmt.setString(4, utente.getNome());
            pstmt.setString(5, utente.getCognome());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        utente.setIdUtente(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il salvataggio dell'utente: " + e.getMessage());
        }
        return false;
    }
}