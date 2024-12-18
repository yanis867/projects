package tutorials;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JFrame {
	JButton button;
	Panel() {
		JFrame frame = new JFrame("Hello");
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		JPanel panelN = new JPanel();
		JPanel panelS = new JPanel();
		JPanel panelE = new JPanel();
		JPanel panelW = new JPanel();
		JPanel panelC = new JPanel();
		
		panelN.setBackground(Color.red);
		panelS.setBackground(Color.BLUE);
		panelE.setBackground(Color.GREEN);
		panelW.setBackground(Color.MAGENTA);
		panelC.setBackground(Color.YELLOW);
		
		frame.add(panelN, BorderLayout.NORTH);
		frame.add(panelS, BorderLayout.SOUTH);
		frame.add(panelE, BorderLayout.EAST);
		frame.add(panelW, BorderLayout.WEST);
		frame.add(panelC, BorderLayout.CENTER);
	}
}
