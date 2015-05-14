package com.demo.realita;

import android.widget.ImageView;

/**
 * Created by Svyatoslav on 5/14/2015.
 */
public class Params{
    ImageView img;
    String param;
    int width;
    int height;

    Params(ImageView img, String param, int width, int height){
        this.img = img;
        this.param = param;
        this.width = width;
        this.height = height;
    }
}
