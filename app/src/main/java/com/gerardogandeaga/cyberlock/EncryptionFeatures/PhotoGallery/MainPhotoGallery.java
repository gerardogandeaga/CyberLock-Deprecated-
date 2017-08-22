package com.gerardogandeaga.cyberlock.EncryptionFeatures.PhotoGallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

public class MainPhotoGallery extends AppCompatActivity
{
    // DATA
    private PhotoDatabaseAccess mPhotoDatabaseAccess;
    private List<Photo> mPhotos;
    // WIDGETS
    private GridView mGrdPhotos;
    private FloatingActionButton mFabAdd;
    private ProgressDialog mProgressDialog;
    // GALLERY
    private final int PICK_IMAGE_MULTIPLE = 1;
    private ArrayList<String> mImagePathList;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_photo_gallery);
        ACTIVITY_INTENT = null;
        this.mContext = this;

        this.mPhotoDatabaseAccess = PhotoDatabaseAccess.getInstance(this);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo Lock");

        this.mGrdPhotos = (GridView) findViewById(R.id.grdPhotos);
        this.mFabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        this.mFabAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openCustomGallery();
            }
        });
    }

    public void openCustomGallery()
    {
        ACTIVITY_INTENT = new Intent(this, CustomPhotoGalleryActivity.class);
        startActivityForResult(ACTIVITY_INTENT, PICK_IMAGE_MULTIPLE);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        loadImagesOntoGridView(this.mPhotoDatabaseAccess, this.mGrdPhotos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_MULTIPLE)
            {
                this.mImagePathList = new ArrayList<String>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");

                loadImagesToDatabase(imagesPath);
            }
        }
    }

    @Contract(pure = true)
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private class photoAdapter extends ArrayAdapter<Photo>
    {

        private photoAdapter(Context context, List<Photo> objects)
        {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.layout_list_item_photo, parent, false);
            }

            ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);
            final Photo photo = mPhotos.get(position);

            imgPhoto.setImageBitmap(photo.ByteArrayToBitmap(photo.getImage()));

            return convertView;
        }
    }

    public void loadImagesOntoGridView(final PhotoDatabaseAccess photoDatabaseAccess, final GridView grdPhotos)
    {
        new AsyncTask<Void, Void, photoAdapter>()
        {
            @Override
            protected void onPostExecute(photoAdapter adapter)
            {
                super.onPostExecute(adapter);

                grdPhotos.setAdapter(adapter);
            }

            @Override
            protected photoAdapter doInBackground(Void... params)
            {
                photoDatabaseAccess.open();
                mPhotos = photoDatabaseAccess.getAllPhotos();
                photoAdapter adapter = new photoAdapter(mContext, mPhotos);
                photoDatabaseAccess.close();

                return adapter;
            }
        }.execute();
    }

    public void loadImagesToDatabase(final String[] imagePaths)
    {
        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                progressBar();
            }

            @Override
            protected Bitmap doInBackground(Void... params)
            {
                for (int i = 0; i < imagePaths.length; i++) // ITERATE THROUGH EACH BITMAP IMAGE, SCALE, COMPRESS AND SAVE TO DATABASE.
                {
                    PhotoDatabaseAccess photoDatabaseAccess = PhotoDatabaseAccess.getInstance(mContext);
                    photoDatabaseAccess.open();
                    Photo temp = new Photo();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imagePaths[i], options);

                    options.inSampleSize = calculateInSampleSize(options, 256, 256);
                    options.inJustDecodeBounds = false;

                    Bitmap bitmap = BitmapFactory.decodeFile(imagePaths[i], options);
                    temp.setImage(temp.BitmapToByteArray(bitmap));

                    loadImagesOntoGridView(mPhotoDatabaseAccess, mGrdPhotos);

                    photoDatabaseAccess.save(temp);
                    photoDatabaseAccess.close();

                    int incrementVal = 100 / imagePaths.length;
                    mProgressDialog.incrementProgressBy(incrementVal);

                    System.out.println("Photo Done!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                super.onPostExecute(bitmap);

                System.out.println("Complete!");
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMax(100);
        mProgressDialog.setMessage("Loading data...");
        mProgressDialog.setTitle("Importing...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart()
    {
        super.onStart();

        if (!APP_LOGGED_IN)
        {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish(); // CLEAN UP AND END
            this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    }
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
