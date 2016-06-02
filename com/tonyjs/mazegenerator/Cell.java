package com.tonyjs.mazegenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Cell extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int SIZE = 20;
	private static final int TOP = 0;
	private static final int RIGHT = 1;
	private static final int BOTTOM = 2;
	private static final int LEFT = 3;
	
	private int row = -1;
	private int col = -1;
	
	private boolean[] wall = {true, true, true, true};
	
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
		return wall[0] && wall[1] && wall[2] && wall[3];
	}
	
	public void removeWall (int w) {
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
	}
	
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(SIZE, SIZE);
		return size;
	}
}
