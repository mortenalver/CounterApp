package alver.CounterApp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import org.apache.http.cookie.CookieSpecRegistry;

/**
 *
 */
public class BasicAlgo implements Algorithm {

    private Bitmap resultImage = null;
    private int result = 0;

    // Settings:
    boolean findDarkParticles = true;
    float threshold; // = 0.75f;

    public BasicAlgo(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public void analyseImage(Bitmap image) {
        Log.d("counter", "BasicAlgo starting");
        Log.d("counter", "Image dimensions: "+image.getWidth()+"x"+image.getHeight());

        resultImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        //resultImage = image.copy(image.getConfig(), true);
        //resultImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight());

        Log.d("counter", "Starting image handling");
        int[] pixels = new int[image.getHeight()*image.getWidth()];
        boolean[][] binaryMatrix = new boolean[image.getHeight()][image.getWidth()];
        try {
            float[] hsv = new float[3];
            image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            for (int i=0; i<pixels.length; i++) {
                //int px = resultImage.getPixel(i, j);
                int px = pixels[i];
                int red = Color.red(px), green = Color.green(px), blue = Color.blue(px);
                Color.colorToHSV(px, hsv);
                int newCol = (findDarkParticles ? 1f-hsv[2] : hsv[2]) > threshold ? /*Color.rgb(red, green, blue)*/Color.WHITE : Color.BLACK;
                pixels[i] = newCol;
                binaryMatrix[i/image.getWidth()][i % image.getWidth()] = newCol == Color.WHITE;

                //resultImage.setPixel(i, j, newCol);
            }

            ParticleAnalyzer pa = new ParticleAnalyzer();
            int count = pa.analyzeImage(binaryMatrix, 10, 200000, true);
            Log.d("counter", "Particle analyzer returned: "+count);

            binaryMatrix = pa.getRetImage();

            for (int i=0; i<binaryMatrix.length; i++)
                for (int j=0; j<binaryMatrix[i].length; j++)
                    pixels[j+i*image.getWidth()] = binaryMatrix[i][j] ? Color.RED : Color.BLACK;

            resultImage.setPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            Log.d("counter", "Done");
        } catch (Throwable ex) {
            Log.d("counter", "Error", ex);
        }



        Log.d("counter", "BasicAlgo done: result = "+result);
    }

    @Override
    public Bitmap getResultImage() {
        return resultImage;
    }

    @Override
    public int getResult() {
        return result;
    }
}
