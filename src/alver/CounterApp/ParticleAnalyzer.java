package alver.CounterApp;

import android.util.Log;

/**
 *
 */
public class ParticleAnalyzer {

    private int MAX_WORKING_SET = 1500;
    private int MAX_PARTICLES = 1500;
    private boolean[][] image, retImage, scratch;
    int count = 0;
    int vnum = 0, vnum_tmp = 0, x_sum = 0, y_sum = 0, xma =0, xmi = 0, yma = 0, ymi = 0;
    int[] xv = new int[MAX_WORKING_SET], yv = new int[MAX_WORKING_SET];
    int[] xv_tmp = new int[MAX_WORKING_SET], yv_tmp = new int[MAX_WORKING_SET];

    public ParticleAnalyzer() {

    }

    public int analyzeImage(boolean[][] binaryImage, int minSize, int maxSize, boolean useSkip) {

        image = binaryImage;
        retImage = new boolean[image.length][image[0].length];
        scratch = new boolean[image.length][image[0].length];

        int skip = useSkip ? (int)Math.floor(0.5*Math.sqrt((float)minSize)) : 1;
        if (skip < 1) skip = 1;

        Log.d("counter", "Skip = "+skip);

        count = 0;
        int totSize = 0, highestSize = 0;
        for (int i=0; i<image.length; i+=skip) {

            for (int j=0; j<image[i].length; j+=skip) {
                boolean value = image[i][j];
                boolean value2 = retImage[i][j];
                if (value && !value2) {
                    // We found a white pixel which isn't part of a previous particle.
                    clearMatr(scratch);
                    int size = handleParticle(i, j);
                    Log.d("counter", "Size="+size+", xmi="+xmi+", xma="+xma+", ymi="+ymi+", yma="+yma);

                    if (size >= minSize && size <= maxSize) {
                        //int markVal = rand() % 255;
                        //com_x[count] = x_sum/size;
                        //com_y[count] = y_sum/size;
                        //p_size[count] = size;
                        //printf("center of mass = %d, %d\n", com_x[count], com_y[count]);
                        //printf("%d, %d, %d, %d\n", xmi, xma, ymi, yma);
                        for (int ii=xmi; ii<=xma; ii++)
                            for (int jj=ymi; jj<=yma; jj++)
                                if (scratch[ii][jj]) retImage[ii][jj] = true;

                        //retimage(com_x[count], com_y[count],0,0) = 255;
                        totSize += size;
                        if (size > highestSize)
                            highestSize = size;
                        //calculateMoment();
                        count++;

                    }
                }

            }
        }

        return count;
    }

    public boolean[][] getRetImage() {
        return retImage;
    }

    private int handleParticle(int si, int sj) {
        Log.d("counter","handleParticle: i="+si+", j="+sj);
        xma = 0;
        xmi = image.length;
        yma = 0;
        ymi = image[0].length;
        int pixelCount = 1;
        x_sum = si;
        y_sum = sj;
        scratch[si][sj] = true;
        vnum = 1;
        xv[0] = si;
        yv[0] = sj;
        while (vnum > 0) {
            vnum_tmp = vnum;
            for (int i=0; i<vnum; i++) {
                xv_tmp[i] = xv[i];
                yv_tmp[i] = yv[i];
            }
            vnum = 0;
            for (int i=0; i<vnum_tmp; i++) {
                findNeighbour(xv_tmp[i], yv_tmp[i]);
            }
            //Log.d("counter", "vnum="+vnum);
            pixelCount += vnum;
        }

        //printf("x_sum = %d\ny_sum= %d\n", x_sum, y_sum);
        //printf("center of mass = %d, %d\n", x_sum/pixelCount, y_sum/pixelCount);

        return pixelCount;

    }

    private boolean findNeighbour(int si, int sj) {
        if (si > 0 && image[si-1][sj] && !scratch[si-1][sj]) {
            xv[vnum] = si-1;
            yv[vnum] = sj;
            check();
        }

        if (si < image.length-1 && image[si+1][sj] && !scratch[si+1][sj]) {
            xv[vnum] = si+1;
            yv[vnum] = sj;
            check();
        }

        if (sj > 0 && image[si][sj-1] && !scratch[si][sj-1]) {
            xv[vnum] = si;
            yv[vnum] = sj-1;
            check();
        }

        if (sj < image[0].length-1 && image[si][sj+1] && !scratch[si][sj+1]) {
            xv[vnum] = si;
            yv[vnum] = sj+1;
            check();
        }

        return true;

    }

    private void check() {
        scratch[xv[vnum]][yv[vnum]] = true;
        x_sum += xv[vnum];
        y_sum += yv[vnum];
        if (xv[vnum] < xmi) xmi = xv[vnum];
        if (xv[vnum] > xma) xma = xv[vnum];
        if (yv[vnum] < ymi) ymi = yv[vnum];
        if (yv[vnum] > yma) yma = yv[vnum];
        vnum++;

    }

    private void clearMatr(boolean[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = false;

            }
        }
    }

}
