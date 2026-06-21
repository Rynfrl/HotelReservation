package model;

import java.time.LocalDate;

public class Reservasi {
    private int idReservasi;
    private int idTamu;
    private int idKamar;
    private LocalDate tanggalCheckin;
    private LocalDate tanggalCheckout;
    private String status;

    public Reservasi() {}

    public Reservasi(int idReservasi, int idTamu, int idKamar, LocalDate tanggalCheckin, LocalDate tanggalCheckout, String status) {
        this.idReservasi = idReservasi;
        this.idTamu = idTamu;
        this.idKamar = idKamar;
        this.tanggalCheckin = tanggalCheckin;
        this.tanggalCheckout = tanggalCheckout;
        this.status = status;
    }

    public int getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(int idReservasi) {
        this.idReservasi = idReservasi;
    }

    public int getIdTamu() {
        return idTamu;
    }

    public void setIdTamu(int idTamu) {
        this.idTamu = idTamu;
    }

    public int getIdKamar() {
        return idKamar;
    }

    public void setIdKamar(int idKamar) {
        this.idKamar = idKamar;
    }

    public LocalDate getTanggalCheckin() {
        return tanggalCheckin;
    }

    public void setTanggalCheckin(LocalDate tanggalCheckin) {
        this.tanggalCheckin = tanggalCheckin;
    }

    public LocalDate getTanggalCheckout() {
        return tanggalCheckout;
    }

    public void setTanggalCheckout(LocalDate tanggalCheckout) {
        this.tanggalCheckout = tanggalCheckout;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
