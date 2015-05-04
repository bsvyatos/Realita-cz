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
    int mSizemin;
    int mSizemax;
    Boolean mBalkon;
    OfferType mOfferType;
    int qParam;
    int minDisposition;
    int maxDisposition;

    public Filter(int mPricemin, int mPricemax, int mSizemin, int mSizemax, boolean mBalkon, int mOfferType, int qParam, int minDisposition, int maxDisposition) {
        this.mPricemin = mPricemin;
        this.mPricemax = mPricemax;
        this.mSizemin = mSizemin;
        this.mSizemax = mSizemax;
        this.mBalkon = mBalkon;
        this.mOfferType = OfferType.values()[mOfferType];
        this.qParam = qParam;
        this.minDisposition = minDisposition;
        this.maxDisposition = maxDisposition;
    }

    public Filter(Parcel in){
        this.mPricemin = in.readInt();
        this.mPricemax = in.readInt();
        this.mSizemin = in.readInt();
        this.mSizemax = in.readInt();
        this.mBalkon =  in.readByte() != 0;
        this.mOfferType = OfferType.values()[in.readInt()];
        this.qParam = in.readInt();
        this.minDisposition = in.readInt();
        this.maxDisposition = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mPricemin);
        parcel.writeInt(mPricemax);
        parcel.writeInt(mSizemin);
        parcel.writeInt(mSizemax);
        parcel.writeByte((byte) (mBalkon ? 1 : 0));
        parcel.writeInt(mOfferType.ordinal());
        parcel.writeInt(qParam);
        parcel.writeInt(minDisposition);
        parcel.writeInt(maxDisposition);
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




