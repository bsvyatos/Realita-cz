package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Svyatoslav on 13-May-15.
 */
public class ImageGalleryActivity extends Activity {
    String mImgArr;
    JSONArray jArray;
    int mNumberOfPages;
    int mCurrentPosition;
    String imgUrl;
    Bitmap bitmap;
    private static final String TAG = ListViewActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_gallery_layout);

        Intent t = getIntent();
        mImgArr = t.getStringExtra("ImageArr");
        mCurrentPosition = t.getIntExtra("Position", 0);
        JSONObject jObj;
        //Get the array of images in url format
        try {
            jObj = new JSONObject(mImgArr);
            jArray = jObj.getJSONArray("photos");
            mNumberOfPages = jArray.length();
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }

        ViewPager mImgPager = (ViewPager) findViewById(R.id.GalleryPager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter();
        mImgPager.setAdapter(mAdapter);

        mImgPager.setCurrentItem(mCurrentPosition);


    }

    public class DetailOnPageListener extends ViewPager.SimpleOnPageChangeListener{
        private int currentPage;
        @Override
        public void onPageSelected(int position){
            currentPage = position;
        }
        public final int getCurrentPage(){
            return currentPage;
        }
//        getActionBar().setTitle("of" + Integer.toString(mNumberOfPages));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_view, menu);
        return true;
    }

    private class ImagePagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return mNumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(ImageGalleryActivity.this);
            //Get url of the image of current position
            try {
                imgUrl = jArray.getString(position);
            } catch(Exception e){
                e.printStackTrace();
            }

            Params asyncParams = new Params(imageView, imgUrl);
            new LoadImage().execute(asyncParams);


            ViewGroup.LayoutParams imageParams =  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

    }

    private static class Params{
        ImageView img;
        String param;
        Params(ImageView img, String param){
            this.img = img;
            this.param = param;
        }
    }


    private class LoadImage extends AsyncTask<Params, Void, Bitmap> {
        private ImageView mImg;
        DisplayMetrics display = ImageGalleryActivity.this.getResources().getDisplayMetrics();

        int width = display.widthPixels;

        protected Bitmap doInBackground(Params... args) {
            mImg = args[0].img;

            try {
                bitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeStream((InputStream) new URL(args[0].param).getContent())
                        , width, 380, true);

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

}
