import javax.swing.*;
import java.awt.*;
import javax.swing.table.TableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataGraphWindow extends JFrame {
    private JLabel dateTime;

    public DataGraphWindow(TableModel tableModel) {
        setTitle("Médilog - Data Graph");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(942, 627);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Set Background
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/Backgroundimg2.png")));
        background.setBounds(0, 0, 942, 627);
        background.setOpaque(false);
        add(background);

        // Add Logo
        JLabel logo = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/logo2.png"))
                .getImage()
                .getScaledInstance(170, 58, Image.SCALE_SMOOTH)));
        logo.setBounds(20, 10, 170, 58);
        background.add(logo);

        // Add Date and Time
        dateTime = new JLabel();
        dateTime.setFont(new Font("Arial", Font.BOLD, 14));
        dateTime.setForeground(Color.BLACK);
        dateTime.setBounds(700, 10, 230, 30);
        updateDateTime();
        background.add(dateTime);

        // Create datasets for the graphs
        double[] weights = new double[tableModel.getRowCount()];
        double[] temperatures = new double[tableModel.getRowCount()];
        double[] systolic = new double[tableModel.getRowCount()];
        double[] diastolic = new double[tableModel.getRowCount()];
        String[] days = new String[tableModel.getRowCount()];

        // Populate datasets
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                days[i] = tableModel.getValueAt(i, 0).toString(); // Day label

                // Remove units before parsing
                String weight = tableModel.getValueAt(i, 1).toString().replace(" KG", "").trim();
                String temp = tableModel.getValueAt(i, 2).toString().replace("°C", "").trim();
                String bp = tableModel.getValueAt(i, 3).toString().replace(" bpm", "").trim(); // Blood pressure

                String[] tension = bp.split("/"); // Split systolic/diastolic

                // Parse weight, temperature, and blood pressure values
                weights[i] = Double.parseDouble(weight);
                temperatures[i] = Double.parseDouble(temp);
                systolic[i] = Double.parseDouble(tension[0].trim());
                diastolic[i] = Double.parseDouble(tension[1].trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error parsing data for graph: " + ex.getMessage(),
                        "Data Parsing Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // Create custom panels for the graphs
        BarChartPanel weightTempPanel = new BarChartPanel(
                "Weight and Temperature", days, 
                new double[][]{weights, temperatures}, 
                new Color[]{new Color(63, 63, 63), new Color(38, 126, 53)},
                new String[]{"Weight", "Temperature"}
        );

        BarChartPanel bloodPressurePanel = new BarChartPanel(
                "Blood Pressure", days, 
                new double[][]{systolic, diastolic}, 
                new Color[]{new Color(38, 126, 53), new Color(63, 63, 63)},
                new String[]{"Systolic", "Diastolic"}
        );

        // Add both charts to a panel
        JPanel chartPanel = new JPanel(new GridLayout(1, 2));
        chartPanel.add(weightTempPanel);
        chartPanel.add(bloodPressurePanel);
        chartPanel.setBounds(50, 150, 850, 400);
        background.add(chartPanel);

        setVisible(true);
    }

    private void updateDateTime() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy  HH:mm:ss");
            dateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    // Custom JPanel for rendering bar charts
    private static class BarChartPanel extends JPanel {
        private String title;
        private String[] categories;
        private double[][] values;
        private Color[] colors;
        private String[] seriesNames;

        public BarChartPanel(String title, String[] categories, double[][] values, Color[] colors, String[] seriesNames) {
            this.title = title;
            this.categories = categories;
            this.values = values;
            this.colors = colors;
            this.seriesNames = seriesNames;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Enable antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set margins and chart area
            int margin = 50;
            int chartWidth = getWidth() - 2 * margin;
            int chartHeight = getHeight() - 2 * margin;

            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(title, margin, margin / 2);

            // Draw axes
            int x0 = margin, y0 = getHeight() - margin;
            g2d.drawLine(x0, margin, x0, y0); // Y-axis
            g2d.drawLine(x0, y0, getWidth() - margin, y0); // X-axis

            // Calculate bar widths and heights
            int numCategories = categories.length;
            int numSeries = values.length;
            int barWidth = chartWidth / (numCategories * numSeries + (numCategories - 1));

            double maxValue = 0;
            for (double[] series : values) {
                for (double v : series) {
                    maxValue = Math.max(maxValue, v);
                }
            }

            // Draw bars
            for (int i = 0; i < numCategories; i++) {
                for (int j = 0; j < numSeries; j++) {
                    int barHeight = (int) ((values[j][i] / maxValue) * (chartHeight - margin));
                    int x = x0 + i * (numSeries * barWidth + barWidth) + j * barWidth;
                    int y = y0 - barHeight;

                    g2d.setColor(colors[j]);
                    g2d.fillRect(x, y, barWidth, barHeight);

                    // Draw value labels
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.drawString(String.valueOf(values[j][i]), x, y - 5);
                }

                // Draw category labels
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.BLACK);
                int labelX = x0 + i * (numSeries * barWidth + barWidth) + (numSeries * barWidth) / 2;
                g2d.drawString(categories[i], labelX - 10, y0 + 20);
            }

            // Draw legend
            int legendX = getWidth() - margin - 150, legendY = margin;
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i < seriesNames.length; i++) {
                g2d.setColor(colors[i]);
                g2d.fillRect(legendX, legendY, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString(seriesNames[i], legendX + 15, legendY + 10);
                legendY += 15;
            }
        }
    }
}
