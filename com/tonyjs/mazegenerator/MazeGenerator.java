package com.tonyjs.mazegenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MazeGenerator extends JFrame {
	private static final long serialVersionUID = 1L;
	private TitleLabel titleLabel = new TitleLabel("Maze");
	private static int rows = 35;
	private static int cols = 35;
	private static Cell[][] cell = new Cell[rows][cols];
	private static JPanel mazePanel = new JPanel();
	private static int currentRow, currentCol;
	private static int endRow = rows - 1;
	private static int endCol = cols - 1;
	private static boolean GAME_IS_WON, SOLVE_MODE, GAME_OVER, GAME_START, NOT_FOUND;
	private static JButton newMazeButton, startGameButton, solveMaze;
	private static Timer timer;
	private static JLabel timerL;
	private static Container container;

	public MazeGenerator() {
		initGUI();
		setTitle("Maze Generator");
		container = getContentPane();
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
		startGameButton = new JButton("Start");
		timerL = new JLabel("Time: 90");
		timerL.setSize(120, 20);
		timerL.setForeground(Color.WHITE);

		newMazeButton.setFocusable(false);
		solveMaze.setFocusable(false);
		solveMaze.setEnabled(false);
		startGameButton.setFocusable(false);

		newMazeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGameButton.setEnabled(true);
				solveMaze.setEnabled(false);
				newMaze();
				timerL.setText("Time: 90");
			}
		});
		buttonPanel.add(newMazeButton);

		solveMaze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SOLVE_MODE = true;
				solveAndDrawMaze();
				GAME_OVER = true;
				solveMaze.setEnabled(false);
			}
		});
		buttonPanel.add(solveMaze);

		startGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GAME_START = true;
				solveMaze.setEnabled(true);
				startGameButton.setEnabled(false);
				startTimer();
			}
		});
		buttonPanel.add(startGameButton);
		buttonPanel.add(timerL);

		// listeners
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (GAME_START && !GAME_IS_WON && !GAME_OVER) {
					int key = e.getKeyCode();
					moveBall(key);
				}
			}
		});
	}

	public static void startTimer() {
		timer = new Timer(1000, new ActionListener() {
			int elapsedSeconds = 90;
			@Override
			public void actionPerformed(ActionEvent e) {
				String seconds = Integer.toString(elapsedSeconds);
				if (GAME_OVER || GAME_IS_WON || !GAME_START) {
					timer.stop();
				} else if (elapsedSeconds == 0) {
					timer.stop();
					setGameStates();
					getGameState();
				} else {
					--elapsedSeconds;
					timerL.setText("Time: " + seconds);
				}
			}
		});
		timer.start();
	}

	public static void setGameStates() {
		GAME_START = false;
		GAME_IS_WON = false;
		GAME_OVER = true;
	}

	public static void resetGameStates() {
		GAME_OVER = false;
		GAME_IS_WON = false;
		GAME_START = false;
	}

	public static void getGameState() {
		if (!GAME_IS_WON && GAME_OVER) {
			newMazeButton.setEnabled(true);
			startGameButton.setEnabled(false);
			Object[] options = {"Play Again", "Show Solution", "Exit Game"};
			int n = JOptionPane.showOptionDialog(container,
					"Game Over!",
					"Maze Generator",
					JOptionPane.WARNING_MESSAGE,
					JOptionPane.WARNING_MESSAGE,
					null,
					options,
					options[2]);
			switch(n) {
			case 0:
				timerL.setText("Time: 90");
				newMaze();
				startGameButton.setEnabled(true);
				solveMaze.setEnabled(false);
				break;
			case 1:
				SOLVE_MODE = true;
				GAME_OVER = false;
				solveAndDrawMaze();
				GAME_OVER = true;
				solveMaze.setEnabled(false);
				break;
			case 2:
				System.exit(0);
			}
		}
	}

	public static void newMaze() {
		mazePanel.removeAll();
		resetGameStates();
		SOLVE_MODE = false;
		NOT_FOUND = true;
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

	private static void generateMaze() {
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

	private static boolean isAvailable(int row, int col) {
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
			GAME_OVER = true;
			if (!SOLVE_MODE) {
				solveMaze.setEnabled(false);
				GAME_IS_WON = true;
				showWinningMessage();
			}
		}
	}

	public static void showWinningMessage() {
		Object[] options = {"Play Again", "Exit Game"};
		int n = JOptionPane.showOptionDialog(container,
				"Winner! You found the exit!",
				"Maze Generator",
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.PLAIN_MESSAGE,
				null,
				options,
				options[1]);
		switch(n) {
		case 0:
			timerL.setText("Time: 90");
			newMaze();
			resetGameStates();
			startGameButton.setEnabled(true);
			solveMaze.setEnabled(false);
			break;
		case 1:
			System.exit(0);
		}
	}

	public static void solveAndDrawMaze() {
		int[][] table = new int[rows][cols];
		if (GAME_OVER) {
			lookupMazeSolution(table, 0, 0, true, true, true, true);
			drawOutSolution(table, false, true, false, true);
		} else {
			lookupMazeSolution(table, currentRow, currentCol, true, true, true, true);
			drawOutSolution(table, cell[currentRow][currentCol].isWall(2), cell[currentRow][currentCol].isWall(0),
					cell[currentRow][currentCol].isWall(1), cell[currentRow][currentCol].isWall(3));
		}
	}

	public static int lookupMazeSolution(int[][] table, int currentTableRow, int currentTableCol,
			boolean steppedUp, boolean steppedDown, boolean steppedRight, boolean steppedLeft) {

		int stepUp = 0;
		int stepDown = 0;
		int stepRight = 0;
		int stepLeft = 0;

		if (currentTableRow == endRow && currentTableCol == endCol) {
			NOT_FOUND = false;
			table[currentTableRow][currentTableCol] = 100;
			return table[currentTableRow][currentTableCol];
		} else {
			if (!cell[currentTableRow][currentTableCol].isWall(0) && NOT_FOUND &&
					(steppedRight || steppedLeft || !steppedDown)) {
				stepUp = lookupMazeSolution(table, currentTableRow - 1, currentTableCol, true, false, false, false);
			}
			if (!cell[currentTableRow][currentTableCol].isWall(1) && NOT_FOUND &&
					(steppedUp || !steppedLeft || steppedDown)) {
				stepRight = lookupMazeSolution(table, currentTableRow, currentTableCol + 1, false, false, true, false);
			}
			if (!cell[currentTableRow][currentTableCol].isWall(2) && NOT_FOUND &&
					(steppedRight || steppedLeft || !steppedUp) ) {
				stepDown = lookupMazeSolution(table, currentTableRow + 1, currentTableCol, false, true, false, false);
			}
			if (!cell[currentTableRow][currentTableCol].isWall(3) && NOT_FOUND &&
					(!steppedRight || steppedUp || steppedDown)) {
				stepLeft = lookupMazeSolution(table, currentTableRow, currentTableCol - 1, false, false, false, true);
			} 

			table[currentTableRow][currentTableCol] = Math.max(Math.max(stepUp, stepDown), Math.max(stepRight, stepLeft));
			return table[currentTableRow][currentTableCol];
		}

	}

	public static void drawOutSolution(int[][] solution, boolean up, boolean down, boolean left, boolean right) {		
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
				drawUp(solution);
			} else if (rightValue > leftValue) {// move right
				moveRight();
				drawRight(solution);
			} else if (leftValue > rightValue) {// move left
				moveLeft();
				drawLeft(solution);
			}
		} else if (down) {// if just moved down don't allow to move back up
			if (downValue > rightValue && downValue > leftValue) {// move down
				moveDown();
				drawDown(solution);
			} else if (rightValue > leftValue) {// move right
				moveRight();
				drawRight(solution);
			} else if (leftValue > rightValue) {// move left
				moveLeft();
				drawLeft(solution);
			}
		} else if (left) {// if just moved left, don't move back right
			if (downValue > upValue && downValue > leftValue) {//move down
				moveDown();
				drawDown(solution);
			} else if (upValue > leftValue) {// move up
				moveUp();
				drawUp(solution);
			} else if (leftValue > upValue) {// move left
				moveLeft();
				drawLeft(solution);
			}
		} else if (right) {// if just moved right, don't move back left
			if (downValue > rightValue && downValue > upValue) {// move down
				moveDown();
				drawDown(solution);
			} else if (upValue > rightValue) {// move up
				moveUp();
				drawUp(solution);
			} else if (rightValue > upValue) {// move right
				moveRight();
				drawRight(solution);
			}
		}
	}
	
	public static void drawDown(int[][] solution) {
		drawOutSolution(solution, false, true, false, false);
	}

	public static void drawUp(int[][] solution) {
		drawOutSolution(solution, true, false, false, false);
	}
	
	public static void drawLeft(int[][] solution) {
		drawOutSolution(solution, false, false, true, false);
	}
	
	public static void drawRight(int[][] solution) {
		drawOutSolution(solution, false, false, false, true);
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
		return SOLVE_MODE;
	}

	public static void main(String[] args) {
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MazeGenerator();
			}
		});
	}
}
