import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolverAlt {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    // private String[] dictionary;

    // private final ModTrieST<Boolean> dictTrie;
    private String rawDict;
    private int[] letterIndex;

    public BoggleSolverAlt(String[] dictionary) {

        // System.out.println("Constructor: " + ((endTime - startTime) / 1000000) + "ms");

        rawDict = String.join(",", dictionary);
        rawDict = "," + rawDict + ",";

        letterIndex = new int[26];

        for (int i = 'A'; i <= 'Z'; i++) {
            String charString = "," + Character.toString((char) i);
            letterIndex[i - 'A'] = rawDict.indexOf(charString);
            // System.out.print((char) i + ": ");
            // System.out.println(letterIndex[i - 'A']);
        }

        // System.out.println("String conversion: " + ((endTime - startTime) / 1000000) + "ms");
    }

    private int search(String txt, String pat, int startIdx) {
        int R = 27; // capital letters plus comma
        int M = pat.length();

        int startOffset = startIdx > 0 ? startIdx :
                          letterIndex[pat.charAt(1) - 'A'];

        int endOffset = letterIndex[pat.charAt(1) - 'A' + 1];

        /*
        System.out.println(
                "Pattern: " + pat + ", start letter: " + pat.charAt(1) + ", end letter: " + (char) (
                        pat.charAt(1) + 1));
        System.out.println("start: " + startOffset + ", end: " + endOffset);

         */

        String letterTxt = txt.substring(startOffset,
                                         endOffset);

        int N = letterTxt.length();

        int ASCII_A = 'A';

        // StdOut.println("Building right table");
        int[] right = new int[R];
        for (int c = 0; c < R; c++)
            right[c] = -1;
        for (int j = 0; j < M; j++) {
            if (pat.charAt(j) == ',') right[26] = j;
            else right[pat.charAt(j) - ASCII_A] = j;
        }

        // StdOut.println("Searching for " + pat);
        int skip;
        for (int i = 0; i <= N - M; i += skip) {
            skip = 0;
            for (int j = M - 1; j >= 0; j--) {
                if (pat.charAt(j) != letterTxt.charAt(i + j)) {
                    int modifiedIdx = letterTxt.charAt(i + j) - ASCII_A;
                    if (letterTxt.charAt(i + j) == ',') modifiedIdx = 26;
                    skip = Math.max(1, j - right[modifiedIdx]);
                    break;
                }
            }
            if (skip == 0) return startOffset + i;
        }
        return -1;
    }


    private Queue<int[]> getNextCharCoords(BoggleBoard board, int[][] wordCoords) {

        Queue<int[]> nextCharCoords = new Queue<>();
        int counter = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                boolean usedFlag = false;
                if (i == 0 && j == 0) continue;
                int newRow = wordCoords[wordCoords.length - 1][0] + i;
                int newCol = wordCoords[wordCoords.length - 1][1] + j;
                if (newRow < 0 || newCol < 0 || newRow > board.rows() - 1
                        || newCol > board.cols() - 1) continue;
                for (int[] usedChars : wordCoords) {
                    if (usedChars[0] == newRow && usedChars[1] == newCol) {
                        usedFlag = true;
                        break;
                    }
                }
                if (!usedFlag) {
                    counter++;
                    nextCharCoords.enqueue(new int[] { newRow, newCol });
                }
            }
        }
        if (counter == 0) return null;
        /*
        System.out.println(
                "Starting from " + wordCoords[wordCoords.length - 1][0] + ", " + wordCoords[
                        wordCoords.length - 1][1] + ": ");
        for (int[] coords : nextCharCoords) {
            System.out.print(Arrays.toString(coords));
        }
        System.out.println();

         */
        return nextCharCoords;
    }

    private String wordFromCoords(BoggleBoard board, int[][] coordList) {
        StringBuilder sb = new StringBuilder();
        // char nextChar;
        for (int i = 0; i < coordList.length; i++) {
            char nextChar = board.getLetter(coordList[i][0], coordList[i][1]);
            sb.append(nextChar);
            if (nextChar == 'Q') sb.append('U');
        }
        return sb.toString();
    }

    private Queue<String> getWordsFromIdx(BoggleBoard board, int[][] startCoords) {
        Queue<int[][]> wordCoordsToProcess = new Queue<>();
        wordCoordsToProcess.enqueue(startCoords);

        Queue<String> wordList = new Queue<>();

        int baseIdx = 0;
        String prevWord = "";

        while (!wordCoordsToProcess.isEmpty()) {
            int[][] nextWord = wordCoordsToProcess.dequeue();
            String newWordBase = wordFromCoords(board, nextWord);
            if (newWordBase.substring(0, prevWord.length()).equals(prevWord))
                baseIdx = search(rawDict, "," + newWordBase, baseIdx);
            else baseIdx = search(rawDict, "," + newWordBase, 0);

            /*
            String nextChar = rawDict.substring(baseIdx + newWordBase.length() + 1,
                                                baseIdx + newWordBase.length() + 2);

            String checkNextPrefix = rawDict
                    .substring(baseIdx + newWordBase.length() + 2,
                               baseIdx + newWordBase.length() + 2 + newWordBase.length());

            // StdOut.println("word: " + newWordBase + ", nextprefix: " + checkNextPrefix);

            boolean hasOneMore = (!nextChar.equals(",") || checkNextPrefix.equals(newWordBase));

             */
            boolean hasBase = baseIdx > -1; // dictTrie.hasKeyWithPrefix(newWordBase);

            if (!hasBase) {
                // StdOut.println("Prefix " + newWordBase + " is a dead end. Skipping");
                continue;
            }

            Queue<int[]> nextCharIdxs = getNextCharCoords(board, nextWord);

            if (nextCharIdxs == null) continue;

            for (int[] nextCharIdx : nextCharIdxs) {
                int[][] newWord = new int[nextWord.length + 1][2];
                for (int i = 0; i < nextWord.length; i++) {
                    newWord[i] = nextWord[i];
                }
                newWord[newWord.length - 1] = nextCharIdx;
                char nextLetter = board.getLetter(nextCharIdx[0], nextCharIdx[1]);
                String newWordString = newWordBase
                        .concat(Character.toString(
                                nextLetter));

                if (nextLetter == 'Q')
                    newWordString = newWordString.concat("U");

                int nextIdx = search(rawDict, "," + newWordString, baseIdx);
                if (nextIdx > -1) wordCoordsToProcess.enqueue(newWord);


                boolean isInDict = (nextIdx > -1)
                        && rawDict.charAt(nextIdx + newWordString.length() + 1) == ',';

                if (newWordString.length() >= 3 && isInDict) {
                    wordList.enqueue(newWordString);
                    // System.out.print(newWordString + ": ");
                    // System.out.println(Arrays.deepToString(newWord));
                }
            }
            // System.out.println("Inner loop: " + ((endTime - startTime) / 1000000) + "ms");
        }
        return wordList;
    }


    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Queue<String> wordList = new Queue<>();
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                // Get all words starting with the (row, col) letter of the board
                // boolean[][] usedCharsMap = new boolean[board.rows()][board.cols()];
                int[][] startCoords = new int[1][2];
                startCoords[0] = new int[] { row, col };
                // StdOut.println("Getting words...");
                for (String word : getWordsFromIdx(board, startCoords)) {
                    boolean dupFlag = false;
                    for (String addedWord : wordList) {
                        if (addedWord.equals(word)) {
                            dupFlag = true;
                            break;
                        }
                    }
                    if (!dupFlag) wordList.enqueue(word);
                }
                // System.out.println(
                //         "Full loop for each starting letter: " + ((endTime - startTime) / 1000000)
                //                 + "ms");
            }
        }
        return wordList;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        // if word in dictionary
        if (search(rawDict, "," + word + ",", 0) > -1) {
            int[] scoreByLength = { 0, 0, 0, 1, 1, 2, 3, 5 };
            int wordLen = word.length();
            if (wordLen >= 8) return 11;

            if (word.contains("Q") && !word.contains("QU")) {
                wordLen += 1;
            }

            return scoreByLength[wordLen];
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolverAlt solver = new BoggleSolverAlt(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}
