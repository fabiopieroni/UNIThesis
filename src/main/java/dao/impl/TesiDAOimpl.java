package dao.impl;

import dao.DatabaseConnection;
import dao.TesiDAO;
import model.Tesi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TesiDAOimpl implements TesiDAO {
  @Override
  public List<Tesi> trovaDisponibili() {
    List<Tesi> lista = new ArrayList<>();
    String sql = "SELECT id_tesi, titolo, descrizione, corso_laurea, stato, id_professore " +
            "FROM tesi WHERE stato = 'PUBBLICATA'";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        lista.add(new Tesi(
                rs.getInt("id_tesi"),
                rs.getString("titolo"),
                rs.getString("descrizione"),
                rs.getString("corso_laurea"),
                rs.getString("stato"),
                rs.getInt("id_professore")
        ));
      }
    } catch (SQLException e) {
      System.err.println("Errore durante il recupero delle tesi disponibili: " + e.getMessage());
    }
    return lista;
  }
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

  @Override
  public Tesi getTesiById(int idTesi) {
    String query = "SELECT * FROM tesi WHERE id_tesi = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setInt(1, idTesi);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return mappaResultSetInTesi(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

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
