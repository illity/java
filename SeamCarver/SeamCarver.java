import java.lang.Math;

public class SeamCarver {

    Picture picture;
    int width;
    int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.height = picture.height();
        this.width = picture.width();
    }

    // current picture
    public Picture picture() {
        return this.picture;
    }

    // width of current picture
    public int width() {
        return this.width;
    }

    // height of current picture
    public int height() {
        return this.height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x == 0 || y == 0 || x == this.width - 1 || y == this.height - 1) {
            return 1000;
        }
        double Rx = picture.get(x - 1, y).getRed() - picture.get(x + 1, y).getRed();
        double Gx = picture.get(x - 1, y).getGreen() - picture.get(x + 1, y).getGreen();
        double Bx = picture.get(x - 1, y).getBlue() - picture.get(x + 1, y).getBlue();
        double Ry = picture.get(x, y - 1).getRed() - picture.get(x, y + 1).getRed();
        double Gy = picture.get(x, y - 1).getGreen() - picture.get(x, y + 1).getGreen();
        double By = picture.get(x, y - 1).getBlue() - picture.get(x, y + 1).getBlue();
        double gradientX = Rx * Rx + Gx * Gx + Bx * Bx;
        double gradientY = Ry * Ry + Gy * Gy + By * By;
        return Math.sqrt(gradientX + gradientY);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] result = new int[this.height];
        double[][] flow = new double[this.height][this.width];
        for (int j = 0; j < this.width; j++) {
            flow[0][j] = 1000;
        }
        for (int j = 1; j < this.height; j++) {
            for (int i = 0; i < this.width; i++) {
                // between a, b, c, whats the highest?
                // first, compare a and b
                // if a<b, then, the lowest must be a or c
                // else, then, the lowest must be b or c
                // but you can consider that if a doesnt exist and the comparison must be
                // between b and c, then, the lowest must be b or c
                // Also, if c doesnt exist, then, the lowest must be a or b
                // which means:

                // if (a) and (a<b) then
                // if a < c: a is lowest
                // c is lowest
                // else
                // if (c) and c<b: c is lowest
                // else: b is lowest'
                if (i != 0 && flow[j - 1][i - 1] < flow[j - 1][i]) {
                    if (i != this.width - 1 && flow[j - 1][i - 1] > flow[j - 1][i + 1]) {
                        // c is lowest
                        flow[j][i] = flow[j - 1][i + 1] + energy(i, j);
                    } else {
                        // a is lowest
                        flow[j][i] = flow[j - 1][i - 1] + energy(i, j);
                    }
                } else {
                    if (i == this.width - 1 || flow[j - 1][i] < flow[j - 1][i + 1]) {
                        // b is lowest
                        flow[j][i] = flow[j - 1][i] + energy(i, j);
                    } else {
                        // c is lowest
                        flow[j][i] = flow[j - 1][i + 1] + energy(i, j);
                    }
                }
                // System.out.format("(%8.2f )", flow[j][i]);
                // System.out.format("%8.2f ", this.energy(i, j));
                // System.out.println(i);
            }
            for (int k = 0; k < this.width; k++)
                flow[j - 1][k] = flow[j][k];
            // System.out.println();
        }

        // Find the lowest flow[j-1] and work till top
        double lowest = Double.POSITIVE_INFINITY;
        int lowestPosition = 0;
        for (int k = 0; k < this.width; k++) {
            if (flow[height - 1][k] < lowest) {
                lowest = flow[height - 1][k];
                lowestPosition = k;
            }
        }

        for (int j = this.height - 1; j >= 0; j--) {
            for (int k = 0; k < this.width; k++) {
                // System.out.format("%8.2f", flow[j][k]);
            }
            // System.out.println();
        }

        int i = lowestPosition;

        for (int j = this.height - 1; j > 0; j--) {
            if (i != 0 && flow[j - 1][i - 1] < flow[j - 1][i]) {
                if (i != this.width - 1 && flow[j - 1][i - 1] > flow[j - 1][i + 1]) {
                    // c is lowest
                    i++;
                } else {
                    // a is lowest
                    i--;
                }
            } else {
                if (i == this.width - 1 || flow[j - 1][i] < flow[j - 1][i + 1]) {
                    // b is lowest
                    i = i;
                } else {
                    // c is lowest
                    i++;
                }
            }
            // System.out.format("(%8.2f )", flow[j][i]);
            // System.out.format("%8.2f ", this.energy(i, j));
            // System.out.println(i);
            result[j] = i;
        }

        result[0] = result[1];
        return result;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] result = new int[this.width];
        double[][] flow = new double[this.width][this.width];
        for (int j = 0; j < this.width; j++) {
            flow[0][j] = 1000;
        }
        for (int j = 1; j < this.width; j++) {
            for (int i = 0; i < this.height; i++) {
                if (i != 0 && flow[j - 1][i - 1] < flow[j - 1][i]) {
                    if (i != this.height - 1 && flow[j - 1][i - 1] > flow[j - 1][i + 1]) {
                        flow[j][i] = flow[j - 1][i + 1] + energy(j, i);
                    } else {
                        flow[j][i] = flow[j - 1][i - 1] + energy(j, i);
                    }
                } else {
                    if (i == this.height - 1 || flow[j - 1][i] < flow[j - 1][i + 1]) {
                        flow[j][i] = flow[j - 1][i] + energy(j, i);
                    } else {
                        flow[j][i] = flow[j - 1][i + 1] + energy(j, i);
                    }
                }
                // System.out.format("(%8.2f )", flow[j][i]);
            }
            // System.out.println();
        }

        double lowest = Double.POSITIVE_INFINITY;
        int lowestPosition = 0;
        
        for (int k = 0; k < this.height; k++) {
            if (flow[k][0] < lowest) {
                lowest = flow[k][0];
                lowestPosition = k;
            }
        }


        int i = lowestPosition;
        // System.out.println(i);

        for (int j = this.width - 1; j > 0; j--) {
            // System.out.format("(%8.2f )", flow[j][i]);
            result[j] = i;
            if (i != 0 && flow[j - 1][i - 1] < flow[j - 1][i]) {
                if (i != this.height - 1 && flow[j - 1][i - 1] > flow[j - 1][i + 1]) {
                    i++;
                } else {
                    i--;
                }
            } else {
                if (i == this.height - 1 || flow[j - 1][i] < flow[j - 1][i + 1]) {
                    i = i; // :D
                } else {
                    i++;
                }
            }
        }

        result[0] = result[1];
        return result;

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        Picture newPicture = new Picture(this.width, this.height -1);
        for (int i = 0; i < this.width; i++) {
            this.picture.setRGB(i, seam[i], -100000);
            for (int j = 0; j < this.height - 1; j++) {
                newPicture.set(i, j, this.picture.get(i , j + (j>=seam[i]?1:0)));
            }
        }
        // this.picture.show();
        this.picture = newPicture;
        // newPicture.show();
        this.height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        Picture newPicture = new Picture(this.width - 1, this.height);
        for (int i = 0; i < this.height; i++) {
            this.picture.setRGB(seam[i], i, -100000);
            for (int j = 0; j < this.width - 1; j++) {
                newPicture.set(j, i, this.picture.get(j + (j>=seam[i]?1:0) , i));
            }
        }
        // this.picture.show();
        this.picture = newPicture;
        // newPicture.show();
        this.width--;
    }

    // unit testing (optional)
    public static void main(String[] args) {
        SeamCarver seamCarver = new SeamCarver(new Picture("a.png"));
        // System.out.printf("width = %d, height = %d\n", seamCarver.width(), seamCarver.height());
        for (int j = 0; j < seamCarver.height(); j++) {
            for (int i = 0; i < seamCarver.width(); i++) {
                // System.out.format("%8.2f ", seamCarver.energy(i, j));
            }
            // System.out.println();
        }
        // System.out.println();
        // int[] result = seamCarver.findHorizontalSeam();
        // int[] result = seamCarver.findVerticalSeam();
        for (int i = 0; i < seamCarver.height(); i++) {
            // System.out.printf("%d ", result[i]);
        }
        // System.out.println();
        for (int i = 0; i < 150; i++) {
            // seamCarver.removeHorizontalSeam(seamCarver.findHorizontalSeam());
            seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());
        }
        seamCarver.picture.show();
        seamCarver.picture.save("test.png");
    }
}
