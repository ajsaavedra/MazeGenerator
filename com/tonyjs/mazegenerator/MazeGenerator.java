package com.tonyjs.mazegenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
	private int row = 0;
	private int col = 0;
	private int endRow = rows - 1;
	private int endCol = cols - 1;

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
		centerPanel.add(mazePanel);
		
		// listeners
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				moveBall(key);
			}
		});
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
		
		row = 0;
		col = 0;
		endRow = rows - 1;
		endCol = cols - 1;
		cell[row][col].setCurrent(true);
		cell[endRow][endCol].setEnd(true);
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
	
	public void moveTo(int nextRow, int nextCol) {
		cell[row][col].setCurrent(false);
		row = nextRow;
		col = nextCol;
		cell[row][col].setCurrent(true);
	}
	
	private void moveBall(int direction) {
		switch (direction) {
		case 38: // up
			if(!cell[row][col].isWall(0)) {
				moveTo(row-1, col);
			}
			break;
		case 40: // down
			if(!cell[row][col].isWall(2)) {
				moveTo(row+1, col);
			}
			break;
		case 39: // right
			if(!cell[row][col].isWall(1)) {
				moveTo(row, col+1);
			}
			break;
		case 37: // left
			if(!cell[row][col].isWall(3)) {
				moveTo(row, col-1);
			}
			break;
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
