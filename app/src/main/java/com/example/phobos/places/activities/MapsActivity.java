package com.example.phobos.places.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.phobos.places.R;
import com.example.phobos.places.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnInfoWindowClickListener {
    private static final int PLACES_LOADER = 0;

    private GoogleMap map;
    private Uri uri;
    private HashMap<String, Long> markers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        uri = getIntent().getData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnInfoWindowClickListener(this);
        getLoaderManager().initLoader(PLACES_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, PlaceListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (uri != null) {
            switch (id) {
                case PLACES_LOADER:
                    String[] projection = {PlaceEntry._ID,
                            PlaceEntry.COLUMN_LATITUDE,
                            PlaceEntry.COLUMN_LONGITUDE,
                            PlaceEntry.COLUMN_TEXT,
                            PlaceEntry.COLUMN_IMAGE,
                            PlaceEntry.COLUMN_LAST_VISITED};
                    return new CursorLoader(this,
                            uri,
                            projection,
                            null,
                            null,
                            null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PLACES_LOADER:
                if (data != null && data.moveToFirst()) {
                    boolean onePlace = data.getCount() == 1;
                    do {
                        final long id = data.getLong(data.getColumnIndex(PlaceEntry._ID));
                        final double lat = data.getDouble(data.getColumnIndex(PlaceEntry.COLUMN_LATITUDE));
                        final double lng = data.getDouble(data.getColumnIndex(PlaceEntry.COLUMN_LONGITUDE));
                        final String text = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_TEXT));
                        final String image = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_IMAGE));
                        final String lastVisited = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_LAST_VISITED));

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(text)
                                .snippet(Utils.formatDate(lastVisited));
                        Marker marker = map.addMarker(markerOptions);
                        loadMarkerIcon(marker, image);
                        markers.put(marker.getId(), id);

                        if (onePlace) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5));
                            marker.showInfoWindow();
                        }
                    } while (data.moveToNext());
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void loadMarkerIcon(final Marker marker, String image) {
        if (TextUtils.isEmpty(image)) return;
        Glide.with(this)
                .load(image)
                .asBitmap()

                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, false);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                        marker.setIcon(icon);
                    }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        long id = markers.get(marker.getId());
        Uri contentUri = PlaceEntry.buildPlaceUri(id);
        Intent intent = new Intent(this, PlaceDetailActivity.class).setData(contentUri);
        startActivity(intent);
    }
}
