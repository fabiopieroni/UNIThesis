package dao.impl;

import dao.DatabaseConnection;
import dao.UtenteDAO;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Utente> trovaTutti() {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT id_utente, email, password, ruolo, nome, cognome FROM utenti ORDER BY cognome, nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Utente(
                        rs.getInt("id_utente"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Ruolo.fromString(rs.getString("ruolo")),
                        rs.getString("nome"),
                        rs.getString("cognome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero degli utenti: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Utente trovaDettagliCompletiPerId(int idUtente) {
        String sql = "SELECT * FROM utenti WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_utente");
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    String email = rs.getString("email");
                    String pass = rs.getString("password");
                    Ruolo ruolo = Ruolo.fromString(rs.getString("ruolo"));

                    if (ruolo == Ruolo.STUDENTE) {
                        return caricaDatiStudente(conn, id, email, pass, nome, cognome);
                    } else if (ruolo == Ruolo.PROFESSORE) {
                        return caricaDatiProfessore(conn, id, email, pass, nome, cognome);
                    } else {
                        Utente u = new Utente();
                        u.setIdUtente(id);
                        u.setEmail(email);
                        u.setPassword(pass);
                        u.setNome(nome);
                        u.setCognome(cognome);
                        u.setRuolo(ruolo);
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dettagli utente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean salvaStudente(Studente studente) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUtente = "INSERT INTO utenti (email, password, ruolo, nome, cognome) VALUES (?, ?, 'STUDENTE', ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUtente, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, studente.getEmail());
                stmt.setString(2, studente.getPassword());
                stmt.setString(3, studente.getNome());
                stmt.setString(4, studente.getCognome());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        studente.setIdUtente(rs.getInt(1));
                    }
                }
            }

            String sqlStudente = "INSERT INTO studenti (id_utente, matricola, cfu_totali, corso_laurea) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlStudente)) {
                stmt.setInt(1, studente.getIdUtente());
                stmt.setString(2, studente.getMatricola());
                stmt.setInt(3, studente.getCfuTotali());
                stmt.setString(4, studente.getCorsoLaurea());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("Errore durante il salvataggio dello studente: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean salvaProfessore(Professore professore) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUtente = "INSERT INTO utenti (email, password, ruolo, nome, cognome) VALUES (?, ?, 'PROFESSORE', ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUtente, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, professore.getEmail());
                stmt.setString(2, professore.getPassword());
                stmt.setString(3, professore.getNome());
                stmt.setString(4, professore.getCognome());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        professore.setIdUtente(rs.getInt(1));
                    }
                }
            }

            String sqlProfessore = "INSERT INTO professori (id_utente, matricola_docente, corso_laurea, num_tesisti_attivi) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlProfessore)) {
                stmt.setInt(1, professore.getIdUtente());
                stmt.setString(2, professore.getMatricolaDocente());
                stmt.setString(3, professore.getCorsoLaurea());
                stmt.setInt(4, professore.getNumTesistiAttivi());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("Errore durante il salvataggio del professore: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean aggiornaStudente(Studente studente) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUtente = "UPDATE utenti SET email = ?, password = ?, nome = ?, cognome = ? WHERE id_utente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUtente)) {
                stmt.setString(1, studente.getEmail());
                stmt.setString(2, studente.getPassword());
                stmt.setString(3, studente.getNome());
                stmt.setString(4, studente.getCognome());
                stmt.setInt(5, studente.getIdUtente());
                stmt.executeUpdate();
            }

            String sqlStudente = "UPDATE studenti SET matricola = ?, cfu_totali = ?, corso_laurea = ? WHERE id_utente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlStudente)) {
                stmt.setString(1, studente.getMatricola());
                stmt.setInt(2, studente.getCfuTotali());
                stmt.setString(3, studente.getCorsoLaurea());
                stmt.setInt(4, studente.getIdUtente());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("Errore durante l'aggiornamento dello studente: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean aggiornaProfessore(Professore professore) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUtente = "UPDATE utenti SET email = ?, password = ?, nome = ?, cognome = ? WHERE id_utente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUtente)) {
                stmt.setString(1, professore.getEmail());
                stmt.setString(2, professore.getPassword());
                stmt.setString(3, professore.getNome());
                stmt.setString(4, professore.getCognome());
                stmt.setInt(5, professore.getIdUtente());
                stmt.executeUpdate();
            }

            String sqlProfessore = "UPDATE professori SET matricola_docente = ?, corso_laurea = ?, num_tesisti_attivi = ? WHERE id_utente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlProfessore)) {
                stmt.setString(1, professore.getMatricolaDocente());
                stmt.setString(2, professore.getCorsoLaurea());
                stmt.setInt(3, professore.getNumTesistiAttivi());
                stmt.setInt(4, professore.getIdUtente());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("Errore durante l'aggiornamento del professore: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}