package com.demo.realita;

import android.content.Context;

import java.io.File;

/**
 * Created by Svyatoslav on 5/23/2015.
 */
public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        this(context, );
    }

    public FileCache(Context context, long evt) {
        //Find the dir to save cached images
        cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        return new File(cacheDir, String.valueOf(url.hashCode()));
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }
}