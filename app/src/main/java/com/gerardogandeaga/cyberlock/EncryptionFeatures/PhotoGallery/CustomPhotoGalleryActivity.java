package com.gerardogandeaga.cyberlock.EncryptionFeatures.PhotoGallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;

public class CustomPhotoGalleryActivity extends Activity
{
    private boolean[] mThumbnailsselection;
    private int mIds[], mCount;
    private String[] mArrPath;
    private ImageAdapter mImageAdapter;

    private GridView mGrdPhotos;
    private Button mBtnSave, mBtnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_gallery);
        mGrdPhotos = (GridView) findViewById(R.id.grdPhotos);
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        @SuppressWarnings("deprecation")
        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.mCount = imagecursor.getCount();
        this.mArrPath = new String[this.mCount];
        mIds = new int[mCount];
        this.mThumbnailsselection = new boolean[this.mCount];
        for (int i = 0; i < this.mCount; i++) {
            imagecursor.moveToPosition(i);
            mIds[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            mArrPath[i] = imagecursor.getString(dataColumnIndex);
        }

        mImageAdapter = new ImageAdapter();
        mGrdPhotos.setAdapter(mImageAdapter);
        imagecursor.close();


        mBtnSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = mThumbnailsselection.length;
                int cnt = 0;
                String selectedImages = "";
                for (int i = 0; i < len; i++) {
                    if (mThumbnailsselection[i]) {
                        cnt++;
                        selectedImages = selectedImages + mArrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("SelectedImages", selectedImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectedImages);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    /**
     * Class method
     */

    /**
     * This method used to set bitmap.
     * @param iv represented ImageView
     * @param id represented id
     */

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }


    /**
     * List adapter
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mCount;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (mThumbnailsselection[id]) {
                        cb.setChecked(false);
                        mThumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        mThumbnailsselection[id] = true;
                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (mThumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        mThumbnailsselection[id] = false;
                    } else {
                        holder.chkImage.setChecked(true);
                        mThumbnailsselection[id] = true;
                    }
                }
            });
            try {
                setBitmap(holder.imgThumb, mIds[position]);
            } catch (Throwable e) {
            }
            holder.chkImage.setChecked(mThumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }


    /**
     * Inner class
     * @author tasol
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

}