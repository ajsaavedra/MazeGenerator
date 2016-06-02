package com.tonyjs.mazegenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MazeGenerator extends JFrame {
	private static final long serialVersionUID = 1L;
	private TitleLabel titleLabel = new TitleLabel("Maze");
	private int rows = 30;
	private int cols = 30;
	private Cell[][] cell = new Cell[rows][cols];
	private JPanel mazePanel = new JPanel();

	public MazeGenerator() {
		initGUI();
		setTitle("Maze Generator");
//		setResizable(false);
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
		centerPanel.add(mazePanel);
	}

	public void newMaze() {
		mazePanel.setLayout(new GridLayout(rows, cols));
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cell[i][j] = new Cell(i, j);
				mazePanel.add(cell[i][j]);
			}
		}
		generateMaze();
	}

	private void generateMaze() {
		ArrayList<Cell> tryLaterCell = new ArrayList<Cell>();
		int totalCells = rows * cols;
		int visitedCells = 1;

		// start at a random cell
		Random rand = new Random();
		int r = rand.nextInt(rows);
		int c = rand.nextInt(cols);

		// while not all cells have been visited
		while (visitedCells < totalCells) {
			ArrayList<Cell> neighbors = new ArrayList<Cell>();

			if (isAvailable(r-1, c)) {
				neighbors.add(cell[r-1][c]);
			}
			if (isAvailable(r+1, c)) {
				neighbors.add(cell[r+1][c]);
			}
			if (isAvailable(r, c-1)) {
				neighbors.add(cell[r][c-1]);
			}
			if (isAvailable(r, c+1)) {
				neighbors.add(cell[r][c+1]);
			}

			if (neighbors.size() > 0) {
				if (neighbors.size() > 1) {
					// try this first cell later
					tryLaterCell.add(cell[r][c]);
				}
				// pick a neighbor and remove the wall
				int pick = rand.nextInt(neighbors.size());
				Cell neighbor = neighbors.get(pick);
				cell[r][c].openTo(neighbor);

				// go to the neighbor
				r = neighbor.getRow();
				c = neighbor.getCol();
				visitedCells++;
			} else {
				// visit a saved cell
				Cell nextCell = tryLaterCell.remove(0);
				r = nextCell.getRow();
				c = nextCell.getCol();
			}
		}
	}

	private boolean isAvailable(int row, int col) {
		boolean available = false;
		if (row >= 0 && row < rows &&
				col >= 0 && col < cols &&
				cell[row][col].hasAllWalls()) {
			available = true;
		}
		return available;
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
