package dao;

import config.DBConnection;
import model.Pembayaran;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PembayaranDAO {

    public boolean save(Pembayaran p) {
        String sql = "INSERT INTO pembayaran (id_reservasi,lama_menginap,total_bayar,tanggal_bayar) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdReservasi());
            ps.setInt(2, p.getLamaMenginap());
            ps.setDouble(3, p.getTotalBayar());
            ps.setTimestamp(4, Timestamp.valueOf(p.getTanggalBayar()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Pembayaran> findAll() {
        List<Pembayaran> list = new ArrayList<>();
        String sql = "SELECT * FROM pembayaran";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pembayaran p = new Pembayaran();
                p.setIdPembayaran(rs.getInt("id_pembayaran"));
                p.setIdReservasi(rs.getInt("id_reservasi"));
                p.setLamaMenginap(rs.getInt("lama_menginap"));
                p.setTotalBayar(rs.getDouble("total_bayar"));
                p.setTanggalBayar(rs.getTimestamp("tanggal_bayar").toLocalDateTime());
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double totalPendapatan() {
        String sql = "SELECT SUM(total_bayar) AS total FROM pembayaran";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Pembayaran findByReservasiId(int idReservasi) {
        String sql = "SELECT * FROM pembayaran WHERE id_reservasi=? ORDER BY id_pembayaran DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReservasi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pembayaran p = new Pembayaran();
                p.setIdPembayaran(rs.getInt("id_pembayaran"));
                p.setIdReservasi(rs.getInt("id_reservasi"));
                p.setLamaMenginap(rs.getInt("lama_menginap"));
                p.setTotalBayar(rs.getDouble("total_bayar"));
                p.setTanggalBayar(rs.getTimestamp("tanggal_bayar").toLocalDateTime());
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Pembayaran> findByDateRange(Date start, Date end) {
        List<Pembayaran> list = new ArrayList<>();
        String sql = "SELECT * FROM pembayaran WHERE DATE(tanggal_bayar) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, start);
            ps.setDate(2, end);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pembayaran p = new Pembayaran();
                p.setIdPembayaran(rs.getInt("id_pembayaran"));
                p.setIdReservasi(rs.getInt("id_reservasi"));
                p.setLamaMenginap(rs.getInt("lama_menginap"));
                p.setTotalBayar(rs.getDouble("total_bayar"));
                p.setTanggalBayar(rs.getTimestamp("tanggal_bayar").toLocalDateTime());
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double[] getMonthlyRevenue(int year) {
        double[] months = new double[12];
        String sql = "SELECT MONTH(tanggal_bayar) as m, SUM(total_bayar) as total FROM pembayaran WHERE YEAR(tanggal_bayar) = ? GROUP BY MONTH(tanggal_bayar)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int m = rs.getInt("m");
                if (m >= 1 && m <= 12) {
                    months[m - 1] = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return months;
    }
}
