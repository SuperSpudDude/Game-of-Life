import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/*
* NOTES
*
* display current generation fully
* calculate next generation
* pass next gen to prev gen
*
*
* TO DO:
* add mouse listener than can populate the array by click/dragging mouse
*
*
* */


public class screen extends JPanel {

    public static void main(String[] args) {

        myPanel p = new myPanel();
        //p.populateCustom();
        p.populate();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI(p);
            }
        });
    }

    private static void createAndShowGUI(myPanel panel) {
        System.out.println("Created GUI on EDT? " +
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Game of Life!");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(panel);
        f.pack();
        f.setVisible(true);
    }

}
class myPanel extends JPanel {
    // width and height initially in pixels
    private int screenH = 600;
    private int screenW = 600;
    private int cellSize = 5;
    private int gridH = screenH / cellSize;
    private int gridW = screenW / cellSize;


    // declare array of cell objects
    cell[][] currentGen = new cell[gridH][gridW];
    cell[][] nextGen = new cell[gridH][gridW];

    // used to initialise generation
    private static boolean getRandBool(){
        return Math.random() < 0.5;
    }

    // initialise current generation
    public void populate(){
        boolean isCell;
        for (int i = 0; i < gridH; i++) {
            for (int j = 0; j < gridW; j++) {
                isCell = getRandBool();
                // initialise with current object
                currentGen[i][j] = new cell();
                // also initialise the nextGen with an object
                nextGen[i][j] = new cell();

                // controls the initial population density
                if(j%10==0)
                    //set state
                    currentGen[i][j].setAliveState(isCell);
            }
        }
    }

    public Dimension getPreferredSize(){
        return new Dimension(screenH,screenW);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        // display current generation
        for (int i = 0; i < gridH; i++) {
            for (int j = 0; j < gridW; j++) {
                if (currentGen[i][j].isAlive()) {
                    // fill quare
                    g.setColor(Color.BLACK);
                    g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
                // show grid
                g.setColor(Color.BLACK);
                g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }

        // calculate the next generation
        for (int i = 0; i < gridH; i++) {
            for (int j = 0; j < gridW; j++) {
                // next generate alive sate is set by determining current generations neighbors
                nextGen[i][j].setAliveState(liveToNextGen(countNeighbors(j,i),j,i));
                //liveToNextGen(countNeighbors(j,i),j,i);
            }
        }

        // pass next gen to current gen
        currentGen = Arrays.copyOf(nextGen, nextGen.length);

        repaint();

        // wait
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private int countNeighbors(int x, int y){
        int num = 0;
        int X;
        int Y;
        for (int i = -1; i < 2; i++){
            for (int j = -1; j < 2; j++){
                // handle canvas wrap around
                X = (x + j + gridW) % gridW;
                Y = (y + i + gridH) % gridH;
                if (currentGen[Y][X].isAlive())
                    num += 1;
            }
        }

        // remove the centre cell (the current cell) if it is alive
        if(currentGen[y][x].isAlive())
            num -= 1;

        return num;
    }

    public boolean liveToNextGen(int neighbors, int x, int y) {
        boolean state = currentGen[y][x].isAlive();
        // Any live cell with fewer than two live neighbours dies, as if by underpopulation.
        if(currentGen[y][x].isAlive() && neighbors < 2)
            state = false;
        // Any live cell with two or three live neighbours lives on to the next generation.
        else if(currentGen[y][x].isAlive() && (neighbors == 2 || neighbors == 3))
            state = true;
        // Any live cell with more than three live neighbours dies, as if by overpopulation.
        else if(currentGen[y][x].isAlive() && neighbors > 3)
            state = false;
        // Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
        else if(!currentGen[y][x].isAlive() && neighbors == 3)
            state = true;

        return state;
    }

}


class cell {
    private boolean alive;

    public void setAliveState(boolean state){ alive = state; }
    public boolean isAlive(){ return alive; }

}
