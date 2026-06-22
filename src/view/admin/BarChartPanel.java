package view.admin;

import javax.swing.*;
import java.awt.*;

public class BarChartPanel extends JPanel {
    private double[] values;
    private String[] labels;
    private String title;

    public BarChartPanel(double[] values, String[] labels, String title) {
        this.values = values;
        this.labels = labels;
        this.title = title;
        setBackground(Color.WHITE);
        putClientProperty("FlatLaf.style", "arc: 15");
    }

    public void setValues(double[] values) {
        this.values = values;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values == null || values.length == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int padding = 50;
        int labelPadding = 30;

        // Draw title
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g2.drawString(title, (panelWidth - titleWidth) / 2, padding / 2 + fmTitle.getAscent() / 2);

        // Find max value
        double maxValue = 0;
        for (double v : values) {
            if (v > maxValue) maxValue = v;
        }
        if (maxValue == 0) maxValue = 1; // prevent divide by zero

        // Draw axes
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(padding, panelHeight - padding - labelPadding, padding, padding);
        g2.drawLine(padding, panelHeight - padding - labelPadding, panelWidth - padding, panelHeight - padding - labelPadding);

        int barWidth = (panelWidth - 2 * padding) / values.length - 10;
        if (barWidth < 5) barWidth = 5;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < values.length; i++) {
            int x = padding + i * (barWidth + 10) + 10;
            int barHeight = (int) ((values[i] / maxValue) * (panelHeight - 2 * padding - labelPadding));
            int y = panelHeight - padding - labelPadding - barHeight;

            // Draw bar
            g2.setColor(Color.decode("#3B82F6")); // Tailwind Blue-500
            g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

            // Draw value on top (formatted as millions/thousands for space if needed)
            if (values[i] > 0) {
                g2.setColor(Color.DARK_GRAY);
                String valStr;
                if (values[i] >= 1000000) {
                    valStr = String.format("%.1fM", values[i] / 1000000.0);
                } else {
                    valStr = String.format("%,.0f", values[i]);
                }
                int valWidth = fm.stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - valWidth) / 2, y - 5);
            }

            // Draw label below
            g2.setColor(Color.BLACK);
            int lblWidth = fm.stringWidth(labels[i]);
            g2.drawString(labels[i], x + (barWidth - lblWidth) / 2, panelHeight - padding - 10);
        }
    }
}
