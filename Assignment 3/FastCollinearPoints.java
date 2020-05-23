import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {
    private static final double EPSILON = 0.00000001;
    private int numSegs = 0;
    private LineSegment[] segList;

    public FastCollinearPoints(
            Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        for (int z = 0; z < points.length; z++) {
            if (points[z] == null) throw new IllegalArgumentException();
            for (int w = 0; w < z; w++) {
                if (points[w].equals(points[z])) throw new IllegalArgumentException();
            }
        }
        segList = new LineSegment[points.length / 4]; // may need to expand this
        Point[][] segPointsList = new Point[points.length / 4][2];

        Point[] pointsCopy = new Point[points.length];
        for (int u = 0; u < points.length; u++) {
            pointsCopy[u] = points[u];
        }
        for (int x = 0; x < points.length; x++) {

            Arrays.sort(pointsCopy, points[x].slopeOrder());

            int slopeCount = 0;
            Point minPoint = null;
            Point maxPoint = null;
            for (int h = 1; h < points.length + 1; h++) {
                if (h != points.length && (Math.abs(
                        points[x].slopeTo(pointsCopy[h]) - points[x].slopeTo(pointsCopy[h - 1]))
                        < EPSILON || points[x].slopeTo(pointsCopy[h]) == points[x]
                        .slopeTo(pointsCopy[h - 1]))) {
                    // StdOut.println(points[x].slopeTo(pointsCopy[h]));
                    if (minPoint == null) minPoint = pointsCopy[h];
                    if (maxPoint == null) maxPoint = pointsCopy[h];
                    if (pointsCopy[h].compareTo(minPoint) < 0) minPoint = pointsCopy[h];
                    if (pointsCopy[h - 1].compareTo(minPoint) < 0) minPoint = pointsCopy[h - 1];
                    if (points[x].compareTo(minPoint) < 0) minPoint = points[x];
                    if (pointsCopy[h].compareTo(maxPoint) > 0) maxPoint = pointsCopy[h];
                    if (pointsCopy[h - 1].compareTo(maxPoint) > 0) maxPoint = pointsCopy[h - 1];
                    if (points[x].compareTo(maxPoint) > 0) maxPoint = points[x];
                    ++slopeCount;
                    continue;
                }
                // if (minPoint != null && minPoint.equals(maxPoint)) continue;
                if (slopeCount >= 2) {
                    LineSegment newLine = new LineSegment(minPoint, maxPoint);
                    // LineSegment newLineB = new LineSegment(maxPoint, minPoint);

                    boolean dupFlag = false;
                    for (int v = 0; v < numSegs; v++) {
                        // segPointsList[numSegs] = new Point[] { minPoint, maxPoint };
                        if ((segPointsList[v][0].equals(minPoint) && segPointsList[v][1]
                                .equals(maxPoint))
                                || (segPointsList[v][0].equals(maxPoint) && segPointsList[v][1]
                                .equals(minPoint))) {
                            dupFlag = true;
                            break;
                        }
                    }
                    if (!dupFlag) {
                        if (numSegs + 1 > segList.length) {
                            LineSegment[] newList = new LineSegment[segList.length * 2];
                            Point[][] newPointsList = new Point[segList.length * 2][2];
                            for (int s = 0; s < segList.length; s++) {
                                newList[s] = segList[s];
                                newPointsList[s] = segPointsList[s];
                            }
                            segList = newList;
                            segPointsList = newPointsList;
                        }
                        segList[numSegs] = newLine;
                        segPointsList[numSegs] = new Point[] { minPoint, maxPoint };
                        ++numSegs;
                    }

                }
                minPoint = null;
                maxPoint = null;
                slopeCount = 0;

            }
        }


    }  // finds all line segments containing 4 or more points

/*
    private static void sort(Point[] a, Comparator<Point> comparator) {
        int N = a.length;
        for (int i = 0; i < N; i++)
            for (int j = i; j > 0 && less(comparator, a[j], a[j - 1]); j--)
                exch(a, j, j - 1);
    }

    private static boolean less(Comparator<Point> c, Point v, Point w) {
        return c.compare(v, w) < 0;
    }

    private static void exch(Point[] a, int i, int j) {
        Point swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

 */
    /*
    private Point minMax(Point[] points, boolean isMin) {
        Point minMaxPoint = points[0];
        int checkNum = -1;
        if (!isMin) checkNum = 1;
        for (int i = 1; i < points.length; i++) {
            if (points[i].compareTo(minMaxPoint) == checkNum) minMaxPoint = points[i];
        }
        return minMaxPoint;
    }

    private boolean isCollinear(Point point1, Point point2, Point point3, Point point4) {
        double firstSlope = point1.slopeTo(point2);
        if (firstSlope == point1.slopeTo(point3) && firstSlope == point1.slopeTo(point4))
            return true;
        return false;
    }

     */


    public int numberOfSegments() {
        return numSegs;
    }      // the number of line segments

    public LineSegment[] segments() {
        LineSegment[] finalSegs = new LineSegment[numSegs];
        for (
                int j = 0;
                j < numSegs; j++) {
            finalSegs[j] = segList[j];
        }

        return finalSegs;
    }
    // the line segments

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
