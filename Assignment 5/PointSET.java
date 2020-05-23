import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

import java.util.Iterator;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> tree;

    public PointSET() {
        tree = new TreeSet<>();
    }                             // construct an empty set of points

    public boolean isEmpty() {
        return tree.isEmpty();
    }                  // is the set empty?

    public int size() {
        return tree.size();
    }                      // number of points in the set

    public void insert(
            Point2D p) {          // add the point to the set (if it is not already in the set)
        if (p == null) throw new IllegalArgumentException();
        if (!this.contains(p)) tree.add(p);
    }

    public boolean contains(Point2D p) {     // does the set contain point p?
        if (p == null) throw new IllegalArgumentException();
        return (tree.contains(p));
    }

    public void draw() {
        for (Iterator<Point2D> it = tree.iterator(); it.hasNext(); ) {
            Point2D point = it.next();
            point.draw();
        }
    }                       // draw all points to standard draw

    public Iterable<Point2D> range(
            RectHV rect) {           // all points that are inside the rectangle (or on the boundary)
        if (rect == null) throw new IllegalArgumentException();
        Stack<Point2D> inPoints = new Stack<>();
        for (Iterator<Point2D> it = tree.iterator(); it.hasNext(); ) {
            Point2D point = it.next();
            if (rect.contains(point)) inPoints.push(point);
        }
        return inPoints;
    }

    public Point2D nearest(
            Point2D p) {          // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) throw new IllegalArgumentException();
        if (this.isEmpty()) return null;
        Point2D nearestPoint = tree.first();
        for (Iterator<Point2D> it = tree.iterator(); it.hasNext(); ) {
            Point2D point = it.next();
            if (point.distanceTo(p) < nearestPoint.distanceTo(p)) {
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }

    public static void main(
            String[] args) {
        PointSET tester = new PointSET();
        Point2D first = new Point2D(2.3, 4.6);
        Point2D second = new Point2D(9.8, 10.4);
        Point2D third = new Point2D(4, 7);
        tester.insert(first);
        tester.insert(second);
        RectHV testRect = new RectHV(1, 4, 10, 11);
        System.out.print("isEmpty: ");
        System.out.println(tester.isEmpty());
        System.out.print("size: ");
        System.out.println(tester.size());
        System.out.println("contains first?:");
        System.out.println(tester.contains(first));
        System.out.println("draw");
        tester.draw();
        testRect.draw();
        System.out.println("Points in testRect:");
        for (Point2D loop : tester.range(testRect)) {
            System.out.println(loop);
        }
        System.out.print("nearest: ");
        System.out.print(tester.nearest(third));

    }                // unit testing of the methods (optional)
}
