import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private static final double EPSILON = 0.00000001;
    private int numSegs = 0;
    private LineSegment[] segList;


    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        segList = new LineSegment[points.length / 4];
        Point[][] segPointsList = new Point[points.length / 4][2];
        // Point[] dupArray = new Point[points.length];
        for (int z = 0; z < points.length; z++) {
            if (points[z] == null) throw new IllegalArgumentException();
            for (int w = 0; w < z; w++) {
                if (points[w].equals(points[z])) throw new IllegalArgumentException();
            }
            // dupArray[z] = points[z];
        }
        // Point minPoint = points[0];
        // Point maxPoint = points[0];
        for (int m = 0; m < points.length; m++) {
            for (int i = m + 1; i < points.length; i++) {
                // if (i == m) continue;
                for (int j = i + 1; j < points.length; j++) {
                    // if (j == i) continue;
                    for (int k = j + 1; k < points.length; k++) {
                        if (k == i || k == m) continue;

                        double firstSlope = points[m].slopeTo(points[i]);
                        double secondSlope = points[m].slopeTo(points[j]);
                        double thirdSlope = points[m].slopeTo(points[k]);
                        if ((Math.abs(firstSlope - secondSlope) < EPSILON
                                || firstSlope == secondSlope)
                                && (Math.abs(firstSlope - thirdSlope) < EPSILON
                                || firstSlope == thirdSlope)) {
                            // StdOut.println("firstSlope: " + firstSlope);
                            //  StdOut.println("points[m].slopeTo(points[j]): " + points[m]
                            //         .slopeTo(points[j]));
                            // StdOut.println(
                            //        "points[m] and points[j]: " + points[m] + ", " + points[j]);

                            Point[] pointList = {
                                    points[m], points[i], points[j], points[k]
                            };
                            Point minPoint = minMax(pointList, true);
                            Point maxPoint = minMax(pointList, false);
                            if (minPoint.equals(maxPoint)) continue;
                            LineSegment newLine = new LineSegment(minPoint, maxPoint);
                            // StdOut.println("Want to add new line: " + newLine.toString());
                            boolean isDup = false;
                            for (int r = 0; r < numSegs; r++) {
                                if ((segPointsList[r][0].equals(minPoint) && segPointsList[r][1]
                                        .equals(maxPoint)) || (segPointsList[r][0].equals(maxPoint)
                                        && segPointsList[r][1].equals(minPoint))) {
                                    isDup = true;
                                    break;
                                }
                            }
                            if (!isDup) {
                                if (numSegs + 1 > segList.length) {
                                    LineSegment[] newList = new LineSegment[segList.length * 2];
                                    Point[][] newPointsList = new Point[segList.length * 2][2];
                                    for (int t = 0; t < segList.length; t++) {
                                        newList[t] = segList[t];
                                        newPointsList[t] = segPointsList[t];
                                    }
                                    segList = newList;
                                    segPointsList = newPointsList;

                                }
                                segList[numSegs] = newLine;
                                segPointsList[numSegs] = new Point[] { minPoint, maxPoint };
                                ++numSegs;
                            }

                        }
                    }
                }
            }
        }


    }

    private Point minMax(Point[] points, boolean isMin) {
        Point minMaxPoint = points[0];
        for (int i = 1; i < points.length; i++) {
            if (isMin && points[i].compareTo(minMaxPoint) < 0) minMaxPoint = points[i];
            if (!isMin && points[i].compareTo(minMaxPoint) > 0) minMaxPoint = points[i];
        }
        return minMaxPoint;
    }

    public int numberOfSegments() {
        return numSegs;
    }    // the number of line segments

    public LineSegment[] segments() {
        LineSegment[] finalSegs = new LineSegment[numSegs];
        for (int j = 0; j < numSegs; j++) {
            finalSegs[j] = segList[j];
        }
        return finalSegs;
    }         // the line segments

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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
