/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * The ProductLoader is used to load a product that may or may not already be partially loaded for use
 * in the ViewProductActivity. The majority of the work is performed in the loadInBackground method.
 *
 * @author Brendon Guss
 * @since  02/20/2018
 */
class ProductLoader extends AsyncTaskLoader<Product>
{
    private Product product;
    private boolean productLoaded;
    private String  upc;
    private WeakReference<ViewProductActivity> viewProductActivity;

    /**
     * Sets the required member variables necessary for the loader. This constructor is used when the product
     * is already partially loaded.
     *
     * @param context The application context.
     * @param viewProductActivity A weak reference to the ViewProductActivity used to update it's progress bar.
     */
    ProductLoader(Context context, Product product, WeakReference<ViewProductActivity> viewProductActivity)
    {
        super(context);

        this.product             = product;
        this.viewProductActivity = viewProductActivity;
        this.productLoaded       = true;
    }

    /**
     * Sets the required member variables necessary for the loader. This constructor is used when the product
     * is not already partially loaded.
     *
     * @param context The application context.
     * @param upc The product's upc.
     * @param viewProductActivity A weak reference to the ViewProductActivity used to update it's progress bar.
     */
    ProductLoader(Context context, String upc, WeakReference<ViewProductActivity> viewProductActivity)
    {
        super(context);

        this.upc = upc;
        this.viewProductActivity = viewProductActivity;
        this.productLoaded = false;
    }

    /**
     * Loads the product's numerous attributes based on if it has already been partially loaded or not.
     * Updates the ViewProductActivity's progress bar based on how much of the product has been loaded.
     *
     * @return The fully loaded product.
     */
    @Override public Product loadInBackground()
    {
        ProgressBar mainProgressBar;

        mainProgressBar = viewProductActivity.get().mainProgressBar;

        final int MAX_PRODUCT_PROGRESS = 100;

        if (!productLoaded)
        {
            mainProgressBar.setMax(MAX_PRODUCT_PROGRESS);
            product = new Product(upc);
            mainProgressBar.setProgress(30);

            product.setLowestPriceInfo();

            product.setLargeImage();
            mainProgressBar.setProgress(70);

            product.setReviewStats();
            mainProgressBar.setProgress(90);

            product.setDescription();
            mainProgressBar.setProgress(MAX_PRODUCT_PROGRESS);
        }
        else
        {
            mainProgressBar.setProgress(25);

            product.setLargeImage();

            if (product.getLargeImage() == null)
            {
                product.setSmallImage();
            }

            mainProgressBar.setProgress(75);

            product.setReviewStats();
            mainProgressBar.setProgress(90);

            product.setDescription();
            mainProgressBar.setProgress(MAX_PRODUCT_PROGRESS);
        }

        return product;
    }

    /**
     * Called when there is new data to deliver.
     */
    @Override public void deliverResult(Product product)
    {
        this.product = product;

        if (isStarted())
        {
            super.deliverResult(product);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (takeContentChanged() || product == null)
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
    @Override public void onCanceled(Product product)
    {
        super.onCanceled(product);
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
