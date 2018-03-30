/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * The ReviewListLoader is used to load reviews for use in the ViewProductActivity.
 * The majority of the work is performed in the loadInBackground method.
 *
 * @author Brendon Guss
 * @since  02/22/2018
 */
class ReviewListLoader extends AsyncTaskLoader<ArrayList<Review>>
{
    private ArrayList <Review> reviewList;
    private Product product;

    /**
     * Sets the required member variables necessary for the loader.
     *
     * @param context The application context.
     * @param product The product to load reviews for.
     */
    ReviewListLoader(Context context, Product product)
    {
        super(context);

        this.product = product;
    }

    /**
     * Loads a page of reviews for a product from each retailer that has reviews
     * except BestBuy.
     *
     * @return The ArrayList of the loaded reviews.
     */
    @Override public ArrayList<Review> loadInBackground()
    {
        reviewList = product.getMoreReviews();
        
        return reviewList;
    }

    /**
     * Called when there is new data to deliver.
     */
    @Override public void deliverResult(ArrayList<Review> reviewList)
    {
        this.reviewList = reviewList;

        if (isStarted())
        {
            super.deliverResult(reviewList);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (reviewList != null)
        {
            deliverResult(reviewList);
        }

        if (takeContentChanged() || reviewList == null)
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
    @Override public void onCanceled(ArrayList<Review> reviewList)
    {
        super.onCanceled(reviewList);
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
