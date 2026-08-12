package dao.impl;

import dao.DatabaseConnection;
import dao.UtenteDAO;
import model.*;

import java.sql.*;

public class UtenteDAOimpl implements UtenteDAO {

    @Override
    public Utente login(String email, String password) {
        String sql = "SELECT * FROM utenti WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idUtente = rs.getInt("id_utente");
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    String emailDb = rs.getString("email");
                    String passDb = rs.getString("password");
                    Ruolo ruolo = Ruolo.fromString(rs.getString("ruolo"));

                    // Caricamento dei dati specifici in base al ruolo
                    if (ruolo == Ruolo.STUDENTE) {
                        return caricaDatiStudente(conn, idUtente, emailDb, passDb, nome, cognome);
                    } else if (ruolo == Ruolo.PROFESSORE) {
                        return caricaDatiProfessore(conn, idUtente, emailDb, passDb, nome, cognome);
                    } else if (ruolo == Ruolo.SEGRETERIA) {
                        SegreteriaDidattica seg = new SegreteriaDidattica();
                        seg.setIdUtente(idUtente);
                        seg.setEmail(emailDb);
                        seg.setPassword(passDb);
                        seg.setNome(nome);
                        seg.setCognome(cognome);
                        seg.setRuolo(Ruolo.SEGRETERIA);
                        return seg;
                    } else {
                        Utente u = new Utente();
                        u.setIdUtente(idUtente);
                        u.setEmail(emailDb);
                        u.setPassword(passDb);
                        u.setNome(nome);
                        u.setCognome(cognome);
                        u.setRuolo(ruolo);
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore durante il login: " + e.getMessage());
        }
        return null;
    }

    private Studente caricaDatiStudente(Connection conn, int id, String email, String pass, String nome, String cognome) throws SQLException {
        String sql = "SELECT * FROM studenti WHERE id_utente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Studente s = new Studente();
                    s.setIdUtente(id);
                    s.setEmail(email);
                    s.setPassword(pass);
                    s.setNome(nome);
                    s.setCognome(cognome);
                    s.setRuolo(Ruolo.STUDENTE);
                    s.setMatricola(rs.getString("matricola"));
                    s.setCfuTotali(rs.getInt("cfu_totali"));
                    s.setCorsoLaurea(rs.getString("corso_laurea"));
                    return s;
                }
            }
        }
        return null;
    }

    private Professore caricaDatiProfessore(Connection conn, int id, String email, String pass, String nome, String cognome) throws SQLException {
        String sql = "SELECT * FROM professori WHERE id_utente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Professore p = new Professore();
                    p.setIdUtente(id);
                    p.setEmail(email);
                    p.setPassword(pass);
                    p.setNome(nome);
                    p.setCognome(cognome);
                    p.setRuolo(Ruolo.PROFESSORE);
                    p.setMatricolaDocente(rs.getString("matricola_docente"));
                    p.setCorsoLaurea(rs.getString("corso_laurea"));
                    p.setNumTesistiAttivi(rs.getInt("num_tesisti_attivi"));
                    return p;
                }
            }
        }
        return null;
    }
    @Override
    public Utente trovaPerId(int idUtente) {
        String sql = "SELECT id_utente, email, password, ruolo, nome, cognome FROM utenti WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUtente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getInt("id_utente"),
                            rs.getString("email"),
                            rs.getString("password"),
                            Ruolo.fromString(rs.getString("ruolo")),
                            rs.getString("nome"),
                            rs.getString("cognome")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca dell'utente per id: " + e.getMessage());
        }
        return null;
    }

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
                            Ruolo.fromString(rs.getString("ruolo")),
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
            pstmt.setString(3, utente.getRuolo() != null ? utente.getRuolo().name() : null);
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

    @Override
    public boolean aggiornaProfilo(int idUtente, String nuovaEmail, String nuovaPassword) {
        String sql = "UPDATE utenti SET email = ?, password = ? WHERE id_utente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuovaEmail);
            stmt.setString(2, nuovaPassword);
            stmt.setInt(3, idUtente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Errore aggiornamento profilo: " + e.getMessage());
            return false;
        }
    }
}