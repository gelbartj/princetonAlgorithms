import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    // private String[] dictionary;

    private final ModTrieST<Boolean> dictTrie;
    private boolean[][] visited;
    // private String rawDict;

    public BoggleSolver(String[] dictionary) {
        // this.dictionary = dictionary;
        // long startTime = System.nanoTime();

        dictTrie = new ModTrieST<>();

        for (String word : dictionary) {
            dictTrie.put(word, Boolean.TRUE);
        }

        // long endTime = System.nanoTime();

        // System.out.println("Constructor: " + ((endTime - startTime) / 1000000.0) + "ms");

        // rawDict = String.join(",", dictionary);

        // System.out.println("String conversion: " + ((endTime - startTime) / 1000000) + "ms");
    }

    private boolean inDict(String word, ModTrieST<Boolean> dict) {
        return dict.contains(word);
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

        return nextCharCoords;
    }

    private Queue<int[]> getNextCharFromSingle(BoggleBoard board, int[] letterCoords) {

        Queue<int[]> nextCharCoords = new Queue<>();
        int counter = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // boolean usedFlag = false;
                if (i == 0 && j == 0) continue;
                int newRow = letterCoords[0] + i;
                int newCol = letterCoords[1] + j;

                if (newRow < 0 || newCol < 0 || newRow > board.rows() - 1
                        || newCol > board.cols() - 1) continue;
                // System.out.println("Starting from: " + letterCoords[0] + ", " + letterCoords[1]);
                // System.out.println("Next letter coords: " + newRow + ", " + newCol);
                // System.out.println("Checking next letter: " + board.getLetter(newRow, newCol));
                if (visited[newRow][newCol]) {
                    // System.out.println("SKIPPING " + board.getLetter(newRow, newCol));
                    continue;
                }

                // if (!usedFlag) {
                counter++;
                nextCharCoords.enqueue(new int[] { newRow, newCol });
                // }
            }
        }
        if (counter == 0) return null;

        return nextCharCoords;
    }

    private String wordFromCoords(BoggleBoard board, int[][] coordList) {
        StringBuilder sb = new StringBuilder();
        char nextChar;
        for (int i = 0; i < coordList.length; i++) {
            nextChar = board.getLetter(coordList[i][0], coordList[i][1]);
            sb.append(nextChar);
            if (nextChar == 'Q') sb.append('U');
        }
        return sb.toString();
    }

    private Iterable<String> getWordsFromIdx(BoggleBoard board, int[][] startCoords,
                                             ModTrieST<Boolean> wordList) {
        Queue<int[][]> wordCoordsToProcess = new Queue<>();
        wordCoordsToProcess.enqueue(startCoords);

        // Queue<String> wordList = new Queue<>();

        while (!wordCoordsToProcess.isEmpty()) {
            // long startTime = System.nanoTime();
            int[][] nextWord = wordCoordsToProcess.dequeue();
            String newWordBase = wordFromCoords(board, nextWord);

            /*
            boolean hasNext = dictTrie
                    .hasKeyWithPrefix(newWordBase); // && next word has more letters but same prefix

            if (!hasNext)
                continue;
             */

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

                if (dictTrie.hasKeyWithPrefix(newWordString))
                    wordCoordsToProcess.enqueue(newWord);

                boolean isInDict = inDict(newWordString, dictTrie);

                if (newWordString.length() >= 3 && isInDict
                        && wordList.get(newWordString) == null) {
                    wordList.put(newWordString, true);
                    // System.out.print(newWordString + ": " + wordList.get(newWordString));
                    // System.out.println(Arrays.deepToString(newWord));
                }
            }
            // long endTime = System.nanoTime();
            // System.out.println("Inner loop: " + ((endTime - startTime) / 1000000.0) + "ms");
        }
        return wordList.keys();
    }

    private void getWordsDFS(BoggleBoard board, int[] letterCoords,
                             ModTrieST<Boolean> wordList, StringBuilder word,
                             ModTrieST.Node startNode) {

        // System.out.println("----------");
        Queue<int[]> nextCharIdxs = getNextCharFromSingle(board, letterCoords);

        if (nextCharIdxs == null || startNode == null || !startNode.hasChildren()) {
            // System.out.println("Ending recursive call");
            return;
        }


        // System.out.println("Processing " + word.toString());
        // System.out.println("Next values: " + Arrays.toString(startNode.getNext()));

        // String words = dictTrie.keysThatMatch(newWordBase + ".").toString();

        // long startTime = System.nanoTime();
        for (int[] nextCharIdx : nextCharIdxs) {

            visited[nextCharIdx[0]][nextCharIdx[1]] = true;

            char nextLetter = board.getLetter(nextCharIdx[0], nextCharIdx[1]);
            word.append(nextLetter);


            // System.out.println("Processing neighbor: " + word.toString());

            ModTrieST.Node nextNode = startNode.getNext(nextLetter - 'A');
            if (nextLetter == 'Q') {
                word.append('U');
                nextNode = nextNode != null ? nextNode.getNext('U' - 'A') : null;
            }
            // System.out.println(nextNode.getVal());

            boolean isInDict = word.length() >= 3 && (nextNode != null
                    && nextNode.getVal() != null);


            if (isInDict
                    && wordList.get(word.toString()) == null) {
                wordList.put(word.toString(), Boolean.TRUE);
                // System.out.print(newWordString + ": " + wordList.get(newWordString));
                // System.out.println(Arrays.deepToString(newWord));
            }

            if (nextNode != null) {
                // System.out.println("Found next key: " + nextNode.getKey());
                // System.out.println(Arrays.toString(startNode.getNext()));
                // System.out.println("Starting recursive call");
                getWordsDFS(board, nextCharIdx, wordList, word, nextNode);
            }

            visited[nextCharIdx[0]][nextCharIdx[1]] = false;

            if (word.length() > 0) {
                word.deleteCharAt(word.length() - 1);
                if (nextLetter == 'Q') word.deleteCharAt(word.length() - 1);
            }
        }
        // long endTime = System.nanoTime();
        // System.out.println("Inner loop: " + ((endTime - startTime) / 1000000.0) + "ms");
    }

    private void resetArray(boolean[][] array) {
        for (int row = 0; row < array.length; row++) {
            for (int col = 0; col < array[0].length; col++) {
                array[row][col] = false;
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        ModTrieST<Boolean> wordList = new ModTrieST<>();
        int[] letterCoords = new int[2];
        visited = new boolean[board.rows()][board.cols()];

        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                // Get all words starting with the (row, col) letter of the board
                // int[][] startCoords = new int[1][2];
                // startCoords[0] = new int[] { row, col };
                // long startTime = System.nanoTime();
                resetArray(visited);
                letterCoords[0] = row;
                letterCoords[1] = col;
                visited[row][col] = true;
                // System.out.println("Status of 0,0: " + visited[0][0]);
                StringBuilder sb = new StringBuilder();
                char startLetter = board.getLetter(row, col);
                sb.append(startLetter);
                if (startLetter == 'Q') sb.append('U');
                // getWordsFromIdx(board, startCoords, wordList);
                // System.out.println("Starting with " + sb.toString());
                getWordsDFS(board, letterCoords, wordList, sb, dictTrie.getNode(sb.toString()));
                // long endTime = System.nanoTime();
                // StdOut.println("Completed letter " + startLetter + " in "
                //                        + (endTime - startTime) / 1000000.0 + "ms");
            }
        }
        return wordList.keys();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        // if word in dictionary

        if (inDict(word, dictTrie)) {
            int[] scoreByLength = { 0, 0, 0, 1, 1, 2, 3, 5 };
            int wordLen = word.length();

            if (word.contains("Q") && !word.contains("QU")) {
                wordLen += 1;
            }

            if (wordLen >= 8) return 11;

            return scoreByLength[wordLen];
        }


        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        long startTime = System.nanoTime();
        BoggleSolver solver = new BoggleSolver(dictionary);
        long endTime = System.nanoTime();
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        long scoreStart = System.nanoTime();
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        long scoreEnd = System.nanoTime();
        StdOut.println("Score = " + score + " in " + (scoreEnd - scoreStart) / 1000000.0 + "ms");
        StdOut.print("Solved in " + (endTime - startTime) / 1000000.0 + "ms");
    }

}
