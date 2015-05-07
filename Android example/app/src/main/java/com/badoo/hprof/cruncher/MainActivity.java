package com.badoo.hprof.cruncher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.badoo.hprof.cruncher.library.HprofCatcher;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HprofCatcher.init(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.fillMemory).setOnClickListener(this);
    }


    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "InfiniteLoopStatement"})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fillMemory) {
            List<Bitmap> data = new ArrayList<>();
            while (true) {
                data.add(Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888));
            }
        }
    }
}
