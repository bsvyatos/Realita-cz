package com.demo.realita;

import android.app.ActionBar;
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
    ActionBar mActionBar;
    private static final String TAG = ListViewActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_gallery_layout);

        Intent t = getIntent();
        mImgArr = t.getStringExtra("ImageArr");
        mCurrentPosition = t.getIntExtra("Position", 0);
        mActionBar = getActionBar();
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
        DetailOnPageListener pageListener = new DetailOnPageListener();
        mImgPager.setOnPageChangeListener(pageListener);

        mImgPager.setCurrentItem(mCurrentPosition);


    }

    public class DetailOnPageListener extends ViewPager.SimpleOnPageChangeListener{
        @Override
        public void onPageSelected(int position){
            mActionBar.setTitle(" " + Integer.toString(position+1) + " of " + Integer.toString(mNumberOfPages));
        }
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
            DisplayMetrics display = ImageGalleryActivity.this.getResources().getDisplayMetrics();

            Params asyncParams = new Params(imageView, imgUrl, display.widthPixels, 280);
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


}
