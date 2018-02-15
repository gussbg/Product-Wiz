package gussproductions.productwiz;

import android.content.AsyncTaskLoader;
import android.content.Context;


/**
 * Created by Brendon on 2/14/2018.
 */

public class AmazonSearchLoader extends AsyncTaskLoader<AmazonProductSearch>
{
    private AmazonProductSearch amazonProductSearch;
    private String              searchKeywords;

    AmazonSearchLoader(Context context, String searchKeywords)
    {
        super(context);

        this.searchKeywords = searchKeywords;
    }

    @Override public AmazonProductSearch loadInBackground()
    {
        amazonProductSearch = new AmazonProductSearch(searchKeywords);

        return amazonProductSearch;
    }

    /**
     * Called when there is new data to deliver to the client.
     */
    @Override public void deliverResult(AmazonProductSearch amazonProductSearch)
    {
        this.amazonProductSearch = amazonProductSearch;

        if (isStarted())
        {
            super.deliverResult(amazonProductSearch);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading()
    {
        if (amazonProductSearch != null)
        {
            deliverResult(amazonProductSearch);
        }

        if (takeContentChanged() || amazonProductSearch == null)
        {
            forceLoad();
        }
    }

    @Override public void onStopLoading()
    {
        cancelLoad();
    }

    @Override public void onCanceled(AmazonProductSearch amazonProductSearch)
    {
        super.onCanceled(amazonProductSearch);
    }

    @Override protected void onReset()
    {
        super.onReset();

        onStopLoading();
    }
}
