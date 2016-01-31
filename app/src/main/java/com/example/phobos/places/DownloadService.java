package com.example.phobos.places;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Vector;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

public class DownloadService extends IntentService {
    private static final String PLACES_URL = "http://interesnee.ru/files/android-middle-level-data.json";
    private static final String DIRECTORY = "Places photos";

    public static final String ACTION_GET_PLACES = "get_places";
    public static final String ACTION_DOWNLOAD_PHOTO = "download_photo";
    public static final String FILE_URL = "file_url";
    public static final String FILE_NAME = "file_name";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_PLACES.equals(action)) {
                handleReceivingPlaces();
            }
            if (ACTION_DOWNLOAD_PHOTO.equals(action)) {
                final String fileUrl = intent.getStringExtra(FILE_URL);
                final String fileName = intent.getStringExtra(FILE_NAME);
                handleDownloading(fileUrl, fileName);
            }
        }
    }

    public static void getPlaces(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_GET_PLACES);
        context.startService(intent);
    }

    public static void downloadPhoto(Context context, String url) {
        int start = url.lastIndexOf("/") + 1;
        int end = url.lastIndexOf("?");
        String name = url.substring(start, end);
        try {
            name = URLDecoder.decode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        downloadFile(context, ACTION_DOWNLOAD_PHOTO, url, name);
    }

    private static void downloadFile(Context context, String action, String fileUrl, String fileName) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(action);
        intent.putExtra(FILE_URL, fileUrl);
        intent.putExtra(FILE_NAME, fileName);
        context.startService(intent);
    }

    private static String getDirPath() {
        return Environment.getExternalStorageDirectory() + "/" + DIRECTORY;
    }

    private void handleReceivingPlaces() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placesJsonStr = null;

        try {
            URL url = new URL(PLACES_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if (is == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() == 0) {
                return;
            }
            placesJsonStr = stringBuilder.toString();
            Log.d("PLACES_JSON", placesJsonStr);
            getPlacesDataFromJson(placesJsonStr);
            Prefs.markSync(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleDownloading(String fileUrl, String fileName) {
        HttpURLConnection urlConnection = null;
        FileOutputStream fos = null;
        InputStream is = null;

        File directory = new File(getDirPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory + "/" + fileName);
        if (!file.exists()) {
            try {
                URL url = new URL(fileUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                fos = new FileOutputStream(file);
                is = urlConnection.getInputStream();
                byte[] buffer = new byte[512];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void getPlacesDataFromJson(String placesJsonStr) throws JSONException {
        final String PLACES = "places";
        final String LATITUDE = "latitude";
        final String LONGITUDE = "longtitude";
        final String TEXT = "text";
        final String IMAGE = "image";
        final String LAST_VISITED = "lastVisited";

        try {
            JSONObject placesJson = new JSONObject(placesJsonStr);
            JSONArray placesArray = placesJson.getJSONArray(PLACES);

            Vector<ContentValues> cVVector = new Vector<>(placesArray.length());
            for(int i = 0; i < placesArray.length(); i++) {
                double latitude;
                double longitude;
                String text;
                String image;
                String lastVisited;

                JSONObject place = placesArray.getJSONObject(i);
                latitude = place.getDouble(LATITUDE);
                longitude = place.getDouble(LONGITUDE);
                text = place.getString(TEXT);
                image = place.getString(IMAGE);
                lastVisited = place.getString(LAST_VISITED);

                ContentValues placeValues = new ContentValues();
                placeValues.put(PlaceEntry.COLUMN_LATITUDE, latitude);
                placeValues.put(PlaceEntry.COLUMN_LONGITUDE, longitude);
                placeValues.put(PlaceEntry.COLUMN_TEXT, text);
                placeValues.put(PlaceEntry.COLUMN_IMAGE, image);
                placeValues.put(PlaceEntry.COLUMN_LAST_VISITED, lastVisited);

                cVVector.add(placeValues);
            }

            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContentResolver().bulkInsert(PlaceEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
