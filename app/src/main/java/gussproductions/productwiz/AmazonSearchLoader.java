/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * The AmazonSearchLoader class is used to load the AmazonProductSearch for the application's main activity in for use
 * in a second loader, ProductListLoader. The majority of the work is performed in the loadInBackground method.
 *
 * @author Brendon Guss
 * @since  02/14/2018
 */
class AmazonSearchLoader extends AsyncTaskLoader<AmazonProductSearch>
{
    private AmazonProductSearch         amazonProductSearch;
    private String                      searchKeywords;

    // This is used in the AmazonProductSearch to update the main activity's progress bar.
    private WeakReference<MainActivity> mainActivity;

    /**
     * Sets the required member variables necessary for the loader.
     *
     * @param context The application context.
     * @param searchKeywords Amazon search keywords.
     * @param mainActivity A weak reference to the main activity used to update it's progress bar.
     */
    AmazonSearchLoader(Context context, String searchKeywords, WeakReference<MainActivity> mainActivity)
    {
        super(context);

        this.searchKeywords = searchKeywords;
        this.mainActivity   = mainActivity;
    }

    /**
     * Constructs (loads) the AmazonProductSearch.
     *
     * @return The fully loaded AmazonProductSearch.
     */
    @Override public AmazonProductSearch loadInBackground()
    {
        amazonProductSearch = new AmazonProductSearch(searchKeywords, mainActivity);

        return amazonProductSearch;
    }

    /**
     * Called when there is new data to deliver.
     */
    @Override public void deliverResult(AmazonProductSearch amazonProductSearch)
    {
        this.amazonProductSearch = amazonProductSearch;

        if (isStarted())
        {
            super.deliverResult(amazonProductSearch);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (amazonProductSearch != null)
        {
            deliverResult(amazonProductSearch);
        }

        if (takeContentChanged() || amazonProductSearch == null)
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
    @Override public void onCanceled(AmazonProductSearch amazonProductSearch)
    {
        super.onCanceled(amazonProductSearch);
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
