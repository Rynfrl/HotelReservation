package model;

import java.time.LocalDateTime;

public class Pembayaran {
    private int idPembayaran;
    private int idReservasi;
    private int lamaMenginap;
    private double totalBayar;
    private LocalDateTime tanggalBayar;

    public Pembayaran() {}

    public Pembayaran(int idPembayaran, int idReservasi, int lamaMenginap, double totalBayar, LocalDateTime tanggalBayar) {
        this.idPembayaran = idPembayaran;
        this.idReservasi = idReservasi;
        this.lamaMenginap = lamaMenginap;
        this.totalBayar = totalBayar;
        this.tanggalBayar = tanggalBayar;
    }

    public int getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public int getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(int idReservasi) {
        this.idReservasi = idReservasi;
    }

    public int getLamaMenginap() {
        return lamaMenginap;
    }

    public void setLamaMenginap(int lamaMenginap) {
        this.lamaMenginap = lamaMenginap;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(double totalBayar) {
        this.totalBayar = totalBayar;
    }

    public LocalDateTime getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(LocalDateTime tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }
}
