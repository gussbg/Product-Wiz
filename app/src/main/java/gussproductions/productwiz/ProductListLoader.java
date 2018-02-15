package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Brendon on 2/11/2018.
 */

public class ProductListLoader extends AsyncTaskLoader<ArrayList<Product>>
{
    private ArrayList <Product> productList;
    private AmazonProductSearch amazonProductSearch;

    ProductListLoader(Context context, AmazonProductSearch amazonProductSearch)
    {
        super(context);

        this.amazonProductSearch = amazonProductSearch;

    }

    @Override public ArrayList<Product> loadInBackground()
    {
        productList = amazonProductSearch.getMoreProducts(AmazonProductSearch.PRODUCTS_PER_PAGE);

        return productList;
    }

    /**
     * Called when there is new data to deliver to the client.
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

    @Override public void onStopLoading()
    {
        cancelLoad();
    }

    @Override public void onCanceled(ArrayList<Product> productList)
    {
        super.onCanceled(productList);
    }

    @Override protected void onReset()
    {
        super.onReset();

        onStopLoading();
    }
}
