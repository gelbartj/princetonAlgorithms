/******************************************************************************
 *  Compilation:  javac TrieST.java
 *  Execution:    java TrieST < words.txt
 *  Dependencies: StdIn.java
 *  Data files:   https://algs4.cs.princeton.edu/52trie/shellsST.txt
 *
 *  A string symbol table for extended ASCII strings, implemented
 *  using a 256-way trie.
 *
 *  % java TrieST < shellsST.txt
 *  by 4
 *  sea 6
 *  sells 1
 *  she 0
 *  shells 3
 *  shore 7
 *  the 5
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieST;

/**
 * The {@code TrieST} class represents an symbol table of key-Boolean
 * pairs, with string keys and generic Booleans.
 * It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 * <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods.
 * It also provides character-based methods for finding the string
 * in the symbol table that is the <em>longest prefix</em> of a given prefix,
 * finding all strings in the symbol table that <em>start with</em> a given prefix,
 * and finding all strings in the symbol table that <em>match</em> a given pattern.
 * A symbol table implements the <em>associative array</em> abstraction:
 * when associating a Boolean with a key that is already in the symbol table,
 * the convention is to replace the old Boolean with the new Boolean.
 * Unlike {@link java.util.Map}, this class uses the convention that
 * Booleans cannot be {@code null}—setting the
 * Boolean associated with a key to {@code null} is equivalent to deleting the key
 * from the symbol table.
 * <p>
 * This implementation uses a 256-way trie.
 * The <em>put</em>, <em>contains</em>, <em>delete</em>, and
 * <em>longest prefix</em> operations take time proportional to the length
 * of the key (in the worst case). Construction takes constant time.
 * The <em>size</em>, and <em>is-empty</em> operations take constant time.
 * Construction takes constant time.
 * <p>
 * For additional documentation, see <a href="https://algs4.cs.princeton.edu/52trie">Section 5.2</a>
 * of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class ModTrieST<Boolean> {
    private static final int R = 28;        // capitals only plus comma and space
    private static final int ASCII_A = 'A';


    private Node root;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    public static class Node {
        private Object val;
        // private String key;
        private Node[] next = new Node[R];
        private boolean hasChildren;

        public Node[] getNext() {
            return next;
        }

        public Node getNext(int letterIdx) {
            return next[letterIdx];
        }

        public Object getVal() {
            return val;
        }

        public boolean hasChildren() {
            return hasChildren;
        }

        public String getKey() {
            return ""; // key;
        }
    }

    /**
     * Initializes an empty string symbol table.
     */
    public ModTrieST() {
    }


    /**
     * Returns the Boolean associated with the given key.
     *
     * @param key the key
     * @return the Boolean associated with the given key if the key is in the symbol table
     * and {@code null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Boolean get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Boolean) x.val;
    }

    public Node getNode(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return x;
    }

    /**
     * Does this symbol table contain the given key?
     *
     * @param key the key
     * @return {@code true} if this symbol table contains {@code key} and
     * {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    private int nextIdx(char c) {
        return (c == ',' ? 26 : c == ' ' ? 27 : c - ASCII_A);
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        // System.out.println("key: " + key);
        return get(x.next[nextIdx(c)], key, d + 1);
    }

    public Node getRoot() {
        return root;
    }

    /**
     * Inserts the key-Boolean pair into the symbol table, overwriting the old Boolean
     * with the new Boolean if the key is already in the symbol table.
     * If the Boolean is {@code null}, this effectively deletes the key from the symbol table.
     *
     * @param key the key
     * @param val the Boolean
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(String key, Boolean val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) delete(key);
        else root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Boolean val, int d) {
        if (x == null) x = new Node();
        // x.key = key;
        if (d == key.length()) {
            if (x.val == null) n++;
            x.val = val;
            return x;
        }
        else {
            x.hasChildren = true;
        }
        char c = key.charAt(d);
        x.next[nextIdx(c)] = put(x.next[nextIdx(c)], key, val, d + 1);
        return x;
    }

    /**
     * Returns the number of key-Boolean pairs in this symbol table.
     *
     * @return the number of key-Boolean pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Is this symbol table empty?
     *
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns all keys in the symbol table as an {@code Iterable}.
     * To iterate over all of the keys in the symbol table named {@code st},
     * use the foreach notation: {@code for (Key key : st.keys())}.
     *
     * @return all keys in the symbol table as an {@code Iterable}
     */
    public Iterable<String> keys() {
        return keysWithPrefix("");
    }


    public boolean hasKeyWithPrefix(String prefix) {
        // Queue<String> results = new Queue<String>();
        Node x = get(root, prefix, 0);
        return collectOne(x, new StringBuilder(prefix));
    }

    private boolean collectOne(Node x, StringBuilder prefix) {
        if (x == null) return false;
        if (x.val != null) return true;
        for (char c = 0; c < R; c++) {
            prefix.append(c);
            if (collectOne(x.next[c], prefix)) return true;
            prefix.deleteCharAt(prefix.length() - 1);
        }
        return false;
    }

    /**
     * Returns all of the keys in the set that start with {@code prefix}.
     *
     * @param prefix the prefix
     * @return all of the keys in the set that start with {@code prefix},
     * as an iterable
     */
    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> results = new Queue<String>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    public Iterable<String> keysWithPrefixAndNode(String prefix, Node start) {
        Queue<String> results = new Queue<String>();
        Node x = get(start, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) return;
        if (x.val != null) results.enqueue(prefix.toString());
        for (char c = 0; c < R; c++) {
            prefix.append(c == 26 ? ',' : c == 27 ? ' ' : (char) (c + ASCII_A));
            collect(x.next[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /*
    public Iterable<String> simulKeysWithPrefix(String[] prefixes, ModTrieST<Boolean> wordList) {
        Queue<String> results = new Queue<String>();
        for (String prefix : prefixes) {
            Node x = get(root, prefix, 0);
            simulCollect(x, new StringBuilder(prefix), results);
            return results;
        }
    }

    private void simulCollect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) return;
        if (x.val != null) results.enqueue(prefix.toString());
        for (char c = 0; c < R; c++) {
            prefix.append(c == 26 ? ',' : c == 27 ? ' ' : (char) (c + ASCII_A));
            collect(x.next[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

     */

    /**
     * Returns all of the keys in the symbol table that match {@code pattern},
     * where . symbol is treated as a wildcard character.
     *
     * @param pattern the pattern
     * @return all of the keys in the symbol table that match {@code pattern},
     * as an iterable, where . is treated as a wildcard character.
     */

    public boolean hasKeyThatMatches(String pattern) {
        return collectOne(root, new StringBuilder(), pattern);
    }

    private boolean collectOne(Node x, StringBuilder prefix, String pattern) {
        if (x == null) return false;
        int d = prefix.length();
        if (d == pattern.length() && x.val != null)
            return true;
        if (d >= pattern.length())
            return false;
        char c = pattern.charAt(d);
        if (c == '.') {
            for (char ch = 0; ch < R; ch++) {
                // StdOut.println("adding " + (ch + ASCII_A));
                prefix.append(ch == 26 ? ',' : ch == 27 ? ' ' : (char) (ch + ASCII_A));
                // StdOut.println(prefix);
                if (collectOne(x.next[ch], prefix, pattern)) return true;
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        else {
            prefix.append(c);
            if (collectOne(x.next[nextIdx(c)], prefix, pattern)) return true;
            prefix.deleteCharAt(prefix.length() - 1);
        }
        return false;
    }

    public Iterable<String> keysThatMatch(String pattern) {
        Queue<String> results = new Queue<String>();
        collect(root, new StringBuilder(), pattern, results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, String pattern, Queue<String> results) {
        if (x == null) return;
        int d = prefix.length();
        if (d == pattern.length() && x.val != null)
            results.enqueue(prefix.toString());
        if (d == pattern.length())
            return;
        char c = pattern.charAt(d);
        if (c == '.') {
            for (char ch = 0; ch < R; ch++) {
                prefix.append(ch == 26 ? ',' : ch == 27 ? ' ' : (char) (ch + ASCII_A));
                collect(x.next[ch], prefix, pattern, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        else {
            prefix.append(c);
            collect(x.next[nextIdx(c)], prefix, pattern, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Returns the string in the symbol table that is the longest prefix of {@code query},
     * or {@code null}, if no such string.
     *
     * @param query the query string
     * @return the string in the symbol table that is the longest prefix of {@code query},
     * or {@code null} if no such string
     * @throws IllegalArgumentException if {@code query} is {@code null}
     */
    public String longestPrefixOf(String query) {
        if (query == null)
            throw new IllegalArgumentException("argument to longestPrefixOf() is null");
        int length = longestPrefixOf(root, query, 0, -1);
        if (length == -1) return null;
        else return query.substring(0, length);
    }

    // returns the length of the longest string key in the subtrie
    // rooted at x that is a prefix of the query string,
    // assuming the first d character match and we have already
    // found a prefix match of given length (-1 if no such match)
    private int longestPrefixOf(Node x, String query, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == query.length()) return length;
        char c = query.charAt(d);
        return longestPrefixOf(x.next[nextIdx(c)], query, d + 1, length);
    }

    /**
     * Removes the key from the set if the key is present.
     *
     * @param key the key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void delete(String key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            if (x.val != null) n--;
            x.val = null;
        }
        else {
            char c = key.charAt(d);
            x.next[nextIdx(c)] = delete(x.next[nextIdx(c)], key, d + 1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (x.val != null) return x;
        for (int c = 0; c < R; c++)
            if (x.next[c] != null)
                return x;
        return null;
    }

    /**
     * Unit tests the {@code TrieST} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        // build symbol table from standard input
        TrieST<Integer> st = new TrieST<Integer>();
        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            st.put(key, i);
        }

        // print results
        if (st.size() < 100) {
            StdOut.println("keys(\"\"):");
            for (String key : st.keys()) {
                StdOut.println(key + " " + st.get(key));
            }
            StdOut.println();
        }

        StdOut.println("longestPrefixOf(\"shellsort\"):");
        StdOut.println(st.longestPrefixOf("shellsort"));
        StdOut.println();

        StdOut.println("longestPrefixOf(\"quicksort\"):");
        StdOut.println(st.longestPrefixOf("quicksort"));
        StdOut.println();

        StdOut.println("keysWithPrefix(\"shor\"):");
        for (String s : st.keysWithPrefix("shor"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysThatMatch(\".he.l.\"):");
        for (String s : st.keysThatMatch(".he.l."))
            StdOut.println(s);
    }
}
