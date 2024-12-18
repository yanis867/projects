import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HealthProfUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;

    public HealthProfUI(int ID_Prof) {
        setTitle("Médilog - Docteurs");
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

        JButton addPatientBtn = new JButton("Ajouter Patient");
        addPatientBtn.setBounds(410, 60, 180, 35);
        addPatientBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addPatientBtn.setBackground(Color.decode("#69b031"));
        addPatientBtn.setForeground(Color.WHITE);
        background.add(addPatientBtn);

        String[] columns = {"Nom Complet", "Numero Tel.", "Taille", "Sexe", "antécédents médicaux", "Infos."};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(Color.LIGHT_GRAY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(Color.decode("#69b031"));
        header.setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(100, 150, 750, 300);
        background.add(scrollPane);

        table.getColumn("Info").setCellRenderer(new LinkCellRenderer());
        table.addMouseListener(new TableMouseListener(ID_Prof));

        addPatientBtn.addActionListener(e -> addPatient(ID_Prof, table));

        loadPatients(ID_Prof);
    }

    private void loadPatients(int ID_Prof) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medilog", "root", "")) {
            String query = "SELECT * FROM user WHERE ID_Prof = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, ID_Prof);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("User_FullName"),
                        rs.getString("User_Contact"),
                        rs.getString("User_Height"),
                        rs.getString("Gender"),
                        rs.getString("Patient_MedHist"),
                        rs.getInt("ID_User")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de BDD lors du chargement des patients: " + ex.getMessage());
        }
    }

    private void addPatient(int ID_Prof, JTable table) {
        String fullName = JOptionPane.showInputDialog(this, "Entrer l'eemail du patient");

        if (fullName != null && !fullName.trim().isEmpty()) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medilog", "root", "")) {
                String query = "SELECT * FROM user WHERE Email_User = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, fullName.trim());

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int currentProfID = rs.getInt("ID_Prof");

                    if (currentProfID != 0 && currentProfID != ID_Prof) {
                        JOptionPane.showMessageDialog(this, "Ce patient est déjà attribué a un autre médecin.");
                    } else if (currentProfID == ID_Prof) {
                        JOptionPane.showMessageDialog(this, "Ce patient est déjà attribué a vous.");
                    } else {
                        String updateQuery = "UPDATE user SET ID_Prof = ? WHERE Email_User = ?";
                        PreparedStatement updatePstmt = conn.prepareStatement(updateQuery);
                        updatePstmt.setInt(1, ID_Prof);
                        updatePstmt.setString(2, fullName.trim());

                        int rowsUpdated = updatePstmt.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(this, "Le patient vous a été attribué avec succès.");
                            Object[] rowData = {
                                    rs.getString("User_FullName"),
                                    rs.getString("User_Contact"),
                                    rs.getString("User_Height"),
                                    rs.getString("Gender"),
                                    rs.getString("Patient_MedHist"),
                                    rs.getInt("ID_User")
                            };
                            tableModel.addRow(rowData);
                        } else {
                            JOptionPane.showMessageDialog(this, "Échec de l'affectation du patient, Veuillez réessayer.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Aucun patient avec cet email dans notre base de données.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de BDD: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "l'email du patient ne doit pas être vide.");
        }
    }

    private class LinkCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel("<html><a href='#'>Afficher</a></html>");
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    private class TableMouseListener extends MouseAdapter {
        private final int ID_Prof;

        public TableMouseListener(int ID_Prof) {
            this.ID_Prof = ID_Prof;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int column = table.columnAtPoint(e.getPoint());

            if (column == 5) {
                int userId = (int) table.getValueAt(row, column);
                new PatientDataUI(userId, ID_Prof).setVisible(true);
            }
        }
    }
}
