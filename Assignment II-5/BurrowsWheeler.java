import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String str = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(str);
        // StringBuilder sb = new StringBuilder();
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
            // sb.append(str.charAt(csa.index(i)));
        }
        // BinaryStdOut.write("\n");
        for (int i = 0; i < csa.length(); i++) {
            BinaryStdOut.write(str.charAt((csa.index(i) + str.length() - 1) % str.length()));
        }
        // BinaryStdOut.write(sb.toString());
        // BinaryStdIn.close();
        BinaryStdOut.close();
    }

    private static int getRadix(char[] t) {
        int radix = 1;
        if (t.length < 1) return 0;
        for (int i = 1; i < t.length; i++) {
            if (t[i - 1] != t[i]) radix++;
        }
        return radix;
    }

    private static void makeNext(char[] t, int nextIdx) {
        int R = 256;
        // getRadix(t);
        // int difference = 256 - R;
        int N = t.length;
        int[] count = new int[R + 1];
        char[] aux = new char[t.length];
        int[] next = new int[t.length];
        for (int i = 0; i < N; i++)
            count[t[i] + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        for (int i = 0; i < N; i++) {
            next[count[t[i]]] = i;
            aux[count[t[i]]++] = t[i];
        }

        int currNext = nextIdx;

        for (int i = 0; i < t.length; i++) {
            BinaryStdOut.write(aux[currNext]);
            currNext = next[currNext];
        }

        /*
        for (int i = 0; i < N; i++)
            a[i] = aux[i];
        */

        /*
        int[] next = new int[first.size()];
        char[] sorted = new char[first.size()];
        // char[] decoded = new char[first.size()];
        boolean[] marked = new boolean[first.size()];

        int counter = 0;
        while (!first.isEmpty()) {
            char nextChar = first.delMin();
            sorted[counter] = nextChar;
            for (int j = 0; j < t.length(); j++) {
                if (nextChar == t.charAt(j)) {
                    if (marked[j]) continue;
                    marked[j] = true;
                    next[counter] = j;
                    break;
                }
            }
            counter++;
        }

        int currNext = nextIdx;

         */


        // decoded[0] = sorted[nextIdx];
        // decoded[decoded.length - 1] = t.charAt(nextIdx);
    }


    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int nextIdx = BinaryStdIn.readInt();
        // StdOut.println(nextIdx);
        // BinaryStdIn.readChar(); // skip newline character
        String transformed = BinaryStdIn.readString();
        char[] t = transformed.toCharArray();

        // MinPQ<Character> first = new MinPQ<>();

        // Arrays.sort(first);

        makeNext(t, nextIdx);


        // BinaryStdIn.close();
        BinaryStdOut.close();
        // BinaryStdOut.write(origString.toString());
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
    }

}
