package com.demo.realita;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.realita.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Svyatoslav on 14-Mar-15.
 */
public class HouseItemAdapter extends ArrayAdapter<HouseItem>{

    Context mContext;
    int mLayoutResourceId;
    //Object contains only the List itself, should be re-written
    FavouriteArray FavArr;
    Bitmap bitmap;
    String imgUrl;
    DecimalFormat mDf;
    private static final String TAG = ListViewActivity.class.getName();


    public HouseItemAdapter(Context context, int resource, FavouriteArray FavArr) {
        super(context, resource);

        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.FavArr = FavArr;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        mDf = new DecimalFormat();
        mDf.setDecimalFormatSymbols(symbols);
        mDf.setGroupingSize(3);
        mDf.setMaximumFractionDigits(2);
    }

    @Override
    public HouseItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {

        PlaceHolder holder = null;

        // if we currently have no row View to reuse..
        if(row == null){
            //Create a new View
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new PlaceHolder();

            holder.addrView = (TextView) row.findViewById(R.id.AddressView);
            holder.infoView = (TextView) row.findViewById(R.id.infoTextView);
            holder.priceView = (TextView) row.findViewById(R.id.priceTextView);
            holder.imgView = (ImageView) row.findViewById(R.id.imageView);
            holder.imgButt = (ImageButton) row.findViewById(R.id.favoriteButton);

            row.setTag(holder);

        } else {
            //Otherwise use existing one
            holder = (PlaceHolder) row.getTag();
        }

        //Getting the data from the data array
        HouseItem houseItem = getItem(position);

        //Setup and reuse the same listener for each row
        holder.imgView.setOnClickListener(PopupListener);
        holder.imgButt.setOnClickListener(favButtonListener);
        Integer rowPosition = position;
        holder.imgView.setTag(rowPosition);
        holder.imgButt.setTag(rowPosition);

        //setting the view to reflect the data we need to display
        holder.addrView.setText(houseItem.mAddress);
        holder.infoView.setText(houseItem.mDescription);
        holder.priceView.setText(mDf.format(houseItem.mPrice) + " Kč");

        //for getting the image
        JSONObject jObj;
        try {
            jObj = new JSONObject(houseItem.mImgPreview);
            JSONArray jArray = jObj.getJSONArray("thumbs");
            imgUrl = jArray.getString(0);
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }

        Params async = new Params(holder.imgView, imgUrl);
        new LoadImage().execute(async);
        //holder.imgView.setImageResource(R.drawable.home1);


        //state of the start
        if(FavArr.favList.contains(houseItem.Id)){
            holder.imgButt.setSelected(true);
        }

        //returning the row
        return row;

    }


    View.OnClickListener PopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer viewPosition = (Integer) view.getTag();
            HouseItem p = getItem(viewPosition);

            Toast.makeText(getContext(), p.mHouseInfo, Toast.LENGTH_SHORT).show();

        }
    };

    View.OnClickListener favButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {
            button.setSelected(!button.isSelected());

            Integer viewPosition = (Integer) button.getTag();
            HouseItem p = getItem(viewPosition);

            if(!button.isSelected()){
                FavArr.favList.add(p.Id);
            } else {
                FavArr.favList.remove(p.Id);
            }

            //Load Shared preferences and save new version of FavArr as "Favourites"
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor prefsEditor = mPrefs.edit();

            Gson gson =  new Gson();
            String json = gson.toJson(FavArr);
            prefsEditor.putString("Favourites", json);
            prefsEditor.commit();

        }
    };


    private static class PlaceHolder {
        TextView addrView;
        TextView infoView;
        TextView priceView;
        ImageView imgView;
        ImageButton imgButt;
    }

    private static class Params{
        ImageView img;
        String param;
        Params(ImageView img, String param){
            this.img = img;
            this.param = param;
        }
    }

    private class LoadImage extends AsyncTask<Params, String, Bitmap> {
        private ImageView mImg;
//        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();



        protected Bitmap doInBackground(Params... args) {
            mImg = args[0].img;

            try {
                bitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeStream((InputStream) new URL(args[0].param).getContent())
                        , 280, 280, true);

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
