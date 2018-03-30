/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The ProductListLoader is used to load products for use in the MainActivity.
 * The majority of the work is performed in the loadInBackground method.
 *
 * @author Brendon Guss
 * @since  02/11/2018
 */
class ProductListLoader extends AsyncTaskLoader<ArrayList<Product>>
{
    private ArrayList<Product>  productList;
    private AmazonProductSearch amazonProductSearch;

    // This is used to update the MainActivity's progress bar.
    private WeakReference<MainActivity> mainActivity;

    /**
     * Sets the required member variables necessary for the loader.
     *
     * @param context The application context.
     * @param amazonProductSearch The AmazonProductSearch that contains the Amazon product information for every product.
     * @param mainActivity A weak reference to the MainActivity used to update it's progress bar.
     */
    ProductListLoader(Context context, AmazonProductSearch amazonProductSearch, WeakReference<MainActivity> mainActivity)
    {
        super(context);

        this.amazonProductSearch = amazonProductSearch;
        this.mainActivity = mainActivity;
    }

    /**
     * Partially loads a list of products (the data that is needed in each list item). The rest of the
     * product data such as review statistics is loaded when a product is tapped on via the ProductLoader.
     *
     * @return The ArrayList of partially loaded products.
     */
    @Override public ArrayList<Product> loadInBackground()
    {
        int progressIncrement;
        final int MAIN_HALF_PROGRESS = 50;

        productList = amazonProductSearch.getMoreProducts(AmazonProductSearch.PRODUCTS_PER_PAGE);

        if (productList.size() != 0)
        {
            progressIncrement = MAIN_HALF_PROGRESS / productList.size();
        }
        else
        {
            progressIncrement = MAIN_HALF_PROGRESS;
        }

        for (Product product : productList)
        {
            product.setSmallImage();
            product.setLowestPriceInfo();
            mainActivity.get().mainProgressBar.incrementProgressBy(progressIncrement);
        }
        
        return productList;
    }

    /**
     * Called when there is new data to deliver.
     */
    @Override public void deliverResult(ArrayList<Product> productList)
    {
        this.productList = productList;

        if (isStarted())
        {
            super.deliverResult(productList);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (productList != null)
        {
            deliverResult(productList);
        }

        if (takeContentChanged() || productList == null)
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
    @Override public void onCanceled(ArrayList<Product> productList)
    {
        super.onCanceled(productList);
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
