package com.example.phobos.places.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobos.places.R;
import com.example.phobos.places.activities.PlaceDetailActivity;
import com.example.phobos.places.activities.PlaceListActivity;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

/**
 * A fragment representing a single Place detail screen.
 * This fragment is either contained in a {@link PlaceListActivity}
 * in two-pane mode (on tablets) or a {@link PlaceDetailActivity}
 * on handsets.
 */
public class PlaceDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PLACE_LOADER = 0;

    public static final String ARG_URI = "uri";
    private Uri uri;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_URI)) {
            uri = arguments.getParcelable(ARG_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.place_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLACE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (uri != null) {
            switch (id) {
                case PLACE_LOADER:
                    return new CursorLoader(getActivity(),
                            uri,
                            new String[]{PlaceEntry.COLUMN_TEXT, PlaceEntry.COLUMN_LAST_VISITED},
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
            case PLACE_LOADER:
                if (data != null && data.moveToFirst()) {
                    String text = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_TEXT));
                    ((TextView)getView()).setText(text);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
