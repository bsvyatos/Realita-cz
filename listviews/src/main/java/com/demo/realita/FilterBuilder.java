package com.demo.realita;

/**
 * Created by Svyatoslav on 08-Apr-15.
 */
public class FilterBuilder {
    static int mPricemin = 0;
    static int mPricemax = 50000000;
    static int mSizemin = 0;
    static int mSizemax = 1000;
    static Boolean mBalkon = true;
    static int mOfferType = OfferType.SALE.ordinal();
    static int qParam = 0;
    static int minDisposition = 0;
    static int maxDisposition = 14;
    
    public FilterBuilder(){ }

    public Filter build(){
        return new Filter(mPricemin, mPricemax, mSizemin, mSizemax, mBalkon, mOfferType, qParam, minDisposition, maxDisposition);
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

    public FilterBuilder minDisposition(int minDisposition){
        this.minDisposition = minDisposition;
        return this;
    }

    public FilterBuilder maxDisposition(int maxDisposition){
        this.maxDisposition = maxDisposition;
        return this;
    }
}
