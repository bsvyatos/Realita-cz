package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.realita.R;

/**
 * Created by Svyatoslav on 19-Mar-15.
 */
public class HouseInfoActivity extends Activity {

    TextView mHouseInfo;
    TextView mHouseAddr;
    TextView mHousePrice;
    TextView mFullDescription;
    ImageView mHouseImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_activity_layout);
        Intent intent = getIntent();
        HouseItem item = intent.getExtras().getParcelable("hIndex");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Get data
        mHouseAddr = (TextView) findViewById(R.id.houseAddress);
        mHouseInfo = (TextView) findViewById(R.id.houseImfo);
        mFullDescription = (TextView) findViewById(R.id.FullDescription);
        mHousePrice = (TextView) findViewById(R.id.housePrice);
        mHouseImg = (ImageView) findViewById(R.id.houseImg);

        //Set fields
        mHouseAddr.setText(item.mAddress);
        mHouseInfo.setText(item.mDescription);
        mHousePrice.setText(Utils.numbersFormat().format(item.mPrice) + " Kč");
        mFullDescription.setText(item.mFullDescription);
        mHouseInfo.setText(item.mDescription);

        //set up the Image
        //int resID = getResources().getIdentifier(item.mImgPreview, "drawable", getPackageName());
        mHouseImg.setImageResource(R.drawable.home1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_view, menu);
        return true;
    }
}
