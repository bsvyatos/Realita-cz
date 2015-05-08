package com.demo.realita;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;

public class Utils {
    private static DecimalFormat mDf = null;
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
}
