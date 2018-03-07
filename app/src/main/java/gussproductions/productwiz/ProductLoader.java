package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Brendon on 2/20/2018.
 */

class ProductLoader extends AsyncTaskLoader<Product>
{
    private Product product;
    private boolean productLoaded;
    private String  upc;

    ProductLoader(Context context, Product product)
    {
        super(context);

        this.product = product;
        productLoaded = true;

    }

    ProductLoader(Context context, String upc)
    {
        super(context);
        productLoaded = false;
        this.upc = upc;
    }

    @Override public Product loadInBackground()
    {

        if (!productLoaded)
        {
            product = new Product(upc);
            product.setImage();
            product.setLowestPriceInfo();
        }

        product.setReviewStats();
        product.setEbayDescription();


        return product;
    }

    /**
     * Called when there is new data to deliver to the client.
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
        if (product != null)
        {
            deliverResult(product);
        }

        if (takeContentChanged() || product == null)
        {
            forceLoad();
        }
    }

    @Override public void onStopLoading()
    {
        cancelLoad();
    }

    @Override public void onCanceled(Product product)
    {
        super.onCanceled(product);
    }

    @Override protected void onReset()
    {
        super.onReset();

        onStopLoading();
    }
}
