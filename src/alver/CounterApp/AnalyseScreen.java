package alver.CounterApp;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *
 */
public class AnalyseScreen extends Activity {

    public static final int sampleWidth = 100;
    public static final int algoPerRow = 4;
    Bitmap photo = null;
    ArrayList<ImageView> iv = new ArrayList<ImageView>();
    ArrayList<Algorithm> algos = new ArrayList<Algorithm>();
    TextView count = null;
    LinearLayout mainL;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainL = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 0);

        Log.d("counter", "1");
        setContentView(mainL);
        Log.d("counter", "2");
        mainL.setOrientation(LinearLayout.VERTICAL);
        Log.d("counter", "3");
        mainL.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        Log.d("counter", "4");

        for (double d=0.1; d<1.0; d+=0.1)
            algos.add(new BasicAlgo((float)d));

        Log.d("counter", "Added "+algos.size()+" algorithms.");

        photo = getSmallPhoto(HomeScreen.photo, sampleWidth);//grabImage(Uri.parse(getIntent().getStringExtra("imageUri")));
        LinearLayout rowL = null;

        for (int i=0; i<algos.size(); i++) {
            ImageView iv = new ImageView(this);
            iv.setImageBitmap(null);

            if ((rowL == null) || (rowL.getChildCount() == algoPerRow)) {
                rowL = new LinearLayout(this);
                rowL.setOrientation(LinearLayout.HORIZONTAL);
                mainL.addView(rowL);
            }
            rowL.addView(iv, layoutParams);
            new DoImageAnalysis(String.valueOf(i+1), algos.get(i), photo, iv).execute("");
        }


        //photo = (Bitmap)getIntent().getExtras().get("photo");
        /*imageView1 = (ImageView)findViewById(R.id.imageView);
        imageView1.setImageBitmap(null);
        imageView2 = (ImageView)findViewById(R.id.imageView1);
        imageView2.setImageBitmap(null);
        count = (TextView)findViewById(R.id.count);

        new DoImageAnalysis(algorithm2, photo, imageView2).execute("");*/

    }

    public Bitmap getSmallPhoto(Bitmap photo, int width) {
        int newHeight = (int)(photo.getHeight()*width/photo.getWidth());
        return Bitmap.createScaledBitmap(photo, width, newHeight, true);
    }


    /*public Bitmap grabImage(Uri imageUri)
    {
        Log.d("counter", "Uri: "+imageUri.toString());
        //this.getContentResolver().notifyChange(imageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try
        {
            Log.d("counter", "her: "+cr);
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
            Log.d("counter", "her 2");
            return bitmap;
        }
        catch (Throwable e)
        {
            Log.d("counter", "Error: "+e.getMessage());
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("counter", "Failed to load", e);
            return null;
        }
    }*/

    private class DoImageAnalysis extends AsyncTask<String, Void, String> {


        private String tag;
        private Algorithm algo;
        private Bitmap photo;
        private ImageView targetView;

        public DoImageAnalysis(String tag, Algorithm algo, Bitmap photo, ImageView targetView) {
            this.tag = tag;
            this.algo = algo;
            this.photo = photo;
            this.targetView = targetView;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("counter", "Image analysis worker: "+tag);
            algo.analyseImage(this.photo);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.d("counter", "onPre, worker "+tag);

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("counter", "onPost: "+algo.getResultImage()+", "+targetView);
            targetView.setImageBitmap(algo.getResultImage());
            //count.setText("Number of particles: "+algo.getResult());
        }
    }
}