package util;

import javax.swing.*;
import java.awt.*;
import java.awt.HeadlessException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Opens a real graphical bar-chart window using pure java.awt / javax.swing
 * (both ship with the standard JDK - no external library / download needed).
 *
 * If the machine has no display (headless environment), we catch that and
 * fall back gracefully instead of crashing - the caller should also print
 * the ConsoleChart version so data is never lost.
 */
public class SwingChart {

    public static void showBarChart(String title, Map<String, ? extends Number> data, String yAxisLabel) {
        if (data.isEmpty()) {
            System.out.println("(no data to plot for '" + title + "')");
            return;
        }
        // Check BEFORE touching any AWT/Swing class - once headless is detected here,
        // it is safe to skip the graphical window entirely (e.g. servers, CI, no monitor).
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("No graphical display available - showing console chart only.");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.add(new BarChartPanel(title, data, yAxisLabel));
                frame.setSize(760, 480);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (HeadlessException e) {
                System.out.println("No graphical display available - showing console chart only.");
            }
        });
    }

    /** Custom JPanel that draws the bars, axis line, labels and values. */
    private static class BarChartPanel extends JPanel {
        private final String title;
        private final List<String> labels = new ArrayList<>();
        private final List<Double> values = new ArrayList<>();
        private final String yAxisLabel;

        BarChartPanel(String title, Map<String, ? extends Number> data, String yAxisLabel) {
            this.title = title;
            this.yAxisLabel = yAxisLabel;
            for (Map.Entry<String, ? extends Number> e : data.entrySet()) {
                labels.add(e.getKey());
                values.add(e.getValue().doubleValue());
            }
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int margin = 60;
            int chartBottom = height - margin;
            int chartTop = margin;
            int chartLeft = margin + 20;
            int chartRight = width - 30;

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            FontMetrics titleFm = g2.getFontMetrics();
            g2.drawString(title, (width - titleFm.stringWidth(title)) / 2, 30);

            // Axes
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(Color.BLACK);
            g2.drawLine(chartLeft, chartTop, chartLeft, chartBottom);       // Y axis
            g2.drawLine(chartLeft, chartBottom, chartRight, chartBottom);   // X axis

            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            if (max == 0) max = 1.0;

            // Y axis label
            g2.drawString(yAxisLabel, 10, chartTop - 10);

            int n = values.size();
            if (n == 0) return;
            int slot = (chartRight - chartLeft) / n;
            int barWidth = Math.max(20, (int) (slot * 0.6));

            Color[] palette = {
                    new Color(66, 133, 244), new Color(219, 68, 55), new Color(244, 180, 0),
                    new Color(15, 157, 88), new Color(171, 71, 188), new Color(0, 172, 193)
            };

            for (int i = 0; i < n; i++) {
                double value = values.get(i);
                int barHeight = (int) ((value / max) * (chartBottom - chartTop - 20));
                int x = chartLeft + i * slot + (slot - barWidth) / 2;
                int y = chartBottom - barHeight;

                g2.setColor(palette[i % palette.length]);
                g2.fillRect(x, y, barWidth, barHeight);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(x, y, barWidth, barHeight);

                // Value on top of the bar
                String valueStr = (value == Math.floor(value)) ? String.valueOf((long) value)
                        : String.format("%.2f", value);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valueStr, x + (barWidth - fm.stringWidth(valueStr)) / 2, y - 5);

                // Rotated label under the bar
                String label = labels.get(i);
                Graphics2D g2r = (Graphics2D) g2.create();
                g2r.translate(x + barWidth / 2.0, chartBottom + 15);
                g2r.rotate(Math.toRadians(30));
                g2r.drawString(truncate(label, 22), 0, 0);
                g2r.dispose();
            }
        }

        private String truncate(String s, int max) {
            return s.length() <= max ? s : s.substring(0, max - 3) + "...";
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(760, 480);
        }
    }
}
