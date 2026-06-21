package dao;

import config.DBConnection;
import model.Tamu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TamuDAO {

    public List<Tamu> findAll() {
        List<Tamu> list = new ArrayList<>();
        String sql = "SELECT * FROM tamu";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tamu> search(String keyword) {
        List<Tamu> list = new ArrayList<>();
        String sql = "SELECT * FROM tamu WHERE nama LIKE ? OR no_hp LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean save(Tamu t) {
        String sql = "INSERT INTO tamu (nama,no_hp,alamat) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNama());
            ps.setString(2, t.getNoHp());
            ps.setString(3, t.getAlamat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Tamu t) {
        String sql = "UPDATE tamu SET nama=?, no_hp=?, alamat=? WHERE id_tamu=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNama());
            ps.setString(2, t.getNoHp());
            ps.setString(3, t.getAlamat());
            ps.setInt(4, t.getIdTamu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tamu WHERE id_tamu=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tamu findById(int id) {
        String sql = "SELECT * FROM tamu WHERE id_tamu=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Tamu mapRow(ResultSet rs) throws SQLException {
        Tamu t = new Tamu();
        t.setIdTamu(rs.getInt("id_tamu"));
        t.setNama(rs.getString("nama"));
        t.setNoHp(rs.getString("no_hp"));
        t.setAlamat(rs.getString("alamat"));
        return t;
    }
}
