package com.demo.realita;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public static String[] HDispositions;
    public static String[] HEstateType;
    public static String[] HOwnership;
    public static String[] HConstruction;
    public static String[] HEquipment;

    private static DecimalFormat mDf = null;
    private static float mDensityDpi = 0.f;

    public static String concatList(List<String> sList, String separator)
    {
        Iterator<String> iter = sList.iterator();
        StringBuilder sb = new StringBuilder();

        while (iter.hasNext())
        {
            sb.append(iter.next()).append( iter.hasNext() ? separator : "");
        }
        return sb.toString();
    }

    public static DecimalFormat numbersFormat() {
        if(mDf == null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            mDf = new DecimalFormat();
            mDf.setDecimalFormatSymbols(symbols);
            mDf.setGroupingSize(3);
            mDf.setMaximumFractionDigits(0);
        }

        return mDf;
    }

    public static void setDensityDpi(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        mDensityDpi = metrics.densityDpi;
    }

    public static int convertDpToPixel(float dp){
        //assert mDensityDpi != 0.f
        float px = dp * (mDensityDpi / 160f);
        return Math.round(px);
    }

    public static float convertPixelsToDp(float px){
        float dp = px / (mDensityDpi / 160f);
        return dp;
    }
}
