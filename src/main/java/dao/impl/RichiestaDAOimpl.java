package dao.impl;

import dao.DatabaseConnection;
import dao.RichiestaDAO;
import model.Richiesta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAOimpl implements RichiestaDAO {

    @Override
    public boolean salvaRichiesta(Richiesta richiesta) {
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
            System.err.println("Errore durante il salvataggio della richiesta: " + e.getMessage());
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
}