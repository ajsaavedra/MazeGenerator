package com.tonyjs.mazegenerator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

public class Cell extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int SIZE = 20;
	private static final int TOP = 0;
	private static final int RIGHT = 1;
	private static final int BOTTOM = 2;
	private static final int LEFT = 3;
	private boolean current = false;
	private boolean end = false;
	private int row = -1;
	private int col = -1;
	private Color solution = new Color(139, 58, 185);

	private boolean[] wall = {true, true, true, true};
	private boolean[] path = {false, false, false, false};

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isWall(int index) {
		return wall[index];
	}

	public boolean hasAllWalls() {
		return wall[TOP] && wall[RIGHT] && wall[BOTTOM] && wall[LEFT];
	}

	public void removeWall(int w) {
		wall[w] = false;
		repaint();
	}

	public void openTo(Cell neighbor) {
		if (row < neighbor.getRow()) {
			removeWall(BOTTOM);
			neighbor.removeWall(TOP);
		} else if (row > neighbor.getRow()) {
			removeWall(TOP);
			neighbor.removeWall(BOTTOM);
		} else if (col < neighbor.getCol()) {
			removeWall(RIGHT);
			neighbor.removeWall(LEFT);
		} else if (col > neighbor.getCol()) {
			removeWall(LEFT);
			neighbor.removeWall(RIGHT);
		}
	}

	public void setCurrent(boolean current) {
		this.current = current;
		repaint();
	}

	public void setEnd(boolean end) {
		this.end = end;
		repaint();
	}

	public void addPath(int side) {
		path[side] = true;
		repaint();
	}

	public void paintComponent(Graphics g) {
		// draw the background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, SIZE, SIZE);
		g.setColor(Color.BLACK);

		// draw the walls
		if (isWall(TOP)) {
			g.drawLine(0, 0, SIZE, 0);
		}
		if (isWall(LEFT)) {
			g.drawLine(0, 0, 0, SIZE);
		}
		if (current) {
			if (MazeSolver.getSolveMode()) {
				g.setColor(solution);
			} else {
				g.setColor(Color.BLUE);
			}
			g.fillOval(3, 3, SIZE-6, SIZE-6);
		} else if (end) {
			g.setColor(Color.RED);
			g.fillOval(3, 3, SIZE-6, SIZE-6);
		}

		if (MazeSolver.getSolveMode()) {
			g.setColor(solution);
		} else {
			g.setColor(Color.BLUE);
		}

		if (path[TOP]) {
			drawDashedLine(g, SIZE/2, 0, SIZE/2, SIZE/2);
		}
		if (path[RIGHT]) {
			drawDashedLine(g, SIZE, SIZE/2, SIZE/2, SIZE/2);
		}
		if (path[BOTTOM]) {
			drawDashedLine(g, SIZE/2, SIZE, SIZE/2, SIZE/2);
		}
		if (path[LEFT]) {
			drawDashedLine(g, 0, SIZE/2, SIZE/2, SIZE/2);
		}
	}

	public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2) {
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 3);
		g2d.setStroke(dashed);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.dispose();
	}

	public Dimension getPreferredSize() {
		Dimension size = new Dimension(SIZE, SIZE);
		return size;
	}
}
