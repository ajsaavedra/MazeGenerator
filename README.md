# MazeGenerator
A simple timed game that randomly generates a new maze each time. Players can either try to solve the maze on time and win the game
or instantly see a solution.

Seeing a solution comes in two forms: if you lose the game, you can view the solution that gets you out of the maze from the starting point.

If you get lost, and want to simply see the solution, the solution will lead you out of the maze from your current position.

## Building
This project repo is ready to be used from Eclipse.

In Eclipse go to: File/New/Project. Select Java.

Un-check "Use default location" and then click Browse and navigate to the top level directory with contains your source, libs, configs, etc.

Eclipse will display a warning that says that your project "overlaps the location of another project".

Re-select "Use default location," and now Eclipse will let you click on the "Next" button to continue configuration of your project.

## Playing
The object of the game is to get from one end to the next.  

In this GUI, the player has to press down on the arrow keys to
traverse the maze from the blue dot on the top left corner to the red dot on the bottom right.  

To start the game, simply press the start button.

Every turn the player makes, a blue trail
shows where they've been. Should the user give up or wish to see the solution, a purple, dashed trail will delineate this.

<img src="./images/solved_maze.png" width="550" height="600" />
