package com.tonyjs.mazegenerator;

public class MazeSolver {
    private Cell[][] cell;
    private int currentRow;
    private int currentCol;
    private int rows;
    private int cols;
    private int endRow;
    private int endCol;
    private boolean GAME_OVER, GAME_START, GAME_IS_WON;
    private static boolean SOLVE_MODE;
    private boolean NOT_FOUND;
    
    public MazeSolver(Cell[][] cell, int rows, int cols) {
        this.cell = cell;
        this.rows = rows;
        this.cols = cols;
        this.endRow = rows - 1;
        this.endCol = cols - 1;
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

    public int lookupMazeSolution(int[][] table, int currentTableRow, int currentTableCol,
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
    
    public void drawDown(int[][] solution) {
        drawOutSolution(solution, false, true, false, false);
    }

    public void drawUp(int[][] solution) {
        drawOutSolution(solution, true, false, false, false);
    }
    
    public void drawLeft(int[][] solution) {
        drawOutSolution(solution, false, false, true, false);
    }
    
    public void drawRight(int[][] solution) {
        drawOutSolution(solution, false, false, false, true);
    }
    
    public void moveUp() {
        moveBall(38);
    }

    public void moveDown() {
        moveBall(40);
    }

    public void moveLeft() {
        moveBall(37);
    }

    public void moveRight() {
        moveBall(39);
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
