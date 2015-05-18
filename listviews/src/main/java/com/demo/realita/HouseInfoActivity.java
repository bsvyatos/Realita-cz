package com.demo.realita;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

        JSONObject jObj;
        //Get the array of images in url format
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

        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(v.getContext(), ImageGalleryActivity.class);
                t.putExtra("ImageArr", item.mImgPreview);
            }
        });

        //Set fields
        final String FullDescription = item.mFullDescription;
        mHouseAddr.setText(item.mAddress);
        mHousePrice.setText(Utils.numbersFormat().format(item.mPrice) + " Kč");
        mFullDescription.setText(item.mFullDescription);
        mHouseInfo.setText(item.mDescription);

        mDescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(v.getContext(), DescriptionActivity.class);
                t.putExtra("Description", FullDescription);
                startActivity(t);
            }
        });

        //set up the Image
        //int resID = getResources().getIdentifier(item.mImgPreview, "drawable", getPackageName());
        ArrayList<Pair<String, String >> house_attrs = createAdditionalHouseParams(item);
        final LinearLayout rl=(LinearLayout) findViewById(R.id.linlayout1);
        final TextView[] tv=new TextView[10];

        for (int i = 0; i < house_attrs.size(); i++)
        {
            Pair<String, String > item = house_attrs.get(i);
            tv[i]=new TextView(this);
            tv[i].setText(item.first + ": " + item.second);
            tv[i].setTextSize((float) 14);
            tv[i].setPadding(10, 10, 10, 10);
            rl.addView(tv[i]);
        }

    }

    private ArrayList<Pair<String, String >> createAdditionalHouseParams(HouseItem item){
        ArrayList<Pair<String, String >> result = new ArrayList<Pair<String, String>>();
        String YES = getResources().getString(R.string.Yes);
        String NO = getResources().getString(R.string.No);
        result.add(new Pair<String, String>(
                getResources().getString(R.string.Ownership), Utils.HOwnership[item.mOwnership]));
        result.add(new Pair<String, String>(
                getResources().getString(R.string.Size), String.valueOf(item.mSize) + " m²"));
        result.add(new Pair<String, String>(
                getResources().getString(R.string.Dispositions), Utils.HDispositions[item.mLayout]));
        if (item.mEnergyType != null)
            result.add(new Pair<String, String>(
                    getResources().getString(R.string.EnergyType), String.valueOf(item.mEnergyType)));
        result.add(new Pair<String, String>(
                getResources().getString(R.string.EstateType), Utils.HEstateType[item.mPropertyType.ordinal()]));
        result.add(new Pair<String, String>(
                getResources().getString(R.string.Construction), Utils.HConstruction[item.mBuildingType]));
        result.add(new Pair<String, String>(
                getResources().getString(R.string.Equipment), Utils.HEquipment[item.mEquipment]));
        result.add(new Pair<String, String>(
                getResources().getString(R.string.Floor), String.valueOf(item.mFloor)));
        if (item.mBalkony != null)
            result.add(new Pair<String, String>(
                    getResources().getString(R.string.Balcony), item.mBalkony ? YES : NO));
        if (item.mTerrace != null)
            result.add(new Pair<String, String>(
                    getResources().getString(R.string.Terrace), item.mTerrace ? YES : NO));

        return result;
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

            //Get url of the image of current position
            try {
                imgUrl = jArray.getString(position);
            } catch(Exception e){
                e.printStackTrace();
            }

            DisplayMetrics display = HouseInfoActivity.this.getResources().getDisplayMetrics();

            Params asyncParams = new Params(imageView, imgUrl, display.widthPixels
                    , Math.round(display.widthPixels/1.3333f));
            new LoadImage().execute(asyncParams);
            //holder.imgView.setImageResource(R.drawable.home1);

            LayoutParams imageParams =  new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            final int pageNum = position;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent t = new Intent(v.getContext(), ImageGalleryActivity.class);
                    t.putExtra("ImageArr", item.mImgPreview);
                    t.putExtra("Position", pageNum);
                    startActivity(t);
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





}
