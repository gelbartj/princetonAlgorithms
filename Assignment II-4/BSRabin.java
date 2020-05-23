import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BSRabin {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    // private String[] dictionary;

    private final ModTrieST<Boolean> dictTrie;
    // private String rawDict;

    public BSRabin(String[] dictionary) {
        // this.dictionary = dictionary;

        long startTime = System.nanoTime();
        dictTrie = new ModTrieST<>();

        for (String word : dictionary) {
            dictTrie.put(word, true);
        }
        long endTime = System.nanoTime();

        // System.out.println("Constructor: " + ((endTime - startTime) / 1000000) + "ms");

        // rawDict = String.join(",", dictionary);

        // System.out.println("String conversion: " + ((endTime - startTime) / 1000000) + "ms");
    }

    private boolean inDict(String word, ModTrieST<Boolean> dict) {
        return dict.contains(word);
    }

    private int[][] getNextCharCoords(BoggleBoard board, int[][] wordCoords) {

        int[][] nextCharCoords = new int[8][2];
        int counter = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                boolean usedFlag = false;
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
                    nextCharCoords[counter++] = new int[] { newRow, newCol };
                }

                else nextCharCoords[counter] = null;
                // counter++;
            }
        }
        // System.out.println(Arrays.deepToString(nextCharCoords));
        if (counter == 0) return null;

        int[][] nextCharCoordsNoNull = new int[counter][2];

        for (int i = 0; i < counter; i++) {
            if (nextCharCoords[i] != null)
                nextCharCoordsNoNull[i] = nextCharCoords[i];
            else break;
        }
        return nextCharCoordsNoNull;
    }

    private StringBuilder wordFromCoords(BoggleBoard board, int[][] coordList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coordList.length; i++) {
            char nextChar = board.getLetter(coordList[i][0], coordList[i][1]);
            sb.append(nextChar);
            if (nextChar == 'Q') sb.append('U');
        }
        return sb;
    }

    private String[] wordsFromIdxs(String wordBase, int[][] idxs, BoggleBoard board) {
        String[] words = new String[idxs.length];
        for (int i = 0; i < words.length; i++) {
            words[i] = wordBase + board.getLetter(idxs[i][0], idxs[i][1]);
        }
        return words;
    }

    private void getWordsFromIdx(BoggleBoard board, int[][] startCoords,
                                 ModTrieST<Boolean> wordList) {
        Queue<int[][]> wordCoordsToProcess = new Queue<>();
        wordCoordsToProcess.enqueue(startCoords);

        // Queue<String> wordList = new Queue<>();

        ModTrieST.Node prevNode = null;
        String prevWordBase = "";
        String oldSection = "";
        int sectionIdx = 0;

        while (!wordCoordsToProcess.isEmpty()) {
            // System.out.println("---------");
            long startTime = System.nanoTime();
            int[][] nextWord = wordCoordsToProcess.dequeue();
            StringBuilder newWordBase = wordFromCoords(board, nextWord);

            // System.out.println("Processing " + newWordBase);
            int[][] nextCharIdxs = getNextCharCoords(board, nextWord);
            if (nextCharIdxs == null) continue;

            // String[] nextWords = wordsFromIdxs(newWordBase.toString(), nextCharIdxs, board);

            String section = oldSection;

            if (prevWordBase.length() > 2 && newWordBase.length() > 2 && !prevWordBase
                    .substring(0, 3)
                    .equals(newWordBase.toString()
                                       .substring(0, 3))) {

                long startTime2 = System.nanoTime();
                Iterable<String> sectionObj =
                        dictTrie.keysWithPrefix(
                                newWordBase.toString());
                // prevNode = dictTrie.getNode(newWordBase.toString());
                long endTime2 = System.nanoTime();
                // System.out.println("keysWithPrefix: " + ((endTime2 - startTime2) / 1000000.0) + "ms");

                int keyCounter = 0;

                for (String prefixSection : sectionObj) {
                    if (++keyCounter == 2) {
                        break;
                    }
                }

                if (keyCounter < 2) {
                    // System.out.println("Skipping, no additional words found after " + newWordBase);
                    continue;
                }

                section = sectionObj.toString();
            }

            else {
                prevWordBase = newWordBase.toString();
                sectionIdx = 0;
            }

            // System.out.println(section);

            String[] pats = new String[nextCharIdxs.length];
            int[][][] newWords = new int[nextCharIdxs.length][nextWord.length + 1][2];

            int counter = 0;
            for (int[] nextCharIdx : nextCharIdxs) {
                if (nextCharIdx == null) break;
                int[][] newWord = new int[nextWord.length + 1][2];
                for (int i = 0; i < nextWord.length; i++) {
                    newWord[i] = nextWord[i];
                }
                newWord[newWord.length - 1] = nextCharIdx;
                char nextLetter = board.getLetter(nextCharIdx[0], nextCharIdx[1]);
                String newWordString = newWordBase.append(nextLetter).toString();
                newWordBase.deleteCharAt(newWordBase.length() - 1);

                if (nextLetter == 'Q')
                    newWordString = newWordString.concat("U");

                pats[counter] = newWordString;
                // System.out.println("Added pattern: " + pats[counter]);
                newWords[counter] = newWord;
                counter++;
            }

            long startTime3 = System.nanoTime();
            RabinKarp rb = new RabinKarp(pats, section.length());

            // System.out.println(Arrays.toString(pats));

            int[] matches = rb.search(section);
            long endTime3 = System.nanoTime();
            //
            // System.out.println("RK: " + ((endTime3 - startTime3) / 1000000) + "ms");

            // System.out.println(Arrays.toString(matches));

            for (int j = 0; j < matches.length; j++) {
                // if (matches[j]
                //         > -1) { // should be working since it is only looking for partial match, but it's not...
                // System.out.println("Found RK match");
                wordCoordsToProcess.enqueue(newWords[j]);
                // }

                boolean isInDict = inDict(pats[j], dictTrie);

                // System.out.println("checking if " + pats[j] + " is in dict");

                if (pats[j].length() >= 3 && isInDict
                        && wordList.get(pats[j]) == null) {
                    // System.out.println("Yes!");
                    wordList.put(pats[j], Boolean.TRUE);
                    // System.out.print(newWordString + ": " + wordList.get(newWordString));
                    // System.out.println(Arrays.deepToString(newWord));
                }
                // else System.out.println("No");
            }
            long endTime = System.nanoTime();
            // System.out.println("While loop: " + ((endTime - startTime) / 1000000.0) + "ms");
        }
        // return wordList.keys();
    }


    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        ModTrieST<Boolean> wordList = new ModTrieST<>();
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                // Get all words starting with the (row, col) letter of the board
                int[][] startCoords = new int[1][2];
                startCoords[0] = new int[] { row, col };
                long startTime = System.nanoTime();
                getWordsFromIdx(board, startCoords, wordList);
                long endTime = System.nanoTime();
            }
        }
        return wordList.keys();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        // if word in dictionary
        try {
            if (inDict(word, dictTrie)) {
                int[] scoreByLength = { 0, 0, 0, 1, 1, 2, 3, 5 };
                int wordLen = word.length();
                if (wordLen >= 8) return 11;

                if (word.contains("Q") && !word.contains("QU")) {
                    wordLen += 1;
                }
                return scoreByLength[wordLen];
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        long startTime = System.nanoTime();
        BSRabin solver = new BSRabin(dictionary);
        long endTime = System.nanoTime();
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        StdOut.print("Solved in " + (endTime - startTime) / 1000000.0 + "ms");
    }

}
