import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Board {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    private final int[][] tiles;
    // private final int neighborCount;
    // private Board[] neighbors = new Board[4];

    private boolean hasnb1 = false;
    private boolean hasnb2 = false;
    private boolean hasnb3 = false;
    private boolean hasnb4 = false;

    private final int dim;
    private int zeroRow;
    private int zeroCol;
    // private int manhP; // P for permanent
    // private int hamP;
    // private String boardStringP;


    public Board(int[][] tilesArg) {
        if (tilesArg == null) throw new IllegalArgumentException("Tile parameter cannot be null.");
        tiles = copyTiles(tilesArg);
        dim = dimension();
        // manhP = manhattan();
        // hamP = hamming();
        // boardStringP = this.onceToString();

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (tiles[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                    break;
                }
            }
        }

        hasnb1 = zeroRow != 0;
        hasnb2 = zeroRow != dim - 1;
        hasnb3 = zeroCol != 0;
        hasnb4 = zeroCol != dim - 1;

        // neighborCount = toI(hasnb1) + toI(hasnb2) + toI(hasnb3) + toI(hasnb4);

        /*
        neighbors[0] = null;
        neighbors[1] = null;
        neighbors[2] = null;
        neighbors[3] = null;
         */
    }

    /*
    private int toI(boolean arg) {
        return (arg ? 1 : 0);
    }

     */

    // string representation of this board
    public String toString() {
        String boardString = Integer.toString(tiles.length) + "\n";
        StringBuilder builder = new StringBuilder();
        builder.append(boardString);
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                builder.append(Integer.toString(tiles[i][j]) + " ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    // board dimension n
    public int dimension() {
        assert tiles != null;
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        int ham = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j] == 0) continue;
                if (i * tiles.length + j + 1 != tiles[i][j]) ham++;
            }
        }
        return ham;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manh = 0;
        int counter = 1;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (tiles[i][j] == 0) {
                    ++counter;
                    continue;
                }
                int currVal = tiles[i][j];
                if (counter != currVal) {
                    int properCol = (currVal % dim == 0) ? dim : currVal % dim;
                    int properRow = (currVal % dim == 0) ? currVal / dim : (currVal / dim) + 1;
                    manh += Math.abs(properRow - (i + 1)) + Math.abs(properCol - (j + 1));
                }
                ++counter;
            }
        }
        return manh;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return (manhattan() == 0);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (this == y) return true;
        if (!(y.getClass().equals(this.getClass()))) return false;
        if (((Board) y).dim != this.dim) return false;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] != ((Board) y).tiles[i][j]) return false;
            }
        }
        return true;
    }

        /* if (boardStringP.equals(boardY.boardStringP)) {
            return true;
        }
        return false;

        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] == null && boardY.neighbors[i] == null) continue;
            if (neighbors[i] == null && boardY.neighbors[i] != null) return false;
            if (neighbors[i] != null && boardY.neighbors[i] == null) return false;
            if (!(neighbors[i].hamP == boardY.neighbors[i].hamP
                    && neighbors[i].manhP == boardY.neighbors[i].manhP
                    && neighbors[i].zeroRow == boardY.neighbors[i].zeroRow
                    && neighbors[i].zeroCol == boardY.neighbors[i].zeroCol)) {
                return false;
            }
        }
        return true;
         */

    private int[][] copyTiles(int[][] tileBoard) {
        int[][] newTiles = new int[tileBoard.length][tileBoard[0].length];
        for (int i = 0; i < tileBoard.length; i++) {
            for (int j = 0; j < tileBoard[0].length; j++) {
                newTiles[i][j] = tileBoard[i][j];
            }
        }
        return newTiles;
    }

    private void exch(int[][] array, int row1, int col1, int row2, int col2) {
        int temp = array[row1][col1];
        array[row1][col1] = array[row2][col2];
        array[row2][col2] = temp;
    }

    private int[][] rexch(int[][] array, int row1, int col1, int row2, int col2) {
        int[][] newArray = copyTiles(array);
        exch(newArray, row1, col1, row2, col2);
        return newArray;
    }


    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> boardStack = new Stack<>();

        if (hasnb1) {
            exch(tiles, zeroRow, zeroCol, zeroRow - 1, zeroCol);
            boardStack.push(new Board(tiles));
            exch(tiles, zeroRow, zeroCol, zeroRow - 1, zeroCol);
        }

        if (hasnb2) {
            exch(tiles, zeroRow, zeroCol, zeroRow + 1, zeroCol);
            boardStack.push(new Board(tiles));
            exch(tiles, zeroRow, zeroCol, zeroRow + 1, zeroCol);
        }

        if (hasnb3) {
            exch(tiles, zeroRow, zeroCol, zeroRow, zeroCol - 1);
            boardStack.push(new Board(tiles));
            exch(tiles, zeroRow, zeroCol, zeroRow, zeroCol - 1);
        }

        if (hasnb4) {
            exch(tiles, zeroRow, zeroCol, zeroRow, zeroCol + 1);
            boardStack.push(new Board(tiles));
            exch(tiles, zeroRow, zeroCol, zeroRow, zeroCol + 1);
        }

        // System.out.println(Arrays.deepToString(neighbors));
        /*
        return new Iterable<Board>() {
            public Iterator<Board> iterator() {
                return new NeighborIterator();
            }
        };
         */

        return boardStack;
    }

    /*
    private class NeighborIterator implements Iterator<Board> {

        private int index = 0;
        private int successCount = 0;

        private Board getCurrent(int idx) {
            if (idx == 0) return nb1;
            else if (idx == 1) return nb2;
            else if (idx == 2) return nb3;
            else return nb4;
        }

        public boolean hasNext() {
            if (successCount < neighborCount) {
                return true;
            }
            return false;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Board next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            while (getCurrent(index) == null) ++index;
            ++successCount;
            return getCurrent(index++);
        }
    }

     */

    // a board that is obtained by exchanging any pair of tiles, not the zero tile
    public Board twin() {
        int firstRow = -1;
        int firstCol = -1;
        int secondRow = -1;
        int secondCol = -1;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (firstRow == -1 && tiles[i][j] != 0) {
                    firstRow = i;
                    firstCol = j;
                    continue;
                }
                if (firstRow != -1 && tiles[i][j] != 0) {
                    secondRow = i;
                    secondCol = j;
                    break;
                }

            }
        }
        return new Board(rexch(tiles, firstRow, firstCol, secondRow, secondCol));
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] test1Tiles = {
                { 1, 0 }, { 3, 2 }
        };
        Board test1 = new Board(test1Tiles);
        StdOut.println("Test board: " + test1);
        for (Board i : test1.neighbors()) {
            StdOut.println(i);
        }

    }

}
