package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

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
    private Context context;
    private boolean loadBookmarks;

    ProductListLoader(Context context, AmazonProductSearch amazonProductSearch)
    {
        super(context);

        this.amazonProductSearch = amazonProductSearch;
    }

    ProductListLoader(Context context)
    {
        super(context);
        this.context = context;

        loadBookmarks = true;
    }

    @Override public ArrayList<Product> loadInBackground()
    {
        if (!loadBookmarks)
        {
            productList = amazonProductSearch.getMoreProducts(AmazonProductSearch.PRODUCTS_PER_PAGE);

            for (Product product : productList)
            {
                product.setImage();
                product.setLowestPriceInfo();
            }
        }
        else
        {
            productList = new ArrayList<>();
            final SharedPreferences sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);

            Map<String, ?> bookmarkedProductsMap = sharedPref.getAll();

            for (Map.Entry<String, ?> bookmarkedProductEntry : bookmarkedProductsMap.entrySet())
            {
                Gson gson = new Gson();
                String json = bookmarkedProductEntry.getValue().toString();
                BookmarkedProduct bookmarkedProduct = gson.fromJson(json, BookmarkedProduct.class);

                productList.add(new Product(bookmarkedProductEntry.getKey(), bookmarkedProduct));
            }

            Collections.sort(productList, new Comparator<Product>()
            {
                @Override
                public int compare(Product firstProduct, Product secondProduct)
                {
                    return firstProduct.getBookmarkedProduct().getDateAdded().compareTo(secondProduct.getBookmarkedProduct().getDateAdded());
                }
            });

            for (Product product : productList)
            {
                product.setImage();
                product.setLowestPriceInfo();

                productList.add(product);
            }
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
