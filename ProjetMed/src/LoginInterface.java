import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginInterface extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton patientRadioButton;
    private JRadioButton profRadioButton;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medilog";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    public LoginInterface() {
        // JFrame properties
        setTitle("Médilog - Connexion");
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

        // Welcome label
        JLabel welcomeLabel = new JLabel("Se Connecter", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(34, 139, 34));
        welcomeLabel.setBounds(50, 10, 200, 30);
        formPanel.add(welcomeLabel);

        // Email field with placeholder 
        emailField = new JTextField();
        emailField.setBounds(50, 60, 200, 30);
        emailField.setHorizontalAlignment(JTextField.CENTER);
        emailField.setForeground(Color.GRAY);
        emailField.setText("Entrer votre Email");
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (emailField.getText().equals("Entrer votre Email")) {
                    emailField.setText("");
                    emailField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setForeground(Color.GRAY);
                    emailField.setText("Entrer votre Email");
                }
            }
        }); 
        formPanel.add(emailField);

        // Password field with placeholder 
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 110, 200, 30);
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0); // Show text for placeholder
        passwordField.setText("Entrer votre mot de passe");
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals("Entrer votre mot de passe")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('●'); // Mask password input
                }
            }
 
            @Override
            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("Entrer votre mot de passe");
                    passwordField.setEchoChar((char) 0); // Show text for placeholder
                }
            }
        });
        formPanel.add(passwordField);

        // Radio buttons for patient and prof
        patientRadioButton = new JRadioButton("Patient");
        patientRadioButton.setBounds(60, 160, 80, 20);
        patientRadioButton.setBackground(Color.decode("#6fa1ad"));
        patientRadioButton.setForeground(Color.BLACK);

        profRadioButton = new JRadioButton("Docteur");
        profRadioButton.setBounds(180, 160, 80, 20);
        profRadioButton.setBackground(Color.decode("#6fa1ad"));
        profRadioButton.setForeground(Color.BLACK);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(patientRadioButton);
        roleGroup.add(profRadioButton);

        formPanel.add(patientRadioButton);
        formPanel.add(profRadioButton);

        // Login button
        JButton loginButton = new JButton("Se Connecter");
        loginButton.setBounds(90, 200, 120, 30);
        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton);
        // Sign-up label
        

        backgroundPanel.add(formPanel); 
        add(backgroundPanel);
    
        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (email.equals("Entrer votre Email") || password.equals("Entrer votre mot de passe")) {
                    JOptionPane.showMessageDialog(null, "Veillez saisir votre email et mot de passe.");
                    return;
                }

                if (!patientRadioButton.isSelected() && !profRadioButton.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Veillez selectionner un role: Patient ou Docteur.");
                    return;
                }

                if (patientRadioButton.isSelected()) {
                    int userId = validateLogin(email, password, "user", "Email_User", "Password_User", "id_user");
                    if (userId != -1) {
                        openDataTablePage(userId);
                    } else {
                        JOptionPane.showMessageDialog(null, "Email ou mot de passe n'existe pas.");
                    }
                } else if (profRadioButton.isSelected()) {
                    int profId = validateLogin(email, password, "healthprof", "Email_Prof", "Password_Prof", "id_prof");
                    if (profId != -1) {
                        openHealthProfPage(profId);
                    } else {
                        JOptionPane.showMessageDialog(null, "Email ou mot de passe n'existe pas.");
                    }
                }
            } 
        })   ;
        JLabel signUpLabel = new JLabel("<html>Vous n'avez pas encore de compte? <u>S'inscrire</u></html>", SwingConstants.CENTER);
        signUpLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        signUpLabel.setForeground(Color.DARK_GRAY);
        signUpLabel.setBounds(25, 240, 250, 50);
        formPanel.add(signUpLabel);

        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { 
                SignUpInterface signUpInterface = new SignUpInterface();
                signUpInterface.setVisible(true); 
                setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signUpLabel.setCursor(Cursor.getDefaultCursor());
            }
        });
        
        
        
        
        

        backgroundPanel.add(formPanel);
        add(backgroundPanel);
    }

    // Method to validate login credentials and return user/prof ID
    private int validateLogin(String email, String password, String tableName, String emailColumn, String passwordColumn, String idColumn) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT " + idColumn + " FROM " + tableName + " WHERE " + emailColumn + " = ? AND " + passwordColumn + " = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(idColumn); // Return the user/prof ID
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur de Connexion BDD: " + e.getMessage());
        }
        return -1; // Return -1 if login fails
    }

    // Method to open the data table page for patients
    private void openDataTablePage(int userId) {
        this.setVisible(false);
        DataTableUI dataTableUI = new DataTableUI(userId);
        dataTableUI.setVisible(true);
    }

    // Method to open the health prof page for professors
    private void openHealthProfPage(int profId) {
        this.setVisible(false);
        HealthProfUI healthProfUI = new HealthProfUI(profId);
        healthProfUI.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginInterface loginInterface = new LoginInterface();
            loginInterface.setVisible(true);
        });
    }
}