package com.example.phobos.places.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.phobos.places.DividerItemDecoration;
import com.example.phobos.places.DownloadService;
import com.example.phobos.places.Prefs;
import com.example.phobos.places.R;
import com.example.phobos.places.adapters.PlaceAdapter;
import com.example.phobos.places.fragments.PlaceDetailFragment;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

/**
 * An activity representing a list of Places. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlaceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PlaceListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, PlaceAdapter.ItemClickListener {
    private static final int PLACES_LOADER = 0;
    private static final String KEY_POSITION = "position";

    private boolean twoPane;
    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private int position = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.place_list);
        assert recyclerView != null;
        adapter = new PlaceAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        if (findViewById(R.id.place_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-sw600dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
            adapter.setMode(PlaceAdapter.SelectionMode.SINGLE);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION)) {
            position = savedInstanceState.getInt(KEY_POSITION);
        }

        if (!Prefs.isSync(this)) {
            DownloadService.getPlaces(this);
        }
        getSupportLoaderManager().initLoader(PLACES_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (position != RecyclerView.NO_POSITION) {
            outState.putInt(KEY_POSITION, position);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void itemClicked(Cursor cursor, int position, long id) {
        this.position = position;
        if (cursor != null) {
            Uri contentUri = PlaceEntry.buildPlaceUri(id);
            if (twoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable("uri", contentUri);
                PlaceDetailFragment fragment = new PlaceDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.place_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, PlaceDetailActivity.class).setData(contentUri);
                startActivity(intent);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PLACES_LOADER:
                Uri placesUri = PlaceEntry.buildPlacesUri();
                return new CursorLoader(this,
                        placesUri,
                        new String[] {PlaceEntry._ID, PlaceEntry.COLUMN_TEXT, PlaceEntry.COLUMN_LAST_VISITED},
                        null,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PLACES_LOADER:
                adapter.swapCursor(data);
                if (position != RecyclerView.NO_POSITION) {
                    recyclerView.smoothScrollToPosition(position);
                    if (twoPane) {
                        adapter.switchSelectedState(position);
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case PLACES_LOADER:
                adapter.swapCursor(null);
                break;
        }
    }
}
