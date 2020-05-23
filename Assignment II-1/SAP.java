import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph graph;
    // private int anc;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Graph is null");
        graph = G;
    }

    /*
    private int getSource(int v, ST<Integer, Integer> edgeTo) {
        int temp = v;
        while (edgeTo.get(temp) != null) {
            temp = edgeTo.get(temp);
        }
        return temp;
    }

    private Stack<Integer> makePath(int commonAnc, int branchEnd, ST<Integer, Integer> edgeTo) {
        Queue<Integer> ancPath = new Queue<>();
        int v = commonAnc;
        while (edgeTo.get(v) != null) {
            ancPath.enqueue(v);
        }
        Stack<Integer> otherBranch = new Stack<>();
        v = branchEnd;
        while (edgeTo.get(v) != null) {
            otherBranch.push(v);
        }
        Stack<Integer> combined = new Stack<>();
        for (int w : otherBranch) {
            combined.push(w);
        }
        for (int w : ancPath) {
            combined.push(w);
        }
        return combined;
    }

    private Stack<Integer> getAncPath(Digraph G, int first, int second) {
        Queue<Integer> queue = new Queue<>();
        SET<Integer> markedFirst = new SET<>();
        SET<Integer> markedSecond = new SET<>();
        ST<Integer, Integer> edgeTo = new ST<>();
        queue.enqueue(first);
        queue.enqueue(second);
        markedFirst.add(first);
        markedSecond.add(second);
        edgeTo.put(first, null);
        edgeTo.put(second, null);
        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            for (int w : G.adj(v)) {
                if (w == second) {
                    System.out.println("second is an ancestor of first. not supposed to happen...");
                    anc = w;
                    return makePath(w, v, edgeTo);
                    // break;
                }
                if (getSource(v, edgeTo) == first) {
                    if (!markedFirst.contains(w)) {
                        if (markedSecond.contains(w)) {
                            System.out.println("Found ancestor! " + w);
                            anc = w;
                            return makePath(w, v, edgeTo);
                            // break;
                        }
                        markedFirst.add(w);
                        edgeTo.put(w, v);
                        queue.enqueue(w);
                    }
                }
                else if (getSource(v, edgeTo) == second) {
                    if (!markedSecond.contains(w)) {
                        if (markedFirst.contains(w)) {
                            System.out.println("Found ancestor!" + w);
                            anc = w;
                            return makePath(w, v, edgeTo);
                        }
                        markedSecond.add(w);
                        edgeTo.put(w, v);
                        queue.enqueue(w);
                    }
                }
            }
        }
        anc = -1;
        return new Stack<>();
    }
     */

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

        int shortestDist = graph.V();

        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int tempDist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (tempDist < shortestDist) shortestDist = tempDist;
            }
        }
        if (shortestDist == graph.V()) return -1;
        return shortestDist;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

        int shortestDist = graph.V();
        int cachedVertex = -1;

        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int tempDist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (tempDist < shortestDist) {
                    shortestDist = tempDist;
                    cachedVertex = vertex;
                }
            }
        }
        if (shortestDist == graph.V()) return -1;
        return cachedVertex;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Null arguments to SAP");
        for (Integer x : v) {
            if (x == null) throw new IllegalArgumentException();
        }
        for (Integer x : w) {
            if (x == null) throw new IllegalArgumentException();
        }
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

        int shortestDist = graph.V();

        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int tempDist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (tempDist < shortestDist) shortestDist = tempDist;
            }
        }
        if (shortestDist == graph.V()) return -1;
        return shortestDist;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Null arguments to SAP");
        for (Integer x : v) {
            if (x == null) throw new IllegalArgumentException();
        }
        for (Integer x : w) {
            if (x == null) throw new IllegalArgumentException();
        }
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

        int shortestDist = graph.V();
        int cachedVertex = -1;

        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                int tempDist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (tempDist < shortestDist) {
                    shortestDist = tempDist;
                    cachedVertex = vertex;
                }
            }
        }
        if (shortestDist == graph.V()) return -1;
        return cachedVertex;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        StdOut.println(G);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
