import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    private Node first = null;
    private Node last = null;
    private int deqSize;

    // construct an empty deque
    public Deque() {
        deqSize = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return (deqSize == 0) || (first == null);
    }

    // return the number of items on the deque
    public int size() {
        return deqSize;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        first.prev = null;
        if (oldFirst != null) {
            oldFirst.prev = first;
        }
        ++deqSize;
        if (deqSize == 1) {
            last = first;
        }
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node oldLast = last;
        last = new Node();
        last.item = item;
        if (oldLast != null) {
            oldLast.next = last;
            last.prev = oldLast;
        }
        ++deqSize;
        if (deqSize == 1) {
            first = last;
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        Node oldFirst = first;
        first = first.next;
        if (first != null) first.prev = null;
        --deqSize;
        return oldFirst.item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        // StdOut.println(deqSize);
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        Node oldLast = last;
        if (oldLast.prev != null) last = oldLast.prev;
        else last = first;
        last.next = null;
        --deqSize;
        return oldLast.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = current.item;
            current = current.next;
            return item;
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deqTest = new Deque<String>();
        StdOut.println(deqTest);
        StdOut.println(deqTest.isEmpty());
        deqTest.addLast("Hello");
        // deqTest.removeLast();
        deqTest.addFirst("Hello2");
        deqTest.addLast("Goodbye");
        deqTest.addFirst("New first");
        deqTest.addLast("New last");
        StdOut.println(deqTest.size());

        for (String s : deqTest) {
            StdOut.println("Iterating: " + s);
        }

        StdOut.println("Removing " + deqTest.removeFirst());
        StdOut.println("Removing " + deqTest.removeFirst());
        StdOut.println("Removing " + deqTest.removeFirst());
        StdOut.println("Removing " + deqTest.removeLast());
        StdOut.println("Removing " + deqTest.removeLast());
        StdOut.println(deqTest.size());

    }

}
