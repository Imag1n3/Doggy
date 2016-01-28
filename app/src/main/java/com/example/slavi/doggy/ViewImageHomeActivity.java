package com.example.slavi.doggy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ViewImageHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_home);

        //Setting the back button to close this activity e.g. return to the previous page
        Button btn_home = (Button)findViewById(R.id.btnimv_back);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Saving the photo to the phone's gallery
        Button btn_save = (Button)findViewById(R.id.btnimv_save);
        final ImageView imageView = (ImageView)findViewById(R.id.imgvAdapter);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();

                String root = Environment.getExternalStorageDirectory().toString();
                File dir = new File(root + "/Pictures/Doggy_Photos/");
                dir.mkdirs();

                Date d = new Date();
                CharSequence s = DateFormat.format("dd-MM-yy_hh-mm-ss", d.getTime());

                String photoName = "HomePage-" + s.toString() + ".jpg";
                File file = new File(dir, photoName);

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(getApplicationContext(), "Saved to " + dir + "/" + photoName, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Adding the photo to the gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                ViewImageHomeActivity.this.sendBroadcast(mediaScanIntent);
            }
        });

        //Sharing the photo
        Button btn_share = (Button)findViewById(R.id.btnimv_share);
        btn_share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();

                String root = Environment.getExternalStorageDirectory().toString();
                File dir = new File(root + "/Pictures/Doggy_Photos/Shared_photos/");
                dir.mkdirs();

                Date d = new Date();
                CharSequence s = DateFormat.format("dd-MM-yy_hh-mm-ss", d.getTime());

                String photoName = "HomePage-" + s.toString() + ".jpg";
                File file = new File(dir, photoName);

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(share,"Share via"));
            }
        });

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            Glide.with(ViewImageHomeActivity.this)
                    .load(GridViewAdapter.items.get(position))
                    .override(width, width)
                    .into(imageView);
        }
    }
}
