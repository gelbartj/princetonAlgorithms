import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

public class KdTree {
    private class InternalTree {
        private Node root;
        private int treeSize = 0;

        private class Node {
            private final Point2D key;
            private String val;
            private Node left;
            private Node right;
            private final boolean isVert;

            public Node(Point2D key, String val, boolean isVert) {
                this.key = key;
                this.val = val;
                this.isVert = isVert;
            }
        }

        private int compareEither(Point2D first, Point2D second, boolean isVert) {
            if (isVert) {
                return Double.compare(first.x(), second.x());
            }
            return Double.compare(first.y(), second.y());
        }

        public String get(Point2D key) {
            Node x = root;
            // boolean isVert = true;
            while (x != null) {
                int cmp = compareEither(key, x.key, x.isVert);
                if (cmp < 0) {
                    x = x.left;
                }
                else if (cmp > 0) {
                    x = x.right;
                }
                else {
                    if (key.equals(x.key)) return x.val;
                    x = x.left; // arbitrarily search left side if keys are equal
                }
            }
            return null;
        }

        public int getIsVert(Point2D key) {
            Node x = root;
            // boolean isVert = true;
            while (x != null) {
                int cmp = compareEither(key, x.key, x.isVert);
                if (cmp < 0) {
                    x = x.left;
                    // isVert = !isVert;
                }
                else if (cmp > 0) {
                    x = x.right;
                    // isVert = !isVert;
                }
                else {
                    if (key.equals(x.key)) return (x.isVert ? 1 : 0);
                    x = x.left; // arbitrary
                }
            }
            return -1;
        }

        public void put(Point2D key, String val) {
            root = put(root, key, val, true);
        }

        private Node put(Node h, Point2D key, String val, boolean isVert) {
            if (h == null) {
                ++treeSize;
                return new Node(key, val, isVert);
            }
            int cmp = compareEither(key, h.key, h.isVert);
            if (cmp < 0) {
                h.left = put(h.left, key, val, !h.isVert);
            }
            else if (cmp > 0) {
                h.right = put(h.right, key, val, !h.isVert);
            }
            else {
                if (h.key.equals(key)) h.val = val;
                else h.left = put(h.left, key, val,
                                  !h.isVert); // arbitrarily add to left side when keys are equal
            }
            return h;
        }

        public int size() {
            return treeSize;
        }

        public Iterable<Point2D> keys() {
            Queue<Point2D> q = new Queue<Point2D>();
            inorder(root, q);
            return q;
        }

        private void inorder(Node x, Queue<Point2D> q) {
            if (x == null) return;
            q.enqueue(x.key);
            inorder(x.left, q);
            inorder(x.right, q);
        }

        public Iterable<Point2D> getPointsInRect(RectHV rect) {
            Queue<Point2D> q = new Queue<Point2D>();
            rectSearch(root, q, rect);
            return q;
        }

        private void rectSearch(Node x, Queue<Point2D> q, RectHV rect) {
            if (x == null) return;
            if (rect.contains(x.key)) q.enqueue(x.key);
            RectHV[] nodeRects = getRects(x.key);
            // if ((x.isVert && rect.xmin() <= x.key.x()) || (!x.isVert && rect.ymin() <= x.key.y()))
            if (nodeRects.length > 1) {
                if (nodeRects[0].intersects(rect))
                    rectSearch(x.left, q, rect);
                // if ((x.isVert && rect.xmax() >= x.key.x()) || (!x.isVert && rect.ymax() >= x.key.y()))
                if (nodeRects[1].intersects(rect))
                    rectSearch(x.right, q, rect);
            }
        }

        private RectHV[] splitRect(RectHV r, double coord, boolean isX) {
            RectHV lowRect;
            RectHV hiRect;
            if (isX) { // split vertically at given x coordinate
                lowRect = new RectHV(r.xmin(), r.ymin(), coord, r.ymax());
                hiRect = new RectHV(coord, r.ymin(), r.xmax(), r.ymax());
            }
            else { // split horizontally at given y coordinate
                lowRect = new RectHV(r.xmin(), r.ymin(), r.xmax(), coord);
                hiRect = new RectHV(r.xmin(), coord, r.xmax(), r.ymax());
            }
            return new RectHV[] { lowRect, hiRect };
        }

        private RectHV[] getRects(Point2D point) {
            if (point == null) return new RectHV[] { };
            RectHV currRect = new RectHV(0.0, 0.0, 1.0, 1.0); // xmin, ymin, xmax, ymax
            Node x = root;
            do {
                int cmp = compareEither(point, x.key, x.isVert);
                RectHV[] splitRects = splitRect(currRect, x.isVert ? x.key.x() : x.key.y(),
                                                x.isVert);
                if (cmp < 0) {
                    currRect = splitRects[0];
                    x = x.left;
                }
                else if (cmp > 0) {
                    currRect = splitRects[1];
                    x = x.right;
                }
                else {
                    if (point.equals(x.key))
                        return splitRects;
                    currRect = splitRects[0];
                    x = x.left; // arbitrarily go to left
                }
            } while (x != null);
            return new RectHV[] { };
        }

        public Point2D getNearest(Point2D p) {
            /*
            StdDraw.setPenColor(StdDraw.DARK_GRAY);
            StdDraw.setPenRadius(0.03);
            p.draw();
            */
            return nearestInNode(root, p, root.key);
        }

        private Point2D nearestInNode(Node x, Point2D p, Point2D closestSoFar) {
            if (x == null) return closestSoFar;

            double closestDist = p.distanceSquaredTo(closestSoFar);

            if (p.distanceSquaredTo(x.key) < closestDist) {
                closestSoFar = x.key;
                closestDist = p.distanceSquaredTo(x.key);
            }

            RectHV[] rects = getRects(x.key);
            RectHV leftRect = rects.length > 0 ? rects[0] : null;
            RectHV rightRect = rects.length > 0 ? rects[1] : null;

            // closestSoFar = nearestInNode(x.left, p, closestSoFar);
            // closestSoFar = nearestInNode(x.right, p, closestSoFar);

            if (leftRect != null && leftRect.contains(p)) {
                // (x.isVert ? p.x() <= x.key.x() : p.y() <= x.key.y())
                if (leftRect.distanceSquaredTo(p) <= closestDist) {
                    closestSoFar = nearestInNode(x.left, p, closestSoFar);
                    // closestDist = p.distanceSquaredTo(closestSoFar);
                }
                if (rightRect != null && rightRect.distanceSquaredTo(p) <= closestDist) {
                    closestSoFar = nearestInNode(x.right, p, closestSoFar);
                }
            }
            else {
                assert rightRect != null;
                if (rightRect.distanceSquaredTo(p) <= closestDist) {
                    closestSoFar = nearestInNode(x.right, p, closestSoFar);
                    // closestDist = p.distanceSquaredTo(closestSoFar);
                }
                if (leftRect != null && leftRect.distanceSquaredTo(p) <= closestDist) {
                    closestSoFar = nearestInNode(x.left, p, closestSoFar);
                }
            }

            return closestSoFar;
        }
    }

    private final InternalTree tree;
    private int nodeNum = 1;

    public KdTree() {
        tree = new InternalTree();
    }                             // construct an empty set of points

    public boolean isEmpty() {
        return tree.size() == 0;
    }                  // is the set empty?

    public int size() {
        return tree.size();
    }                      // number of points in the set


    public void insert(
            Point2D p) {          // add the point to the set (if it is not already in the set)
        if (p == null) throw new IllegalArgumentException();
        if (!this.contains(p)) {
            tree.put(p, Integer.toString(nodeNum++));
        }
    }

    public boolean contains(Point2D p) {     // does the set contain point p?
        if (p == null) throw new IllegalArgumentException();
        return (tree.get(p) != null);
    }

    private double[] newCoords(Point2D it, boolean isVert) {
        RectHV[] boundingRects = tree.getRects(it);
        RectHV parentRect = new RectHV(Math.min(boundingRects[0].xmin(), boundingRects[1].xmin()),
                                       Math.min(boundingRects[0].ymin(), boundingRects[1].ymin()),
                                       Math.max(boundingRects[0].xmax(), boundingRects[1].xmax()),
                                       Math.max(boundingRects[0].ymax(), boundingRects[1].ymax()));
        if (isVert) return new double[] { parentRect.ymin(), parentRect.ymax() };
        else return new double[] { parentRect.xmin(), parentRect.xmax() };
        /*
        if (isVert) {
            // check for horizontal lines above and below
            double newYmax = 1;
            double newYmin = 0;
            for (Point2D[] line : lines) {
                if (line == null || line[0] == null || line[1] == null) break;
                double xIntMin = Math.min(line[0].x(), line[1].x());
                double xIntMax = Math.max(line[0].x(), line[1].x());
                if (!(line[0].y() == line[1].y() && it.x() >= xIntMin
                        && it.x()
                        <= xIntMax))
                    continue;  // check line is horizontal and point passed to function is within interval
                if (line[0].y() <= it.y() && line[0].y() > newYmin) newYmin = line[0].y();
                if (line[0].y() >= it.y() && line[0].y() < newYmax)
                    newYmax = line[0].y();
            }
            return new double[] { newYmin, newYmax };
        }
        else {
            // check for vertical lines left and right
            double newXmax = 1;
            double newXmin = 0;
            for (Point2D[] line : lines) {
                if (line == null || line[0] == null || line[1] == null) break;
                double yIntMin = Math.min(line[0].y(), line[1].y());
                double yIntMax = Math.max(line[0].y(), line[1].y());
                if (!(line[0].x() == line[1].x() && it.y() >= yIntMin
                        && it.y() <= yIntMax))
                    continue; // check line is vertical and point passed to function is within interval
                if (line[0].x() <= it.x() && line[0].x() > newXmin) newXmin = line[0].x();
                if (line[0].x() >= it.x() && line[0].x() < newXmax)
                    newXmax = line[0].x();
            }
            return new double[] { newXmin, newXmax };
        }

         */
    }

    public void draw() {
        // boolean isVert = true;

        // Point2D[][] lines = new Point2D[tree.size() + 1][2];

        for (Point2D it : tree.keys()) {
            // StdDraw.pause(200);
            if (it == null) break;
            StdDraw.setPenRadius(0.01);
            boolean isVert = tree.getIsVert(it) == 1;
            if (isVert) {
                double[] newYs = newCoords(it, isVert);
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(it.x(), newYs[0],
                             it.x(), newYs[1]);
                /*
                lines[lineIdx++] = new Point2D[] {
                        new Point2D(it.x(), newYs[0]), new Point2D(it.x(), newYs[1])
                };

                 */
            }
            else {
                double[] newXs = newCoords(it, isVert);
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(newXs[0], it.y(),
                             newXs[1], it.y());
                /*
                lines[lineIdx++] = new Point2D[] {
                        new Point2D(newXs[0], it.y()), new Point2D(newXs[1], it.y())
                };

                 */
            }
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.015);
            it.draw();
            // StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(it.x(), it.y(), tree.get(it));
            // isVert = !isVert;
        }
        StdDraw.setPenRadius(0.005);
    }                       // draw all points to standard draw

    public Iterable<Point2D> range(
            RectHV rect) {           // all points that are inside the rectangle (or on the boundary)
        if (rect == null) throw new IllegalArgumentException();
        /*
        Queue<Point2D> inPoints = new Queue<>();
        for (Point2D it : tree.getPointsInRect(rect)) {
            if (rect.contains(it)) inPoints.enqueue(it);
        }

         */
        return tree.getPointsInRect(rect);
    }

    public Point2D nearest(
            Point2D p) {          // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) throw new IllegalArgumentException();
        if (this.isEmpty()) return null;
        /*
        Point2D nearestPoint = tree.keys().iterator().next();
        for (Point2D it : tree.keys()) {
            if (it.distanceSquaredTo(p) < nearestPoint.distanceSquaredTo(p)) {
                nearestPoint = it;
            }
        } */
        return tree.getNearest(p);
    }

    public static void main(
            String[] args) {
        KdTree tester = new KdTree();

        int n = Integer.parseInt(args[0]);
        for (int i = 0; i < n; i++) {
            double x = StdRandom.uniform(0.0, 1.0);
            double y = StdRandom.uniform(0.0, 1.0);
            tester.insert(new Point2D(x, y));
        }
        tester.draw();

        System.out.print("isEmpty: ");
        System.out.println(tester.isEmpty());
        RectHV testRect = new RectHV(0.1, 0.1, 0.9, 0.9);
        // testRect.draw();
        System.out.println("Points in testRect:");
        for (Point2D loop : tester.range(testRect)) {
            System.out.println(loop);
        }
        System.out.print("nearest to center: ");
        Point2D testPoint = new Point2D(0.5, 0.5);
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.GRAY);
        testPoint.draw();
        System.out.print(tester.tree.get(tester.nearest(testPoint)));


    }                // unit testing of the methods (optional)
}
