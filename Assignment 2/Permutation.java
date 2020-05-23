import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {

    public static void main(String[] args) {
        int k;
        // String[] strings;
        String input;
        RandomizedQueue<String> rq = new RandomizedQueue<>();

        if (args.length > 0) {
            k = Integer.parseInt(args[0]);
            int probCounter = 0;
            // StdOut.println("Before while loop");
            while (!StdIn.isEmpty()) {
                input = StdIn.readString();
                if (input.equals("STOP")) break;
                // StdOut.println("Input is " + input);
                double prob = StdRandom.uniform(0.0, 1.0);
                if (prob >= 0.5 && probCounter < k) {
                    rq.enqueue(input);
                }
            }
            // StdOut.println("After while loop");
            int printCounter = 0;
            for (String j : rq) {
                if (printCounter < k) {
                    StdOut.println(j);
                    ++printCounter;
                }
                else break;
            }
        }


    }
}
