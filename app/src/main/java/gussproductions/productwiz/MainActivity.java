package gussproductions.productwiz;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity
{
    String              query;
    Context             context;
    ProductListLoader   productListLoader;
    ProductListAdapter  productListAdapter;
    AmazonSearchLoader  amazonSearchLoader;
    AmazonProductSearch amazonProductSearch;

    // Views
    ListView    listView;
    Button      btnLoadMore;
    ProgressBar progressBar;
    ProgressBar mainProgressBar;
    TextView    noProductsMessage;
    SearchView  searchView;

    private final int PRODUCT_LOADER_ID = 1;
    private final int AMAZON_SEARCH_LOADER_ID = 2;

    WeakReference<MainActivity> mainActivity;

    long currentScrollTime;
    long lastScrollTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        context            = getApplicationContext();
        listView           = findViewById(R.id.listView);
        mainActivity       = new WeakReference<>(this);
        productListAdapter = new ProductListAdapter(context);
        btnLoadMore        = new Button(context);
        progressBar        = new ProgressBar(context);
        mainProgressBar    = findViewById(R.id.mainProgressBar);
        noProductsMessage  = findViewById(R.id.noProducts);

        btnLoadMore.setText(getResources().getString(R.string.load_more_products));
        btnLoadMore.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnLoadMore.setTextColor(Color.WHITE);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            noProductsMessage.setVisibility(View.GONE);


            productListAdapter.clear();



            query = intent.getStringExtra(SearchManager.QUERY);

            mainProgressBar.setVisibility(View.VISIBLE);

            listView.setAdapter(productListAdapter);
            listView.setVisibility(View.GONE);

            listView.setOnItemClickListener(onItemClickListener);
            listView.setOnScrollListener(onScrollListener);


            /**
             * Listening to Load More button click event
             **/


            btnLoadMore.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    getLoaderManager().restartLoader(PRODUCT_LOADER_ID, null, productLoaderListener);
                    listView.removeFooterView(btnLoadMore);
                    listView.addFooterView(progressBar);
                }
            });


            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().restartLoader(AMAZON_SEARCH_LOADER_ID, null, amazonSearchLoaderListener);
            searchView.clearFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 4 || requestCode == 0)
        {
            productListAdapter.notifyDataSetChanged();
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem viewBookmarkButton = menu.findItem(R.id.viewBookmarks);
        final MenuItem scanBarcodeButton = menu.findItem(R.id.barcodeScan);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Detect SearchView icon clicks
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewBookmarkButton.setVisible(false);
                scanBarcodeButton.setVisible(false);
            }
        });
        // Detect SearchView close
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                viewBookmarkButton.setVisible(true);
                scanBarcodeButton.setVisible(true);

                listView.invalidateViews();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {


            @Override
            public boolean onQueryTextSubmit(String query)
            {
                productListAdapter.clear();

                listView.removeFooterView(btnLoadMore);
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.barcodeScan)
        {
            Intent scanBarcode = new Intent(this, BarcodeCaptureActivity.class);

            this.startActivity(scanBarcode);
        }
        else if (item.getItemId() == R.id.viewBookmarks)
        {
            Intent viewBookmarks = new Intent(this, ViewBookmarksActivity.class);

            //this.startActivity(viewBookmarks);

            startActivityForResult(viewBookmarks, 4);
        }

        return true;
    }

    LoaderManager.LoaderCallbacks<ArrayList<Product>> productLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Product>>()
    {
        @Override public Loader<ArrayList<Product>> onCreateLoader(int id, Bundle args)
        {
            productListLoader = new ProductListLoader(context, amazonProductSearch, mainActivity);

            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return productListLoader;
        }

        @Override public void onLoadFinished(Loader<ArrayList<Product>> productListLoader, ArrayList<Product> products)
        {
            // Add the new data in the adapter.
            productListAdapter.addAll(products);

            productListAdapter.notifyDataSetChanged();

            mainProgressBar.setVisibility(View.GONE);
            mainProgressBar.setProgress(0);

            listView.removeFooterView(progressBar);

            if (products.size() == 0)
            {
                noProductsMessage.setVisibility(View.VISIBLE);
            }
            else if (products.size() < 10)
            {
                listView.removeFooterView(btnLoadMore);
                listView.setVisibility(View.VISIBLE);
            }
            else
            {
                listView.setVisibility(View.VISIBLE);

                if (listView.getFooterViewsCount() == 0 && products.size() == 10)
                {
                    listView.addFooterView(btnLoadMore);
                }
            }
        }

        @Override public void onLoaderReset(Loader<ArrayList<Product>> productListLoader)
        {
            productListAdapter.clear();
        }
    };

    LoaderManager.LoaderCallbacks<AmazonProductSearch> amazonSearchLoaderListener = new LoaderManager.LoaderCallbacks<AmazonProductSearch>()
    {
        @Override public Loader<AmazonProductSearch> onCreateLoader(int id, Bundle args)
        {
            amazonSearchLoader = new AmazonSearchLoader(context, query, mainActivity);

            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return amazonSearchLoader;
        }

        @Override public void onLoadFinished(Loader<AmazonProductSearch> amazonSearchLoader, AmazonProductSearch productSearch)
        {
            amazonProductSearch = productSearch;
            getLoaderManager().restartLoader(PRODUCT_LOADER_ID, null, productLoaderListener);
        }

        @Override public void onLoaderReset(Loader<AmazonProductSearch> amazonSearchLoader) {}
    };

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            productListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {}
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
        {
            Intent intent = new Intent(context, ViewProductActivity.class);

            intent.putExtra(getResources().getString(R.string.calling_activity), getResources().getString(R.string.main_activity_class));

            Product product = (Product) arg0.getItemAtPosition(position);

            intent.putExtra(getResources().getString(R.string.product_data), product);

            startActivityForResult(intent, 0);
        }
    };

}
