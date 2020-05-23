import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {

    // private Board[] solutions = new Board[10];
    private boolean solvable = true;
    private SearchNode solution;
    private int nodeCount = 0;

    private class SearchNode {
        private final Board board;
        private final int moves;
        private final SearchNode prevNode;
        private final int manhattan;
        // private int hamming;

        public SearchNode(Board board, int moves, SearchNode prevNode) {
            this.board = board;
            this.moves = moves;
            this.prevNode = prevNode;
            this.manhattan = board.manhattan();
        }

    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Constructor argument cannot be null");
        // System.out.println("Initial board: " + initial.toString());
        // System.out.println("Twin: " + initial.twin().toString());
        // System.out.println("Manhattan " + initial.twin().manhattan());
        // System.out.print("Starting with ");
        // System.out.print(initial);
        solvable = solve(initial);
    }

    /*
    private MinPQ<SearchNode> trim(MinPQ<SearchNode> original, int newSize) {
        MinPQ<SearchNode> newPQ = new MinPQ<>(compareNodes());
        System.out.println("Starting with original size: " + original.size());
        for (int counter = 0; counter < newSize; counter++) {
            SearchNode currNode = (!original.isEmpty() ? original.delMin() : null);
            if (currNode == null) break;
            newPQ.insert(currNode);
            // System.out.println("Inserting: " + currNode.board);
            int childCounter = 0;

            while (currNode.prevNode != null) {
                newPQ.insert(currNode.prevNode);
                currNode = currNode.prevNode;
                // System.out.println(++childCounter + ": Inserting previous nodes");
            }


        }
        // System.out.println("new size is: " + newPQ.size());
        return newPQ;
    }
                */

    private Comparator<SearchNode> compareNodes() {
        return new Comparator<SearchNode>() {
            public int compare(SearchNode a, SearchNode b) {
                /* System.out.println(
                        "Comparing a with manhattan " + Integer.toString(a.manhattan) + ":");
                System.out.print(a.board);
                System.out.println("To b with manhattan " + Integer.toString(b.manhattan) + ":");
                System.out.print(b.board);
                System.out.println("a.moves + a.manhattan - ... = " + Integer
                        .toString(a.moves + a.manhattan - (b.moves + b.manhattan))); */
                return (getPriority(a) - getPriority(b));
            }
        };
    }

    private int getPriority(SearchNode sn) {
        return sn.moves + sn.manhattan;
    }

    private boolean solve(Board initialBoard) {
        MinPQ<SearchNode> pq = new MinPQ<>(compareNodes());
        MinPQ<SearchNode> twinpq = new MinPQ<>(compareNodes());
        pq.insert(new SearchNode(initialBoard, 0, null));
        twinpq.insert(new SearchNode(initialBoard.twin(), 0, null));
        SearchNode lowestNode = pq.delMin();
        SearchNode lowestTwinNode = twinpq.delMin();
        boolean foundTwin = false;
        // solutions[0] = lowestNode.board;
        int counter = 0;


        while (!lowestNode.board.isGoal()) {
            // System.out.println("Checking neighbors for:");
            // System.out.print(lowestNode.board);
            // System.out.print("While count: " + Integer.toString(counter) + "\n---\n");

            for (Board neighbor : lowestNode.board.neighbors()) {
                /*
                System.out.print("Neighbor: " + neighbor);
                System.out.println("Had manhattan: " + Integer.toString(neighbor.manhattan()));
                System.out.println("Had moves: " + Integer.toString(counter + 1) + "\n");
                */
                if (lowestNode.prevNode != null && neighbor.equals(lowestNode.prevNode.board)) {
                    // System.out.println("Skipping duplicate");
                    continue;
                }
                SearchNode tentative = new SearchNode(neighbor, lowestNode.moves + 1, lowestNode);
                // System.out.println("Tentative: " + tentative.moves);
                // if (!pq.isEmpty()) System.out.println("Min: " + pq.min().moves);
                // System.out.println("Lowestnode: " + lowestNode.moves);
                // if (pq.isEmpty() || (getPriority(tentative) <= (getPriority(lowestNode) + 2) ||
                //         (getPriority(tentative) <= (getPriority(pq.min()) + 2)))) {
                // System.out.println("Inserting node with priority: " + getPriority(tentative));
                pq.insert(tentative);
                // }

            }
            // System.out.println("pq min: " + pq.min());
            lowestNode = pq.delMin();

            /*
            System.out.print("Just deleted :");
            System.out.print(lowestNode.board);
            System.out.println("Had manhattan: " + Integer.toString(lowestNode.manhattan));
            System.out.println("Had moves: " + Integer.toString(lowestNode.moves) + "\n");
            System.out.println("--------");
            */

            if (lowestTwinNode != null && lowestTwinNode.board.isGoal()) foundTwin = true;
            if (!foundTwin) {
                assert lowestTwinNode != null;
                for (Board neighbor2 : lowestTwinNode.board.neighbors()) {
                    if (lowestTwinNode.prevNode != null && neighbor2
                            .equals(lowestTwinNode.prevNode.board))
                        continue;
                    SearchNode twinTentative = new SearchNode(neighbor2, lowestTwinNode.moves + 1,
                                                              lowestTwinNode);
                    // if (compareNodes().compare(twinTentative, lowestTwinNode) <= 0) {
                    twinpq.insert(twinTentative);
                    // }

                }
                lowestTwinNode = twinpq.delMin();
            }
            if (foundTwin && !lowestNode.board.isGoal()) return false;

            /*
            if (++counter >= solutions.length - 1) {
                solutions = resizeArray(solutions);
            }
            solutions[counter - 1] = lowestNode.board;
             */
            // if (counter % 100000 == 0) System.out.println(counter);
            /*
            if (++counter % 50000 == 0) {
                // pq = trim(pq, 5000);
                // twinpq = trim(twinpq, 5000);
                // break;
            }

             */
            ++counter;
        }
        solution = lowestNode;
        nodeCount = counter;
        return true;
        // System.out.println(lowestNode.board.toString());
    }

    /*
    private Board[] resizeArray(Board[] arrayArg) {
        Board[] newArray = new Board[arrayArg.length * 2];
        for (int i = 0; i < arrayArg.length; i++) {
            newArray[i] = arrayArg[i];
        }
        return newArray;
    }

     */

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        if (!this.isSolvable()) return -1;

        SearchNode tempNode = solution.prevNode;

        int moveCount = 0;

        if (tempNode == null) return 0;
        while (tempNode.prevNode != null) {
            tempNode = tempNode.prevNode;
            ++moveCount;
        }
        return moveCount + 1;
        /*
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null) return i;
        }
        return solutions.length;
         */
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (!this.isSolvable()) return null;

        Stack<Board> finalStack = new Stack<>();
        SearchNode currNode = solution;
        finalStack.push(currNode.board);
        while (currNode.prevNode != null) {
            currNode = currNode.prevNode;
            finalStack.push(currNode.board);
        }

        return finalStack;
        /*
        return new Iterable<Board>() {
            public Iterator<Board> iterator() {
                return new BoardIterator();
            }
        };

         */
    }

    /*
    private class BoardIterator implements Iterator<Board> {
        private int index = moves();

        public boolean hasNext() {
            // System.out.println("INdex: " + index);
            if (index >= 0) {
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
            SearchNode currNode = solution;
            // StdOut.print("Internal counter: " + Integer.toString(index) + "\n");
            for (int k = 0; k < index; k++) {
                currNode = currNode.prevNode;
            }
            --index;
            return currNode.board;
        }
    }

     */

    // test client (see below)


    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println(
                    "Minimum number of moves = " + solver.moves() + ", found in " + solver.nodeCount
                            + " searches");
            // int newCounter = 0;
            for (Board board : solver.solution()) {
                StdOut.println(board);
                // StdOut.print("newCounter: " + Integer.toString(newCounter++) + "\n");
            }
        }
    }
}
