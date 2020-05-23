import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] queue;
    private int head;
    private int tail; // index of the latest object
    private int capacity;

    // construct an empty randomized queue
    public RandomizedQueue() {
        queue = (Item[]) new Object[0];
        head = -1;
        tail = -1;
        capacity = 0;
        // StdOut.println(queue);
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return (capacity == 0);
    }

    // return the number of items on the randomized queue
    public int size() {
        return (capacity);
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (tail + 1 < queue.length) {
            tail++;
            queue[tail] = item;
            capacity++;
            if (capacity == 1) head = tail;
        }
        else {
            // StdOut.println("Doubling array size to " + queue.length * 2);
            Item[] newQ = (Item[]) new Object[Math.max(1, queue.length * 2)];
            for (int i = 0; i < queue.length; i++) {
                newQ[i] = queue[i];
            }
            queue = newQ;
            tail++;
            queue[tail] = item;
            if (head == -1) head = 0;
            capacity++;
            if (capacity == 1) head = tail;
        }
    }

    private Item[] shrinkArray(Item[] passedArr) {
        // StdOut.println("Shrinking array size to " + passedArr.length / 2);
        Item[] newArray = (Item[]) new Object[passedArr.length / 2];
        for (int i = 0; i < (tail - head + 1); i++) {
            newArray[i] = passedArr[i + head];
        }
        tail = tail - head;
        head = 0;
        return newArray;
    }

    // remove and return a random item
    public Item dequeue() {
        // return dequeueAny(queue, head, tail);
        if (capacity == 0) throw new NoSuchElementException();
        int randIdx = StdRandom.uniform(head, tail + 1);
        // StdOut.println("randIdx: " + randIdx);
        // StdOut.println("head: " + head);
        // StdOut.println("capacity: " + capacity);
        Item tempItem = queue[randIdx];
        if (randIdx == head) {
            queue[head] = null;
            if (capacity > 1) ++head;
            else if (capacity == 1) head = -1;
            if (tail < head) tail = head;
            capacity--;
            return tempItem;
        }
        else if (randIdx == tail) {
            queue[tail] = null;
            --tail;
            if (tail < head) head = tail;
            capacity--;
            return tempItem;
        }
        if (Math.abs(randIdx - head) < Math.abs(randIdx - tail)) {
            // shift objects from head toward tail
            for (int i = randIdx; i < head; i--) {
                queue[i] = queue[i - 1];
            }
            ++head;
            --capacity;
        }
        else {
            // shift objects from tail toward head
            for (int i = randIdx; i < tail; i++) {
                queue[i] = queue[i + 1];
            }
            --tail;
            --capacity;
        }
        if (tail - head <= queue.length / 4) queue = shrinkArray(queue);
        return tempItem;

    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (capacity == 0) throw new NoSuchElementException();
        int randIdx = StdRandom.uniform(head, tail + 1);
        return queue[randIdx];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Item[] qCopy = (Item[]) new Object[capacity];

        // int newHead = 0;
        // int newTail = tail - head;
        int itemsRemaining = qCopy.length;
        private int randIdx;
        private int loopCount;
        private int newLoopCount;

        public ListIterator() {
            for (int i = 0; i < itemsRemaining; i++) {
                qCopy[i] = queue[i + head];
            }
        }

        public boolean hasNext() {
            return itemsRemaining > 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --itemsRemaining;
            randIdx = StdRandom.uniform(0, qCopy.length);
            while (qCopy[randIdx] == null) {
                randIdx = StdRandom.uniform(0, qCopy.length);
            }
            Item tempItem = qCopy[randIdx];
            qCopy[randIdx] = null;
            ++loopCount;
            if (loopCount == qCopy.length / 2) {
                // StdOut.println("Cleaning up array");
                newLoopCount = 0;
                loopCount = 0;
                Item[] newQ = (Item[]) new Object[qCopy.length / 2 + 1];
                for (int k = 0; k < qCopy.length; k++) {
                    if (qCopy[k] == null) continue;
                    newQ[newLoopCount] = qCopy[k];
                    newLoopCount++;
                }
                qCopy = newQ;
            }
            return tempItem;
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.enqueue(442);
        rq.dequeue(); // ==>442
        rq.enqueue(191);
        StdOut.println(rq.dequeue()); // ==>null
        rq.isEmpty();
        rq.size();
        for (int i : rq) {
            StdOut.println(i);
        }

    }

}
