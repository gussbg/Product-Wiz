package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Brendon on 2/11/2018.
 */

class ProductListLoader extends AsyncTaskLoader<ArrayList<Product>>
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

        for (Product product : productList)
        {
            product.getAmazonProductInfo().image = loadImage(product.getAmazonProductInfo().imageURL);
        }

        return productList;
    }

    private Bitmap loadImage(String imageURL)
    {
        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mIcon11;
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
