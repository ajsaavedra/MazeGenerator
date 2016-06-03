package com.tonyjs.mazegenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MazeGenerator extends JFrame {
	private static final long serialVersionUID = 1L;
	private TitleLabel titleLabel = new TitleLabel("Maze");
	private static int rows = 30;
	private static int cols = 30;
	private static Cell[][] cell = new Cell[rows][cols];
	private static JPanel mazePanel = new JPanel();
	private static int currentRow;
	private static int currentCol;
	private static int endRow = rows - 1;
	private static int endCol = cols - 1;
	private static boolean gameIsWon;
	private static boolean solveMode;
	private boolean notFound;
	private JButton newMazeButton;
	private static JButton solveMaze;

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

		//button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.BLACK);
		add(buttonPanel, BorderLayout.PAGE_END);

		newMazeButton = new JButton("New Maze");
		solveMaze = new JButton("Solve Maze");
		newMazeButton.setFocusable(false);
		newMazeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newMaze();
				solveMaze.setEnabled(true);
			}
		});
		buttonPanel.add(newMazeButton);

		solveMaze.setFocusable(false);
		solveMaze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solveMode = true;
				solveAndDrawMaze();
				solveMaze.setEnabled(false);
			}
		});
		buttonPanel.add(solveMaze);

		// listeners
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (!gameIsWon) {
					int key = e.getKeyCode();
					moveBall(key);
				}
			}
		});
	}

	public void newMaze() {
		mazePanel.removeAll();
		gameIsWon = false;
		solveMode = false;
		notFound = true;
		mazePanel.setLayout(new GridLayout(rows, cols));
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cell[i][j] = new Cell(i, j);
				mazePanel.add(cell[i][j]);
			}
		}
		generateMaze();

		currentRow = 0;
		currentCol = 0;
		endRow = rows - 1;
		endCol = cols - 1;
		cell[currentRow][currentCol].setCurrent(true);
		cell[endRow][endCol].setEnd(true);
		mazePanel.revalidate();
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

	public static void moveTo(int nextRow, int nextCol, int firstDirection, int secondDirection) {
		cell[currentRow][currentCol].setCurrent(false);
		cell[currentRow][currentCol].addPath(firstDirection);
		currentRow = nextRow;
		currentCol = nextCol;
		cell[currentRow][currentCol].setCurrent(true);
		cell[currentRow][currentCol].addPath(secondDirection);
	}

	private static void moveBall(int direction) {
		switch (direction) {
		case 38: // up
			if (!cell[currentRow][currentCol].isWall(0)) {
				moveTo(currentRow-1, currentCol, 0, 2);
				while (!cell[currentRow][currentCol].isWall(0) &&
						cell[currentRow][currentCol].isWall(1) &&
						cell[currentRow][currentCol].isWall(3)) {
					moveTo(currentRow-1, currentCol, 0, 2);
				}
			} else {
				SoundEffect.playWallEffect();
			}
			break;
		case 40: // down
			if (!cell[currentRow][currentCol].isWall(2)) {
				moveTo(currentRow+1, currentCol, 2, 0);
				while (!cell[currentRow][currentCol].isWall(2) &&
						cell[currentRow][currentCol].isWall(1) &&
						cell[currentRow][currentCol].isWall(3)) {
					moveTo(currentRow+1, currentCol, 2, 0);
				}
			} else {
				SoundEffect.playWallEffect();
			}
			break;
		case 39: // right
			if (!cell[currentRow][currentCol].isWall(1)) {
				moveTo(currentRow, currentCol+1, 1, 3);
				while (!cell[currentRow][currentCol].isWall(1) &&
						cell[currentRow][currentCol].isWall(0) &&
						cell[currentRow][currentCol].isWall(2)) {
					moveTo(currentRow, currentCol+1, 1, 3);
				}
			} else {
				SoundEffect.playWallEffect();
			}
			break;
		case 37: // left
			if (!cell[currentRow][currentCol].isWall(3)) {
				moveTo(currentRow, currentCol-1, 3, 1);
				while (!cell[currentRow][currentCol].isWall(3) &&
						cell[currentRow][currentCol].isWall(0) &&
						cell[currentRow][currentCol].isWall(2)) {
					moveTo(currentRow, currentCol-1, 3, 1);
				}
			} else {
				SoundEffect.playWallEffect();
			}
			break;
		}

		if (currentRow == endRow && currentCol == endCol) {
			if (!solveMode) {
				JOptionPane.showMessageDialog(mazePanel, "You Win! Play Again?");
				solveMaze.setEnabled(false);
			}
			gameIsWon = true;
		}
	}

	public void solveAndDrawMaze() {
		int[][] table = new int[rows][cols];
		currentCol = 0; 
		currentRow = 0;
		lookupMazeSolution(table, currentCol, currentRow, true, true, true, true);
		drawOutSolution(table, false, true, false, true);
	}

	public int lookupMazeSolution(int[][] table, int currentTableRow, int currentTableCol, boolean steppedUp,
			boolean steppedDown, boolean steppedRight, boolean steppedLeft) {

		int stepUp = 0;
		int stepDown = 0;
		int stepRight = 0;
		int stepLeft = 0;

		if (currentTableRow == endRow && currentTableCol == endCol) {
			notFound = false;
			table[currentTableRow][currentTableCol] = 100;
			return table[currentTableRow][currentTableCol];
		} else {
			if (!cell[currentTableRow][currentTableCol].isWall(0) && (steppedRight || steppedLeft || !steppedDown) && notFound) {
				stepUp = lookupMazeSolution(table, currentTableRow - 1, currentTableCol, true, false, false, false);
			}
			if (!cell[currentTableRow][currentTableCol].isWall(1) && (steppedUp || !steppedLeft || steppedDown) && notFound) {
				stepRight = lookupMazeSolution(table, currentTableRow, currentTableCol + 1, false, false, true, false);
			}
			if (!cell[currentTableRow][currentTableCol].isWall(2) && (steppedRight || steppedLeft || !steppedUp) && notFound) {
				stepDown = lookupMazeSolution(table, currentTableRow + 1, currentTableCol, false, true, false, false);
			}
			if (!cell[currentTableRow][currentTableCol].isWall(3) && (!steppedRight || steppedUp || steppedDown) && notFound) {
				stepLeft = lookupMazeSolution(table, currentTableRow, currentTableCol - 1, false, false, false, true);
			} 

			table[currentTableRow][currentTableCol] = Math.max(Math.max(stepUp, stepDown), Math.max(stepRight, stepLeft));
			return table[currentTableRow][currentTableCol];
		}

	}

	public void drawOutSolution(int[][] solution, boolean up, boolean down, boolean left, boolean right) {		
		int upValue = 0;
		int downValue = 0;
		int leftValue = 0;
		int rightValue = 0;

		Cell location = cell[currentRow][currentCol];

		if (!location.isWall(0) && !down) {
			upValue = solution[location.getRow()-1][location.getCol()];
		}
		if (!location.isWall(1) && !left) {
			rightValue = solution[location.getRow()][location.getCol()+1];
		}
		if (!location.isWall(2) && !up) {
			downValue = solution[location.getRow()+1][location.getCol()];
		}
		if (!location.isWall(3) && !right) {
			leftValue = solution[location.getRow()][location.getCol()-1];
		}

		if (up) {// if just moved up don't allow to move back down
			if (upValue > rightValue && upValue > leftValue) {// move up
				moveUp();
				drawOutSolution(solution, true, false, false, false);
			} else if (rightValue > leftValue) {// move right
				moveRight();
				drawOutSolution(solution, false, false, false, true);
			} else if (leftValue > rightValue) {// move left
				moveLeft();
				drawOutSolution(solution, false, false, true, false);
			}
		} else if (down) {// if just moved down don't allow to move back up
			if (downValue > rightValue && downValue > leftValue) {// move down
				moveDown();
				drawOutSolution(solution, false, true, false, false);
			} else if (rightValue > leftValue) {// move right
				moveRight();
				drawOutSolution(solution, false, false, false, true);
			} else if (leftValue > rightValue) {// move left
				moveLeft();
				drawOutSolution(solution, false, false, true, false);
			}
		} else if (left) {// if just moved left, don't move back right
			if (downValue > upValue && downValue > leftValue) {//move down
				moveDown();
				drawOutSolution(solution, false, true, false, false);
			} else if (upValue > leftValue) {// move up
				moveUp();
				drawOutSolution(solution, true, false, false, false);
			} else if (leftValue > upValue) {// move left
				moveLeft();
				drawOutSolution(solution, false, false, true, false);
			}
		} else if (right) {// if just moved right, don't move back left
			if (downValue > rightValue && downValue > upValue) {// move down
				moveDown();
				drawOutSolution(solution, false, true, false, false);
			} else if (upValue > rightValue) {// move up
				moveUp();
				drawOutSolution(solution, true, false, false, false);
			} else if (rightValue > upValue) {// move right
				moveRight();
				drawOutSolution(solution, false, false, false, true);
			}
		}
	}

	public static void moveUp() {
		moveBall(38);
	}

	public static void moveDown() {
		moveBall(40);
	}

	public static void moveLeft() {
		moveBall(37);
	}

	public static void moveRight() {
		moveBall(39);
	}
	
	public static boolean getSolveMode() {
		return solveMode;
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
