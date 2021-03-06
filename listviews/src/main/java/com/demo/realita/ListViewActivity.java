package com.demo.realita;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends BaseActivity {

    //Azure mobile service instance
    private MobileServiceClient mClient;
    private ListView mListView;
    private HouseItemAdapter mHouseItemAdapter;
    MobileServiceJsonTable mHouseTable;
    String fileName = "mFile";
    FavouriteArray FavArr;
    private static final String TAG = ListViewActivity.class.getName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //get FavArr from shared preferences
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = mPrefs.getString("Favourites", "");
        FavArr = gson.fromJson(json, FavouriteArray.class);
        if(FavArr == null){
            List<String> fList = new ArrayList<String>();
            FavArr = new FavouriteArray(fList);
        }

        //First time application is started code
        if (!mPrefs.getBoolean("firstTime", false)) {
            //Write my newly created Filter to file
            mFilter = new FilterBuilder().build();
            SaveFilter();
            //Save state to shared preferences
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        //Load my file from internal storage and get Filter
        mFilter = LoadFilter();
        mFilter.qParam = 0;
        try {
            mClient = new MobileServiceClient(
                    ConnectValues.AZUREMOBILEURL.toString(),
                    ConnectValues.AZUREMOBILEPWD.toString(),
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mHouseTable = mClient.getTable("HouseItem");

        if (mHouseTable == null) {
            Log.v("RealitaCz", "mHouseTalble was never received");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        super.onCreateDrawer();

        setNavDrawerPos();

        mListView = (ListView) findViewById(R.id.myListView);

        mHouseItemAdapter = new HouseItemAdapter(getApplicationContext(), R.layout.row, FavArr);
        showAll(mListView);
        if(mListView != null){
            mListView.setAdapter(mHouseItemAdapter);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent t = new Intent(view.getContext(), HouseInfoActivity.class);
                t.putExtra("hIndex", mHouseItemAdapter.getItem(i));
                startActivity(t);
            }
        });

        if (savedInstanceState == null) {
            selectItem(0);
        }

    } //OnCreate()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                Intent t = new Intent(this, FilterActivity.class);
                t.putExtra("mFilter", mFilter);
                startActivityForResult(t, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class SqlString {
        public String sql_start;
        public String sql_end;

        SqlString() {
            this.sql_start = "";
            this.sql_end = "";
        }
    }

    private SqlString SqlBuilder(Filter filter) {
        SqlString ret = new SqlString();
        List<String> where = new ArrayList<String>();

        int pMin;
        int pMax;

        if(filter.mOfferType.ordinal() == 0){
            pMin = filter.mPricemin;
            pMax = filter.mPricemax;
        } else {
            pMin = filter.mPriceMonthMin;
            pMax = filter.mPriceMonthMax;
        }

        where.add("mOfferType = " + filter.mOfferType.ordinal());
        if (pMin != FilterBuilder.mPricemin)
            where.add("mPrice >= " + filter.mPricemin);
        if (pMax != FilterBuilder.mPricemax)
            where.add("mPrice <= " + filter.mPricemax);
        if (filter.mSizemin != FilterBuilder.mSizemin)
            where.add("mSize >= " + filter.mSizemin);
        if (filter.mSizemax != FilterBuilder.mSizemax)
            where.add("mSize <= " + filter.mSizemax);
        if (filter.mBalkon != null)
            where.add("mBalkony = " + (filter.mBalkon ? "1" : "0"));

        ret.sql_end = Utils.concatList(where, " AND ");

        return ret;
    } //SqlBuilder()

    public void showAll(View view) {
        //Show everything, unfiltered
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SqlString sql_string = SqlBuilder(mFilter);

                    final JsonElement result =
                            mHouseTable.where()
                                    .parameter("sql_start", sql_string.sql_start)
                                    .parameter("address", mFilter.mAddress)
                                    .parameter("sql_end", sql_string.sql_end)
                                    .execute().get();

                    final JsonArray results = result.getAsJsonArray();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mHouseItemAdapter.clear();
                        for (JsonElement item : results) {
                            HouseItem house = new HouseItem(item);
                            mHouseItemAdapter.add(house);

                            //This is terrible and it has no reason to be here This should be done in query instead
/*                            if ((mFilter.qParam == 2 &&
                                    FavArr.favList.contains(item.getAsJsonObject().getAsJsonPrimitive("id").getAsString())) || mFilter.qParam < 2) {
                                HouseItem house = new HouseItem(item);
                                mHouseItemAdapter.add(house);
                            }
*/                        }
                        //set text view for houses' count
                        TextView numbHouses = (TextView) findViewById(R.id.HousesText);
                        numbHouses.setText("Houses: " + mHouseItemAdapter.getCount());

                        if(mFilter.qParam == 2 && result == null){
                            //Favourites was pressed but there are currently no favourites, deal with it here
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
            }
        }.execute();
    } // showAll()

    @Override
    protected void onPause() {
        //save mFilter
        SaveFilter();
        super.onPause();
    }

    @Override
    protected void drawerHandler(int pos) {
        switch(pos){
            case 0:
                mFilter.mOfferType = OfferType.SALE;
                break;
            case 1:
                mFilter.mOfferType = OfferType.RENT;
                break;
            case 2:
                mFilter.mOfferType = OfferType.MATE;
                break;
            case 3:
                //favourite
                //mFilter.qParam = 2;
            case 4:
                //Notifications
            case 5:
                //Sign/Log in
        }
        SaveFilter();
        showAll(mListView);
        return;
    }

    private void setNavDrawerPos(){
        if(mFilter.mOfferType == OfferType.SALE){
            selectItemImp(0);
        } else if(mFilter.mOfferType == OfferType.RENT){
            selectItemImp(1);
        } else  if(mFilter.mOfferType == OfferType.MATE){
            selectItemImp(2);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                mFilter = data.getExtras().getParcelable("Filter");
                SaveFilter();
                finish();
                startActivity(getIntent());
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Your Settings were not saved", Toast.LENGTH_LONG).show();
            }
        }
    }//onActivityResult

    void SaveFilter(){
        Gson gson = new Gson();
        String json = gson.toJson(mFilter);
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch(Exception e){
            Log.e(TAG, "Can't save Filter to file: " + e.getMessage());
        }
    }

    Filter LoadFilter(){
        Filter mFltr = new FilterBuilder().build();
        Gson gson = new Gson();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(getFilesDir(), fileName)));
            mFltr = gson.fromJson(br, Filter.class);
        } catch(Exception e){
            Log.e(TAG, "Can't Load filter: " + e.getMessage());
        }
        return mFltr;
    }


}

