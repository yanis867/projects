package ehealth_app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Login extends JFrame {
	JPanel banner, footer, lpane, rpane;
	Login() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("E-health - Connexion");
		this.setSize(950, 535);
		this.setResizable(true);
		this.setVisible(true);
		
		banner = new JPanel();
		banner.setBackground(new Color(0xdaf3ff));
		banner.setPreferredSize(new Dimension(100, 70));
		this.add(banner, BorderLayout.NORTH);
	}

}
