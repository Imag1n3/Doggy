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
import android.widget.TextView;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BeagleActivity extends AppCompatActivity {

    private ProgressDialog dl;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private TextView tView;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> mostPopular = new ArrayList<>();
    private ArrayList<String> mostRecent = new ArrayList<>();
    private TreeMap<String, Long> note_counts = new TreeMap<>();
    private HashMap<String, Long> timestamps = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beagle);

        DownloadImages download = new DownloadImages();
        download.execute();

        //Setting the home button to close this activity e.g. return to the home page
        Button btn_home = (Button)findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close the current activity
                finish();
                //Reload the home page
                Intent intent = new Intent(BeagleActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        //Button that allows ordering by most recent or most popular
        Button btn_orderBy = (Button)findViewById(R.id.btn_orderBy);
        String[] options = new String[] {"Most popular", "Most recent"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, options);

        btn_orderBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BeagleActivity.this)
                        .setTitle("Order by")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        gridView = (GridView)findViewById(R.id.grdv_beagle);
                                        gridViewAdapter = new GridViewAdapter(BeagleActivity.this, mostPopular);

                                        gridView.setAdapter(gridViewAdapter);

                                        tView = (TextView)findViewById(R.id.txtv_filter);
                                        tView.setText("Most popular");
                                        break;
                                    case 1:
                                        gridView = (GridView)findViewById(R.id.grdv_beagle);
                                        gridViewAdapter = new GridViewAdapter(BeagleActivity.this, mostRecent);

                                        gridView.setAdapter(gridViewAdapter);

                                        tView = (TextView)findViewById(R.id.txtv_filter);
                                        tView.setText("Most recent");
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    //Overriding the back button, forcing the Grid View in the home page to reload
    @Override
    public void onBackPressed() {
        //Close the current activity
        finish();
        //Reload the home page
        Intent intent = new Intent(BeagleActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    //Async task which downloads most recent photos for tag beagle
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

            //First query (gets 20 photos with tag beagle)
            Map<String, Long> options = new HashMap<>();
            options.put("limit", 20l);
            Long timeStamp = 0l;

            PhotoPost photoPost;
            for (Post post : client.tagged("beagle", options)) {

                if(post instanceof PhotoPost) {
                    photoPost = (PhotoPost) post;
                    for (Photo photo : photoPost.getPhotos()) {

                        if (!(photo.getSizes().get(0).getUrl()).contains(".gif") && urls.size() < 20) {
                            timeStamp = post.getTimestamp();
                            urls.add(photo.getSizes().get(0).getUrl());
                            //Adding the photos and their notecounts in hashmap
                            note_counts.put(photo.getSizes().get(0).getUrl(), post.getNoteCount());
                            //Adding the photo's URLS and their timestamps in a hashmap
                            timestamps.put(photo.getSizes().get(0).getUrl(), post.getTimestamp());
                        } else {
                            break;
                        }
                    }
                }
            }

            //Second query (gets 20 photos with tag beagle)
            Map<String, Long> options2 = new HashMap<>();
            options2.put("limit", 20l);
            options2.put("before", timeStamp);

            for (Post post : client.tagged("beagle", options2)) {
                if (post instanceof PhotoPost) {
                    photoPost = (PhotoPost) post;
                    for (Photo photo : photoPost.getPhotos()) {

                        if (!(photo.getSizes().get(0).getUrl()).contains(".gif") && urls.size() < 40) {
                            timeStamp = post.getTimestamp();
                            urls.add(photo.getSizes().get(0).getUrl());
                            //Adding the photos and their notecounts in hashmap
                            note_counts.put(photo.getSizes().get(0).getUrl(), post.getNoteCount());
                            //Adding the photo's URLS and their timestamps in a hashmap
                            timestamps.put(photo.getSizes().get(0).getUrl(), post.getTimestamp());
                        } else {
                            break;
                        }
                    }
                }
            }

            //Third query (gets 10 photos with tag beagle)
            Map<String, Long> options3 = new HashMap<>();
            options3.put("limit", 20l);
            options3.put("before", timeStamp);

            for (Post post : client.tagged("beagle", options3)) {
                if (post instanceof PhotoPost) {
                    photoPost = (PhotoPost) post;
                    for (Photo photo : photoPost.getPhotos()) {
                        if (!(photo.getSizes().get(0).getUrl()).contains(".gif") && urls.size() < 50) {
                            urls.add(photo.getSizes().get(0).getUrl());
                            //Adding the photos and their notecounts in hashmap
                            note_counts.put(photo.getSizes().get(0).getUrl(), post.getNoteCount());
                            //Adding the photo's URLS and their timestamps in a hashmap
                            timestamps.put(photo.getSizes().get(0).getUrl(), post.getTimestamp());
                        } else {
                            break;
                        }
                    }
                }
            }

            //Map for sorting by Most Popular
            TreeMap<String, Long> desc = new TreeMap<>(note_counts.descendingMap());
            mostPopular.addAll(desc.keySet());
            //Map for sorting by Most Recent
            Map<String, Long> asc = sortByComparator(timestamps);
            mostRecent.addAll(asc.keySet());

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = ProgressDialog.show(BeagleActivity.this, "Downloading photos...", "");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dl.hide();

            gridView = (GridView)findViewById(R.id.grdv_beagle);
            gridViewAdapter = new GridViewAdapter(BeagleActivity.this, mostRecent);
            gridView.setAdapter(gridViewAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(BeagleActivity.this, ViewImageBeagActivity.class);
                    intent.putExtra("position", position);

                    startActivity(intent);
                }
            });
        }
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
