/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The MainActivity is where the user can search for products via keywords, scan a product's
 * barcode by tapping on the Barcode icon, or they can tap on the Bookmark icon to view their
 * bookmarked products. The keyword search returns up to 10 products at a time to reduce loading
 * time. All products that are returned by this search must be available on Amazon as it uses its
 * keyword search. It searches the other three retailers to determine the lowest price in this activity.
 *
 * @author Brendon Guss
 * @since  02/08/2018
 */
public class MainActivity extends AppCompatActivity
{
    private Context             context;
    private String              query;
    private ProductListAdapter  productListAdapter;
    private AmazonProductSearch amazonProductSearch;

    private WeakReference<MainActivity> mainActivity;

    // Views
    private   ListView    productListView;
    private   Button      btnLoadMore;
    private   ProgressBar progressBar;
    protected ProgressBar mainProgressBar;
    private   TextView    noProductsMessage;
    private   SearchView  searchView;

    private final int PRODUCT_LIST_LOADER_ID = 1;
    private final int PRODUCT_REQUEST_CODE   = 0;
    private final int BOOKMARK_REQUEST_CODE  = 4;

    /**
     * Creates the MainActivity and sets its members.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        context            = getApplicationContext();
        productListView    = findViewById(R.id.listView);
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

    /**
     * When a new intent is detected, it is set and handled in this method.
     *
     * @param intent The new intent.
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * The action search intent is handled here and possibly other intents if needed in the future.
     *
     * @param intent The intent to handle.
     */
    private void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            final int AMAZON_SEARCH_LOADER_ID = 2;

            query = intent.getStringExtra(SearchManager.QUERY);

            productListAdapter.clear(); // The productListAdapter is cleared of previous results.
            productListView.setAdapter(productListAdapter);

            productListView.setOnItemClickListener(onItemClickListener);
            productListView.setOnScrollListener(onScrollListener);

            noProductsMessage.setVisibility(View.GONE);
            productListView.setVisibility(View.GONE);
            mainProgressBar.setVisibility(View.VISIBLE);

            // If this button is tapped, up to 10 more products are loaded.
            btnLoadMore.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    getLoaderManager().restartLoader(PRODUCT_LIST_LOADER_ID, null, productListLoaderListener);
                    productListView.removeFooterView(btnLoadMore);
                    productListView.addFooterView(progressBar);
                }
            });

            // Prepares the AmazonSearchLoader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().restartLoader(AMAZON_SEARCH_LOADER_ID, null, amazonSearchLoaderListener);
            searchView.clearFocus();
        }
    }

    /**
     * The method is called when the ViewProductActivity  or ViewBookmarksActivity finishes,
     * if it is launched from this activity. It refreshes the views of this activity to
     * ensure that they reflect changes to displayed products resulting from both of these
     * activities.
     *
     * @param requestCode The request code from the finished activity.
     * @param resultCode The result code from the finished activity.
     * @param data Unused intent data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == BOOKMARK_REQUEST_CODE || requestCode == PRODUCT_REQUEST_CODE)
        {
            productListAdapter.notifyDataSetChanged();
            searchView.clearFocus();
        }
    }

    /**
     * This method sets up the action bar menu icons such as search, scan barcode,
     * and view bookmarks.
     *
     * @param menu The Action Bar menu.
     * @return Returns false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater;
        final MenuItem viewBookmarkButton;
        final MenuItem scanBarcodeButton;
        final SearchManager searchManager;

        if (getSupportActionBar() != null)
        {
            // Disables application title in action bar
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // Enables back button.
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // Set the application icon.
            getSupportActionBar().setIcon(R.mipmap.productwiz_round_icon);
        }

        // Inflate the options menu from XML
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        viewBookmarkButton = menu.findItem(R.id.viewBookmarks);
        scanBarcodeButton  = menu.findItem(R.id.barcodeScan);

        // Get the SearchView and set the searchable configuration
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView    = (SearchView)    menu.findItem(R.id.menuSearch).getActionView();

        // Sets the searchable information.
        if (searchManager != null)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        // Detect SearchView icon clicks
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // When the search icon is tapped, these icons are removed from the view to
                // give more space to type in keywords.
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
                // When the search view is closed, these two icons are brought back into view.
                viewBookmarkButton.setVisible(true);
                scanBarcodeButton.setVisible(true);

                productListView.invalidateViews();
                return false;
            }
        });

        // When a keyword search is submitted, the previous results are cleared.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                productListAdapter.clear();
                productListView.setAdapter(productListAdapter);
                productListView.removeFooterView(btnLoadMore);
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

    /**
     * This method is called whenever a menu item is clicked.
     *
     * @param item The menu item.
     * @return Returns false to allow normal menu processing to proceed, true to consume it here.
     */
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

            startActivityForResult(viewBookmarks, BOOKMARK_REQUEST_CODE);
        }

        return true;
    }

    // The product list loader callback listeners are defined below.
    LoaderManager.LoaderCallbacks<ArrayList<Product>> productListLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Product>>()
    {
        /**
         * This method is called when the loader is created, it constructs the necessary attributes for the loader
         * to begin loading, it is not fully loaded when it is returned however.
         *
         * @param id Loader ID.
         * @param args Bundle arguments.
         * @return ProductList loader.
         */
        @Override public Loader<ArrayList<Product>> onCreateLoader(int id, Bundle args)
        {
            // This is called when a new Loader needs to be created.
            return new ProductListLoader(context, amazonProductSearch, mainActivity);
        }

        /**
         * This method is called when the loader has finished. All of the views are set with data here.
         *
         * @param productListLoader The productListLoader.
         * @param products The loaded ArrayList of partially loaded products.
         */
        @Override public void onLoadFinished(Loader<ArrayList<Product>> productListLoader, ArrayList<Product> products)
        {
            final int PRODUCTS_MAX_LOAD = 10;

            // Add the new data in the adapter.
            productListAdapter.addAll(products);

            // Review product list views.
            productListAdapter.notifyDataSetChanged();

            // Reset progress bar.
            mainProgressBar.setVisibility(View.GONE);
            mainProgressBar.setProgress(0);

            productListView.removeFooterView(progressBar);

            // Handles the display of the no products message and the load more products button.
            if (products.size() == 0)
            {
                noProductsMessage.setVisibility(View.VISIBLE);
            }
            else if (products.size() < PRODUCTS_MAX_LOAD)
            {
                productListView.removeFooterView(btnLoadMore);
                productListView.setVisibility(View.VISIBLE);
            }
            else
            {
                productListView.setVisibility(View.VISIBLE);

                if (productListView.getFooterViewsCount() == 0 && products.size() == PRODUCTS_MAX_LOAD)
                {
                    productListView.addFooterView(btnLoadMore);
                }
            }
        }

        /**
         * This method is called when the loader is reset.
         *
         * @param productListLoader The productListLoader.
         */
        @Override public void onLoaderReset(Loader<ArrayList<Product>> productListLoader)
        {
            productListAdapter.clear();
        }
    };

    // The Amazon product search callback listeners are defined below.
    LoaderManager.LoaderCallbacks<AmazonProductSearch> amazonSearchLoaderListener
            = new LoaderManager.LoaderCallbacks<AmazonProductSearch>()
    {
        /**
         * This method is called when the loader is created, it constructs the necessary attributes for the loader
         * to begin loading.
         *
         * @param id Loader ID.
         * @param args Bundle arguments.
         * @return The AmazonSearchLoader.
         */
        @Override public Loader<AmazonProductSearch> onCreateLoader(int id, Bundle args)
        {
            // This is called when a new Loader needs to be created.
            return new AmazonSearchLoader(context, query, mainActivity);
        }

        /**
         * This method is called when the AmazonProductSearch loader is finished. The product loader is then started.
         *
         * @param amazonSearchLoader The Amazon search loader.
         * @param productSearch The loaded Amazon product search.
         */
        @Override public void onLoadFinished(Loader<AmazonProductSearch> amazonSearchLoader,
                                             AmazonProductSearch productSearch)
        {
            amazonProductSearch = productSearch;

            // Prepares the ProductListLoader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().restartLoader(PRODUCT_LIST_LOADER_ID, null, productListLoaderListener);
        }

        /**
         * This method is called when the loader is reset.
         *
         * @param amazonSearchLoader The Amazon search loader.
         */
        @Override public void onLoaderReset(Loader<AmazonProductSearch> amazonSearchLoader) {}
    };

    /**
     * The onScrollListener detects when a change in scroll behavior is detected.
     */
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener()
    {
        /**
         * This method is called when the state of user scrolling is changed such as when a scroll ends.
         *
         * @param view The ListView.
         * @param scrollState The scroll sate.
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            productListAdapter.notifyDataSetChanged();
        }

        // Unused method, needs to be overridden for the onScrollListener.
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {}
    };

    /**
     * This listener is called when a product from the productList is tapped on.
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
        {
            Intent viewProduct = new Intent(context, ViewProductActivity.class);

            viewProduct.putExtra(getResources().getString(R.string.calling_activity),
                    getResources().getString(R.string.main_activity_class));
            viewProduct.putExtra(getResources().getString(R.string.product_data),
                    (Product) arg0.getItemAtPosition(position));

            startActivityForResult(viewProduct, PRODUCT_REQUEST_CODE);
        }
    };
}
