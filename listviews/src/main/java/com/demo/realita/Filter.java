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
    int mPriceMonthMin;
    int mPriceMonthMax;
    int mSizemin;
    int mSizemax;
    Boolean mBalkon;
    OfferType mOfferType;
    int qParam;
    int minDisposition;
    int maxDisposition;
    String mAddress;

    public Filter(int mPricemin, int mPricemax, int mSizemin, int mPriceMonthMin, int mPriceMonthMax, int mSizemax, boolean mBalkon
            , int mOfferType, int qParam, int minDisposition, int maxDisposition, String mAddress) {
        this.mPricemin = mPricemin;
        this.mPricemax = mPricemax;
        this.mPriceMonthMin = mPriceMonthMin;
        this.mPriceMonthMax = mPriceMonthMax;
        this.mSizemin = mSizemin;
        this.mSizemax = mSizemax;
        this.mBalkon = mBalkon;
        this.mOfferType = OfferType.values()[mOfferType];
        this.qParam = qParam;
        this.minDisposition = minDisposition;
        this.maxDisposition = maxDisposition;
        this.mAddress = mAddress;
    }

    public Filter(Parcel in){
        this.mPricemin = in.readInt();
        this.mPricemax = in.readInt();
        this.mPriceMonthMin = in.readInt();
        this.mPriceMonthMax = in.readInt();
        this.mSizemin = in.readInt();
        this.mSizemax = in.readInt();
        this.mBalkon =  in.readByte() != 0;
        this.mOfferType = OfferType.values()[in.readInt()];
        this.qParam = in.readInt();
        this.minDisposition = in.readInt();
        this.maxDisposition = in.readInt();
        this.mAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mPricemin);
        parcel.writeInt(mPricemax);
        parcel.writeInt(mPriceMonthMin);
        parcel.writeInt(mPriceMonthMax);
        parcel.writeInt(mSizemin);
        parcel.writeInt(mSizemax);
        parcel.writeByte((byte) (mBalkon ? 1 : 0));
        parcel.writeInt(mOfferType.ordinal());
        parcel.writeInt(qParam);
        parcel.writeInt(minDisposition);
        parcel.writeInt(maxDisposition);
        parcel.writeString(mAddress);
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




