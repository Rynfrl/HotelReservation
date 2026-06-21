package model;

public class Tamu {
    private int idTamu;
    private String nama;
    private String noHp;
    private String alamat;

    public Tamu() {}

    public Tamu(int idTamu, String nama, String noHp, String alamat) {
        this.idTamu = idTamu;
        this.nama = nama;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    public int getIdTamu() {
        return idTamu;
    }

    public void setIdTamu(int idTamu) {
        this.idTamu = idTamu;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
