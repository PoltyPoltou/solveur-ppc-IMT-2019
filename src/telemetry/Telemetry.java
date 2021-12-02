package telemetry;

import definition.Csp;

/**
 * used on a backtrack method to have some infos
 */
public class Telemetry {

    private int backtrackCount;
    private int nodesVisited;
    private long timeLaunch;
    private double timeForFirstSolution;
    private double timeForAll;
    private boolean printDuringSolve;
    private int nbSol;

    public Telemetry(boolean printDuringSolve) {
        this.backtrackCount = 0;
        this.nodesVisited = 0;
        this.timeLaunch = 0;
        this.timeForFirstSolution = 0;
        this.timeForAll = 0;
        this.printDuringSolve = printDuringSolve;
        this.nbSol = 0;
    }

    public int getBacktrackCount() {
        return backtrackCount;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public long getTimeLaunch() {
        return timeLaunch;
    }

    public void setPrintDuringSolve(boolean printDuringSolve) {
        this.printDuringSolve = printDuringSolve;
    }

    public void addNodeVisited() {
        ++nodesVisited;
    }

    public void backtracked() {
        ++backtrackCount;
        if (printDuringSolve) {
            System.out.println("Backtrack happened, count : " + backtrackCount);
        }
    }

    public void allInstanciated(boolean solution) {
        if (solution && this.nbSol == 1) {
            this.timeForFirstSolution = (System.currentTimeMillis() - this.timeLaunch) / 1000.;
            this.nbSol += 1;
            System.out.println("First solution found in " + timeForFirstSolution + " s");
        } else if (solution) {
            if (this.printDuringSolve) {
                System.out.println("Solution found");
            }
            ++this.nbSol;
        } else {
            if (this.printDuringSolve) {
                System.out.println("Got to the end of the tree, instance was not valid.");
            }
        }
    }

    public void start() {
        this.timeLaunch = System.currentTimeMillis();
    }

    public void end(Csp csp) {
        this.timeForAll = (System.currentTimeMillis() - this.timeLaunch) / 1000.;
        System.out.println("---------------------------------------------------");
        System.out.println("End of search, it took : " + this.timeForAll + " s, " + this.timeForFirstSolution
                + " s for first solution");
        System.out.println("We found " + this.nbSol + " solution(s)");
        System.out.println("Backtrack done : " + this.backtrackCount);
        System.out.println("Nodes visited : " + this.nodesVisited);
    }

    public int getSolutionCount() {
        return this.nbSol;
    }

}
