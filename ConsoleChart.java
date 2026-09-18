package util;

import java.util.Map;

/**
 * Lightweight text-based bar chart, printed straight to the console.
 * Works everywhere (no display required), and is a handy fallback
 * alongside the graphical SwingChart.
 */
public class ConsoleChart {

    private static final int MAX_BAR_WIDTH = 40;

    public static void printBarChart(String title, Map<String, ? extends Number> data, String unitLabel) {
        System.out.println();
        System.out.println("===== " + title + " =====");

        if (data.isEmpty()) {
            System.out.println("(no data to display)");
            return;
        }

        double max = data.values().stream().mapToDouble(Number::doubleValue).max().orElse(1.0);
        if (max == 0) max = 1.0;

        for (Map.Entry<String, ? extends Number> entry : data.entrySet()) {
            double value = entry.getValue().doubleValue();
            int barLength = (int) Math.round((value / max) * MAX_BAR_WIDTH);
            String bar = "#".repeat(Math.max(barLength, value > 0 ? 1 : 0));
            System.out.printf("%-25s | %-" + MAX_BAR_WIDTH + "s %.2f %s%n",
                    truncate(entry.getKey(), 25), bar, value, unitLabel);
        }
        System.out.println();
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
