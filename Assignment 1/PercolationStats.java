import edu.princeton.cs.algs4.StdRandom;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;
    private final int n;
    private final int trials;
    private double[] trialResults;
    private double meanResult;
    private double stdDevResult;


    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.trials = trials;
        this.monteCarlo();
    }

    private void monteCarlo() {
        trialResults = new double[trials];
        // int bestRatio = 1000;
        for (int trialCount = 0; trialCount < trials; ++trialCount) {
            // System.out.println("Starting trial " + (trialCount + 1));
            Percolation perc = new Percolation(n);
            // int whileCounter = 0;
            // int currRatio;
            do {
                int randRow = StdRandom.uniform(1, n + 1);
                int randCol = StdRandom.uniform(1, n + 1);
                // System.out.println("rands: " + randRow + ", " + randCol);
                perc.open(randRow, randCol);

                /* whileCounter++;
                if (whileCounter > 1000) {
                    System.out.println("Breaking while loop");
                    break;
                } */
            } while (!perc.percolates());
            // System.out.println("Sites opened: " + perc.numberOfOpenSites());
            // currRatio = perc.numberOfOpenSites() / n;
            /* if (currRatio < bestRatio) {
                bestRatio = currRatio;
            }
            */
            // System.out.println("Ratio to perfect: " + perc.numberOfOpenSites() / n);

            trialResults[trialCount] = (double) perc.numberOfOpenSites() / (double) (n * n);
        }
        meanResult = mean();
        stdDevResult = stddev();
        // System.out.println("Mean: " + meanResult);
        // System.out.println("Stddev: " + stdDevResult);
        /* System.out.println(
                "Confidence interval: " + confidenceLo() + " - "
                        + confidenceHi());
        */
        // System.out.println("Best ratio: " + bestRatio);
    }

    // sample mean of percolation threshold
    public double mean() {
        return edu.princeton.cs.algs4.StdStats.mean(trialResults);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return edu.princeton.cs.algs4.StdStats.stddev(trialResults);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return meanResult - CONFIDENCE_95 * stdDevResult / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return meanResult + CONFIDENCE_95 * stdDevResult / Math.sqrt(trials);
    }

    // test client (see below)
    public static void main(String[] args) {
        int gridSize = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats newPerc = new PercolationStats(gridSize, trials);
        // newPerc.MonteCarlo();
    }

}
