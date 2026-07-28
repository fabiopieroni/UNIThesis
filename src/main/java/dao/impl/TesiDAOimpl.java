package dao.impl;

import dao.DatabaseConnection;
import dao.TesiDAO;
import model.Tesi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TesiDAOimpl implements TesiDAO {

  // 1. Inserimento nuova proposta tesi
  @Override
  public boolean salvaTesi(Tesi tesi) {
    String query = "INSERT INTO tesi (titolo, descrizione, corso_laurea, stato, id_professore) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, tesi.getTitolo());
      stmt.setString(2, tesi.getDescrizione());
      stmt.setString(3, tesi.getCorsoLaurea());
      stmt.setString(4, tesi.getStato() != null ? tesi.getStato() : "DISPONIBILE");
      stmt.setInt(5, tesi.getIdProfessore());

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // 2. Modifica proposta tesi
  @Override
  public boolean aggiornaTesi(Tesi tesi) {
    String query = "UPDATE tesi SET titolo = ?, descrizione = ?, corso_laurea = ?, stato = ? WHERE id_tesi = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, tesi.getTitolo());
      stmt.setString(2, tesi.getDescrizione());
      stmt.setString(3, tesi.getCorsoLaurea());
      stmt.setString(4, tesi.getStato());
      stmt.setInt(5, tesi.getIdTesi());

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // 3a. Ricerca per Parola Chiave (nel titolo o nella descrizione)
  @Override
  public List<Tesi> cercaPerParolaChiave(String keyword) {
    List<Tesi> risultati = new ArrayList<>();
    String query = "SELECT * FROM tesi WHERE LOWER(titolo) LIKE ? OR LOWER(descrizione) LIKE ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      String param = "%" + keyword.toLowerCase() + "%";
      stmt.setString(1, param);
      stmt.setString(2, param);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        risultati.add(mappaResultSetInTesi(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return risultati;
  }

  // 3b. Ricerca per Professore (tramite ID)
  @Override
  public List<Tesi> cercaPerProfessore(int idProfessore) {
    List<Tesi> risultati = new ArrayList<>();
    String query = "SELECT * FROM tesi WHERE id_professore = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setInt(1, idProfessore);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        risultati.add(mappaResultSetInTesi(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return risultati;
  }

  // Metodo privato di supporto per convertire una riga del ResultSet in un oggetto Tesi
  private Tesi mappaResultSetInTesi(ResultSet rs) throws SQLException {
    return new Tesi(
      rs.getInt("id_tesi"),
      rs.getString("titolo"),
      rs.getString("descrizione"),
      rs.getString("corso_laurea"),
      rs.getString("stato"),
      rs.getInt("id_professore")
    );
  }
}
