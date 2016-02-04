package com.example.phobos.places.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phobos.places.R;
import com.example.phobos.places.utils.Utils;
import com.example.phobos.places.activities.MapsActivity;
import com.example.phobos.places.activities.PlaceDetailActivity;
import com.example.phobos.places.activities.PlaceListActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

/**
 * A fragment representing a single Place detail screen.
 * This fragment is either contained in a {@link PlaceListActivity}
 * in two-pane mode (on tablets) or a {@link PlaceDetailActivity}
 * on handsets.
 */
public class PlaceDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener {
    private static final int PLACE_LOADER = 0;
    private static final int CODE_PICK_IMAGE = 10;
    private static final String KEY_NEW_IMAGE_URI = "image_uri";
    private static final String KEY_CALENDAR = "calendar";

    public static final String ARG_URI = "uri";

    private Uri uri;
    private boolean newPlace;
    private String newImageUri;
    private Calendar calendar;

    private ImageButton imageView;
    private TextInputLayout etText;
    private TextInputLayout etLatitude;
    private TextInputLayout etLongitude;
    private EditText etLastVisited;

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
        if (arguments != null) {
            if (arguments.containsKey(ARG_URI)) {
                uri = arguments.getParcelable(ARG_URI);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.place_detail, container, false);
        imageView = (ImageButton) rootView.findViewById(R.id.imageView);
        etText = (TextInputLayout) rootView.findViewById(R.id.inputTextLayout);
        etLatitude = (TextInputLayout) rootView.findViewById(R.id.inputLatitudeLayout);
        etLongitude = (TextInputLayout) rootView.findViewById(R.id.inputLongitudeLayout);
        etLastVisited = (EditText) rootView.findViewById(R.id.etLastVisited);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_NEW_IMAGE_URI, newImageUri);
        outState.putSerializable(KEY_CALENDAR, calendar);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLACE_LOADER, null, this);

        newPlace = uri == null;
        if (newPlace) {
            getActivity().setTitle(R.string.title_new_place);
        }

        if (savedInstanceState != null) {
            newImageUri = savedInstanceState.getString(KEY_NEW_IMAGE_URI);
            loadImage(newImageUri);
            calendar = (Calendar) savedInstanceState.getSerializable(KEY_CALENDAR);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String title = getString(R.string.select_photo);
                startActivityForResult(Intent.createChooser(intent, title), CODE_PICK_IMAGE);
            }
        });

        etLastVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        PlaceDetailFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.dismissOnPause(true);
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (uri != null) {
            switch (id) {
                case PLACE_LOADER:
                    String[] projection = {PlaceEntry._ID,
                            PlaceEntry.COLUMN_LATITUDE,
                            PlaceEntry.COLUMN_LONGITUDE,
                            PlaceEntry.COLUMN_TEXT,
                            PlaceEntry.COLUMN_IMAGE,
                            PlaceEntry.COLUMN_LAST_VISITED};
                    return new CursorLoader(getActivity(),
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
            case PLACE_LOADER:
                if (data != null && data.moveToFirst()) {
                    final String image = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_IMAGE));
                    final String text = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_TEXT));
                    final double latitude = data.getDouble(data.getColumnIndex(PlaceEntry.COLUMN_LATITUDE));
                    final double longitude = data.getDouble(data.getColumnIndex(PlaceEntry.COLUMN_LONGITUDE));
                    final String lastVisited = data.getString(data.getColumnIndex(PlaceEntry.COLUMN_LAST_VISITED));

                    loadImage(image);
                    etText.getEditText().setText(text);
                    etLatitude.getEditText().setText(String.valueOf(latitude));
                    etLongitude.getEditText().setText(String.valueOf(longitude));
                    etLastVisited.setText(Utils.formatDate(lastVisited));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_place_detail, menu);
        MenuItem actionPlace = menu.findItem(R.id.action_place);
        if (newPlace) {
            actionPlace.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_place) {
            Intent map = new Intent(getActivity(), MapsActivity.class).setData(uri);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(),
                    android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(map, options.toBundle());
            return true;
        }

        if (id == R.id.action_done) {
            if (isValid()) {
                save();
                if (getActivity() instanceof PlaceDetailActivity) {
                    getActivity().finish();
                }
                Toast.makeText(getActivity(), R.string.place_saved, Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            newImageUri = data.getDataString();
            loadImage(newImageUri);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String formatDate = Utils.formatDate(calendar.getTime());
        etLastVisited.setText(formatDate);
    }

    private void loadImage(String image) {
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_add_a_photo_96dp)
                .error(R.drawable.ic_add_a_photo_96dp)
                .crossFade()
                .into(imageView);
    }

    private void save() {
        ContentValues placeValues = new ContentValues();
        placeValues.put(PlaceEntry.COLUMN_LATITUDE, etLatitude.getEditText().getText().toString());
        placeValues.put(PlaceEntry.COLUMN_LONGITUDE, etLongitude.getEditText().getText().toString());
        placeValues.put(PlaceEntry.COLUMN_TEXT, etText.getEditText().getText().toString());
        if (newImageUri != null) {
            placeValues.put(PlaceEntry.COLUMN_IMAGE, newImageUri);
        }
        if (calendar != null) {
            placeValues.put(PlaceEntry.COLUMN_LAST_VISITED, Utils.dateToStr(calendar.getTime()));
        }

        if (newPlace) {
            Uri newPlaceUri = PlaceEntry.buildPlacesUri();
            getActivity().getContentResolver().insert(newPlaceUri, placeValues);
        } else {
            getActivity().getContentResolver().update(uri, placeValues, null, null);
        }
    }

    private boolean isValid() {
        int errors = 0;
        if (etText.getEditText().getText().length() == 0) {
            etText.setError(getString(R.string.required_field));
            errors++;
        }

        if (etLatitude.getEditText().getText().length() == 0) {
            etLatitude.setError(getString(R.string.required_field));
            errors++;
        }

        if (etLongitude.getEditText().getText().length() == 0) {
            etLongitude.setError(getString(R.string.required_field));
            errors++;
        }

        return errors == 0;
    }
}
