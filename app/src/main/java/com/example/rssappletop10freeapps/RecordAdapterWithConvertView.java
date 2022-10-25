package com.example.rssappletop10freeapps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RecordAdapterWithConvertView extends ArrayAdapter {

    private static final String TAG = "R.A.WithConvertView";

    private final int layoutResource;
    private final LayoutInflater layoutInflater;

    private List<Record> feedEntries;

    private ArrayList<Bitmap> images;
    private final int NUMBER_OF_RECORDS = 10;

    public RecordAdapterWithConvertView(@NonNull Context context,
                                        int resource,
                                        List<Record> feedEntries) {
        super(context, resource, feedEntries);

        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.feedEntries = feedEntries;

        // Data members
        images = new ArrayList<Bitmap>(NUMBER_OF_RECORDS);

        for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
            images.add(null);
        }

    }

    public int getCount() {
        return feedEntries.size();

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

        }
        else {
            //viewHolder = (ViewHolder) convertView.getTag();
        }

        // This is expensive
        TextView nameText = convertView.findViewById(R.id.nameText);
        TextView artistText = convertView.findViewById(R.id.artistText);
        TextView releaseDateText = convertView.findViewById(R.id.releaseDateText);
        ImageView appImage = convertView.findViewById(R.id.appImage);

        Record currentEntry = feedEntries.get(position);


        nameText.setText(currentEntry.getName());
        artistText.setText(currentEntry.getArtist());
        releaseDateText.setText(currentEntry.getReleaseDate());


        // Getting an image from a URL, outside of the main/UI thread:
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // or
        //ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.execute(new Runnable() {
            @Override
            public void run() { //Background work here

                HttpURLConnection connection = null;

                try {
                    URL url = new URL(currentEntry.getImageURL());

                    if(connection == null) {
                        Log.d(TAG, "connection is null: ");
                        connection = (HttpURLConnection) url.openConnection();
                        //connection.setDoInput(true);

                        connection.connect();

                    }

                    InputStream input = connection.getInputStream();
                    Bitmap imageIcon = BitmapFactory.decodeStream(input);


                    if(images.get(position) == null) {
                        images.set(position, imageIcon);
                        imageIcon = Bitmap.createScaledBitmap(imageIcon,100,100,true);
                        appImage.setImageBitmap(imageIcon);
                        Log.d(TAG, "Setting image " + position + " for the first time");
                    }
                    else {
                        appImage.setImageBitmap(images.get(position));
                    }

                    connection.disconnect();
                    //Log.d(TAG, "image found!");
                }
                catch(MalformedURLException e) {
                    Log.d(TAG, e.getMessage());
                }
                catch(Exception e) {
                    Log.d(TAG, e.toString());

                }

            } // end run
        });


        return convertView;
    }

}


