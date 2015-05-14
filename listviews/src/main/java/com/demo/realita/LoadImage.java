package com.demo.realita;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Svyatoslav on 5/14/2015.
 */

public class LoadImage extends AsyncTask<Params, Void, Bitmap> {
    private ImageView mImg;
    Bitmap bitmap;

    protected Bitmap doInBackground(Params... args) {
        mImg = args[0].img;

        try {
            bitmap = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeStream((InputStream) new URL(args[0].param).getContent())
                    , args[0].width, args[0].height, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap image) {
        if (image != null) {
            mImg.setImageBitmap(image);
        }
    }
}

