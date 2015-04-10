package com.demo.realita;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Svyatoslav on 07-Apr-15.
 */

public class Filter implements Parcelable{
    int mPricemin;
    int mPricemax;
    double mSizemin;
    double mSizemax;
    boolean mBalkon;

    public Filter(int mPricemin, int mPricemax, double mSizemin, double mSizemax, boolean mBalkon) {
        this.mPricemin = mPricemin;
        this.mPricemax = mPricemax;
        this.mSizemin = mSizemin;
        this.mSizemax = mSizemax;
        this.mBalkon = mBalkon;
    }

    public Filter(Parcel in){
        this.mPricemin = in.readInt();
        this.mPricemax = in.readInt();
        this.mSizemin = in.readDouble();
        this.mSizemax = in.readDouble();
        this.mBalkon =  in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mPricemin);
        parcel.writeInt(mPricemax);
        parcel.writeDouble(mSizemin);
        parcel.writeDouble(mSizemax);
        parcel.writeByte((byte) (mBalkon ? 1 : 0));
    }

    public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {

        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

}




