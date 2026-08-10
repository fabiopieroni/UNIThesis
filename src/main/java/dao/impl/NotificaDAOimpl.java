package dao.impl;

import dao.NotificaDAO;
import dao.DatabaseConnection;
import model.Notifica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificaDAOimpl implements NotificaDAO {

    @Override
    public boolean salva(Notifica n) {
        String query = "INSERT INTO notifiche (id_utente, messaggio, letta) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, n.getIdUtente());
            ps.setString(2, n.getMessaggio());
            ps.setBoolean(3, n.isLetta());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    n.setIdNotifica(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Notifica> findByUtente(int idUtente) {
        List<Notifica> lista = new ArrayList<>();
        String query = "SELECT * FROM notifiche WHERE id_utente = ? ORDER BY data_invio DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idUtente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notifica n = new Notifica();
                n.setIdNotifica(rs.getInt("id_notifica"));
                n.setIdUtente(rs.getInt("id_utente"));
                n.setMessaggio(rs.getString("messaggio"));
                n.setDataInvio(rs.getTimestamp("data_invio"));
                n.setLetta(rs.getBoolean("letta"));
                lista.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean segnaComeLetta(int idNotifica) {
        String query = "UPDATE notifiche SET letta = TRUE WHERE id_notifica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idNotifica);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}