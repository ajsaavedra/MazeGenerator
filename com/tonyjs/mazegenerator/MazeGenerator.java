package com.tonyjs.mazegenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MazeGenerator extends JFrame {
	private static final long serialVersionUID = 1L;
	private TitleLabel titleLabel = new TitleLabel("Maze");
	private int rows = 5;
	private int cols = 5;
	private Cell[][] cell = new Cell[rows][cols];
	private JPanel mazePanel = new JPanel();
	
	public MazeGenerator() {
		initGUI();
		setTitle("Maze Generator");
		setResizable(false);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void initGUI() {
		add(titleLabel, BorderLayout.PAGE_START);
		
		// center panel
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.BLACK);
		add(centerPanel, BorderLayout.CENTER);
		
		// maze panel
		newMaze();
		add(mazePanel, BorderLayout.CENTER);
		
	}
	
	public void newMaze() {
		mazePanel.setLayout(new GridLayout(rows, cols));
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cell[i][j] = new Cell(rows, cols);
				mazePanel.add(cell[i][j]);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MazeGenerator();
			}
		});
	}
}
