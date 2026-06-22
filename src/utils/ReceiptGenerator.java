package utils;

import model.Pembayaran;
import model.Reservasi;
import model.Kamar;
import model.Tamu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {
    
    public static String generateReceipt(Pembayaran p, Reservasi r, Kamar k, Tamu t, double dibayar, double kembalian) {
        File dir = new File("struk");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String fileName = "struk/STRUK_TRX_" + p.getIdPembayaran() + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==============================================");
            writer.println("            HOTEL RESERVATION SYSTEM          ");
            writer.println("               STRUK PEMBAYARAN               ");
            writer.println("==============================================");
            writer.println("ID Pembayaran : TRX-" + p.getIdPembayaran());
            if (p.getTanggalBayar() != null) {
                writer.println("Tanggal Bayar : " + p.getTanggalBayar().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
            writer.println("----------------------------------------------");
            writer.println("Data Tamu:");
            writer.println("Nama          : " + t.getNama());
            writer.println("No HP         : " + t.getNoHp());
            writer.println("----------------------------------------------");
            writer.println("Data Menginap:");
            writer.println("Kamar         : " + k.getNomorKamar() + " (" + k.getTipeKamar() + ")");
            writer.println("Check-In      : " + r.getTanggalCheckin());
            writer.println("Check-Out     : " + r.getTanggalCheckout());
            writer.println("Lama Menginap : " + p.getLamaMenginap() + " malam");
            writer.println("Harga / Malam : Rp " + String.format("%,.2f", k.getHarga()));
            writer.println("----------------------------------------------");
            writer.println("TOTAL TAGIHAN : Rp " + String.format("%,.2f", p.getTotalBayar()));
            writer.println("DIBAYAR       : Rp " + String.format("%,.2f", dibayar));
            writer.println("KEMBALIAN     : Rp " + String.format("%,.2f", kembalian));
            writer.println("==============================================");
            
            String kasirName = utils.Session.getUser() != null ? utils.Session.getUser().getUsername() : "System";
            writer.println("Kasir yang Melayani: " + kasirName);
            writer.println("==============================================");
            
            writer.println("        Terima Kasih Atas Kunjungan Anda      ");
            writer.println("==============================================");
            return new File(fileName).getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
