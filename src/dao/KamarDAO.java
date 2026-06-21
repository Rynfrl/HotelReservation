package dao;

import config.DBConnection;
import model.Kamar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KamarDAO {

    public List<Kamar> findAll() {
        List<Kamar> list = new ArrayList<>();
        String sql = "SELECT * FROM kamar";
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

    public List<Kamar> search(String keyword) {
        List<Kamar> list = new ArrayList<>();
        String sql = "SELECT * FROM kamar WHERE nomor_kamar LIKE ? OR tipe_kamar LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Kamar findById(int id) {
        String sql = "SELECT * FROM kamar WHERE id_kamar=?";
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

    public boolean save(Kamar k) {
        String sql = "INSERT INTO kamar (nomor_kamar,tipe_kamar,harga,status) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNomorKamar());
            ps.setString(2, k.getTipeKamar());
            ps.setDouble(3, k.getHarga());
            ps.setString(4, k.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // duplicate nomor_kamar handled by caller via validation
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Kamar k) {
        String sql = "UPDATE kamar SET nomor_kamar=?, tipe_kamar=?, harga=?, status=? WHERE id_kamar=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNomorKamar());
            ps.setString(2, k.getTipeKamar());
            ps.setDouble(3, k.getHarga());
            ps.setString(4, k.getStatus());
            ps.setInt(5, k.getIdKamar());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kamar WHERE id_kamar=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Kamar> findAvailable() {
        List<Kamar> list = new ArrayList<>();
        String sql = "SELECT * FROM kamar WHERE status='TERSEDIA'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) AS cnt FROM kamar";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM kamar WHERE status=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Kamar mapRow(ResultSet rs) throws SQLException {
        Kamar k = new Kamar();
        k.setIdKamar(rs.getInt("id_kamar"));
        k.setNomorKamar(rs.getString("nomor_kamar"));
        k.setTipeKamar(rs.getString("tipe_kamar"));
        k.setHarga(rs.getDouble("harga"));
        k.setStatus(rs.getString("status"));
        return k;
    }
}
