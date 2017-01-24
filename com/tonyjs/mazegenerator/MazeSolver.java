package com.tonyjs.mazegenerator;

public class MazeSolver {
    private Cell[][] cell;
    private int currentRow;
    private int currentCol;
    private int rows;
    private int cols;
    private int endRow;
    private int endCol;
    private int UP, DOWN, LEFT, RIGHT;
    private boolean GAME_OVER, GAME_START, GAME_IS_WON;
    private static boolean SOLVE_MODE;
    private boolean NOT_FOUND;
    private int[][] solution;

    public MazeSolver(Cell[][] cell, int rows, int cols) {
        this.cell = cell;
        this.rows = rows;
        this.cols = cols;
        this.endRow = rows - 1;
        this.endCol = cols - 1;
        this.UP = 0;
        this.RIGHT = 1;
        this.DOWN = 2;
        this.LEFT = 3;
    }

    public void setSolveMode(boolean b) {
        SOLVE_MODE = b;
    }

    public void setGameOver(boolean b) {
        GAME_OVER = b;
    }

    public void setGameWon(boolean b) {
        GAME_IS_WON = b;
    }

    public void setGameStart(boolean b) {
        GAME_START = b;
    }

    public boolean isGameOver() {
        return GAME_OVER;
    }

    public boolean isGameWon() {
        return GAME_IS_WON;
    }

    public static boolean getSolveMode() {
        return SOLVE_MODE;
    }

    public boolean isGameStart() {
        return GAME_START;
    }

    public void setGameStates() {
        GAME_START = false;
        GAME_IS_WON = false;
        GAME_OVER = true;
    }

    public void resetGameStates() {
        GAME_OVER = false;
        GAME_IS_WON = false;
        GAME_START = false;
        NOT_FOUND = true;
        SOLVE_MODE = false;
    }

    public void solveAndDrawMaze() {
        solution = new int[rows][cols];
        if (GAME_OVER) {
            lookupMazeSolution(solution, 0, 0, -1);
        } else {
            lookupMazeSolution(solution, currentRow, currentCol, -1);
        }
        drawOutSolution();
    }

    public int lookupMazeSolution(int[][] table, int currentTableRow, int currentTableCol, int direction) {
        int stepUp = 0;
        int stepDown = 0;
        int stepRight = 0;
        int stepLeft = 0;

        if (currentTableRow == endRow && currentTableCol == endCol) {
            NOT_FOUND = false;
            table[currentTableRow][currentTableCol] = 100;
            return table[currentTableRow][currentTableCol];
        } else if (NOT_FOUND) {
            if (!cell[currentTableRow][currentTableCol].isWall(0) &&
                    (direction != DOWN)) {
                stepUp = lookupMazeSolution(table, currentTableRow - 1, currentTableCol, UP);
            }
            if (!cell[currentTableRow][currentTableCol].isWall(1) &&
                    (direction != LEFT)) {
                stepRight = lookupMazeSolution(table, currentTableRow, currentTableCol + 1, RIGHT);
            }
            if (!cell[currentTableRow][currentTableCol].isWall(2) &&
                    (direction != UP)) {
                stepDown = lookupMazeSolution(table, currentTableRow + 1, currentTableCol, DOWN);
            }
            if (!cell[currentTableRow][currentTableCol].isWall(3) &&
                    (direction != RIGHT)) {
                stepLeft = lookupMazeSolution(table, currentTableRow, currentTableCol - 1, LEFT);
            }
        }

        table[currentTableRow][currentTableCol] = Math.max(Math.max(stepUp, stepDown), Math.max(stepRight, stepLeft));
        return table[currentTableRow][currentTableCol];
    }

    public void drawOutSolution() {
        while (currentRow < endRow || currentCol < endCol) {
            Cell location = cell[currentRow][currentCol];
            boolean[] path = location.getWalls();
            int realRow = currentRow;
            int realCol = currentCol;
            for (int i = 0; i < path.length; i++) {
                if (!location.isWall(i)) {
                    if (i == UP) {
                        realRow = location.getRow()-1;
                        realCol = location.getCol();
                    } else if (i == DOWN) {
                        realRow = location.getRow()+1;
                        realCol = location.getCol();
                    } else if (i == RIGHT) {
                        realRow = location.getRow();
                        realCol = location.getCol()+1;
                    } else if (i == LEFT){
                        realRow = location.getRow();
                        realCol = location.getCol()-1;
                    }
                }
                if (solution[realRow][realCol] == 100) {
                    moveAndDraw(i);
                    solution[realRow][realCol] = 0;
                    currentRow = realRow;
                    currentCol = realCol;
                }
            }
        }
    }

    public void moveAndDraw(int direction) {
        if (direction == UP) {
            moveBall(38);
        } else if (direction == RIGHT) {
            moveBall(39);
        } else if (direction == DOWN) {
            moveBall(40);
        } else {
            moveBall(37);
        }
    }

    public void moveBall(int direction) {
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
    }

    public boolean isWon() {
        if (currentRow == endRow && currentCol == endCol) {
            GAME_OVER = true;
            if (!SOLVE_MODE) {
                return true;
            }
        }
        return false;
    }

    public void moveTo(int nextRow, int nextCol, int firstDirection, int secondDirection) {
        cell[currentRow][currentCol].setCurrent(false);
        cell[currentRow][currentCol].addPath(firstDirection);
        currentRow = nextRow;
        currentCol = nextCol;
        cell[currentRow][currentCol].setCurrent(true);
        cell[currentRow][currentCol].addPath(secondDirection);
    }

}
