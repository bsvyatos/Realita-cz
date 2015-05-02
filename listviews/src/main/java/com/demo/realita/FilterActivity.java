package com.demo.realita;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
        ,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    Button bSave;
    Filter mFilter;
    EditText sMin;
    EditText sMax;
    TextView dispMin;
    String[] HDispositions;
    TextView dMin;
    TextView dMax;

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

        setText(R.id.min_price, Integer.toString(mFilter.mPricemin) + " K?");
        LinearLayout pMinLayout = (LinearLayout) findViewById(R.id.MinPriceButton);
        pMinLayout.setOnClickListener(pMinOnClick);

        setText(R.id.max_price, Integer.toString(mFilter.mPricemax) + " K?");
        LinearLayout pMaxLayout = (LinearLayout) findViewById(R.id.MaxPriceButton);
        pMaxLayout.setOnClickListener(pMaxOnClick);

        sMin = (EditText) findViewById(R.id.size_min);
        sMin.setHint(Double.toString(mFilter.mSizemin));

        sMax = (EditText) findViewById(R.id.size_max);
        sMax.setHint(Double.toString(mFilter.mSizemax));

        bSave = (Button) findViewById(R.id.button_save);

        SeekBar mDisposition = (SeekBar) findViewById(R.id.disposition_bar);
        mDisposition.setMax(14);
        dispMin = (TextView) findViewById(R.id.disposition_min);
        HDispositions = getResources().getStringArray(R.array.Disposition);
        mDisposition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dispMin.setText(HDispositions[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dMin = (TextView) findViewById(R.id.disp_min_txt);
        dMax = (TextView) findViewById(R.id.disp_max_txt);

        RangeSeekBar<Integer> seekBar_disp = new RangeSeekBar<Integer>(0, 14, getApplicationContext());
        seekBar_disp.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                // handle changed range values
                dMin.setText(HDispositions[minValue]);
                dMax.setText(HDispositions[maxValue]);
                Log.i(TAG, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
            }
        });

        // add RangeSeekBar to pre-defined layout
        ViewGroup layout = (ViewGroup) findViewById(R.id.seekbar_placeholder);
        layout.addView(seekBar_disp);

        bSave.setOnClickListener(bHandler);

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
    }

    View.OnClickListener bHandler = new View.OnClickListener() {
        public void onClick(View v) {

            try {
                mFilter.mSizemin = Integer.parseInt(sMin.getText().toString());
            } catch(NumberFormatException s){ }
            try {
                mFilter.mSizemax = Integer.parseInt(sMax.getText().toString());
            } catch(NumberFormatException s){ }

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

    public void showNoticeDialog(String msg, int mDialogState) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = FilterDialogFragment.newInstance(msg, "Ok", "Cancel", mDialogState);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String uInput, int mDialogState) {
        // User touched the dialog's positive button
        int mInput = Integer.parseInt(uInput);
        if(mDialogState == 0){
            mFilter.mPricemin = mInput;
            setText(R.id.min_price, Integer.toString(mFilter.mPricemin) + " K?");
        } else{
            mFilter.mPricemax = mInput;
            setText(R.id.max_price, Integer.toString(mFilter.mPricemax) + " K?");
        }
        dialog.dismiss();
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

}