package alver.CounterApp;

import android.graphics.Bitmap;

/**
 * Algorithm that takes an input image and outputs analysis data as well as a visualization of the result.
 */
public interface Algorithm {

    public void analyseImage(Bitmap image);

    public Bitmap getResultImage();

    public int getResult();

}
