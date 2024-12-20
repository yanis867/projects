import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
//patient form
public class PatientUi extends JFrame {
    private JTextField heightField, medicalHistoryField;
    private JSpinner birthdaySpinner; // Using JSpinner for date selection
    private JButton signUpButton;
    private int userId; // To store the ID_User passed from SignUpInterface

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medilog";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    // Constructor to accept the ID_User
    public PatientUi(int userId) {
        this.userId = userId; // Assign the passed userId
        setTitle("Patient - S'inscrire");
        setSize(942, 627);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon(getClass().getResource("/Backgroundimg.png"));
                if (background.getImage() != null) {
                    g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(null);

        // Logo
        ImageIcon logo = new ImageIcon(getClass().getResource("/logo1.png"));
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setBounds(396, 20, 150, 100);
        backgroundPanel.add(logoLabel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBounds(321, 140, 300, 300);
        formPanel.setBackground(Color.decode("#6fa1ad"));
        formPanel.setLayout(null);

        JLabel patientFormLabel = new JLabel("Formulaire Patient", SwingConstants.CENTER);
        patientFormLabel.setFont(new Font("Arial", Font.BOLD, 20));
        patientFormLabel.setForeground(Color.decode("#3a6a4e"));
        patientFormLabel.setBounds(50, 10, 200, 30);
        formPanel.add(patientFormLabel);

        // Birthday chooser
        JLabel birthdayLabel = new JLabel("Date de naissance:");
        birthdayLabel.setBounds(50, 50, 200, 20);
        formPanel.add(birthdayLabel);

        // Configure JSpinner as a date picker
        SpinnerDateModel dateModel = new SpinnerDateModel();
        birthdaySpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(birthdaySpinner, "yyyy/MM/dd");
        birthdaySpinner.setEditor(dateEditor);
        birthdaySpinner.setBounds(50, 70, 200, 30);
        formPanel.add(birthdaySpinner);

        // Height field
        heightField = createPlaceholderField("Entrer votre Taille (cm)");
        heightField.setBounds(50, 120, 200, 30);
        formPanel.add(heightField);

        // Medical History field
        medicalHistoryField = createPlaceholderField("Entrer vos Antécédents Médicaux");
        medicalHistoryField.setBounds(50, 170, 200, 30);
        formPanel.add(medicalHistoryField);

        // Sign Up button
        signUpButton = new JButton("S'inscrire");
        signUpButton.setBounds(100, 240, 100, 30);
        signUpButton.setBackground(new Color(34, 139, 34));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        formPanel.add(signUpButton);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get input values
                Date birthday = (Date) birthdaySpinner.getValue();
                String height = heightField.getText().trim();
                String medicalHistory = medicalHistoryField.getText().trim();

                // Validate inputs
                if (birthday == null || height.isEmpty() || medicalHistory.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Tous les champs doivent être remplis pour continuer.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate height
                if (!isValidHeight(height)) {
                    JOptionPane.showMessageDialog(null, "La Taille doit être un nombre réel strictement positif.", "Taille invailde", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Format the date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String formattedBirthday = sdf.format(birthday);

                // Insert data into the database
                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    String userSql = "UPDATE user SET User_DateBirth = ?, Patient_MedHist = ?, User_Height = ? WHERE ID_User = ?";
                    PreparedStatement userStatement = connection.prepareStatement(userSql);
                    userStatement.setString(1, formattedBirthday);
                    userStatement.setString(2, medicalHistory);
                    userStatement.setString(3, height);
                    userStatement.setInt(4, userId); // Use the passed ID_User
                    userStatement.executeUpdate();

                    // Notify success
                    JOptionPane.showMessageDialog(null, "Vous vous êtes inscrit avec succès!");

                    // Open DataTableUI and pass userId
                    openDataTablePage(userId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erreur de manipulation de base de données: " + ex.getMessage(), "Erreur BDD", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backgroundPanel.add(formPanel);
        add(backgroundPanel);
    }

    private boolean isValidHeight(String height) {
        try {
            double value = Double.parseDouble(height);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private void openDataTablePage(int userId) {
        this.setVisible(false);
        DataTableUI dataTableUI = new DataTableUI(userId);
        dataTableUI.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int userId = 1;
            PatientUi patientUi = new PatientUi(userId);
            patientUi.setVisible(true);
        });
    }
}
