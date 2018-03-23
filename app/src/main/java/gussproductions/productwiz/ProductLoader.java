package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * Created by Brendon on 2/20/2018.
 */

class ProductLoader extends AsyncTaskLoader<Product>
{
    private Product product;
    private boolean productLoaded;
    private String  upc;
    private WeakReference<ViewProductActivity> viewProductActivity;

    ProductLoader(Context context, Product product, WeakReference<ViewProductActivity> viewProductActivity)
    {
        super(context);

        this.product = product;
        this.viewProductActivity = viewProductActivity;
        productLoaded = true;

    }

    ProductLoader(Context context, String upc, WeakReference<ViewProductActivity> viewProductActivity)
    {
        super(context);
        productLoaded = false;
        this.upc = upc;
        this.viewProductActivity = viewProductActivity;
    }

    @Override public Product loadInBackground()
    {
        ProgressBar mainProgressBar;

        if (!productLoaded)
        {
            mainProgressBar = viewProductActivity.get().mainProgressBar;

            mainProgressBar.setMax(100);
            product = new Product(upc);
            mainProgressBar.setProgress(30);

            product.setLowestPriceInfo();

            product.setLargeImage();
            mainProgressBar.setProgress(70);
            product.setReviewStats();
            mainProgressBar.setProgress(90);
            product.setDescription();
        }
        else
        {
            viewProductActivity.get().mainProgressBar.setProgress(25);
            product.setLargeImage();
            viewProductActivity.get().mainProgressBar.setProgress(75);
            product.setReviewStats();
            viewProductActivity.get().mainProgressBar.setProgress(90);
            product.setDescription();
            viewProductActivity.get().mainProgressBar.setProgress(100);
        }

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
        /*
        if (product != null)
        {
            deliverResult(product);
        }
*/
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
