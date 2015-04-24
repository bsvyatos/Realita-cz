package com.demo.realita;

/**
 * Created by Svyatoslav on 08-Apr-15.
 */
public class FilterBuilder {
    private int mPricemin = 0;
    private int mPricemax = 50000000;
    private double mSizemin = 0;
    private double mSizemax = 1000;
    private boolean mBalkon = true;
    private String mOfferType = "Pronájem";
    private int qParam = 0;

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

    public FilterBuilder OfferType(String mOfferType){
        this.mOfferType = mOfferType;
        return this;
    }

    public FilterBuilder qParam(int qParam){
        this.qParam = qParam;
        return this;
    }
}
