package gussproductions.productwiz;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brendon on 2/18/2018.
 */

public class ViewProductActivity extends AppCompatActivity
{
    Context context;
    ProgressBar mainProgressBar;
    ProductLoader   productLoader;
    String callingActivity;
    Intent intent;
    ImageView imageView;
    TextView productTitle;
    ReviewListLoader reviewListLoader;
    ReviewListAdapter reviewListAdapter;
    Product product;
    Button loadReviews;
    Button loadMoreReviews;
    ProgressBar reviewProgress;

    View header;

    ListView reviewList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_listview);

        context = getApplicationContext();

        header = getLayoutInflater().inflate(R.layout.view_product, null);

        reviewList = findViewById(R.id.reviewList);

        reviewProgress = new ProgressBar(context);

        reviewList.addHeaderView(header);

        loadMoreReviews = new Button(context);

        loadMoreReviews.setText(getResources().getString(R.string.load_more_reviews));
        loadMoreReviews.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        loadMoreReviews.setTextColor(Color.WHITE);

        /**
         * Listening to Load More button click event
         **/
        loadMoreReviews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                getLoaderManager().restartLoader(5, null, reviewLoaderListener);
                reviewList.removeFooterView(loadMoreReviews);
                reviewList.addFooterView(reviewProgress);
            }
        });



        reviewList.setHeaderDividersEnabled(false);

        intent = getIntent();

        callingActivity = intent.getStringExtra(getResources().getString(R.string.calling_activity));

        if (callingActivity.equals(getResources().getString(R.string.main_activity_class)))
        {
            product = (Product) intent.getSerializableExtra(getResources().getString(R.string.product_data));
        }



        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context            = getApplicationContext();
        mainProgressBar    = findViewById(R.id.progressBarProduct);
        imageView          = findViewById(R.id.imageViewProduct);
        productTitle       = findViewById(R.id.productTitle);

        loadReviews = findViewById(R.id.loadReviews);





        reviewListAdapter = new ReviewListAdapter(context);

        reviewList.setAdapter(reviewListAdapter);

        //reviewList.setVisibility(View.GONE);




        productTitle.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        loadReviews.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);




        //mainProgressBar.setVisibility(View.VISIBLE);



        //String barcode = intent.getStringExtra("Barcode");







        //TextView textView = findViewById(R.id.barcodeText);

        //textView.setText(product.getLowestPriceProductURL());

        //ImageView imageView = findViewById(R.id.imageViewProduct);

        //imageView.setImageBitmap(product.getImage());

        getLoaderManager().restartLoader(0, null, productLoaderListener).forceLoad();
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


    LoaderManager.LoaderCallbacks<Product> productLoaderListener = new LoaderManager.LoaderCallbacks<Product>()
    {
        @Override public Loader<Product> onCreateLoader(int id, Bundle args)
        {


            if (callingActivity.equals(getResources().getString(R.string.main_activity_class)))
            {


                productLoader = new ProductLoader(context, product);


            }
            else if (callingActivity.equals(getResources().getString(R.string.barcode_capture_activity_class)))
            {
                String upc = intent.getStringExtra("Barcode");

                productLoader = new ProductLoader(context, upc);
            }



            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return productLoader;
        }

        @Override public void onLoadFinished(Loader<Product> productLoader, Product loadedProduct)
        {


            //System.out.println(product.)



            product = loadedProduct;

            mainProgressBar.setVisibility(View.GONE);




            imageView.setImageBitmap(product.getImage());

            //product.getImage();
            imageView.setVisibility(View.VISIBLE);

            productTitle.setText(product.getUPC());
            productTitle.setVisibility(View.VISIBLE);

            loadReviews.setVisibility(View.VISIBLE);



            loadReviews.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    getLoaderManager().restartLoader(5, null, reviewLoaderListener);
                    reviewList.removeFooterView(loadReviews);
                }
            });



        }

        @Override public void onLoaderReset(Loader<Product> productLoader)
        {

        }
    };

    LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Review>>()
    {
        @Override public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args)
        {
            reviewListLoader = new ReviewListLoader(context, product);

            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return reviewListLoader;
        }

        @Override public void onLoadFinished(Loader<ArrayList<Review>> reviewListLoader, ArrayList<Review> reviews)
        {
            // Add the new data in the adapter.
            if (reviews != null)
            {
                reviewListAdapter.addAll(reviews);
            }


            reviewListAdapter.notifyDataSetChanged();

            //mainProgressBar.setVisibility(View.GONE);



            loadReviews.setVisibility(View.GONE);
            reviewList.removeFooterView(reviewProgress);

            if (!product.hasMoreReviews())
            {
                reviewList.removeFooterView(loadMoreReviews);
            }
            else if (reviewList.getFooterViewsCount() == 0)
            {
                reviewList.addFooterView(loadMoreReviews);
            }



            System.out.println("Num of reviews: " + reviews.size());

            reviewList.setHeaderDividersEnabled(true);
            reviewList.setVisibility(View.VISIBLE);

        }

        @Override public void onLoaderReset(Loader<ArrayList<Review>> reviewListLoader)
        {
            reviewListAdapter.setData(null);
        }
    };


}
