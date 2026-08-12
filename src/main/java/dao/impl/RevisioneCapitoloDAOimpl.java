package dao.impl;

import dao.DatabaseConnection;
import dao.RevisioneCapitoloDAO;
import model.RevisioneCapitolo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevisioneCapitoloDAOimpl implements RevisioneCapitoloDAO {

    @Override
    public boolean salva(RevisioneCapitolo r) {
        String query = "INSERT INTO revisioni_capitoli (id_tesi, num_capitolo, titolo_capitolo, percorso_pdf, note_professore, stato_revisione) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getIdTesi());
            ps.setInt(2, r.getNumCapitolo());
            ps.setString(3, r.getTitoloCapitolo());
            ps.setString(4, r.getPercorsoPdf());
            ps.setString(5, r.getNoteProfessore());
            ps.setString(6, r.getStatoRevisione() != null ? r.getStatoRevisione() : "IN_REVISIONE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    r.setIdRevisione(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean rinviaCorrezione(int idRevisione, String nuovoPercorsoPdf) {
        String query = "UPDATE revisioni_capitoli SET percorso_pdf = ?, stato_revisione = 'IN_REVISIONE', " +
                "data_invio = CURRENT_TIMESTAMP WHERE id_revisione = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nuovoPercorsoPdf);
            ps.setInt(2, idRevisione);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<RevisioneCapitolo> findByTesi(int idTesi) {
        List<RevisioneCapitolo> lista = new ArrayList<>();
        String query = "SELECT * FROM revisioni_capitoli WHERE id_tesi = ? ORDER BY num_capitolo ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idTesi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevisioneCapitolo r = new RevisioneCapitolo();
                r.setIdRevisione(rs.getInt("id_revisione"));
                r.setIdTesi(rs.getInt("id_tesi"));
                r.setNumCapitolo(rs.getInt("num_capitolo"));
                r.setTitoloCapitolo(rs.getString("titolo_capitolo"));
                r.setPercorsoPdf(rs.getString("percorso_pdf"));
                r.setNoteProfessore(rs.getString("note_professore"));
                r.setStatoRevisione(rs.getString("stato_revisione"));
                r.setDataInvio(rs.getTimestamp("data_invio"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean aggiornaStatoENote(int idRevisione, String stato, String note) {
        String query = "UPDATE revisioni_capitoli SET stato_revisione = ?, note_professore = ? WHERE id_revisione = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, stato);
            ps.setString(2, note);
            ps.setInt(3, idRevisione);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}