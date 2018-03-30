/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The ViewBookmarksActivity is where a user can view bookmarked products and
 * see how much the price has changed since they were added. They can also tap
 * on each bookmarked product for more information or remove bookmarked products.
 * This activity is launched from tapping the Bookmark icon in the MainActivity's
 * action bar.
 *
 * @author Brendon Guss
 * @since  02/22/2018
 */
public class ViewBookmarksActivity extends AppCompatActivity
{
    private Context             context;
    private BookmarkListAdapter bookmarkListAdapter;
    private BookmarkedProduct   viewedBookmark;

    private WeakReference<ViewBookmarksActivity> viewBookmarksActivity;

    // Views
    private   ListView    bookmarkList;
    private   ProgressBar progressBar;
    protected ProgressBar mainProgressBar;
    private   TextView    noBookmarksMessage;

    private final int PRODUCT_REQUEST_CODE = 5;

    /**
     * Creates the ViewBookmarksActivity and sets its members.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final int BOOKMARK_LOADER_ID = 3;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookmarks);

        context               = getApplicationContext();
        bookmarkListAdapter   = new BookmarkListAdapter(context);
        viewBookmarksActivity = new WeakReference<>(this);

        progressBar        = new ProgressBar(context);
        bookmarkList       = findViewById(R.id.bookmarkList);
        mainProgressBar    = findViewById(R.id.bookmarksProgressBar);
        noBookmarksMessage = findViewById(R.id.noBookmarks);

        if (getSupportActionBar() != null)
        {
            // Disables application title in action bar.
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // Enables back button.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bookmarkListAdapter.clear();
        bookmarkList.setAdapter(bookmarkListAdapter);

        // This listener is called when a bookmarked product from the bookmarkList is tapped on.
        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> bookmarkListView, View bookmarkView, int position , long arg3)
            {
                Intent viewProduct = new Intent(context, ViewProductActivity.class);

                viewProduct.putExtra(getResources().getString(R.string.calling_activity),
                        getResources().getString(R.string.bookmark_activity_class));

                // The viewed bookmark is set so that it can be removed from the bookmarkList if it is removed
                // in the ViewProductActivity.
                viewedBookmark = (BookmarkedProduct) bookmarkListView.getItemAtPosition(position);
                viewProduct.putExtra(getResources().getString(R.string.product_data), viewedBookmark.getProduct());

                startActivityForResult(viewProduct, PRODUCT_REQUEST_CODE);
            }
        });

        mainProgressBar.setVisibility(View.VISIBLE);

        // Prepares BookmarkListLoader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().restartLoader(BOOKMARK_LOADER_ID, null, bookmarkListLoaderListener);
    }

    /**
     * This method is called whenever a menu item is clicked.
     *
     * @param item The menu item.
     * @return Returns false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // Back button in action bar tapped; goto parent activity (MainActivity).
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The method is called when the ViewProductActivity finishes, if it is launched from this activity.
     *
     * @param requestCode The ViewProductActivity request code.
     * @param resultCode The ViewProductActivity result code.
     * @param bookmarkResult A boolean that is true if the bookmark was removed in the ViewProductActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent bookmarkResult)
    {
        if (requestCode == PRODUCT_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                boolean bookmarkRemoved = bookmarkResult.getBooleanExtra(
                        getResources().getString(R.string.view_bookmark_result), false);

                // If the bookmark was removed after viewing the product in the ViewProductActivity it is removed from
                // the bookmarkList.
                if (bookmarkRemoved)
                {
                    bookmarkListAdapter.remove(viewedBookmark);
                    bookmarkListAdapter.notifyDataSetChanged();

                    // If the removed bookmark was the last bookmark to be removed, a message is displayed stating
                    // that there are no more bookmarks.
                    if (bookmarkListAdapter.getCount() == 0)
                    {
                        bookmarkList.setVisibility(View.GONE);
                        noBookmarksMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    // The bookmark list loader callback listeners are defined below.
    LoaderManager.LoaderCallbacks<ArrayList<BookmarkedProduct>> bookmarkListLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<BookmarkedProduct>>()
    {
        /**
         * This method is called when the loader is created, it constructs the necessary attributes for the loader
         * to begin loading, it is not fully loaded when it is returned however.
         *
         * @param id Loader ID.
         * @param args Bundle arguments.
         * @return The BookmarkList Loader.
         */
        @Override public Loader<ArrayList<BookmarkedProduct>> onCreateLoader(int id, Bundle args)
        {
            final SharedPreferences sharedPref
                    = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);

            // This is called when a new Loader needs to be created.
            return new BookmarkListLoader(context, sharedPref, viewBookmarksActivity);
        }

        /**
         * This method is called when the loader has finished. All of the views are set with data here.
         *
         * @param bookmarkListLoader The bookmarkListLoader.
         * @param bookmarkedProducts The loaded ArrayList of bookmarked products.
         */
        @Override public void onLoadFinished(Loader<ArrayList<BookmarkedProduct>> bookmarkListLoader,
                                             ArrayList<BookmarkedProduct> bookmarkedProducts)
        {
            // Add the new data in the adapter.
            bookmarkListAdapter.addAll(bookmarkedProducts);

            bookmarkListAdapter.notifyDataSetChanged();

            mainProgressBar.setVisibility(View.GONE);
            bookmarkList.removeFooterView(progressBar);

            final SharedPreferences sharedPref
                    = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);

            if (bookmarkedProducts.size() == 0 && sharedPref.getAll().size() == 0)
            {
                noBookmarksMessage.setVisibility(View.VISIBLE);
            }
            else
            {
                bookmarkList.setVisibility(View.VISIBLE);
            }
        }

        /**
         * This method is called when the loader is reset.
         *
         * @param bookmarkListLoader The bookmarkListLoader.
         */
        @Override public void onLoaderReset(Loader<ArrayList<BookmarkedProduct>> bookmarkListLoader)
        {
            bookmarkListAdapter.clear();
        }
    };
}
