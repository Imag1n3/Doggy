package com.example.slavi.doggy;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    static ArrayList<String> items;

    public GridViewAdapter(Context context, ArrayList<String> items){
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView img;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int imageWidth = width / 2;
        if (convertView == null) {
            img = new ImageView(context);
            convertView = img;
        } else {
            img = (ImageView) convertView;
        }

        Glide.with(context)
                .load(items.get(position))
                .asBitmap()
                .override(imageWidth, imageWidth)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);

        return convertView;
    }
}
