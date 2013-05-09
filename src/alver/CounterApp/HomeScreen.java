package alver.CounterApp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

public class HomeScreen extends Activity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PHOTO = 100;
    Button analyseButton, galleryButton;
    ImageView imageView = null;
    public static Bitmap photo = null;
    Uri mImageUri = null;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        this.imageView = (ImageView)this.findViewById(R.id.imageView);
        analyseButton = (Button)this.findViewById(R.id.analysebutton);
        galleryButton = (Button)this.findViewById(R.id.gallerybutton);
        Button photoButton = (Button) this.findViewById(R.id.getImage);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);


                /*
                // Get full size image:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo;
                try
                {
                    // place where to store camera taken picture
                    photo = createTemporaryFile("picture", ".jpg");
                    photo.delete();
                }
                catch(Exception e)
                {
                    Log.v("counter", "Can't create file to take picture!");
                    Toast.makeText(HomeScreen.this, "Please check SD card! Image shot is impossible!", 10000);
                    return;
                }
                mImageUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                //start camera intent
                Log.d("counter", "Starting camera capture");
                HomeScreen.this.startActivityForResult(intent, CAMERA_REQUEST);*/
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });


        analyseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent analyseIntent = new Intent(HomeScreen.this, AnalyseScreen.class);
                //analyseIntent.putExtra("imageUri", mImageUri.toString());
                //analyseIntent.putExtra("photo", photo);
                startActivityForResult(analyseIntent, CAMERA_REQUEST);
            }
        });
    }

    public Bitmap grabImage()
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            return bitmap;
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("counter", "Failed to load", e);
            return null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            this.photo = (Bitmap) data.getExtras().get("data");
            Log.d("counter", "Original image dimensions: " + photo.getWidth() + "x" + photo.getHeight());
            imageView.setImageBitmap(photo);
            analyseButton.setEnabled(true);
        }
        else if ((requestCode == SELECT_PHOTO) && (resultCode == RESULT_OK)) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            photo = BitmapFactory.decodeFile(filePath);

            Log.d("counter", "Selected image dimensions: " + photo.getWidth() + "x" + photo.getHeight());
            imageView.setImageBitmap(photo);
            analyseButton.setEnabled(true);


        }
        /*if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK)
        {
            Log.d("counter", "Camera activity finished.");
            //... some code to inflate/create/find appropriate ImageView to place grabbed image
            photo  = grabImage();
            if (photo != null) {
                Log.d("counter", "Original image dimensions: " + photo.getWidth() + "x" + photo.getHeight());
                imageView.setImageBitmap(photo);
                analyseButton.setEnabled(true);

            }

        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }
}
