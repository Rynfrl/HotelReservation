package service;

import dao.KamarDAO;
import dao.ReservasiDAO;
import model.Kamar;
import model.Reservasi;
import utils.DateHelper;

import java.time.LocalDate;
import java.util.List;

public class ReservasiService {
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private KamarDAO kamarDAO = new KamarDAO();

    public boolean createReservasi(Reservasi r) {
        // validation
        if (r.getTanggalCheckout().isBefore(r.getTanggalCheckin()) || r.getTanggalCheckout().isEqual(r.getTanggalCheckin())) {
            return false;
        }
        Kamar kamar = kamarDAO.findById(r.getIdKamar());
        if (kamar == null) return false;
        if (!"TERSEDIA".equals(kamar.getStatus())) return false;
        boolean saved = reservasiDAO.save(r);
        if (saved) {
            kamar.setStatus("DIPESAN");
            kamarDAO.update(kamar);
        }
        return saved;
    }

    public boolean checkIn(int idReservasi) {
        Reservasi r = reservasiDAO.findById(idReservasi);
        if (r == null) return false;
        if (!"DIPESAN".equals(r.getStatus())) return false;
        boolean ok = reservasiDAO.updateStatus(idReservasi, "CHECK_IN");
        if (ok) {
            Kamar k = kamarDAO.findById(r.getIdKamar());
            if (k != null) {
                k.setStatus("TERISI");
                kamarDAO.update(k);
            }
        }
        return ok;
    }

    public boolean checkOut(int idReservasi) {
        Reservasi r = reservasiDAO.findById(idReservasi);
        if (r == null) return false;
        if (!"CHECK_IN".equals(r.getStatus())) return false;
        boolean ok = reservasiDAO.updateStatus(idReservasi, "SELESAI");
        if (ok) {
            Kamar k = kamarDAO.findById(r.getIdKamar());
            if (k != null) {
                k.setStatus("TERSEDIA");
                kamarDAO.update(k);
            }
        }
        return ok;
    }

    public List<Reservasi> listAll() {
        return reservasiDAO.findAll();
    }
}
