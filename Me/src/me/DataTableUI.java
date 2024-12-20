package me;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTableUI extends JFrame {
    private JLabel dateTime;
    private DefaultTableModel tableModel; 
    private boolean addedToday = false;
   
    private int todayRowIndex = -1;
    public DataTableUI(int userId) {
        setTitle("Sahtec - Data Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(942, 627); // Window size 
        setLocationRelativeTo(null); // Center the window 
        setResizable(false);
        getContentPane().setLayout(null);  

        // Set Background
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/Backgroundimg2.png")));
        background.setBounds(0, 0, 942, 627); // Background size
        getContentPane().add(background);

        // Add Logo
        JLabel logo = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/logo2.png"))
                .getImage()
                .getScaledInstance(170, 58, Image.SCALE_SMOOTH))); // Logo size
        logo.setBounds(20, 10, 170, 58); // Logo position
        background.add(logo);

        // Create Table
        String[] columns = {"Day", "Weight (KG)", "Temperature (°C)", "Heart Rate (bpm)"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        // Customize Table
        table.setRowHeight(30); 
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(Color.LIGHT_GRAY);

        // Disable Column Reordering
        table.getTableHeader().setReorderingAllowed(false);

        // Customize Table Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(Color.decode("#69b031"));
        header.setForeground(Color.WHITE);

        // Add Table with Header to a Scroll Pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(170, 190, 600, 210);
        background.add(scrollPane);

        // Load Data from Database
        loadDataFromDatabase(userId);

        // Add Current Date and Time
        dateTime = new JLabel();
        dateTime.setFont(new Font("Arial", Font.BOLD, 14));
        dateTime.setForeground(Color.BLACK);
        dateTime.setBounds(700, 10, 230, 30);
        updateDateTime();
        background.add(dateTime);
        
                // Add Buttons
     // Add "Add Doctor" button
        JButton addDoctorBtn = new JButton("ADD DOCTOR");
        getContentPane().add(addDoctorBtn);
        addDoctorBtn.setBounds(20, 60, 140, 35);
        addDoctorBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addDoctorBtn.setBackground(Color.decode("#69b031"));
        addDoctorBtn.setForeground(Color.WHITE);

        // Add action listener for "Add Doctor" button
        addDoctorBtn.addActionListener(e -> addDoctor(userId));

                JButton addInfoBtn = new JButton("ADD TODAY'S INFO"); 
                getContentPane().add(addInfoBtn);
                addInfoBtn.setBounds(192, 60, 180, 35);
                addInfoBtn.setFont(new Font("Arial", Font.BOLD, 14));
                addInfoBtn.setBackground(Color.decode("#69b031"));
                addInfoBtn.setForeground(Color.WHITE);
                JButton dataTableBtn = new JButton("DATA TABLE");
                getContentPane().add(dataTableBtn);
                dataTableBtn.setBounds(394, 60, 180, 35);
                dataTableBtn.setFont(new Font("Arial", Font.BOLD, 14));
                dataTableBtn.setBackground(Color.LIGHT_GRAY);
                dataTableBtn.setForeground(Color.BLACK);
                
                        JButton dataGraphBtn = new JButton("DATA GRAPH");
                        getContentPane().add(dataGraphBtn);
                        dataGraphBtn.setBounds(583, 60, 180, 35);
                        dataGraphBtn.setFont(new Font("Arial", Font.BOLD, 14));
                        dataGraphBtn.setBackground(Color.decode("#69b031"));
                        dataGraphBtn.setForeground(Color.WHITE);
                        JButton notificationsBtn = new JButton("NOTIFICATIONS");
                        getContentPane().add(notificationsBtn);
                        notificationsBtn.setBounds(773, 60, 143, 35);
                        notificationsBtn.setFont(new Font("Arial", Font.BOLD, 14));
                        notificationsBtn.setBackground(Color.decode("#008CBA"));
                        notificationsBtn.setForeground(Color.WHITE);
                        
     // Add Action to Notifications Button
                        notificationsBtn.addActionListener(e -> showNotifications(userId));
                        
                                // Add Action to Data Graph Button
                                dataGraphBtn.addActionListener(e -> {
                                    SwingUtilities.invokeLater(() -> new DataGraphWindow(tableModel).setVisible(true));
                                });
                
                     // Updated addInfoBtn ActionListener
                        addInfoBtn.addActionListener(e -> {
                            if (addedToday) {
                                int choice = JOptionPane.showOptionDialog(
                                    this,
                                    "You have already added today's info. What would you like to do?",
                                    "Today's Info Exists",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new String[]{"Overwrite", "Delete", "Cancel"},
                                    "Cancel"
                                );
                
                                switch (choice) {
                                    case 0: // Overwrite
                                        overwriteTodaysInfo(userId);
                                        break;
                                    case 1: // Delete
                                        deleteTodaysInfo(userId);
                                        break;
                                    case 2: // Cancel
                                    default:
                                        // Do nothing
                                        break;
                                }
                            } else {
                                addTodaysInfo(userId);
                            }
                        });
    }
    private void addDoctor(int userId) {
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = "";

        // Step 1: Fetch all doctors from the database
        String query = "SELECT ID_Prof, Prof_FullName, Prof_Gender FROM healthprof";
        DefaultTableModel doctorModel = new DefaultTableModel(new String[]{"Name", "Gender"}, 0);
        JTable doctorTable = new JTable(doctorModel);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("Prof_FullName");
                String gender = rs.getString("Prof_Gender");
                doctorModel.addRow(new Object[]{name, gender});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading doctors: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 2: Display doctors in a dialog
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Choose a Doctor", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedDoctorName = doctorModel.getValueAt(selectedRow, 0).toString();
            int selectedDoctorId = -1;

            // Fetch the selected doctor's ID
            String doctorQuery = "SELECT ID_Prof FROM healthprof WHERE Prof_FullName = ?";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(doctorQuery)) {

                stmt.setString(1, selectedDoctorName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        selectedDoctorId = rs.getInt("ID_Prof");
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error fetching selected doctor ID: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 3: Check if the user already has a doctor
            String checkUserQuery = "SELECT ID_Prof FROM user WHERE ID_User = ?";
            int currentDoctorId = -1;

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(checkUserQuery)) {

                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentDoctorId = rs.getInt("ID_Prof");
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error checking user doctor: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 4: Confirm doctor change
            if (currentDoctorId != 0) {
                int changeDoctor = JOptionPane.showConfirmDialog(this,
                    "You already follow a doctor. Do you want to change?",
                    "Doctor Change Confirmation",
                    JOptionPane.YES_NO_OPTION);

                if (changeDoctor != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Step 5: Update the user's doctor
            String updateUserQuery = "UPDATE user SET ID_Prof = ? WHERE ID_User = ?";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(updateUserQuery)) {

                stmt.setInt(1, selectedDoctorId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Doctor added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error updating user's doctor: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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

    

    private void addTodaysInfo(int userId) {
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = "";

        String weight = null;
        String temp = null;
        String heartRate = null;

        boolean weightEntered = false;
        boolean tempEntered = false;
        boolean heartRateEntered = false;

        // Prompt for weight
        while (!weightEntered) {
            weight = JOptionPane.showInputDialog(this, "Enter Weight (KG):");
            if (weight == null || weight.trim().isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Skip entering weight?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) break;
            } else {
                try {
                    double weightValue = Double.parseDouble(weight);
                    if (weightValue < 30 || weightValue > 150) {
                        JOptionPane.showMessageDialog(this, 
                            "Warning: The entered weight (" + weightValue + " KG) is outside the normal range (30-150 KG). Please contact a doctor.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    weightEntered = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid weight value. Please enter a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Prompt for temperature
        while (!tempEntered) {
            temp = JOptionPane.showInputDialog(this, "Enter Temperature (°C):");
            if (temp == null || temp.trim().isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Skip entering temperature?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) break;
            } else {
                try {
                    double tempValue = Double.parseDouble(temp);
                    if (tempValue < 35 || tempValue > 42) {
                        JOptionPane.showMessageDialog(this, 
                            "Warning: The entered temperature (" + tempValue + "°C) is outside the normal range (35-42°C). Please contact a doctor.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    tempEntered = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid temperature value. Please enter a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Prompt for heart rate
        while (!heartRateEntered) {
            String heartRateFirst = JOptionPane.showInputDialog(this, "Enter First Heart Rate Value (bpm):");
            String heartRateSecond = JOptionPane.showInputDialog(this, "Enter Second Heart Rate Value (bpm):");

            if ((heartRateFirst == null || heartRateFirst.trim().isEmpty()) || (heartRateSecond == null || heartRateSecond.trim().isEmpty())) {
                int confirm = JOptionPane.showConfirmDialog(this, "Skip entering heart rate?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) break;
            } else {
                heartRate = heartRateFirst + "/" + heartRateSecond;
                heartRateEntered = true;
            }
        }

        // Calculate the new row ID
        int newRowId = tableModel.getRowCount() + 1;

        // Add data to the table
        tableModel.addRow(new Object[] {
            "Day " + newRowId,
            weightEntered ? weight + " KG" : "--",
            tempEntered ? temp + "°C" : "--",
            heartRateEntered ? heartRate + " bpm" : "--"
        });

        // Save to the database
        if (weightEntered && tempEntered && heartRateEntered) {
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO patient_data (ID_User, Patient_Weight, Patient_Temp, Patient_Heartbt, ID_Row) VALUES (?, ?, ?, ?, ?)")) {

                stmt.setInt(1, userId);
                stmt.setString(2, weight);
                stmt.setString(3, temp);
                stmt.setString(4, heartRate);
                stmt.setInt(5, newRowId);

                stmt.executeUpdate();
                todayRowIndex = tableModel.getRowCount() - 1;
                addedToday = true;

                JOptionPane.showMessageDialog(this,
                    "Today's info has been added to the database successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error adding today's info: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Incomplete data. Fill all fields to save to the database.",
                "Partial Data",
                JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataTableUI(1).setVisible(true));
    }

    private void updateDateTime() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy  HH:mm:ss");
            dateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }
    private void overwriteTodaysInfo(int userId) {
        if (todayRowIndex != -1) {
            int rowId = todayRowIndex + 1; // Assuming rows are in sequential order 
            deleteRowFromDatabase(userId, rowId);
            tableModel.removeRow(todayRowIndex);
        } 
        addedToday = false;
        addTodaysInfo(userId);
    }

 // Delete today's info
    private void deleteTodaysInfo(int userId) {
        if (todayRowIndex != -1) {
            int rowId = todayRowIndex + 1; // Assuming rows are sequential
            deleteRowFromDatabase(userId, rowId);
            tableModel.removeRow(todayRowIndex);
            addedToday = false;
            todayRowIndex = -1;

            JOptionPane.showMessageDialog(this, 
                "Today's info has been deleted successfully.", 
                "Deleted", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void showNotifications(int userId) {
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = "";
 
        String query = "SELECT n.Message, h.Prof_FullName " +
                       "FROM notification n " +
                       "JOIN healthprof h ON n.ID_Prof = h.ID_Prof " +
                       "WHERE n.ID_User = ?";
 
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                JTable notificationTable = new JTable();
                DefaultTableModel notificationModel = new DefaultTableModel(new String[]{"Message", "From"}, 0);
                notificationTable.setModel(notificationModel);

                while (rs.next()) {
                    String message = rs.getString("Message");
                    String profName = rs.getString("Prof_FullName");
                    notificationModel.addRow(new Object[]{message, profName});
                }

                JScrollPane scrollPane = new JScrollPane(notificationTable);
                scrollPane.setPreferredSize(new Dimension(400, 200));

                JOptionPane.showMessageDialog(this, scrollPane, "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading notifications: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteRowFromDatabase(int userId, int rowId) {
        String url = "jdbc:mysql://localhost:3306/sahtech";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM patient_data WHERE ID_User = ? AND ID_Row = ?")) {

            stmt.setInt(1, userId);
            stmt.setInt(2, rowId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting row from database: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }


}