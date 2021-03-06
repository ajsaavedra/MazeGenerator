package com.tonyjs.mazegenerator;

import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private Cell[][] cell;
    private int rows;
    private int cols;
    private Random rand;
    private int row;
    private int col;
    private int visitedCells = 1;
    private ArrayList<Cell> savedCells = new ArrayList<Cell>();

    public Maze(Cell[][] cell, int rows, int cols) {
        this.cell = cell;
        this.rows = rows;
        this.cols = cols;
        this.rand = new Random();
        this.row = rand.nextInt(rows);
        this.col = rand.nextInt(cols);
    }

    public void carveOutMaze() {
        if (visitedCells < rows * cols) {
            ArrayList<Cell> neighbors = getNeighbors(row, col);
            if (neighbors.size() > 0) {
                if (neighbors.size() > 1) {
                    savedCells.add(cell[row][col]);
                }
                // pick a neighbor and remove the wall
                int pick = rand.nextInt(neighbors.size());
                Cell neighbor = neighbors.get(pick);
                cell[row][col].openTo(neighbor);

                // go to the neighbor
                row = neighbor.getRow();
                col = neighbor.getCol();
                ++visitedCells;
            } else {
                Cell savedCell = savedCells.remove(0);
                row = savedCell.getRow();
                col = savedCell.getCol();
            }
            carveOutMaze();
        }
    }
    
    private ArrayList<Cell> getNeighbors(int row, int col) {
        ArrayList<Cell> neighbors = new ArrayList<Cell>();
        if (isAvailable(row-1, col)) {
            neighbors.add(cell[row-1][col]);
        }
        if (isAvailable(row+1, col)) {
            neighbors.add(cell[row+1][col]);
        }
        if (isAvailable(row, col-1)) {
            neighbors.add(cell[row][col-1]);
        }
        if (isAvailable(row, col+1)) {
            neighbors.add(cell[row][col+1]);
        }
        return neighbors;
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
}
