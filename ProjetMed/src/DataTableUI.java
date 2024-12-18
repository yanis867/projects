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
        setTitle("Médilog - Table de données");
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
        logo.setBounds(20, 5, 170, 58); // Logo position
        background.add(logo);

        // Create Table
        String[] columns = {"Jour", "Poids (KG)", "Température (°C)", "Tension (mmHg)"};
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
        
                // Add Buttons
     // Add "Add Doctor" button
        JButton addDoctorBtn = new JButton("Ajouter un Docteur");
        //getContentPane().add(addDoctorBtn);
        background.add(addDoctorBtn);
        addDoctorBtn.setBounds(20, 60, 170, 35);
        addDoctorBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addDoctorBtn.setBackground(Color.decode("#69b031"));
        addDoctorBtn.setForeground(Color.WHITE);

        // Add action listener for "Add Doctor" button
        addDoctorBtn.addActionListener(e -> addDoctor(userId));

                JButton addInfoBtn = new JButton("Aj. infos. d'aujoud'hui"); 
                //getContentPane().add(addInfoBtn);
                background.add(addInfoBtn);
                addInfoBtn.setBounds(200, 60, 200, 35);
                addInfoBtn.setFont(new Font("Arial", Font.BOLD, 14));
                addInfoBtn.setBackground(Color.decode("#69b031"));
                addInfoBtn.setForeground(Color.WHITE);
                JButton dataTableBtn = new JButton("Table de données");
                //getContentPane().add(dataTableBtn);
                background.add(dataTableBtn);
                dataTableBtn.setBounds(410, 60, 160, 35);
                dataTableBtn.setFont(new Font("Arial", Font.BOLD, 14));
                dataTableBtn.setBackground(Color.LIGHT_GRAY);
                dataTableBtn.setForeground(Color.BLACK);
                
                        JButton dataGraphBtn = new JButton("Graphe de données");
                        //getContentPane().add(dataGraphBtn);
                        background.add(dataGraphBtn);
                        dataGraphBtn.setBounds(583, 60, 180, 35);
                        dataGraphBtn.setFont(new Font("Arial", Font.BOLD, 14));
                        dataGraphBtn.setBackground(Color.decode("#69b031"));
                        dataGraphBtn.setForeground(Color.WHITE);
                        JButton notificationsBtn = new JButton("Notifications");
                        //getContentPane().add(notificationsBtn);
                        background.add(notificationsBtn);
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
                                    "Vous avez déjà ajouté les informations d'aujourd'hui. Qu'aimeriez-vous faire ?",
                                    "Les informations d'aujourd'hui existent",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new String[]{"Écraser", "Supprimer", "Annuler"},
                                    "Annuler"
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
        String url = "jdbc:mysql://localhost:3306/medilog";
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
                "Erreur lors du chargement des médecins : " + e.getMessage(),
                "Erreur de BDD",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 2: Display doctors in a dialog
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Choisir un docteur", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectionner un docteur.", "Selectionner rien", JOptionPane.WARNING_MESSAGE);
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
                    "Erreur lors de la récupération de l'identifiant du médecin sélectionné : " + e.getMessage(),
                    "Erreur BDD",
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
                    "Erreur lors de la vérification du médecin de l'utilisateur : " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 4: Confirm doctor change
            if (currentDoctorId != 0) {
                int changeDoctor = JOptionPane.showConfirmDialog(this,
                    "Vous suivez déjà un médecin. Voulez-vous le changer?",
                    "Confirmation de changement de docteur",
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
                    "Docteur ajouté avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la M.à.J. du médecin de l'utilisateur: " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void loadDataFromDatabase(int userId) {
        String url = "jdbc:mysql://localhost:3306/medilog";
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
                "Erreur lors du chargement des données: " + e.getMessage(),
                "Erreur BDD",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    

    private void addTodaysInfo(int userId) {
        String url = "jdbc:mysql://localhost:3306/medilog";
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
            weight = JOptionPane.showInputDialog(this, "Entrer le poids (KG):");
            if (weight == null || weight.trim().isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Sauter la saisie du poids?", "Confirmer", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) break;
            } else {
                try {
                    double weightValue = Double.parseDouble(weight);
                    if (weightValue < 30 || weightValue > 150) {
                        JOptionPane.showMessageDialog(this, 
                            "Attention : Le poids saisi (" + weightValue + " KG) est en dehors de la plage normale (30-150 KG). Veuillez contacter un médecin.",
                            "Avertissement",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    weightEntered = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valeur de poids invalide. Veuillez saisir une valeur numérique.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Prompt for temperature
        while (!tempEntered) {
            temp = JOptionPane.showInputDialog(this, "Entrer la température (°C):");
            if (temp == null || temp.trim().isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Sauter la saisie de la température?", "Confirmer", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) break;
            } else {
                try {
                    double tempValue = Double.parseDouble(temp);
                    if (tempValue < 35 || tempValue > 42) {
                        JOptionPane.showMessageDialog(this, 
                            "Attention: La température saisie (" + tempValue + "°C) est en dehors de la plage normale (35-42°C). Veuillez contacter un médecin.",
                            "Avertissement",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    tempEntered = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valeur de température invalide. Veuillez saisir une valeur numérique.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Prompt for heart rate
        while (!heartRateEntered) {
            String heartRateFirst = JOptionPane.showInputDialog(this, "Entrer la tension systolique (/10 mmHg):");
            String heartRateSecond = JOptionPane.showInputDialog(this, "Entrer la tension diastolique (/10 mmHg):");

            if ((heartRateFirst == null || heartRateFirst.trim().isEmpty()) || (heartRateSecond == null || heartRateSecond.trim().isEmpty())) {
                int confirm = JOptionPane.showConfirmDialog(this, "sauter la saisie de la tension?", "Confirmer", JOptionPane.YES_NO_OPTION);
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
            "Jour " + newRowId,
            weightEntered ? weight + " KG" : "--",
            tempEntered ? temp + "°C" : "--",
            heartRateEntered ? heartRate + "/10 mmHg" : "--"
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
                    "Les informations d'aujourd'hui ont été ajoutées à la base de données avec succès!",
                    "Succèss",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout des informations d'aujourd'hui: " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Données incomplètes. Remplissez tous les champs pour enregistrer dans la base de données.",
                "Données incomplètes",
                JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataTableUI(1).setVisible(true));
    }

    /*private void updateDateTime() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy  HH:mm:ss");
            dateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }*/
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
                "Les informations d'aujourd'hui ont été supprimées avec succès.", 
                "Supprimé", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void showNotifications(int userId) {
        String url = "jdbc:mysql://localhost:3306/medilog";
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
                "Erreur de chargement des notifications: " + e.getMessage(),
                "Erreur BDD",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteRowFromDatabase(int userId, int rowId) {
        String url = "jdbc:mysql://localhost:3306/medilog";
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
                "Erreur lors de la suppression d'une ligne de la base de données: " + e.getMessage(), 
                "Erreur BDD", 
                JOptionPane.ERROR_MESSAGE);
        }
    }


}