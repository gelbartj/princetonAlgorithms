public class Percolation {

    private int[] parentGrid;
    private boolean[] openGrid;
    // private boolean isValid;
    private final int gridSize;
    private int[] sz;
    private int openCount = 0;
    private int currCell;
    private final int[] topCell;
    private final int[] bottomCell;
    private int[][] openedBottomCells;
    private int openedBottomCount;
    // private final int topCellNum;
    // private final int bottomCellNum;

    // creates n-by-n grid, with all sites initially blocked
    // blocked = 0

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        openedBottomCells = new int[n][2];
        openedBottomCount = 0;
        topCell = new int[] { n, n + 1 }; // arbitrary location
        bottomCell = new int[] { n, n + 2 };

        int topCellNum = n * n;
        int bottomCellNum = n * n + 1;

        parentGrid = new int[n * n + 2];
        openGrid = new boolean[n * n + 2];
        sz = new int[n * n + 2];
        int cellCounter = 0;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                parentGrid[cellCounter] = cellCounter;
                openGrid[cellCounter] = false;
                sz[cellCounter] = 1;
                ++cellCounter;
            }
        }
        parentGrid[topCellNum] = topCellNum;
        parentGrid[bottomCellNum] = bottomCellNum;
        openGrid[topCellNum] = true;
        openGrid[bottomCellNum] = true;
        sz[bottomCellNum] = 1;
        sz[topCellNum] = 1;
        gridSize = n;
    }

    /*
    private boolean isValidN(int row, int col) {
        if (row < 1 || row > gridSize || col < 1 || col > gridSize) {
            System.out.println("Illegal arguments: " + row + ", " + col);
            throw new IllegalArgumentException();
        }
        return true;
    }
     */

    private int getCellNumber(int row, int col) {
        return (row - 1) * gridSize + col - 1;
    }

    private int[] getCellCoords(int cellNum) {
        int cellCol = cellNum % gridSize;
        int cellRow = cellNum / gridSize + 1;
        return new int[] { cellRow, cellCol };
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || col < 1 || row > gridSize || col > gridSize)
            throw new IllegalArgumentException();

        currCell = getCellNumber(row, col);
        if (openGrid[currCell]) {
            // System.out.println("Already open: " + row + ", " + col);
            return;
        }
        openGrid[currCell] = true;

        if (row + 1 <= gridSize && isOpen(row + 1, col)) {
            union(row, col, row + 1, col);
        }
        if (col + 1 <= gridSize && isOpen(row, col + 1)) {
            union(row, col, row, col + 1);
        }
        if (row - 1 > 0 && isOpen(row - 1, col)) {
            union(row, col, row - 1, col);
        }
        if (col - 1 > 0 && isOpen(row, col - 1)) {
            union(row, col, row, col - 1);
        }

        if (row == gridSize) {
            openedBottomCells[openedBottomCount] = new int[] { row, col };
            openedBottomCount++;
        }


        if (row == 1) {
            union(row, col, topCell[0], topCell[1]);
        }

        if (isFull(row, col)) {
            for (int l = 0; l < openedBottomCount; l++) {
                if (openedBottomCells[l][0] == 0 || openedBottomCells[l][1] == 0) continue;
                if (connected(row, col, openedBottomCells[l][0], openedBottomCells[l][1]))
                    union(row, col, bottomCell[0], bottomCell[1]);
            }
        }

        /* if (row == gridSize) {
            union(row, col, bottomCell[0], bottomCell[1]);
        } */
        ++openCount;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        // isValid = isValidN(row, col);
        if (row < 1 || col < 1 || row > gridSize || col > gridSize)
            throw new IllegalArgumentException();
        currCell = getCellNumber(row, col);
        return openGrid[currCell];
    }


    private int root(int row, int col) {
        currCell = getCellNumber(row, col);
        while (parentGrid[currCell] != currCell) {
            parentGrid[currCell] = parentGrid[parentGrid[currCell]]; // path compression
            currCell = parentGrid[currCell];
        }
        return currCell;
    }

    private boolean connected(int row1, int col1, int row2, int col2) {
        return root(row1, col1) == root(row2, col2);
    }

    /*
    private boolean find(int row1, int col1, int row2, int col2) {
        return root(row1, col1) == root(row2, col2);
    }
    */

    private void union(int row1, int col1, int row2, int col2) {
        if (new int[] { row2, col2 } != topCell && new int[] { row2, col2 } != bottomCell) {
            if (!isOpen(row1, col1) && !isOpen(row2, col2)) {
                return;
            }
        }

        int root1 = root(row1, col1);
        int root2 = root(row2, col2);

        if (root1 == root2) return;

        if (sz[root1] < sz[root2]) {
            parentGrid[root1] = parentGrid[root2];
            sz[root2] += sz[root1];
        }
        else {
            parentGrid[root2] = parentGrid[root1];
            sz[root1] += sz[root2];
        }
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        // isValid = isValidN(row, col);
        if (row < 1 || col < 1 || row > gridSize || col > gridSize)
            throw new IllegalArgumentException();
        return isOpen(row, col) && connected(row, col, topCell[0], topCell[1]);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return connected(topCell[0], topCell[1], bottomCell[0], bottomCell[1]);
    }

    // test client (optional)
    public static void main(String[] args) {

        Percolation test = new Percolation(2);
        test.open(1, 1);
        test.open(1, 2);
        test.open(2, 2);
        System.out.println(test.percolates());

    }
}
