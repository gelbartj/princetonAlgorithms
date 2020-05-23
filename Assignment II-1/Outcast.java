import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {       // constructor takes a WordNet object
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {  // given an array of WordNet nouns, return an outcast
        String outcast = nouns[0];
        int largestDist = 0;

        for (int i = 0; i < nouns.length; i++) {
            int currDist = 0;
            for (int j = 0; j < nouns.length; j++) {
                currDist += wordnet.distance(nouns[i], nouns[j]);
            }
            if (currDist > largestDist) {
                largestDist = currDist;
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast outcast = new Outcast(wordnet);
        // for (int t = 2; t < args.length; t++) {
        In in = new In(args[0]);
        String[] nouns = in.readAllStrings();
        StdOut.println(args[0] + ": " + outcast.outcast(nouns));
        // }
    }
}
