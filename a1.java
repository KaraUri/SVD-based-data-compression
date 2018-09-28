import Jama.*;

public class a1 {

    public static void main(String argv[]) {
        new a1().start();
    }

    public void problem1(Matrix image, int width, int height) {
        SingularValueDecomposition svd = image.svd();

        Matrix u = svd.getU();
        Matrix d = svd.getS();
        Matrix v = svd.getV();

        PGM_PPM_Handler.saveFilePGM_PPM(".resources/u.pgm", Double2DArrayToCharArray(scaleMatrix(u).getArray()), 1, height, width);
        PGM_PPM_Handler.saveFilePGM_PPM(".resources/d.pgm", Double2DArrayToCharArray(scaleMatrix(d).getArray()), 1, width, width);
        PGM_PPM_Handler.saveFilePGM_PPM(".resources/v.pgm", Double2DArrayToCharArray(scaleMatrix(v).getArray()), 1, width, width);

        Matrix approx = new Matrix(height, width);

        for (int p = 1; p <= width; p++) {
            double singularValue = d.get(p - 1, p - 1);
            Matrix uColumnP = u.getMatrix(0, height - 1, p - 1, p - 1);
            Matrix vTransposeRowP = v.getMatrix(0, width - 1, p - 1, p - 1).transpose();

            Matrix delta = uColumnP.times(vTransposeRowP).times(singularValue);

            approx.plusEquals(delta);


            Matrix difference = image.minus(approx);

            double maxDiff = 0;
            double total = 0;
            double totalSquaredErrors = 0;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    difference.set(i, j, Math.abs(difference.get(i, j)));

                    if (difference.get(i, j) > maxDiff) {
                        maxDiff = difference.get(i, j);
                    }
                    total += difference.get(i, j);
                }
            }

            double mean = total / (height * width);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    totalSquaredErrors += Math.pow(mean - difference.get(i, j), 2);
                }
            }


            double stddev = Math.sqrt(1.0 / (height * width) * totalSquaredErrors);

            double compression = 1 - (p * (height + width + 1.0) / (height * width));

            System.out.println("p=" + p + " maxdiff= " + maxDiff + ", mean= " + mean + ", stddev= " + stddev + ", compression=" + compression);


            PGM_PPM_Handler.saveFilePGM_PPM("/Users/tangsong/Downloads/369_A1-master/resources/approx_" + p + ".pgm", Double2DArrayToCharArray(stripMatrix(approx).getArray()), 1, height, width);
        }
    }

    public void start() {
        int[] imageId = new int[1];
        int[] imageHeight = new int[1];
        int[] imageWidth = new int[1];

        char[] result = PGM_PPM_Handler.readFilePGM_PPM("/Users/tangsong/Downloads/369_A1-master/resources/img.pgm", imageId, imageHeight, imageWidth);

        Matrix image = new Matrix(charArrayToDouble2DArray(result, imageHeight[0], imageWidth[0]));

        problem1(image, imageWidth[0], imageHeight[0]);


    }

    double[][] charArrayToDouble2DArray(char[] in, int rows, int cols) {
        double[][] result = new double[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                result[row][col] = in[row * cols + col];
            }
        }

        return result;
    }
    char[] Double2DArrayToCharArray(double[][] in) {
        char[] result = new char[in.length * in[0].length];

        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[0].length; col++) {

                result[row * in[0].length + col] = (char)Math.round(in[row][col]);
            }
        }

        return result;
    }
    Matrix scaleMatrix(Matrix m) {
        Matrix scaled = new Matrix(m.getRowDimension(), m.getColumnDimension());

        double max = m.get(0, 0);
        double min = m.get(0, 0);

        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                if (max < m.get(i, j)) {
                    max = m.get(i, j);
                }
                if (min > m.get(i, j)) {
                    min = m.get(i, j);
                }
            }
        }

        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                scaled.set(i, j, 255 * ((m.get(i, j) - min) / (max - min)));
            }
        }

        return scaled;
    }
    Matrix stripMatrix(Matrix m) {
        Matrix stripped = new Matrix(m.getRowDimension(), m.getColumnDimension());
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                if (m.get(i, j) < 0) {
                    stripped.set(i, j, 0);
                } else if (m.get(i, j) > 255) {
                    stripped.set(i, j, 255);
                } else {
                    stripped.set(i, j, m.get(i, j));
                }
            }
        }

        return stripped;
    }
}