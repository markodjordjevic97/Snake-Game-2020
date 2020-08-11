package com;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main extends JFrame{

	private static final long serialVersionUID = 1L; 	// obavestava da li se ova klasa podudara sa objektom,
														// napravljenog u Main klasi !!!

	public static void main(String[] args) {
		
		Polje polje = new Polje();
		
		JFrame frame = new JFrame();
		ImageIcon naslovnaSlika = new ImageIcon("C:\\Users\\_\\eclipse-workspace\\Snake12-2018\\Resource\\glavaDole.png");
		frame.setIconImage(naslovnaSlika.getImage());
		frame.setBounds(400,100,600,600);
		frame.setVisible(true);
		frame.setTitle("Snake 2020");
		frame.setResizable(false);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(polje);
		
	}

}
