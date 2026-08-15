package dao;

import dao.impl.RichiestaDAOimpl;
import model.Richiesta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RichiestaDAOimplBusinessTest {

    private final RichiestaDAOimpl dao = new RichiestaDAOimpl();
    private final List<Integer> utentiCreati = new ArrayList<>();
    private final List<Integer> tesiCreate = new ArrayList<>();
    private final List<Integer> richiesteCreate = new ArrayList<>();

    private int inserisciUtenteProfessore(int numTesistiAttivi) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int idUtente;
            String sqlUtente = "INSERT INTO utenti (email, password, ruolo, nome, cognome) " +
                    "VALUES (?, 'pw', 'PROFESSORE', 'Test', 'Prof') RETURNING id_utente";
            try (PreparedStatement ps = conn.prepareStatement(sqlUtente)) {
                ps.setString(1, "test.prof." + System.nanoTime() + "@unifi.it");
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idUtente = rs.getInt(1);
                }
            }
            String sqlProf = "INSERT INTO professori (id_utente, matricola_docente, corso_laurea, num_tesisti_attivi) " +
                    "VALUES (?, ?, 'Informatica', ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlProf)) {
                ps.setInt(1, idUtente);
                ps.setString(2, "DOC" + System.nanoTime() % 100000);
                ps.setInt(3, numTesistiAttivi);
                ps.executeUpdate();
            }
            utentiCreati.add(idUtente);
            return idUtente;
        }
    }

    private int inserisciUtenteStudente() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int idUtente;
            String sqlUtente = "INSERT INTO utenti (email, password, ruolo, nome, cognome) " +
                    "VALUES (?, 'pw', 'STUDENTE', 'Test', 'Studente') RETURNING id_utente";
            try (PreparedStatement ps = conn.prepareStatement(sqlUtente)) {
                ps.setString(1, "test.studente." + System.nanoTime() + "@studenti.it");
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idUtente = rs.getInt(1);
                }
            }
            String sqlStud = "INSERT INTO studenti (id_utente, matricola, cfu_totali, corso_laurea) " +
                    "VALUES (?, ?, 100, 'Informatica')";
            try (PreparedStatement ps = conn.prepareStatement(sqlStud)) {
                ps.setInt(1, idUtente);
                ps.setString(2, "MAT" + System.nanoTime() % 100000);
                ps.executeUpdate();
            }
            utentiCreati.add(idUtente);
            return idUtente;
        }
    }

    private int inserisciTesi(int idProfessore) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO tesi (titolo, descrizione, corso_laurea, stato, id_professore) " +
                    "VALUES ('Tesi Test', 'desc', 'Informatica', 'DISPONIBILE', ?) RETURNING id_tesi";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idProfessore);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int id = rs.getInt(1);
                    tesiCreate.add(id);
                    return id;
                }
            }
        }
    }

    private int inserisciRichiesta(int idStudente, int idTesi, String stato) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO richieste_tesi (id_studente, id_tesi, stato, motivazione) " +
                    "VALUES (?, ?, ?, 'test') RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idStudente);
                ps.setInt(2, idTesi);
                ps.setString(3, stato);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int id = rs.getInt(1);
                    richiesteCreate.add(id);
                    return id;
                }
            }
        }
    }

    private String leggiStato(int idRichiesta) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT stato FROM richieste_tesi WHERE id = ?")) {
            ps.setInt(1, idRichiesta);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getString("stato");
            }
        }
    }

    private int leggiNumTesisti(int idProfessore) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT num_tesisti_attivi FROM professori WHERE id_utente = ?")) {
            ps.setInt(1, idProfessore);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("num_tesisti_attivi");
            }
        }
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (int id : richiesteCreate) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM richieste_tesi WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            for (int id : tesiCreate) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tesi WHERE id_tesi = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            for (int id : utentiCreati) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM utenti WHERE id_utente = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
        }
        richiesteCreate.clear();
        tesiCreate.clear();
        utentiCreati.clear();
    }

    // Test 10: prof con 5 tesisti attivi -> accettazione fallisce
    @Test
    void accettaFalliceSeProfessoreHaGia5Tesisti() throws SQLException {
        int idProf = inserisciUtenteProfessore(5);
        int idStudente = inserisciUtenteStudente();
        int idTesi = inserisciTesi(idProf);
        int idRichiesta = inserisciRichiesta(idStudente, idTesi, "IN_ATTESA");

        boolean risultato = dao.accettaRichiesta(idRichiesta, idProf);

        assertFalse(risultato);
        assertEquals("IN_ATTESA", leggiStato(idRichiesta));
        assertEquals(5, leggiNumTesisti(idProf));
    }

    // Test 11: prof con 4 tesisti attivi -> accettazione riesce, diventa 5
    @Test
    void accettaRiesceSeProfessoreHa4Tesisti() throws SQLException {
        int idProf = inserisciUtenteProfessore(4);
        int idStudente = inserisciUtenteStudente();
        int idTesi = inserisciTesi(idProf);
        int idRichiesta = inserisciRichiesta(idStudente, idTesi, "IN_ATTESA");

        boolean risultato = dao.accettaRichiesta(idRichiesta, idProf);

        assertTrue(risultato);
        assertEquals("ACCETTATA", leggiStato(idRichiesta));
        assertEquals(5, leggiNumTesisti(idProf));
    }

    // Test 12: studente con candidatura IN_ATTESA prova a ricandidarsi (anche a tesi diversa) -> fallisce
    @Test
    void candidaturaFalliceSeStudenteHaGiaRichiestaInAttesa() throws SQLException {
        int idProf = inserisciUtenteProfessore(0);
        int idStudente = inserisciUtenteStudente();
        int idTesi1 = inserisciTesi(idProf);
        int idTesi2 = inserisciTesi(idProf);
        inserisciRichiesta(idStudente, idTesi1, "IN_ATTESA");

        Richiesta nuovaRichiesta = new Richiesta(idStudente, idTesi2, "seconda candidatura");
        boolean risultato = dao.salvaRichiesta(nuovaRichiesta);

        assertFalse(risultato);
    }

    // Test 13: studente con candidatura RIFIUTATA -> può ricandidarsi
    @Test
    void candidaturaRiesceSeRichiestaPrecedenteERifiutata() throws SQLException {
        int idProf = inserisciUtenteProfessore(0);
        int idStudente = inserisciUtenteStudente();
        int idTesi1 = inserisciTesi(idProf);
        int idTesi2 = inserisciTesi(idProf);
        inserisciRichiesta(idStudente, idTesi1, "RIFIUTATA");

        Richiesta nuovaRichiesta = new Richiesta(idStudente, idTesi2, "seconda candidatura");
        boolean risultato = dao.salvaRichiesta(nuovaRichiesta);

        assertTrue(risultato);
        if (risultato) richiesteCreate.add(nuovaRichiesta.getId());
    }

    // Test 14: accettare una candidatura -> le altre IN_ATTESA sulla stessa tesi diventano RIFIUTATA
    @Test
    void accettaRifiutaAltreCandidatureSullaStessaTesi() throws SQLException {
        int idProf = inserisciUtenteProfessore(0);
        int idTesi = inserisciTesi(idProf);

        int idStudente1 = inserisciUtenteStudente();
        int idStudente2 = inserisciUtenteStudente();

        int idRichiesta1 = inserisciRichiesta(idStudente1, idTesi, "IN_ATTESA");
        int idRichiesta2 = inserisciRichiesta(idStudente2, idTesi, "IN_ATTESA");

        boolean risultato = dao.accettaRichiesta(idRichiesta1, idProf);

        assertTrue(risultato);
        assertEquals("ACCETTATA", leggiStato(idRichiesta1));
        assertEquals("RIFIUTATA", leggiStato(idRichiesta2));
    }

    // Test 15 (rivisto): il vincolo UNIQUE del DB impedisce fisicamente a uno
    // studente di avere due candidature attive contemporaneamente, anche su
    // tesi diverse -> verifica della protezione a livello database
    @Test
    void dbImpedisceDoppiaRichiestaAttivaStessoStudenteSuTesiDiverse() throws SQLException {
        int idProf = inserisciUtenteProfessore(0);
        int idStudente = inserisciUtenteStudente();
        int idTesi1 = inserisciTesi(idProf);
        int idTesi2 = inserisciTesi(idProf);

        inserisciRichiesta(idStudente, idTesi1, "IN_ATTESA");

        assertThrows(SQLException.class, () -> {
            inserisciRichiesta(idStudente, idTesi2, "IN_ATTESA");
        });
    }
}