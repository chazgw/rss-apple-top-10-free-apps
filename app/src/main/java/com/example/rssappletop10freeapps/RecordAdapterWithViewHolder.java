package com.example.rssappletop10freeapps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RecordAdapterWithViewHolder extends ArrayAdapter {

    private static final String TAG = "R.A.WithViewHolder";
    //TextView nameText;
    //TextView artistText;
    //TextView releaseDateText;
    //ImageView appImage;

    private final int layoutResource;
    private final LayoutInflater layoutInflater;

    private List<Record> feedEntries;

    private ArrayList<Bitmap> images;
    private final int NUMBER_OF_RECORDS = 10;

    public RecordAdapterWithViewHolder(@NonNull Context context,
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

        ViewHolder viewHolder;
        Record currentEntry = feedEntries.get(position);

        if(convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        // Getting an image from a URL, outside of the main/UI thread:
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        // or
        ExecutorService executor = Executors.newFixedThreadPool(5);

        executor.execute(new Runnable() {
            @Override
            public void run() { //Background work here

                HttpURLConnection connection = null;

                try {
                    URL url = new URL(currentEntry.getImageURL());

                    if(connection == null) {
                        //Log.d(TAG, "connection is null: ");
                        connection = (HttpURLConnection) url.openConnection();
                        //connection.setDoInput(true);
                        connection.connect();


                        InputStream input = connection.getInputStream();
                        Bitmap imageIcon = BitmapFactory.decodeStream(input);


                        viewHolder.nameText.setText(currentEntry.getName());
                        viewHolder.artistText.setText(currentEntry.getArtist());
                        viewHolder.releaseDateText.setText(currentEntry.getReleaseDate());
                        //viewHolder.appImage.setImageBitmap(images.get(position));
                        imageIcon = Bitmap.createScaledBitmap(imageIcon, 100, 100, true);
                        viewHolder.appImage.setImageBitmap(imageIcon);

                    }
                    //viewHolder.nameText.setText("a");
                   // viewHolder.artistText.setText("b");
                    //viewHolder.releaseDateText.setText("c");

                    if(images.get(position) == null) {
                        //images.set(position, imageIcon);

                        //Log.d(TAG, "Setting image " + position + " for the first time");
                        //viewHolder.nameText.setText(currentEntry.getName());
                       // viewHolder.artistText.setText(currentEntry.getArtist());
                        //viewHolder.releaseDateText.setText(currentEntry.getReleaseDate());
                    }
                    else {
                       // viewHolder.nameText.setText(currentEntry.getName());
                        //viewHolder.artistText.setText(currentEntry.getArtist());
                        //viewHolder.releaseDateText.setText(currentEntry.getReleaseDate());
                        //viewHolder.appImage.setImageBitmap(images.get(position));
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

    private class ViewHolder {

        final TextView nameText;
        final TextView artistText;
        final TextView releaseDateText;
        final ImageView appImage;

        public ViewHolder(View v) {
            nameText = v.findViewById(R.id.nameText);
            artistText = v.findViewById(R.id.artistText);
            releaseDateText = v.findViewById(R.id.releaseDateText);
            appImage = v.findViewById(R.id.appImage);
        }

    } // end ViewHolder class

}


