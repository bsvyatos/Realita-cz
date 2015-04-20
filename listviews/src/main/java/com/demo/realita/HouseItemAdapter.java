package com.demo.realita;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.realita.R;
import com.google.gson.Gson;

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

    public HouseItemAdapter(Context context, int resource) {
        super(context, resource);

        this.mContext = context;
        this.mLayoutResourceId = resource;


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

        //setting the view to reflect the data we need to display
        holder.addrView.setText(houseItem.mAddress);
        holder.infoView.setText(houseItem.mHouseInfo);
        holder.priceView.setText(String.valueOf(houseItem.mPrice) + " Kč");

        //for getting the image
        int resID = mContext.getResources().getIdentifier(houseItem.mImgPreview, "drawable", mContext.getPackageName());
        holder.imgView.setImageResource(resID);

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
            //Creating a shared preference
            SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(getContext());

            //Retrieve Filter object from Shared Preferences if possible
            Gson gson =  new Gson();
            String json = mPrefs.getString("Favourites", "");
            FavouriteArray FavArr = gson.fromJson(json, FavouriteArray.class);

            if(FavArr == null){
                List<String> fList = new ArrayList<String>();
                FavArr = new FavouriteArray(fList);
            }

            Integer viewPosition = (Integer) button.getTag();
            HouseItem p = getItem(viewPosition);

            if(button.isSelected()){
                FavArr.favList.add(p.Id);
            } else {
                FavArr.favList.remove(p.Id);
            }

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            json = gson.toJson(FavArr);
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
}
