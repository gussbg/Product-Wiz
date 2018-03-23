package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Brendon on 2/11/2018.
 */

class ProductListLoader extends AsyncTaskLoader<ArrayList<Product>>
{
    private ArrayList <Product> productList;
    private AmazonProductSearch amazonProductSearch;
    private WeakReference<MainActivity> mainActivity;

    ProductListLoader(Context context, AmazonProductSearch amazonProductSearch, WeakReference<MainActivity> mainActivity)
    {
        super(context);

        this.amazonProductSearch = amazonProductSearch;
        this.mainActivity = mainActivity;
    }

    @Override public ArrayList<Product> loadInBackground()
    {
        productList = amazonProductSearch.getMoreProducts(AmazonProductSearch.PRODUCTS_PER_PAGE);

        int progressIncrement;

        if (productList.size() != 0)
        {
            progressIncrement = 50 / productList.size();
        }
        else
        {
            progressIncrement = 50;
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
