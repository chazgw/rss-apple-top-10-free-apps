package com.example.rssappletop10freeapps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ExecutorAndHandler";

    // data members
    private ListView listApps;

    private Button loadXMLButton;

    // Debug
    private ArrayList<String> currentCharsRead;

    //String url = "https://feeds.megaphone.fm/ADL9840290619"; // True Crime RSS feed
    String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";

    ParseXML parseXML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseXML = new ParseXML();
        currentCharsRead = new ArrayList<String>();  // debug
        Log.d(TAG, " onCreate() start");

        // Instantiations
        // Widgets
        listApps = findViewById(R.id.xmlListView);
        loadXMLButton = findViewById(R.id.loadXMLButton);

        //StartExecutor();

        loadXMLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                StartExecutor();

            }
        });

    }  // end OnCreate()

    private void StartExecutor() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        // or
        //ExecutorService executor = Executors.newFixedThreadPool(4);
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() { //Background work here

                String rssFeed = downloadXML(url);

                parseXML.parse(rssFeed);


                // Instantiations
                // 1.
                // Note: The RecordAdapter without a ConvertView doesn't
                // handle images well

                RecordAdapter recordAdapter = new RecordAdapter(
                        MainActivity.this,
                        R.layout.list_record,
                        parseXML.getRecords());



                // 2. With ConvertView
                /*
                RecordAdapterWithConvertView recordAdapter = new RecordAdapterWithConvertView(
                        MainActivity.this,
                        R.layout.list_record,
                        parseXML.getRecords());

                */

                // 3. With ViewHolder pattern
                /*
                RecordAdapterWithViewHolder recordAdapter = new RecordAdapterWithViewHolder(
                        MainActivity.this,
                        R.layout.list_record,
                        parseXML.getRecords());
                */


                // 1. no handler setup
                // listApps.setAdapter(recordAdapter);
                // Result in an Exception:
                // -CalledFromWrongThreadException: Only the original thread that
                // -created a view hierarchy can touch its views.

                // 2. handler setup

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        listApps.setAdapter(recordAdapter);
                    }
                });


                if(rssFeed == null) { Log.e(TAG, "Error downloading RSS feed"); }

            }  // end run

        });  // end execute()

        //Log.d(TAG, "executor.shutdown() ");
        executor.shutdown();

    }


    private String downloadXML(String urlString) {
        // Common theme - Open HTTP connection
        // - uses InputStreamReader()

        StringBuilder xmlResult = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Closeable resource = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            // Two options for instantiation:
            // 1. Each one Individually
            //InputStream inputStream = connection.getInputStream();
            //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            //BufferedReader reader = new BufferedReader(inputStreamReader);

            // Or
            // 2. Chained
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            resource = reader;

            int charsRead;
            char[] inputBuffer = new char[500];

            while(true) {

                charsRead = reader.read(inputBuffer);  // needs inputBuffer argument
                if(charsRead < 0) {
                    // signals the end of the data stream
                    //Log.d(TAG,"end of stream");
                    break;
                }
                if(charsRead >= 0) {   // number of characters read from the stream
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    //Log.d(TAG, "appending to xmlResult: ");
                    // currentCharsRead.add(Integer.toString(charsRead)); // debug

                }

            }

            // Debug - characters read
            /*
            for (int i = 0; i < currentCharsRead.size(); i++) {

                // Print all elements of List
                Log.d(TAG, currentCharsRead.get(i));
            }
             */

        } catch (MalformedURLException e) {
            Log.e(TAG, "malformed url: ");
            Log.e(TAG, "e.toString(): " + e.toString());
            // java.net.MalformedURLException: unknown protocol: (value)
            Log.e(TAG, "e.getMessage(): " + e.getMessage());
            // unknown protocol: (value)
            Log.e(TAG, "e.printStackTrace(): ");
            // unknown protocol: (value), plus a Stack Trace output
            e.printStackTrace();
        }
        catch (SecurityException e) {
            Log.e(TAG, "security exception");
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(TAG, "I/O Exception");
            Log.e(TAG, e.toString());
            e.printStackTrace();

        }
        catch(Exception e) {
            Log.e(TAG, "Geneal Exception");
            Log.e(TAG, e.toString());
            e.printStackTrace();

        }
        finally {
            Log.d(TAG, "finally block");
            connection.disconnect();
        }

        //return null;
        return xmlResult.toString();

    }

}
