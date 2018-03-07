package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Brendon on 2/22/2018.
 */

public class ReviewListLoader extends AsyncTaskLoader<ArrayList<Review>>
{
    private ArrayList <Review> reviewList;
    private Product product;

    ReviewListLoader(Context context, Product product)
    {
        super(context);

        this.product = product;
    }

    @Override public ArrayList<Review> loadInBackground()
    {
        reviewList = product.getMoreReviews(); //024543023920

        return reviewList;
    }


    /**
     * Called when there is new data to deliver to the client.
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

    @Override public void onStopLoading()
    {
        cancelLoad();
    }

    @Override public void onCanceled(ArrayList<Review> reviewList)
    {
        super.onCanceled(reviewList);
    }

    @Override protected void onReset()
    {
        super.onReset();

        onStopLoading();
    }
}
