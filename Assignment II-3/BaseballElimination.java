/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final int numTeams;
    private final int[] winsTable;
    private final int[] lossesTable;
    private final int[] remainingTable;
    private final int[][] remainingGamesTable;
    private boolean[] isEliminated;
    private final ST<String, Integer> teams;
    private final String[] teamsArray;
    private String[][] certificate;

    public BaseballElimination(
            String filename) {                   // create a baseball division from given filename in format specified below

        In file = new In(filename);
        numTeams = Integer.parseInt(file.readLine());

        winsTable = new int[numTeams];
        lossesTable = new int[numTeams];
        remainingTable = new int[numTeams];
        remainingGamesTable = new int[numTeams][numTeams];
        isEliminated = new boolean[numTeams];
        teams = new ST<String, Integer>();
        teamsArray = new String[numTeams];
        certificate = new String[numTeams][numTeams - 1];

        int counter = 0;
        while (file.hasNextLine()) {
            String rawLine = file.readLine();
            while (Character.compare(rawLine.charAt(0), ' ') == 0) {
                rawLine = rawLine.substring(1);
            }
            String[] lineData = rawLine.split("\\s+");
            teams.put(lineData[0], counter);
            teamsArray[counter] = lineData[0];
            winsTable[counter] = Integer.parseInt(lineData[1]);
            lossesTable[counter] = Integer.parseInt(lineData[2]);
            remainingTable[counter] = Integer.parseInt(lineData[3]);
            int[] remGamesArray = new int[numTeams];
            for (int i = 4; i < lineData.length; i++) {
                remGamesArray[i - 4] = Integer.parseInt(lineData[i]);
            }
            remainingGamesTable[counter++] = remGamesArray;
        }
        if (numTeams <= 1) return;

        // StdOut.print(Arrays.deepToString(remainingGamesTable));
        for (String team : teams.keys()) {
            FlowNetwork fn = makeFN(team);
            if (fn != null) {
                int gamePairs = binomialCoeff(numTeams - 1, 2);
                FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);
                counter = 0;

                /*
                for (FlowEdge edge : fn.edges())
                    StdOut.printf("Edge: %d->%d, %f / %f\n", edge.from(), edge.to(),
                                  edge.flow(),
                                  edge.capacity());
                StdOut.println("-----------------");

                 */

                for (FlowEdge edge : fn.adj(0)) {
                    /*
                    StdOut.printf("Edge: %d->%d, %f / %f\n", edge.from(), edge.to(),
                                  edge.flow(),
                                  edge.capacity());
                    */
                    if (edge.flow() < edge.capacity()) {
                        isEliminated[teams.get(team)] = true;
                        for (FlowEdge edge2 : fn.adj(fn.V() - 1)) {
                            if (ff.inCut(edge2.from())) {
                                StdOut.println("Counter: " + counter);
                                StdOut.println("Team idx: " + (edge2.from() - gamePairs - 1));
                                certificate[teams.get(team)][counter++] = teamsArray[edge2.from()
                                        - gamePairs - 1];
                            }
                        }
                        break;
                    }

                }

            }
        }
    }

    private int binomialCoeff(int n, int k) {
        // Base Cases
        if (k == 0 || k == n)
            return 1;

        // Recur
        return binomialCoeff(n - 1, k - 1) +
                binomialCoeff(n - 1, k);
    }

    private FlowNetwork makeFN(String team) {
        int gamePairs = binomialCoeff(numTeams - 1, 2);
        int numVertices = 1 + gamePairs + (numTeams - 1) + 1 + 1;
        int teamIdx = teams.get(team);
        FlowNetwork fn = new FlowNetwork(numVertices);
        int remainingGames = wins(team) + remaining(team);

        for (int k = 0; k < numTeams; k++) {
            if (k == teamIdx) continue;
            if (remainingGames - wins(teamsArray[k]) < 0) {
                isEliminated[teamIdx] = true;
                certificate[teamIdx][0] = teamsArray[k];
                return null;
            }
            // StdOut.printf("Adding edge from %d to %d\n", gamePairs + k, numVertices - 1);
            fn.addEdge(new FlowEdge(1 + gamePairs + k, numVertices - 1,
                                    remainingGames - wins(teamsArray[k])));
        }

        int counter = 1;
        for (int i = 0; i < numTeams; i++) {
            if (i == teams.get(team)) continue;
            for (int j = i; j < numTeams; j++) {
                if (j == teams.get(team) || i == j) continue;
                fn.addEdge(new FlowEdge(0, counter, against(teamsArray[i], teamsArray[j])));
                // StdOut.printf("Adding edge from %d to %d\n", 0, counter);
                fn.addEdge(new FlowEdge(counter, 1 + gamePairs + i, Double.POSITIVE_INFINITY));
                // StdOut.printf("Adding edge from %d to %d\n", counter, gamePairs + i);
                fn.addEdge(new FlowEdge(counter, 1 + gamePairs + j, Double.POSITIVE_INFINITY));
                // StdOut.printf("Adding edge from %d to %d\n", counter, gamePairs + j);
                counter++;
            }
        }
        // StdOut.println("Final counter: " + counter);
        // StdOut.println("Compared to gamePairs: " + gamePairs);
        return fn;
    }

    public int numberOfTeams() {                   // number of teams
        return numTeams;
    }

    public Iterable<String> teams() {                            // all teams
        return teams.keys();
    }

    public int wins(String team) {                   // number of wins for given team
        return winsTable[teams.get(team)];
    }

    public int losses(String team) {                 // number of losses for given team
        return lossesTable[teams.get(team)];
    }

    public int remaining(String team) {              // number of remaining games for given team
        return remainingTable[teams.get(team)];
    }

    public int against(String team1,
                       String team2) {    // number of remaining games between team1 and team2
        return remainingGamesTable[teams.get(team1)][teams.get(team2)];
    }

    public boolean isEliminated(String team) {           // is given team eliminated?
        return isEliminated[teams.get(team)];
    }

    public Iterable<String> certificateOfElimination(
            String team) { // subset R of teams that eliminates given team; null if not eliminated
        Stack<String> certStack = new Stack<>();
        String[] certArray = certificate[teams.get(team)];
        if (certArray[0] == null) return null;
        for (int i = 0; i < certArray.length; i++) {
            if (certArray[i] != null) certStack.push(certArray[i]);
        }
        return certStack;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
