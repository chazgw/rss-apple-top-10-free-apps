package com.example.rssappletop10freeapps;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseXML {

    private static final String TAG = "ParseApplications";

    private ArrayList<Record> records;

    // Constructor
    public ParseXML() {
        this.records = new ArrayList<Record>();
        Log.d(TAG, "constructor()");

    }

    // Get/Set
    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setApplications(ArrayList<Record> applications) {
        this.records = applications;
    }

    public boolean parse(String xmlData) {
        Log.d(TAG, "parse()");
        boolean status = true;
        Record currentRecord = null;
        boolean nowInNewEntry = false;  // are we at the part where we want to process an entry?
        // - different parts might have the same name, like id
        String textValue = "";

        try{

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();
            Log.d(TAG, "eventType: " + eventType);

            while(eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = xpp.getName();  // some will be nested!
                //Log.d(TAG, "tagName: " + tagName);

                switch(eventType) {
                    case XmlPullParser.START_TAG:

                        //Log.d(TAG, "START_TAG: " + tagName);

                        if("entry".equalsIgnoreCase(tagName)) {
                            nowInNewEntry = true;
                            currentRecord = new Record();
                            //applications.add(currentRecord);
                        }
                        break;

                    case XmlPullParser.END_TAG:

                        //Log.d(TAG, "END_TAG: " + tagName);

                        if(nowInNewEntry) {
                            if("entry".equalsIgnoreCase(tagName)) {
                                records.add(currentRecord);
                                nowInNewEntry = false;  // reset for next time
                            }
                            else if("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            }
                            else if("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            }
                            else if("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            }
                            else if("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            }
                            else if("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    default:

                        break;

                } // end switch

                eventType = xpp.next();  // orig location

            }  // end while


            // Debug
            for(Record app : records) {
                //Log.d(TAG, "*************");
                //Log.d(TAG, app.toString());

            }

        }
        catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;

    }

}
