package dao.impl;
import model.RichiestaConDettagli;
import dao.DatabaseConnection;
import dao.RichiestaDAO;
import model.Richiesta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAOimpl implements RichiestaDAO {

    @Override
    public boolean salvaRichiesta(Richiesta richiesta) {
        if (haRichiestaAttiva(richiesta.getIdStudente())) {
            System.err.println("Lo studente ha già una candidatura attiva.");
            return false;
        }

        String sql = "INSERT INTO richieste_tesi (id_studente, id_tesi, motivazione) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, richiesta.getIdStudente());
            pstmt.setInt(2, richiesta.getIdTesi());
            pstmt.setString(3, richiesta.getMotivazione());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        richiesta.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            // Se per qualche race condition il check sopra non basta,
            // il vincolo DB (unique index) fa scattare qui una violazione
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                System.err.println("Candidatura attiva già presente (vincolo DB).");
            } else {
                System.err.println("Errore durante il salvataggio della richiesta: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public List<Richiesta> trovaPerStudente(int idStudente) {
        List<Richiesta> lista = new ArrayList<>();
        String sql = "SELECT id, id_studente, id_tesi, stato, data_richiesta, motivazione FROM richieste_tesi WHERE id_studente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idStudente);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Richiesta r = new Richiesta();
                    r.setId(rs.getInt("id"));
                    r.setIdStudente(rs.getInt("id_studente"));
                    r.setIdTesi(rs.getInt("id_tesi"));
                    r.setStato(rs.getString("stato"));
                    r.setDataRichiesta(rs.getTimestamp("data_richiesta"));
                    r.setMotivazione(rs.getString("motivazione"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca delle richieste dello studente: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Integer trovaIdTesiAccettataPerStudente(int idStudente) {
        String sql = "SELECT id_tesi FROM richieste_tesi WHERE id_studente = ? AND stato = 'ACCETTATA' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idStudente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_tesi");
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca della tesi accettata: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean aggiornaStato(int idRichiesta, String nuovoStato) {
        String sql = "UPDATE richieste_tesi SET stato = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuovoStato);
            pstmt.setInt(2, idRichiesta);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento dello stato della richiesta: " + e.getMessage());
        }
        return false;
    }
    @Override
    public List<RichiestaConDettagli> trovaPerProfessore(int idProfessore) {
        List<RichiestaConDettagli> lista = new ArrayList<>();
        String sql = "SELECT r.id, u.nome, u.cognome, t.titolo, t.id_tesi, r.stato, r.data_richiesta, r.motivazione " +
                "FROM richieste_tesi r " +
                "JOIN studenti s ON r.id_studente = s.id_utente " +
                "JOIN utenti u ON s.id_utente = u.id_utente " +
                "JOIN tesi t ON r.id_tesi = t.id_tesi " +
                "WHERE t.id_professore = ? " +
                "ORDER BY r.data_richiesta DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProfessore);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new RichiestaConDettagli(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("titolo"),
                            rs.getInt("id_tesi"),
                            rs.getString("stato"),
                            rs.getTimestamp("data_richiesta"),
                            rs.getString("motivazione")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero delle richieste del professore: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean accettaRichiesta(int idRichiesta, int idProfessore) {
        String getTesiSql = "SELECT id_tesi FROM richieste_tesi WHERE id = ?";
        String checkSql = "SELECT num_tesisti_attivi FROM professori WHERE id_utente = ? FOR UPDATE";
        String updateRichiesta = "UPDATE richieste_tesi SET stato = 'ACCETTATA' WHERE id = ?";
        String updateProfessore = "UPDATE professori SET num_tesisti_attivi = num_tesisti_attivi + 1 WHERE id_utente = ?";
        String updateTesi = "UPDATE tesi SET stato = 'IN_CORSO' WHERE id_tesi = ?";
        String rifiutaAltreSql = "UPDATE richieste_tesi SET stato = 'RIFIUTATA' " +
                "WHERE id_tesi = ? AND id != ? AND stato = 'IN_ATTESA'";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int idTesi;
            try (PreparedStatement getTesi = conn.prepareStatement(getTesiSql)) {
                getTesi.setInt(1, idRichiesta);
                try (ResultSet rs = getTesi.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    idTesi = rs.getInt("id_tesi");
                }
            }

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, idProfessore);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int attivi = rs.getInt("num_tesisti_attivi");
                        if (attivi >= 5) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            try (PreparedStatement upd1 = conn.prepareStatement(updateRichiesta);
                 PreparedStatement upd2 = conn.prepareStatement(updateProfessore);
                 PreparedStatement upd3 = conn.prepareStatement(updateTesi);
                 PreparedStatement upd4 = conn.prepareStatement(rifiutaAltreSql)) {

                upd1.setInt(1, idRichiesta);
                upd1.executeUpdate();

                upd2.setInt(1, idProfessore);
                upd2.executeUpdate();

                upd3.setInt(1, idTesi);
                upd3.executeUpdate();

                upd4.setInt(1, idTesi);
                upd4.setInt(2, idRichiesta);
                upd4.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore durante l'accettazione: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rifiutaRichiesta(int idRichiesta) {
        return aggiornaStato(idRichiesta, "RIFIUTATA");
    }

    @Override
    public boolean haRichiestaAttiva(int idStudente) {
        String sql = "SELECT 1 FROM richieste_tesi WHERE id_studente = ? AND stato IN ('IN_ATTESA', 'ACCETTATA')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idStudente);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true se trova almeno una riga
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il controllo della richiesta attiva: " + e.getMessage());
            return true; // fail-safe: in caso di errore, blocca per precauzione
        }
    }
}