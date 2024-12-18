package me;

// Updated PatientDataUI
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientDataUI extends JFrame {
    private JLabel dateTime;
    private DefaultTableModel tableModel;

    public PatientDataUI(int userId, int ID_Prof) {
        setTitle("Sahtec - Data Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(942, 627);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/Backgroundimg2.png")));
        background.setBounds(0, 0, 942, 627);
        add(background);

        JLabel logo = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/logo2.png"))
                .getImage()
                .getScaledInstance(170, 58, Image.SCALE_SMOOTH)));
        logo.setBounds(20, 10, 170, 58);
        background.add(logo);

        JButton backButton = new JButton("BACK");
        backButton.setBounds(20, 70, 100, 30);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.decode("#69b031"));
        backButton.setForeground(Color.WHITE);
        background.add(backButton);

        backButton.addActionListener(e -> dispose());

        JButton dataTableBtn = new JButton("DATA TABLE");
        dataTableBtn.setBounds(310, 60, 180, 35);
        dataTableBtn.setFont(new Font("Arial", Font.BOLD, 14));
        dataTableBtn.setBackground(Color.LIGHT_GRAY);
        dataTableBtn.setForeground(Color.BLACK);
        background.add(dataTableBtn);

        JButton dataGraphBtn = new JButton("DATA GRAPH");
        dataGraphBtn.setBounds(500, 60, 180, 35);
        dataGraphBtn.setFont(new Font("Arial", Font.BOLD, 14));
        dataGraphBtn.setBackground(Color.decode("#69b031"));
        dataGraphBtn.setForeground(Color.WHITE);
        background.add(dataGraphBtn);

        JButton contactPatientBtn = new JButton("CONTACT PATIENT");
        contactPatientBtn.setBounds(690, 60, 180, 35);
        contactPatientBtn.setFont(new Font("Arial", Font.BOLD, 14));
        contactPatientBtn.setBackground(Color.decode("#69b031"));
        contactPatientBtn.setForeground(Color.WHITE);
        background.add(contactPatientBtn);

        String[] columns = {"Day", "Weight (KG)", "Temperature (°C)", "Heart Rate (bpm)"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(Color.LIGHT_GRAY);
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(Color.decode("#69b031"));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(170, 190, 600, 210);
        background.add(scrollPane);

        loadDataFromDatabase(userId);

        dataGraphBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> new DataGraphWindow(tableModel).setVisible(true)));

        dateTime = new JLabel();
        dateTime.setFont(new Font("Arial", Font.BOLD, 14));
        dateTime.setForeground(Color.BLACK);
        dateTime.setBounds(700, 10, 230, 30);
        updateDateTime();
        background.add(dateTime);

        contactPatientBtn.addActionListener(e -> {
            String message = JOptionPane.showInputDialog(this, "Enter message for the patient:", 
                                                          "Contact Patient", JOptionPane.PLAIN_MESSAGE);
            if (message != null && !message.trim().isEmpty()) {
                String patientName = getPatientName(userId);
                if (patientName != null) {
                    //message = "Message for " + patientName + ":\n" + message;
                    insertMessageIntoDatabase(message, userId, ID_Prof);
                }
            }
        });
    }

    private void loadDataFromDatabase(int userId) {
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = "";

        String query = "SELECT ID_Row, Patient_Weight, Patient_Temp, Patient_Heartbt " +
                       "FROM patient_data WHERE ID_User = ? ORDER BY ID_Row";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("ID_Row");
                    String weight = rs.getString("Patient_Weight") + " KG";
                    String temp = rs.getString("Patient_Temp") + "°C";
                    String heartRate = rs.getString("Patient_Heartbt") + " bpm";

                    tableModel.addRow(new Object[]{"Day " + day, weight, temp, heartRate});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    } 
 
    private void insertMessageIntoDatabase(String message, int userId, int ID_Prof) {
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = "";

        String query = "INSERT INTO notification (Message, ID_User, ID_Prof) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, message);
            stmt.setInt(2, userId);
            stmt.setInt(3, ID_Prof);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Message sent to patient.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error sending message: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getPatientName(int userId) {
        String patientName = null;
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = ""; 

        String query = "SELECT User_FullName FROM user WHERE ID_User = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patientName = rs.getString("User_FullName");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error fetching patient name: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        return patientName;
    }

    private void updateDateTime() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy  HH:mm:ss");
            dateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }
}