package com.example.slavi.doggy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ProgressDialog dl;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> orderedUrls = new ArrayList<>();
    private HashMap<String, Long> timestamps = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Check for internet connection
        try {
            if (!isOnline()) {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Button btn_breed = (Button)findViewById(R.id.btn_selectBreed);
        String[] breeds = new String[] {"Labrador", "Beagle", "Cockerspaniel"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, breeds);

        btn_breed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Select Dog Breed")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch(which) {
                                    case 0:
                                        startActivity(new Intent(HomeActivity.this, LabradorActivity.class));
                                        finish();
                                        break;
                                    case 1:
                                        startActivity(new Intent(HomeActivity.this, BeagleActivity.class));
                                        finish();
                                        break;
                                    case 2:
                                        startActivity(new Intent(HomeActivity.this, CockerspanielActivity.class));
                                        finish();
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        DownloadImages download = new DownloadImages();
        download.execute();
    }

    //Async task which downloads most recent photos for tag labrador
    private class DownloadImages extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            //Authenticate via OAuth
            JumblrClient client = new JumblrClient(
                    "LfbunCVV9nOklMzFaoSGgmYG7jU4Jzt1kMjchWDXxXjEabhNtT",
                    "Aeftb6z3MpR9Nxj9wUFXMYyrcKTIMeCaJnFJ1gdvNH8oczDfMF"
            );
            client.setToken(
                    "LsvYhkMInXLtIvU5JffQSMesYzEvbyxwl0nksaGdi9TZmRgLUy",
                    "UILbtYeCzHyXQ6ATHh3vG4wcqV8p1hncGoLImSvX8jy8EpGDNf"
            );

            //First query (gets 10 photos with tag labrador)
            Map<String, Long> options = new HashMap<>();
            options.put("limit", 20l);

            PhotoPost photoPost;
            for (Post post : client.tagged("labrador", options)) {

                if (post instanceof PhotoPost) {
                    photoPost = (PhotoPost) post;
                    for (Photo photo : photoPost.getPhotos()) {

                        if (!(photo.getSizes().get(0).getUrl()).contains(".gif") && urls.size() < 10) {
                            urls.add(photo.getSizes().get(0).getUrl());
                            //Adding the photo's URLS and their timestamps in a hashmap
                            timestamps.put(photo.getSizes().get(0).getUrl(), post.getTimestamp());
                        } else {
                            break;
                        }
                    }
                }
            }

            //Second query (gets 10 photos with tag beagle)
            Map<String, Long> options2 = new HashMap<>();
            options2.put("limit", 20l);
            //options2.put("before", timeStamp);

            for (Post post : client.tagged("beagle", options2)) {
                if (post instanceof PhotoPost) {
                    photoPost = (PhotoPost) post;
                    for (Photo photo : photoPost.getPhotos()) {

                        if (!(photo.getSizes().get(0).getUrl()).contains(".gif") && urls.size() < 20) {
                            urls.add(photo.getSizes().get(0).getUrl());
                            //Adding the photo's URLS and their timestamps in a hashmap
                            timestamps.put(photo.getSizes().get(0).getUrl(), post.getTimestamp());
                        } else {
                            break;
                        }
                    }
                }
            }

            //Third query (gets 10 photos with tag cockerspaniel)
            Map<String, Long> options3 = new HashMap<>();
            options3.put("limit", 20l);
            //options3.put("before", timeStamp);

            for (Post post : client.tagged("cockerspaniel", options3)) {
                if (post instanceof PhotoPost) {
                    photoPost = (PhotoPost) post;
                    for (Photo photo : photoPost.getPhotos()) {

                        if (!(photo.getSizes().get(0).getUrl()).contains(".gif") && urls.size() < 30) {
                            urls.add(photo.getSizes().get(0).getUrl());
                            //Adding the photo's URLS and their timestamps in a hashmap
                            timestamps.put(photo.getSizes().get(0).getUrl(), post.getTimestamp());
                        } else {
                            break;
                        }
                    }
                }
            }

            //Sorting the posts by timestamp
            Map<String, Long> sorted = sortByComparator(timestamps);
            orderedUrls.addAll(sorted.keySet());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = ProgressDialog.show(HomeActivity.this, "Downloading photos...", "");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dl.hide();

            gridView = (GridView) findViewById(R.id.grdv_home);
            gridViewAdapter = new GridViewAdapter(HomeActivity.this, orderedUrls);
            gridView.setAdapter(gridViewAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(HomeActivity.this, ViewImageHomeActivity.class);
                    intent.putExtra("position", position);

                    startActivity(intent);
                }
            });
        }
    }

    //Method that checks if internet connection is present
    public boolean isOnline() throws InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Method for sorting a Map by ascending order
    private static Map<String, Long> sortByComparator(Map<String, Long> unsortedMap) {

        // Convert Map to List
        List<Map.Entry<String, Long>> list =
                new LinkedList<>(unsortedMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Long> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<String, Long>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Long> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
