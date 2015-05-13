package com.demo.realita;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.realita.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Svyatoslav on 19-Mar-15.
 */
public class HouseInfoActivity extends Activity {

    TextView mHouseInfo;
    TextView mHouseAddr;
    TextView mHousePrice;
    TextView mFullDescription;
    LinearLayout mDescButton;
    CustomViewPager mViewPager;
    ImagePagerAdapter mPagerAdapter;
    int NumberOfPages;
    Bitmap bitmap;
    String imgUrl;
    HouseItem item;
    JSONArray jArray;
    private static final String TAG = ListViewActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_activity_layout);
        Intent intent = getIntent();
        item = intent.getExtras().getParcelable("hIndex");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Get data
        mHouseAddr = (TextView) findViewById(R.id.houseAddress);
        mHouseInfo = (TextView) findViewById(R.id.houseImfo);
        mFullDescription = (TextView) findViewById(R.id.FullDescription);
        mHousePrice = (TextView) findViewById(R.id.housePrice);
        mDescButton = (LinearLayout) findViewById(R.id.DescButton);

        //getting the image
        JSONObject jObj;
        try {
            jObj = new JSONObject(item.mImgPreview);
            jArray = jObj.getJSONArray("photos");
            NumberOfPages = jArray.length();
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }


        mViewPager = (CustomViewPager) findViewById(R.id.houseImgPager);
        mPagerAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        //Set fields
        final String mDescription = item.mFullDescription;
        mHouseAddr.setText(item.mAddress);
        mHouseInfo.setText(mDescription);
        mHousePrice.setText(Utils.numbersFormat().format(item.mPrice) + " Kč");
        mFullDescription.setText(item.mFullDescription);
        mHouseInfo.setText(item.mDescription);

        mDescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(v.getContext(), DescriptionActivity.class);
                t.putExtra("Description", mDescription);
                startActivity(t);
            }
        });

        //set up the Image
        //int resID = getResources().getIdentifier(item.mImgPreview, "drawable", getPackageName());
        final String[] str={"one","two","three","asdfgf"};
        final LinearLayout rl=(LinearLayout) findViewById(R.id.linlayout1);
        final TextView[] tv=new TextView[10];

        for(int i=0;i<str.length;i++)
        {
            tv[i]=new TextView(this);
            tv[i].setText(str[i]);
            tv[i].setTextSize((float) 20);
            tv[i].setPadding(10, 10, 10, 10);
            rl.addView(tv[i]);
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
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(HouseInfoActivity.this);
            try {
                imgUrl = jArray.getString(position);
            } catch(Exception e){
                e.printStackTrace();
            }

            Params asyncParams = new Params(imageView, imgUrl);
            new LoadImage().execute(asyncParams);
            //holder.imgView.setImageResource(R.drawable.home1);

            //imageView.setImageResource(res[position]);
            LayoutParams imageParams =  new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            final int pageNum = position;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO implement image gallery to open here
                    Toast.makeText(HouseInfoActivity.this, "You clicked " + Integer.toString(pageNum) + " page",
                            Toast.LENGTH_SHORT).show();
                }
            });

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
        DisplayMetrics display = HouseInfoActivity.this.getResources().getDisplayMetrics();

        int width = display.widthPixels;

        protected Bitmap doInBackground(Params... args) {
            mImg = args[0].img;

            try {
                bitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeStream((InputStream) new URL(args[0].param).getContent())
                        , width, 280, true);

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
