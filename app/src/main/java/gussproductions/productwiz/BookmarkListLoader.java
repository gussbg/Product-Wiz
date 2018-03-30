/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * The BookmarkListLoader is used to load bookmarked products for use in the ViewBookmarksActivity.
 * The majority of the work is performed in the loadInBackground method.
 *
 * @author Brendon Guss
 * @since  03/14/2018
 */
class BookmarkListLoader extends AsyncTaskLoader<ArrayList<BookmarkedProduct>>
{
    private ArrayList <BookmarkedProduct> bookmarkList;

    // This is used to update the ViewBookmarksActivity's progress bar.
    private WeakReference<ViewBookmarksActivity> viewBookmarksActivity;

    private final SharedPreferences sharedPref;

    /**
     * Sets the required member variables necessary for the loader.
     *
     * @param context The application context.
     * @param sharedPref The shared preferences are used to load the bookmarks from the device.
     * @param viewBookmarksActivity A weak reference to the ViewBookmarksActivity used to update it's progress bar.
     */
    BookmarkListLoader(Context context, SharedPreferences sharedPref,
                       WeakReference<ViewBookmarksActivity> viewBookmarksActivity)
    {
        super(context);

        this.sharedPref            = sharedPref;
        this.viewBookmarksActivity = viewBookmarksActivity;
    }

    /**
     * Loads the bookmarked product information and latest product information primarily to get the current prices
     * as they are likely different from when the product was bookmarked.
     *
     * @return The loaded ArrayList of bookmarked products.
     */
    @Override public ArrayList<BookmarkedProduct> loadInBackground()
    {
        Map<String, ?> bookmarkedProductsMap;
        int progressIncrement;

        final int MAX_BOOKMARK_PROGRESS = 100;

        bookmarkList          = new ArrayList<>();
        bookmarkedProductsMap = sharedPref.getAll();

        // Adds bookmarks stored in the shared preferences.
        for (Map.Entry<String, ?> bookmarkedProductEntry : bookmarkedProductsMap.entrySet())
        {
            Gson gson = new Gson();
            String json = bookmarkedProductEntry.getValue().toString();
            BookmarkedProduct bookmarkedProduct = gson.fromJson(json, BookmarkedProduct.class);

            bookmarkList.add(bookmarkedProduct);
        }

        // Loaded bookmarks are sorted in descending order by date added.
        Collections.sort(bookmarkList, new Comparator<BookmarkedProduct>()
        {
            @Override
            public int compare(BookmarkedProduct firstProduct, BookmarkedProduct secondProduct)
            {
                return secondProduct.getDateAdded().compareTo(firstProduct.getDateAdded());
            }
        });

        // Determines how much the bookmark load progress bar should be incremented every time a bookmarked
        // product is loaded.
        if (bookmarkList.size() != 0)
        {
            progressIncrement = MAX_BOOKMARK_PROGRESS / bookmarkList.size();
        }
        else
        {
            progressIncrement = MAX_BOOKMARK_PROGRESS;
        }

        // Product is updated to reflect the latest prices and other information.
        for (BookmarkedProduct bookmarkedProduct : bookmarkList)
        {
            Product product = new Product(bookmarkedProduct.getUPC());

            product.setSmallImage();
            product.setLowestPriceInfo();

            bookmarkedProduct.setProduct(product);

            viewBookmarksActivity.get().mainProgressBar.incrementProgressBy(progressIncrement);
        }

        return bookmarkList;
    }

    /**
     * Called when there is new data to deliver.
     */
    @Override public void deliverResult(ArrayList<BookmarkedProduct> bookmarkList)
    {
        this.bookmarkList = bookmarkList;

        if (isStarted())
        {
            super.deliverResult(bookmarkList);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (bookmarkList != null)
        {
            deliverResult(bookmarkList);
        }

        if (takeContentChanged() || bookmarkList == null)
        {
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override public void onStopLoading()
    {
        cancelLoad();
    }

    /**
     * Handles a request to cancel the loader.
     */
    @Override public void onCanceled(ArrayList<BookmarkedProduct> bookmarkList)
    {
        super.onCanceled(bookmarkList);
    }

    /**
     * Handles a request to reset the loader.
     */
    @Override protected void onReset()
    {
        super.onReset();
        onStopLoading();
    }
}
