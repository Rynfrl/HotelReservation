package dao;

import config.DBConnection;
import model.Reservasi;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservasiDAO {

    public List<Reservasi> findAll() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT * FROM reservasi";
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

    public Reservasi findById(int id) {
        String sql = "SELECT * FROM reservasi WHERE id_reservasi=?";
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

    public boolean save(Reservasi r) {
        String sql = "INSERT INTO reservasi (id_tamu,id_kamar,tanggal_checkin,tanggal_checkout,status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdTamu());
            ps.setInt(2, r.getIdKamar());
            ps.setDate(3, Date.valueOf(r.getTanggalCheckin()));
            ps.setDate(4, Date.valueOf(r.getTanggalCheckout()));
            ps.setString(5, r.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int idReservasi, String status) {
        String sql = "UPDATE reservasi SET status=? WHERE id_reservasi=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idReservasi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reservasi> findByDateRange(Date start, Date end) {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT * FROM reservasi WHERE tanggal_checkin >= ? AND tanggal_checkout <= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, start);
            ps.setDate(2, end);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) AS cnt FROM reservasi";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Reservasi mapRow(ResultSet rs) throws SQLException {
        Reservasi r = new Reservasi();
        r.setIdReservasi(rs.getInt("id_reservasi"));
        r.setIdTamu(rs.getInt("id_tamu"));
        r.setIdKamar(rs.getInt("id_kamar"));
        r.setTanggalCheckin(rs.getDate("tanggal_checkin").toLocalDate());
        r.setTanggalCheckout(rs.getDate("tanggal_checkout").toLocalDate());
        r.setStatus(rs.getString("status"));
        return r;
    }
}
