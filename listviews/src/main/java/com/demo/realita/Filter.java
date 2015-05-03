package com.demo.realita;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Svyatoslav on 07-Apr-15.
 */

public class Filter implements Parcelable{
    int mPricemin;
    int mPricemax;
    double mSizemin;
    double mSizemax;
    Boolean mBalkon;
    OfferType mOfferType;
    int qParam;

    public Filter(int mPricemin, int mPricemax, double mSizemin, double mSizemax, Boolean mBalkon, int mOfferType, int qParam) {
        this.mPricemin = mPricemin;
        this.mPricemax = mPricemax;
        this.mSizemin = mSizemin;
        this.mSizemax = mSizemax;
        this.mBalkon = mBalkon;
        this.mOfferType = OfferType.values()[mOfferType];
        this.qParam = qParam;
    }

    public Filter(Parcel in){
        this.mPricemin = in.readInt();
        this.mPricemax = in.readInt();
        this.mSizemin = in.readDouble();
        this.mSizemax = in.readDouble();
        this.mBalkon =  in.readByte() != 0;
        this.mOfferType = OfferType.values()[in.readInt()];
        this.qParam = in.readInt();
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
        parcel.writeInt(mOfferType.ordinal());
        parcel.writeInt(qParam);
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




