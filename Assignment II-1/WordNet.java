import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Stack;

public class WordNet {
    private final ST<String, Stack<Integer>> nounIndex = new ST<>();
    private final ST<Integer, String[]> synsByInt = new ST<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        In synsetsVar = new In(synsets);
        String[] synArray = synsetsVar.readAllLines();
        In hyperVar = new In(hypernyms);
        String[] hypArray = hyperVar.readAllLines();

        for (String syn : synArray) {
            String[] vals = syn.split(",");
            String[] nouns = vals[1].split(" ");
            synsByInt.put(Integer.parseInt(vals[0]), nouns);
            for (String noun : nouns) {
                Stack<Integer> currIdxs = nounIndex.get(noun);
                // Stack<Integer> newIdxs;
                if (currIdxs == null) {
                    currIdxs = new Stack<Integer>();
                    currIdxs.push(Integer.parseInt(vals[0]));
                }
                else {
                    currIdxs.push(Integer.parseInt(vals[0]));
                }
                nounIndex.put(noun, currIdxs);
            }
        }
        // if (hypArray[0].split(",").length > 2)
        //    throw new IllegalArgumentException("Cannot have two roots");
        // private ST<Integer, int[]> hyps = new ST<>();
        Digraph graph = new Digraph(synsByInt.size());
        for (String hyp : hypArray) {
            String[] idAndVals = hyp.split(",", 2);
            int hypId = Integer.parseInt(idAndVals[0]);
            String[] vals = idAndVals.length > 1 ? idAndVals[1].split(",") : new String[] { };
            int[] intVals = new int[vals.length];
            for (int i = 0; i < vals.length; i++) {
                intVals[i] = Integer.parseInt(vals[i]);
            }
            for (int hyper : intVals) {
                graph.addEdge(hypId, hyper);
            }
        }

        DepthFirstOrder dfo = new DepthFirstOrder(graph);

        if (dfo.hasCycle())
            throw new IllegalArgumentException("Found a cycle. Must be DAG.");

        sap = new SAP(graph);
    }

    private class DepthFirstOrder {
        private boolean[] marked;
        private final Stack<Integer> reversePost;
        private boolean cycleFlag = false;

        public DepthFirstOrder(Digraph G) {
            reversePost = new Stack<Integer>();
            marked = new boolean[G.V()];
            for (int v = 0; v < G.V(); v++) {
                if (!marked[v]) dfs(G, v);
                if (cycleFlag) break;
            }
        }

        private boolean contains(Stack<Integer> stack, int val) {
            for (int loop : stack) {
                if (val == loop) return true;
            }
            return false;
        }

        private void dfs(Digraph G, int v) {
            marked[v] = true;
            for (int w : G.adj(v)) {
                if (!marked[w]) dfs(G, w);
                else {
                    // vertex has been marked but not returned
                    if (!contains(reversePost, w)) cycleFlag = true;
                    break; // to avoid infinite loop
                }
            }
            reversePost.push(v);
        }

        public Iterable<Integer> reversePost() {
            return reversePost;
        }

        public boolean hasCycle() {
            return cycleFlag;
        }
    }


    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounIndex.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return nounIndex.get(word) != null;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Entered null arguments or words not in dictionary");

        return sap.length(nounIndex.get(nounA), nounIndex.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        return String
                .join(" ", synsByInt.get(sap.ancestor(nounIndex.get(nounA), nounIndex.get(nounB))));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet test = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.println("Finished init");
        // WordNet test = new WordNet(args[0], args[1]);
        System.out.printf("Shortest common ancestor between %s and %s: %s", args[0], args[1],
                          test.sap(args[0], args[1]));
        System.out.println();
        System.out.printf("Distance: %d", test.distance(args[0], args[1]));
    }
}
