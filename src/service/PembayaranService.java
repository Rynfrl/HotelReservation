package service;

import dao.PembayaranDAO;
import dao.ReservasiDAO;
import dao.KamarDAO;
import model.Pembayaran;
import model.Reservasi;
import model.Kamar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PembayaranService {
    private PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private KamarDAO kamarDAO = new KamarDAO();

    public PembayaranService() {}

    public double calculateTotal(int idReservasi) {
        Reservasi r = reservasiDAO.findById(idReservasi);
        if (r == null) return 0.0;
        Kamar k = kamarDAO.findById(r.getIdKamar());
        if (k == null) return 0.0;
        long days = ChronoUnit.DAYS.between(r.getTanggalCheckin(), r.getTanggalCheckout());
        if (days <= 0) days = 1;
        return days * k.getHarga();
    }

    public boolean pay(int idReservasi) {
        Reservasi r = reservasiDAO.findById(idReservasi);
        if (r == null) return false;
        double total = calculateTotal(idReservasi);
        long days = java.time.temporal.ChronoUnit.DAYS.between(r.getTanggalCheckin(), r.getTanggalCheckout());
        if (days <= 0) days = 1;
        Pembayaran p = new Pembayaran();
        p.setIdReservasi(idReservasi);
        p.setLamaMenginap((int) days);
        p.setTotalBayar(total);
        p.setTanggalBayar(LocalDateTime.now());
        boolean saved = pembayaranDAO.save(p);
        return saved;
    }

    public double totalPendapatan() {
        return pembayaranDAO.totalPendapatan();
    }
}
