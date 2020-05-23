// import edu.princeton.cs.algs4.MinPQ;

import edu.princeton.cs.algs4.Quick3string;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    // private String[] origSuffixes;
    private String[] sortedSuffixes;
    // private int[] sortedOrder;


    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        // origSuffixes = new String[s.length()];
        sortedSuffixes = new String[s.length()];

        for (int i = 0; i < s.length(); i++) {
            sortedSuffixes[i] = circAtIndex(s, i).concat("_" + i);
        }

        Quick3string.sort(sortedSuffixes);
    }

    /*
    private void printArray(String s, int[] circArray) {
        System.out.println("[");
        for (int i : circArray) {
            System.out.print(circAtIndex(s, i) + ", ");
        }
        System.out.println("]");
    }

     */

    private String circAtIndex(String s, int i) {
        return (s.substring(i)).concat(s.substring(0, i));
    }


    // length of s
    public int length() {
        return sortedSuffixes.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > sortedSuffixes.length - 1) throw new IllegalArgumentException();

        return Integer
                .parseInt(sortedSuffixes[i].substring(sortedSuffixes[i].lastIndexOf('_') + 1));
    }

    // unit testing (required)
    public static void main(String[] args) {
        String str = args[0];
        CircularSuffixArray csa = new CircularSuffixArray(str);
        StdOut.println("For string " + str);
        StdOut.println("Length:  " + csa.length());
        StdOut.println("Index of third sorted in original: " + csa.index(1));
    }

}
