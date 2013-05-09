package alver.CounterApp;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 */
public class AnalyseScreen extends Activity {

    public static final int sampleWidth = 50;
    Bitmap photo = null;
    ImageView imageView1, imageView2;
    Algorithm algorithm1 = new BasicAlgo(0.75f);
    Algorithm algorithm2 = new BasicAlgo(0.65f);
    TextView count = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysescreen);
        //photo = (Bitmap)getIntent().getExtras().get("photo");
        photo = getSmallPhoto(HomeScreen.photo, sampleWidth);//grabImage(Uri.parse(getIntent().getStringExtra("imageUri")));
        imageView1 = (ImageView)findViewById(R.id.imageView);
        imageView1.setImageBitmap(null);
        imageView2 = (ImageView)findViewById(R.id.imageView1);
        imageView2.setImageBitmap(null);
        count = (TextView)findViewById(R.id.count);
        new DoImageAnalysis(algorithm1, photo, imageView1).execute("");
        new DoImageAnalysis(algorithm2, photo, imageView2).execute("");

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


        private Algorithm algo;
        private Bitmap photo;
        private ImageView targetView;

        public DoImageAnalysis(Algorithm algo, Bitmap photo, ImageView targetView) {
            this.algo = algo;
            this.photo = photo;
            this.targetView = targetView;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("counter", "doInBackground");
            algo.analyseImage(this.photo);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.d("counter", "onPre");

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("counter", "onPost");
            targetView.setImageBitmap(algo.getResultImage());
            count.setText("Number of particles: "+algo.getResult());
        }
    }
}