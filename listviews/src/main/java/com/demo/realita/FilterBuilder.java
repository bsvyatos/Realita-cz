package com.demo.realita;

/**
 * Created by Svyatoslav on 08-Apr-15.
 */
public class FilterBuilder {
    public static int mPricemin = 0;
    public static int mPricemax = 50000000;
    public static double mSizemin = 0;
    public static double mSizemax = 1000;
    public static Boolean mBalkon = null;
    public static int mOfferType = OfferType.SALE.ordinal();
    public static int qParam = 0;
    
    public FilterBuilder(){ }

    public Filter build(){
        return new Filter(mPricemin, mPricemax, mSizemin, mSizemax, mBalkon, mOfferType, qParam);
    }

    public FilterBuilder priceMin(int mPricemin){
        this.mPricemin = mPricemin;
        return this;
    }

    public FilterBuilder priceMax(int mPricemax){
        this.mPricemax = mPricemax;
        return this;
    }

    public FilterBuilder sizeMin(int mSizemin){
        this.mSizemin = mSizemin;
        return this;
    }

    public FilterBuilder sizeMax(int mSizemax){
        this.mSizemax = mSizemax;
        return this;
    }

    public FilterBuilder Balkon(boolean mBalkon){
        this.mBalkon = mBalkon;
        return this;
    }

    public FilterBuilder OfferType(int mOfferType){
        this.mOfferType = mOfferType;
        return this;
    }
    public FilterBuilder qParam(int qParam){
        this.qParam = qParam;
        return this;
    }
}
