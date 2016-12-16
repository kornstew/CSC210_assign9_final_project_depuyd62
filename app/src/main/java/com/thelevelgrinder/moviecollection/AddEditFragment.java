package com.thelevelgrinder.moviecollection;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.thelevelgrinder.moviecollection.data.DatabaseDescription.Movie;

/**
 * Created by Computer Science on 11/30/2016.
 */
public class AddEditFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
    // defines callback method implemented by MainActivity
    public interface AddEditFragmentListener
    {
        // called when movieis saved
        void onAddEditCompleted(Uri movieUri);
    }
    // constant used to identify the Loader
    private static final int MOVIE_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri movieUri; // Uri of selected movir
    private boolean addingNewMovie = true; // adding (true) or editing

    // EditTexts for movie information
    private TextInputLayout titleTextInputLayout;
    private TextInputLayout yearTextInputLayout;
    private TextInputLayout directorTextInputLayout;
    private TextInputLayout genreTextInputLayout;
    private TextInputLayout ratedTextInputLayout;
    private FloatingActionButton saveMovieFAB;
    private CoordinatorLayout coordinatorLayout; // used with SnackBars

    // set AddEditFragmentListener when Fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // remove AddEditFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // inflate GUI and get references to EditTexts
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        titleTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.titleTextInputLayout);
        titleTextInputLayout.getEditText().addTextChangedListener(
                titleChangedListener);
        yearTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.yearTextInputLayout);
        directorTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.directorTextInputLayout);
        genreTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.genreTextInputLayout);
        ratedTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.ratedTextInputLayout);
        // set FloatingActionButton's event listener
        saveMovieFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveMovieFAB.setOnClickListener(saveMovieButtonClicked);
        updateSaveButtonFAB();

        // used to display SnackBars with brief messages
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null if creating new movie

        if (arguments != null) {
            addingNewMovie = false;
            movieUri = arguments.getParcelable(MainActivity.MOVIE_URI);
        }

        // if editing an existing movie, create Loader to get the movie
        if (movieUri != null)
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);

        return view;
    }
    // detects when the text in the titleTextInputLayout's EditText changes
    // to hide or show saveButtonFAB
    private final TextWatcher titleChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {}

        // called when the text in titleTextInputLayout changes
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };
    // shows saveButtonFAB only if the title is not empty
    private void updateSaveButtonFAB() {
        String input =
                titleTextInputLayout.getEditText().getText().toString();

        // if there is a title for the movie, show the FloatingActionButton
        if (input.trim().length() != 0)
            saveMovieFAB.show();
        else
            saveMovieFAB.hide();
    }
    // responds to event generated when user saves a movie
    private final View.OnClickListener saveMovieButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the virtual keyboard
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveMovie(); // save movie to the database
                }
            };
    // saves movie information to the database
    private void saveMovie() {
        // create ContentValues object containing movie's key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(Movie.COLUMN_TITLE,
                titleTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_YEAR,
                yearTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_DIRECTOR,
                directorTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_GENRE,
                genreTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_RATED,
                ratedTextInputLayout.getEditText().getText().toString());


        if (addingNewMovie) {
            // use Activity's ContentResolver to invoke
            // insert on the MovieContentProvider
            Uri newMovieUri = getActivity().getContentResolver().insert(
                    Movie.CONTENT_URI, contentValues);

            if (newMovieUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.movie_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newMovieUri);
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.movie_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            // use Activity's ContentResolver to invoke
            // insert on the MovieContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    movieUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(movieUri);
                Snackbar.make(coordinatorLayout,
                        R.string.movie_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.movie_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }
    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        movieUri, // Uri of movie to display
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        null); // sort order
            default:
                return null;
        }
    }
    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the movie exists in the database, display its data
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int titleIndex = data.getColumnIndex(Movie.COLUMN_TITLE);
            int yearIndex = data.getColumnIndex(Movie.COLUMN_YEAR);
            int directorIndex = data.getColumnIndex(Movie.COLUMN_DIRECTOR);
            int genreIndex = data.getColumnIndex(Movie.COLUMN_GENRE);
            int ratedIndex = data.getColumnIndex(Movie.COLUMN_RATED);


            // fill EditTexts with the retrieved data
            titleTextInputLayout.getEditText().setText(
                    data.getString(titleIndex));
            yearTextInputLayout.getEditText().setText(
                    data.getString(yearIndex));
            directorTextInputLayout.getEditText().setText(
                    data.getString(directorIndex));
            genreTextInputLayout.getEditText().setText(
                    data.getString(genreIndex));
            ratedTextInputLayout.getEditText().setText(
                    data.getString(ratedIndex));


            updateSaveButtonFAB();
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
