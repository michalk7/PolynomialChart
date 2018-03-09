package zad2;

import java.awt.*;
import java.util.HashMap;

import javax.swing.*;

public class ChartFrame {

	
	public ChartFrame() {
		SwingUtilities.invokeLater(() -> createFrame());
	}
	
	private void createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle("Chart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 100);
		frame.setPreferredSize(new Dimension(400, 400));
		frame.setResizable(true);
		
		HashMap<String, JTextField> wspolrzedneWykresu = new HashMap<>();
		String[] nazwyWspolrzednych = {"xmin", "xmax", "ymin", "ymax"};
		JPanel panelForCoordinates = new JPanel();
		panelForCoordinates.setLayout(new GridLayout(0, 4));
		for(int i = 0; i < 4; i++) {
			wspolrzedneWykresu.put(nazwyWspolrzednych[i], new JTextField());
			panelForCoordinates.add(wspolrzedneWykresu.get(nazwyWspolrzednych[i]));
		}
		JTextField poleNaWielomian = new JTextField();
		
		JPanel panelForTextFields = new JPanel();
		panelForTextFields.setLayout(new GridLayout(2, 0));
		panelForTextFields.add(panelForCoordinates);
		panelForTextFields.add(poleNaWielomian);
		
		ChartPanel chartPanel = new ChartPanel(wspolrzedneWykresu, poleNaWielomian);
		frame.add(chartPanel, BorderLayout.CENTER);
		chartPanel.addMouseMotionListener(chartPanel);
		poleNaWielomian.addActionListener(e -> {
				chartPanel.setPaintChart(true);
				chartPanel.setParseNewCoordinates(true);
				chartPanel.setReadNewPolynomial(true);
				chartPanel.repaint();
		});
		
		for(int i = 0; i < 4; i++) {
			wspolrzedneWykresu.get(nazwyWspolrzednych[i]).addActionListener(e -> {
				chartPanel.setPaintChart(true);
				chartPanel.setParseNewCoordinates(true);
				chartPanel.setReadNewPolynomial(true);
				chartPanel.repaint();
			});
		}
		frame.add(panelForTextFields, BorderLayout.SOUTH);
		
		frame.pack();
		frame.setVisible(true);
	}
}
