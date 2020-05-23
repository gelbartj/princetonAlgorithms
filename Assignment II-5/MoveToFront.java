import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {

    private static final int R = 256;
    private static final LinkedList<Character> LL = new LinkedList<>();

    private static void makeInitialList() {
        LL.clear();
        for (int i = 0; i < R; i++) {
            LL.add((char) i);
        }
    }

    private static void updateList(int i) {
        char c = LL.get(i);
        LL.remove(i);
        LL.addFirst(c);
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        makeInitialList();
        while (!BinaryStdIn.isEmpty()) {
            char nextChar = BinaryStdIn.readChar();
            // StdOut.print("Encoding " + nextChar);
            int charIdx = LL.indexOf(nextChar);
            BinaryStdOut.write(charIdx, 8);
            updateList(charIdx);
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        makeInitialList();
        while (!BinaryStdIn.isEmpty()) {
            int nextIdx = BinaryStdIn.readInt(8);
            char nextChar = LL.get(nextIdx);
            BinaryStdOut.write(nextChar);
            updateList(nextIdx);
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }

}
