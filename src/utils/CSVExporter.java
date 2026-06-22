package utils;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVExporter {
    
    public static boolean exportTableToCSV(JTable table, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(filePath)))) {
            TableModel model = table.getModel();
            
            // Print Headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.print("\"" + model.getColumnName(i) + "\"");
                if (i < model.getColumnCount() - 1) {
                    writer.print(",");
                }
            }
            writer.println();
            
            // Print Rows
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    String strVal = (val == null) ? "" : val.toString();
                    // Escape quotes
                    strVal = strVal.replace("\"", "\"\"");
                    writer.print("\"" + strVal + "\"");
                    
                    if (j < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
