import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    // current picture
    public Picture picture() {
        return this.picture;
    }

    // width of current picture
    public int width() {
        return this.picture.width();
    }

    // height of current picture
    public int height() {
        return this.picture.height();
    }

    private int squaredEnergy(int x, int y) {
        // column x, row y
        if (x < 0 || y < 0 || x > width() - 1 || y > height() - 1)
            throw new IllegalArgumentException();
        if (x == width() - 1 || y == height() - 1 || x == 0 || y == 0) return 1000 * 1000;
        Color xPlus = this.picture.get(x + 1, y);
        Color xMinus = this.picture.get(x - 1, y);
        Color yPlus = this.picture.get(x, y + 1);
        Color yMinus = this.picture.get(x, y - 1);

        int dx = (xPlus.getRed() - xMinus.getRed()) * (xPlus.getRed() - xMinus.getRed())
                + (xPlus.getBlue() - xMinus.getBlue()) * (xPlus.getBlue() - xMinus.getBlue())
                + (xPlus.getGreen() - xMinus.getGreen()) * (xPlus.getGreen() - xMinus.getGreen());

        int dy = (yPlus.getRed() - yMinus.getRed()) * (yPlus.getRed() - yMinus.getRed())
                + (yPlus.getBlue() - yMinus.getBlue()) * (yPlus.getBlue() - yMinus.getBlue())
                + (yPlus.getGreen() - yMinus.getGreen()) * (yPlus.getGreen() - yMinus.getGreen());

        return dx + dy;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return Math.sqrt(squaredEnergy(x, y));
    }

    private class Seam {
        private int[] seam;
        // private int[][][] pointTo;
        private double[][] energy;
        // private boolean isVert;

        public Seam(Picture picture, boolean isVert) {
            // this.isVert = isVert;
            // System.out.println(startIdx);
            if (picture == null) throw new IllegalArgumentException();
            seam = new int[isVert ? picture.height() : picture.width()];

            // pointTo = new int[picture.width()][picture.height()][2];
            energy = new double[picture.width()][picture.height()];

            for (int col = 0; col < picture.width(); col++) {
                for (int row = 0; row < picture.height(); row++) {
                    if ((!isVert && col == 0) || (isVert && row == 0)) {
                        energy[col][row] = energy(col, row);
                        // pointTo[col][row] = new int[] { -1, -1 };
                    }
                    else energy[col][row] = Double.POSITIVE_INFINITY;
                }
            }

            if (isVert) {
                if (picture.height() > 1) buildAllVertVals();
                else {
                    seam = new int[] { getMinEnergyIdx(isVert) };
                    return;
                }
            }
            else {
                if (picture.width() > 1) buildAllHorizVals();
                else {
                    seam = new int[] { getMinEnergyIdx(isVert) };
                    return;
                }
            }

            double lowestEnergy = Double.POSITIVE_INFINITY; // .MAX_VALUE;
            int lowestDim = 0;

            // Scan final row / column for lowest energy
            for (int dim = 0; dim < (isVert ? picture.width() : picture.height()); dim++) {
                double candidateEnergy = isVert ? energy[dim][picture.height() - 1] :
                                         energy[picture.width() - 1][dim];
                if (candidateEnergy
                        < lowestEnergy) {
                    lowestEnergy = candidateEnergy;
                    lowestDim = dim;
                }
            }
            /*
            System.out.println("Lowest energy found: " + lowestEnergy);
            System.out.println("Lowest dim (should be 258):" + lowestDim);

             */

            // Build final seam array
            int lowestIdx = lowestDim;
            double cumulEnergy = 0;
            for (int i = (isVert ? picture.height() : picture.width()) - 1; i > 0; i--) {
                // if (lowestIdx == -1) break;
                seam[i] = lowestIdx;
                cumulEnergy += isVert ? energy(lowestIdx, i) : energy(i, lowestIdx);

                double prevParent = isVert ? energy[lowestIdx][i - 1] : energy[i - 1][lowestIdx];
                double lesserParent = (lowestIdx - 1 >= 0) ?
                                      (isVert ? energy[lowestIdx - 1][i - 1] :
                                       energy[i - 1][lowestIdx - 1]) :
                                      Double.POSITIVE_INFINITY;
                double greaterParent =
                        (lowestIdx + 1 < (isVert ? picture.width() : picture.height())) ?
                        (isVert ? energy[lowestIdx + 1][i - 1] :
                         energy[i - 1][lowestIdx + 1]) :
                        Double.POSITIVE_INFINITY;

                double lowestParent = Math.min(Math.min(prevParent, lesserParent), greaterParent);
                if (lowestParent == lesserParent) lowestIdx -= 1;
                else if (lowestParent == greaterParent) lowestIdx += 1;
                // lowestIdx = isVert ? pointTo[lowestIdx][i][0] : pointTo[i][lowestIdx][1];
            }
            if (seam.length > 1) seam[0] = seam[1];
            cumulEnergy += 1000;
            /*
            System.out.println(
                    "Final entry in seam: " + seam[(isVert ? picture.height() : picture.width())
                            - 1]);
            System.out.println("First entry in seam: " + seam[0]);
            System.out.println("Cumulative energy: " + cumulEnergy);

             */
        }

        private int getMinEnergyIdx(boolean isVert) {
            double lowestEnergy = Double.POSITIVE_INFINITY; // .MAX_VALUE;
            int lowestDim = 0;

            // Scan final row / column for lowest energy
            for (int dim = 0; dim < (isVert ? picture.width() : picture.height()); dim++) {
                double candidateEnergy = isVert ? energy(dim, picture.height() - 1) :
                                         energy(picture.width() - 1, dim);
                if (candidateEnergy
                        < lowestEnergy) {
                    lowestEnergy = candidateEnergy;
                    lowestDim = dim;
                }
            }
            return lowestDim;
        }

        private void buildAllVertVals() {
            for (int row = 1; row < picture.height();
                 row++) {
                for (int col = 0; col < picture.width(); col++) {
                    double parent1 = Double.POSITIVE_INFINITY;
                    if (col > 0)
                        parent1 = energy[col - 1][row - 1];

                    double parent2 = energy[col][row - 1];

                    double parent3 = Double.POSITIVE_INFINITY;
                    if (col < picture.width() - 1)
                        parent3 = energy[col + 1][row - 1];

                    double minVal = Math.min(Math.min(parent1, parent2), parent3);
                    double nextEnergy = energy(col, row); // squaredEnergy(col, row);

                    // if (col == 0) System.out.println(minVal);

                    if (minVal == parent1) {
                        energy[col][row] = energy[col - 1][row - 1] + nextEnergy;
                        // pointTo[col][row] = new int[] { col - 1, row - 1 };
                    }
                    else if (minVal == parent2) {
                        energy[col][row] = energy[col][row - 1] + nextEnergy;
                        // pointTo[col][row] = new int[] { col, row - 1 };
                    }
                    else {
                        energy[col][row] = energy[col + 1][row - 1] + nextEnergy;
                        // pointTo[col][row] = new int[] { col + 1, row - 1 };
                    }
                }
            }
        }

        private void buildAllHorizVals() {
            for (int col = 1; col < picture.width();
                 col++) {
                for (int row = 0; row < picture.height(); row++) {
                    double parent1 = Double.POSITIVE_INFINITY;
                    if (row > 0)
                        parent1 = energy[col - 1][row - 1];

                    double parent2 = energy[col - 1][row];

                    double parent3 = Double.POSITIVE_INFINITY;
                    if (row < picture.height() - 1)
                        parent3 = energy[col - 1][row + 1];

                    double minVal = Math.min(Math.min(parent1, parent2), parent3);
                    double nextEnergy = energy(col, row);

                    if (minVal == parent1) {
                        energy[col][row] = energy[col - 1][row - 1] + nextEnergy;
                        // pointTo[col][row] = new int[] { col - 1, row - 1 };
                    }
                    else if (minVal == parent2) {
                        energy[col][row] = energy[col - 1][row] + nextEnergy;
                        // pointTo[col][row] = new int[] { col - 1, row };
                    }
                    else {
                        energy[col][row] = energy[col - 1][row + 1] + nextEnergy;
                        // pointTo[col][row] = new int[] { col - 1, row + 1 };
                    }
                }
            }
        }

        public int[] getSeam() {
            return seam;
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Seam hzSeam = new Seam(picture, false);
        return hzSeam.getSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        Seam vtSeam = new Seam(picture, true);
        return vtSeam.getSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != picture.width()) throw new IllegalArgumentException();
        if (!validateSeam(seam, false)) throw new IllegalArgumentException();
        if (picture.height() <= 1) throw new IllegalArgumentException();

        // duplicate current picture except change dimension by 1 pixel
        Picture newPic = new Picture(picture.width(), picture.height() - 1);
        for (int i = 0; i < seam.length; i++) {
            // newPic.set(i, seam[i], picture.get(i, seam[i] + 1));
            for (int j = seam[i]; j < newPic.height(); j++) {
                newPic.set(i, j, picture.get(i, j + 1));
            }
            for (int j = seam[i] - 1; j >= 0; j--) {
                newPic.set(i, j, picture.get(i, j));
            }
            // newPic.set(i, newPic.height() - 1, Color.BLACK);
        }

        picture = newPic;
    }

    private boolean validateSeam(int[] seam, boolean isVert) {
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0) return false;
            if (isVert && (seam[i] > picture.width() - 1)) return false;
            if (!isVert && (seam[i] > picture.height() - 1)) return false;
            if (i > 0 && (Math.abs(seam[i] - seam[i - 1]) > 1)) return false;
        }
        return true;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != picture.height()) throw new IllegalArgumentException();
        if (!validateSeam(seam, true)) throw new IllegalArgumentException();
        if (picture.width() <= 1) throw new IllegalArgumentException();
        Picture newPic = new Picture(picture.width() - 1, picture.height());

        for (int i = 0; i < seam.length; i++) {
            // newPic.set(seam[i], i, picture.get(seam[i] + 1, i));
            for (int j = seam[i]; j < newPic.width(); j++) {
                newPic.set(j, i, picture.get(j + 1, i));
            }
            for (int j = seam[i] - 1; j >= 0; j--) {
                newPic.set(j, i, picture.get(j, i));
            }
            // newPic.set(newPic.width() - 1, i, Color.BLACK);
        }
        picture = newPic;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // no tests
    }

}
