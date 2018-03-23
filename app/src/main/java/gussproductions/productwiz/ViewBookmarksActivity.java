package gussproductions.productwiz;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ViewBookmarksActivity extends AppCompatActivity
{
    Context context;
    BookmarkListLoader   bookmarkListLoader;
    BookmarkListAdapter  bookmarkListAdapter;

    // Views
    ListView    listView;
    ProgressBar progressBar;
    ProgressBar mainProgressBar;
    TextView    noBookmarksMessage;

    BookmarkedProduct viewedBookmark;

    WeakReference<ViewBookmarksActivity> viewBookmarksActivity;

    private final int BOOKMARK_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookmarks);

        context             = getApplicationContext();
        bookmarkListAdapter = new BookmarkListAdapter(context);
        progressBar         = new ProgressBar(context);
        listView            = findViewById(R.id.bookmarkList);
        mainProgressBar     = findViewById(R.id.bookmarksProgressBar);
        noBookmarksMessage  = findViewById(R.id.noBookmarks);

        noBookmarksMessage.setVisibility(View.GONE);

        bookmarkListAdapter.clear();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mainProgressBar.setVisibility(View.VISIBLE);

        listView.setAdapter(bookmarkListAdapter);
        listView.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent intent = new Intent(context, ViewProductActivity.class);

                intent.putExtra(getResources().getString(R.string.calling_activity), getResources().getString(R.string.bookmark_activity_class));

                viewedBookmark =  (BookmarkedProduct) arg0.getItemAtPosition(position);
                Product product = viewedBookmark.getProduct();

                intent.putExtra(getResources().getString(R.string.product_data), product);



                //this.startActivity(viewBookmarks);

                startActivityForResult(intent, 5);
            }
        });

        viewBookmarksActivity = new WeakReference<>(this);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().restartLoader(BOOKMARK_LOADER_ID, null, bookmarkLoaderListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent bookmarkResult) {

        //ArrayList<Product> productList = productListAdapter.getProductList();
        //productListAdapter.clear();
        //listView.setVisibility(View.GONE);

        //productListAdapter.setData(productList);

        if (requestCode == 5)
        {
            if (resultCode == RESULT_OK)
            {
                boolean bookmarkRemoved = bookmarkResult.getBooleanExtra(getResources().getString(R.string.view_bookmark_result), false);

                if (bookmarkRemoved)
                {
                    bookmarkListAdapter.remove(viewedBookmark);
                    bookmarkListAdapter.notifyDataSetChanged();

                    if (bookmarkListAdapter.getCount() == 0)
                    {
                        listView.setVisibility(View.GONE);
                        noBookmarksMessage.setVisibility(View.VISIBLE);
                    }
                }
            }


        }

        //Product viewedProduct = (Product) data.getSerializableExtra(getResources().getString(R.string.product_data));



    }

    LoaderManager.LoaderCallbacks<ArrayList<BookmarkedProduct>> bookmarkLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<BookmarkedProduct>>()
    {
        @Override public Loader<ArrayList<BookmarkedProduct>> onCreateLoader(int id, Bundle args)
        {
            final SharedPreferences sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);
            bookmarkListLoader = new BookmarkListLoader(context, sharedPref, viewBookmarksActivity);

            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return bookmarkListLoader;
        }

        @Override public void onLoadFinished(Loader<ArrayList<BookmarkedProduct>> bookmarkListLoader, ArrayList<BookmarkedProduct> bookmarkedProducts)
        {
            // Add the new data in the adapter.
            bookmarkListAdapter.addAll(bookmarkedProducts);

            bookmarkListAdapter.notifyDataSetChanged();

            mainProgressBar.setVisibility(View.GONE);
            listView.removeFooterView(progressBar);

            final SharedPreferences sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);

            if (bookmarkedProducts.size() == 0 && sharedPref.getAll().size() == 0)
            {
                noBookmarksMessage.setVisibility(View.VISIBLE);
            }
            else
            {
                listView.setVisibility(View.VISIBLE);
            }
        }

        @Override public void onLoaderReset(Loader<ArrayList<BookmarkedProduct>> bookmarkListLoader)
        {
            bookmarkListAdapter.clear();
        }
    };
}
