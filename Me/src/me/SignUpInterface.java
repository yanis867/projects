package me;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SignUpInterface extends JFrame {
    private JTextField fullNameField;
    private JTextField emergencyContactField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton patientButton;
    private JRadioButton healthProfButton;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    private JButton nextButton;
 
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sahtech";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private JSeparator separator;
 
    // Constructor
    public SignUpInterface() {
        setTitle("SAHTECH - Create Account");
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
        formPanel.setBounds(321, 140, 300, 400);
        formPanel.setBackground(Color.decode("#6fa1ad"));
        formPanel.setLayout(null);

        // "Create your account" label
        JLabel createAccountLabel = new JLabel("Create your account", SwingConstants.CENTER);
        createAccountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        createAccountLabel.setForeground(Color.decode("#3a6a4e"));
        createAccountLabel.setBounds(50, 10, 200, 30);
        formPanel.add(createAccountLabel);

        // Full Name field
        fullNameField = new JTextField();
        fullNameField.setBounds(50, 50, 200, 30);
        addPlaceholder(fullNameField, "Full Name");
        formPanel.add(fullNameField);

        // Emergency Contact field
        emergencyContactField = new JTextField();
        emergencyContactField.setBounds(50, 100, 200, 30);
        addPlaceholder(emergencyContactField, "Emergency Contact");
        formPanel.add(emergencyContactField);

        // Email field
        emailField = new JTextField();
        emailField.setBounds(50, 150, 200, 30);
        addPlaceholder(emailField, "Email@gmail.com");
        formPanel.add(emailField);
 
        // Password field
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 200, 200, 30);
        addPlaceholder(passwordField, "Enter your password");
        formPanel.add(passwordField);

        

    
     // Gender Label
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(50, 247, 80, 20);
        genderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        genderLabel.setForeground(Color.WHITE);
        formPanel.add(genderLabel);

        // Gender radio buttons
        maleButton = new JRadioButton("Male");
        maleButton.setBounds(156, 237, 80, 30);
        maleButton.setBackground(Color.decode("#6fa1ad"));

        femaleButton = new JRadioButton("Female");
        femaleButton.setBounds(156, 257, 80, 30);
        femaleButton.setBackground(Color.decode("#6fa1ad"));

        // Group the gender radio buttons
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        formPanel.add(maleButton);
        formPanel.add(femaleButton);

        // Role Label
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 300, 80, 20);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(Color.WHITE);
        formPanel.add(roleLabel);

        // Role radio buttons
        patientButton = new JRadioButton("Patient");
        patientButton.setBounds(156, 290, 100, 30);
        patientButton.setBackground(Color.decode("#6fa1ad"));

        healthProfButton = new JRadioButton("Health Prof");
        healthProfButton.setBounds(156, 310, 100, 30);
        healthProfButton.setBackground(Color.decode("#6fa1ad"));

        // Group the user type radio buttons
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(patientButton); 
        userTypeGroup.add(healthProfButton);

        formPanel.add(patientButton);
        formPanel.add(healthProfButton);


        // Next button
        nextButton = new JButton("NEXT");
        nextButton.setBounds(100, 350, 100, 30);
        nextButton.setBackground(new Color(34, 139, 34));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        formPanel.add(nextButton);

        // Action Listener for next button
     
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = fullNameField.getText().trim();
                String emergencyContact = emergencyContactField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : null);
                String userType = patientButton.isSelected() ? "Patient" : (healthProfButton.isSelected() ? "Health Prof" : null);

                // Validate inputs
                if (fullName.isEmpty() || emergencyContact.isEmpty() || email.isEmpty() || password.isEmpty() || gender == null || userType == null) {
                    JOptionPane.showMessageDialog(null, "All fields are required. Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!email.endsWith("@gmail.com")) {
                    JOptionPane.showMessageDialog(null, "Email must end with '@gmail.com'.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    String sql;

                    if (userType.equals("Health Prof")) {
                        // Insert into healthprof table
                        sql = "INSERT INTO healthprof (Prof_FullName, Email_Prof, Password_Prof, Role_Prof, Prof_Contact, Prof_Gender) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                        statement.setString(1, fullName);
                        statement.setString(2, email);
                        statement.setString(3, password);
                        statement.setString(4, userType);
                        statement.setString(5, emergencyContact);
                        statement.setString(6, gender);

                        int rowsInserted = statement.executeUpdate();
                        if (rowsInserted > 0) {
                            // Get the generated ID_Prof 
                            var rs = statement.getGeneratedKeys();
                            if (rs.next()) {
                                int profId = rs.getInt(1);
                                JOptionPane.showMessageDialog(null, "You signed up successfully. Welcome!");
                                HealthProfUI healthProfUi = new HealthProfUI(profId);
                                healthProfUi.setVisible(true);
                            }
                        }
                    } else {
                        // Insert into user table for patients
                        sql = "INSERT INTO user (User_FullName, Email_User, Password_User, Gender, User_Role, User_Contact) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                        statement.setString(1, fullName);
                        statement.setString(2, email);
                        statement.setString(3, password);
                        statement.setString(4, gender); 
                        statement.setString(5, userType);
                        statement.setString(6, emergencyContact);

                        int rowsInserted = statement.executeUpdate();
                        if (rowsInserted > 0) {
                            // Get the generated ID_User
                            var rs = statement.getGeneratedKeys();
                            if (rs.next()) {
                                int userId = rs.getInt(1);
                                
                                PatientUi patientUi = new PatientUi(userId);
                                patientUi.setVisible(true);
                            }
                        }
                    }
                    dispose(); // Close the current window
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        backgroundPanel.add(formPanel);
        
        separator = new JSeparator();
        separator.setBounds(50, 287, 200, 2);
        formPanel.add(separator);
        getContentPane().add(backgroundPanel);
    }

    // Method to add placeholder behavior
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) { 
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
 
            @Override 
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignUpInterface signUpInterface = new SignUpInterface();
            signUpInterface.setVisible(true);
        });
    }
}