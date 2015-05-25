package com.demo.realita;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar.Tab;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


/**
 * Created by Svyatoslav on 08-Apr-15.
 */
public class FilterActivity extends FragmentActivity
        implements FilterDialogFragment.NoticeDialogListener
        ,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
        ,ActionBar.TabListener{
    Button bSave;
    Filter mFilter;
    String[] HDispositions;
    TextView dMin;
    TextView dMax;
    TextView minSize;
    TextView maxSize;
    int tabPos = 0;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final String TAG = ListViewActivity.class.getName();

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final String[] COUNTRIES = new String[] {
            "Czech", "Slovakia"
    };

    private static final LatLngBounds BOUNDS_CZ_SK = new LatLngBounds(
            new LatLng(48.567424, 12.006364), new LatLng(50.0276543, 22.553239));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the Google API Client if it has not been initialised yet.
        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

        setContentView(R.layout.filter_layout);

        Intent intent = getIntent();
        mFilter = intent.getExtras().getParcelable("mFilter");

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        //PlaceTypes has a bug and can't be used at the moment
        //Set<Integer> cz_places = new HashSet<Integer>(Arrays.asList(Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_1));
        //Set<Integer> cz_places = PlaceTypes.ALL;
        //AutocompleteFilter cz_filter = AutocompleteFilter.create(cz_places);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_CZ_SK, null);
        mAutocompleteView.setAdapter(mAdapter);

        //Set dialogs on minimum/maximum price
        setText(R.id.min_price, Integer.toString(mFilter.mPricemin) + " K?");
        LinearLayout pMinLayout = (LinearLayout) findViewById(R.id.MinPriceButton);
        pMinLayout.setOnClickListener(pMinOnClick);

        setText(R.id.max_price, Integer.toString(mFilter.mPricemax) + " K?");
        LinearLayout pMaxLayout = (LinearLayout) findViewById(R.id.MaxPriceButton);
        pMaxLayout.setOnClickListener(pMaxOnClick);

        //Set dialog for Estate type
        RelativeLayout estateType = (RelativeLayout) findViewById(R.id.EstateTypeButton);
        estateType.setOnClickListener(eTypeOnClick);
        bSave = (Button) findViewById(R.id.button_save);

        HDispositions = getResources().getStringArray(R.array.DispositionType);
        dMin = (TextView) findViewById(R.id.disp_min_txt);
        dMax = (TextView) findViewById(R.id.disp_max_txt);

        RangeSeekBar<Integer> seekBarDisp = new RangeSeekBar<Integer>(0, 14, getApplicationContext());
        if(mFilter.minDisposition != 0){
            seekBarDisp.setSelectedMinValue(mFilter.minDisposition);
        }
        if(mFilter.maxDisposition != 14){
            seekBarDisp.setSelectedMaxValue(mFilter.maxDisposition);
        }
        seekBarDisp.setNotifyWhileDragging(true);
        seekBarDisp.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                // handle changed range values
                dMin.setText(HDispositions[minValue]);
                dMax.setText(HDispositions[maxValue]);
                mFilter.minDisposition = minValue;
                mFilter.maxDisposition = maxValue;
                Log.i(TAG, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
            }
        });

        // add RangeSeekBar to pre-defined layout
        ViewGroup layout = (ViewGroup) findViewById(R.id.seekbar_placeholder);
        layout.addView(seekBarDisp);

        minSize = (TextView) findViewById(R.id.size_min_txt);
        maxSize = (TextView) findViewById(R.id.size_max_txt);

        RangeSeekBar<Integer> seekBarSize = new RangeSeekBar<Integer>(4, 15, getApplicationContext());
        seekBarSize.setNotifyWhileDragging(true);
        if(mFilter.mSizemin != 0){
            seekBarSize.setSelectedMinValue(mFilter.mSizemin/10);
        }
        if(mFilter.mSizemax != 1000){
            seekBarSize.setSelectedMaxValue(mFilter.mSizemax/10);
        }

        seekBarSize.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                // handle changed range values
                String minSizeOut = Integer.toString(minValue*10) + "m\u00B2";
                String maxSizeOut = Integer.toString(maxValue*10) + "m\u00B2";
                mFilter.mSizemin = minValue*10;
                mFilter.mSizemax = maxValue*10;

                if(minValue == 4){
                    minSizeOut = "Less than 40 m\u00B2";
                    mFilter.mSizemin = 0;
                }

                if(maxValue == 15){
                    maxSizeOut = "More than 150 m\u00B2";
                    mFilter.mSizemax = 1000;
                }

                minSize.setText(minSizeOut);
                maxSize.setText(maxSizeOut);
                Log.i(TAG, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
            }
        });

        layout = (ViewGroup) findViewById(R.id.seekbar_size);
        layout.addView(seekBarSize);


        bSave.setOnClickListener(bHandler);

        setUpTabs();

        ActionBar actionBar = getActionBar();

        //mFilter.mOfferType = OfferType.RENT;

        if(mFilter.mOfferType == OfferType.SALE){
            actionBar.setSelectedNavigationItem(0);
        } else if(mFilter.mOfferType == OfferType.RENT){
            actionBar.setSelectedNavigationItem(1);
        } else  if(mFilter.mOfferType == OfferType.MATE){
            actionBar.setSelectedNavigationItem(2);
        }


        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);

    } //OnCreate()

    View.OnClickListener bHandler = new View.OnClickListener() {
        public void onClick(View v) {

            Toast.makeText(getApplicationContext(), "Your preferences have been successfully saved", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("Filter", mFilter);
            setResult(RESULT_OK, returnIntent);
            finish();

        }
    };

    View.OnClickListener pMinOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showNoticeDialog("Minimum Price", 0);
        }
    };

    View.OnClickListener pMaxOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showNoticeDialog("Maximum Price", 1);
        }
    };

    View.OnClickListener eTypeOnClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            showEstateTypeDialog();
        }
    };

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            mFilter.mAddress = (String) item.description;

            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    private void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned and which Google APIs our app uses.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

        // Disable API access in the adapter because the client was not initialised correctly.
        mAdapter.setGoogleApiClient(null);

    }

    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "GoogleApiClient connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);
        Log.e(TAG, "GoogleApiClient connection suspended.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.action_settings == item.getItemId()){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showEstateTypeDialog() {
        DialogFragment dialog = new EstateTypeDialog();
        dialog.show(getSupportFragmentManager(), "Pick Estate Type");
    }

    public void showNoticeDialog(String msg, int mDialogState) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = FilterDialogFragment.newInstance(msg, "Ok", "Cancel", mDialogState);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String uInput, int mDialogState) {
        // User touched the dialog's positive button
        //TODO change solution, too much code is being repeated
        int mInput = Integer.parseInt(uInput);
        int pMin;
        int pMax;
        String mCurr = " K?";
        boolean isSale = mFilter.mOfferType == OfferType.RENT ? true : false;

        if(mDialogState == 0){
            pMin = mInput;
            if(isSale){
                mFilter.mPricemin = pMin;
            } else {
                mFilter.mPriceMonthMin = pMin;
                mCurr += "/month";
            }
            setText(R.id.min_price, Integer.toString(pMin) + mCurr);

        } else{
            pMax = mInput;
            if(isSale){
                mFilter.mPricemax = pMax;
            } else {
                mFilter.mPriceMonthMax = pMax;
                mCurr += "/month";
            }
            setText(R.id.max_price, Integer.toString(pMax) + mCurr);
        }
        dialog.dismiss();
        findViewById(R.id.filter_main_layout).requestFocus();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        dialog.dismiss();
    }

    public void setText(int res, String toThis){
        TextView v = (TextView) findViewById(res);
        v.setText(toThis);
    }

    private void setUpTabs(){
        //Get and set ap actionBar
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_TABS);

        // for each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText("Buy")
                .setTabListener(this), false);
        actionBar.addTab(actionBar.newTab().setText("Rent")
                .setTabListener(this), false);
        actionBar.addTab(actionBar.newTab().setText("Roommate")
                .setTabListener(this), false);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, show the tab contents in the
        // container view.
        Toast.makeText(getApplicationContext(), "Idk what to say, it prob works", Toast.LENGTH_SHORT).show();
        String priceEnd;
        tabPos = tab.getPosition();
        int pMin;
        int pMax;

        if(tabPos == 0){
            priceEnd = " K?";
            pMin =  mFilter.mPricemin;
            pMax = mFilter.mPricemax;
        } else {
            priceEnd = " K?/month";
            pMin = mFilter.mPriceMonthMin;
            pMax = mFilter.mPriceMonthMax;
        }

        switch (tabPos){
            case 0:
                mFilter.mOfferType = OfferType.SALE;
                break;
            case 1:
                mFilter.mOfferType = OfferType.RENT;
                break;
            case 2:
                mFilter.mOfferType = OfferType.MATE;
        }

        setText(R.id.min_price, Integer.toString(pMin) + priceEnd);
        setText(R.id.max_price, Integer.toString(pMax) + priceEnd);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) { }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) { }

}