package dao.impl;

import dao.DatabaseConnection;
import dao.CorsoDiLaureaDAO;
import model.CorsoDiLaurea;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CorsoDiLaureaDAOimpl implements CorsoDiLaureaDAO {

    @Override
    public List<CorsoDiLaurea> trovaTutti() {
        List<CorsoDiLaurea> corsi = new ArrayList<>();
        String sql = "SELECT id, nome, dipartimento FROM corsi_di_laurea";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                corsi.add(new CorsoDiLaurea(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("dipartimento")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dei corsi di laurea: " + e.getMessage());
        }
        return corsi;
    }

    @Override
    public boolean salva(CorsoDiLaurea corso) {
        String sql = "INSERT INTO corsi_di_laurea (nome, dipartimento) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, corso.getNome());
            pstmt.setString(2, corso.getDipartimento());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        corso.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il salvataggio del corso di laurea: " + e.getMessage());
        }
        return false;
    }
}