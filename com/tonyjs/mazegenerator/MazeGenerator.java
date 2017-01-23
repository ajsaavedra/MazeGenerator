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
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MazeGenerator extends JFrame {
	private static final long serialVersionUID = 1L;
	private static int rows = 30;
	private static int cols = 30;
	private static Cell[][] cell = new Cell[rows][cols];
	private static JPanel mazePanel = new JPanel();
	private static int currentRow, currentCol;
	private static int endRow = rows - 1;
	private static int endCol = cols - 1;
	private static JButton newMazeButton, startGameButton, solveMaze;
	private static Timer timer;
	private static JLabel timerL;
	private static Container container;
	private static MazeSolver mazeSolver;

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

	public MazeGenerator(boolean b) {}
	
	public int getRows() {
	    return rows;
	}

	public int getCols() {
	    return cols;
	}

	public void initGUI() {
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
				mazeSolver.setSolveMode(true);
				mazeSolver.solveAndDrawMaze();
				mazeSolver.setGameOver(true);
				solveMaze.setEnabled(false);
			}
		});
		buttonPanel.add(solveMaze);

		startGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    mazeSolver.setGameStart(true);
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
				if (mazeSolver.isGameStart() && !mazeSolver.isGameWon() && !mazeSolver.isGameOver()) {
					int key = e.getKeyCode();
					mazeSolver.moveBall(key);
					if (mazeSolver.isWon()) {
					    solveMaze.setEnabled(false);
		                mazeSolver.setGameWon(true);
		                showWinningMessage();
					}
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
				if (mazeSolver.isGameOver() || mazeSolver.isGameWon() || !mazeSolver.isGameStart()) {
					timer.stop();
				} else if (elapsedSeconds == 0) {
					timer.stop();
					mazeSolver.setGameStates();
					getGameState();
				} else {
					--elapsedSeconds;
					timerL.setText("Time: " + seconds);
				}
			}
		});
		timer.start();
	}

	public static void getGameState() {
		if (!mazeSolver.isWon() && mazeSolver.isGameOver()) {
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
				mazeSolver.setSolveMode(true);
				mazeSolver.setGameOver(false);
				mazeSolver.solveAndDrawMaze();
				mazeSolver.setGameOver(true);
				solveMaze.setEnabled(false);
				break;
			case 2:
				System.exit(0);
			}
		}
	}

	public static void newMaze() {
		mazePanel.removeAll();
		mazePanel.setLayout(new GridLayout(rows, cols));
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cell[i][j] = new Cell(i, j);
				mazePanel.add(cell[i][j]);
			}
		}

		generateMaze();
		mazeSolver = new MazeSolver(cell, rows, cols);
		mazeSolver.resetGameStates();

		currentRow = 0;
		currentCol = 0;
		endRow = rows - 1;
		endCol = cols - 1;
		cell[currentRow][currentCol].setCurrent(true);
		cell[endRow][endCol].setEnd(true);
		mazePanel.revalidate();
	}

	private static void generateMaze() {
		Maze maze = new Maze(cell);
		maze.carveOutMaze();
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
			mazeSolver.resetGameStates();
			startGameButton.setEnabled(true);
			solveMaze.setEnabled(false);
			break;
		case 1:
			System.exit(0);
		}
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
