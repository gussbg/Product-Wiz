package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Brendon on 3/14/2018.
 */

public class BookmarkListLoader extends AsyncTaskLoader<ArrayList<BookmarkedProduct>>
{
    private ArrayList <BookmarkedProduct> bookmarkList;
    private final SharedPreferences sharedPref;
    private WeakReference<ViewBookmarksActivity> viewBookmarksActivity;

    BookmarkListLoader(Context context, SharedPreferences sharedPref, WeakReference<ViewBookmarksActivity> viewBookmarksActivity)
    {
        super(context);
        this.sharedPref = sharedPref;
        this.viewBookmarksActivity = viewBookmarksActivity;
    }

    @Override public ArrayList<BookmarkedProduct> loadInBackground()
    {
        bookmarkList = new ArrayList<>();
        Map<String, ?> bookmarkedProductsMap = sharedPref.getAll();
        int progressIncrement;


        for (Map.Entry<String, ?> bookmarkedProductEntry : bookmarkedProductsMap.entrySet())
        {
            Gson gson = new Gson();
            String json = bookmarkedProductEntry.getValue().toString();
            BookmarkedProduct bookmarkedProduct = gson.fromJson(json, BookmarkedProduct.class);

            bookmarkList.add(bookmarkedProduct);
        }

        Collections.sort(bookmarkList, new Comparator<BookmarkedProduct>()
        {
            @Override
            public int compare(BookmarkedProduct firstProduct, BookmarkedProduct secondProduct)
            {
                return firstProduct.getDateAdded().compareTo(secondProduct.getDateAdded());
            }
        });

        if (bookmarkList.size() != 0)
        {
            progressIncrement = 100 / bookmarkList.size();
        }
        else
        {
            progressIncrement = 100;
        }

        for (BookmarkedProduct bookmarkedProduct : bookmarkList)
        {
            Product product = new Product(bookmarkedProduct.getUPC());

            product.setSmallImage();
            product.setLowestPriceInfo();

            bookmarkedProduct.setProduct(product);

            viewBookmarksActivity.get().mainProgressBar.incrementProgressBy(progressIncrement);
        }

        return bookmarkList;
    }

    /**
     * Called when there is new data to deliver to the client.
     */
    @Override public void deliverResult(ArrayList<BookmarkedProduct> bookmarkList)
    {
        this.bookmarkList = bookmarkList;

        if (isStarted())
        {
            super.deliverResult(bookmarkList);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (bookmarkList != null)
        {
            deliverResult(bookmarkList);
        }

        if (takeContentChanged() || bookmarkList == null)
        {
            forceLoad();
        }
    }

    @Override public void onStopLoading()
    {
        cancelLoad();
    }

    @Override public void onCanceled(ArrayList<BookmarkedProduct> bookmarkList)
    {
        super.onCanceled(bookmarkList);
    }

    @Override protected void onReset()
    {
        super.onReset();

        onStopLoading();
    }
}
