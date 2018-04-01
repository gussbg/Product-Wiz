/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * The ViewProductActivity is where users can view detailed product information such as
 * it's review statistics, reviews from multiple retailers, and prices from multiple
 * retailers.
 *
 * @author Brendon Guss
 * @since  02/18/2018
 */
public class ViewProductActivity extends AppCompatActivity
{
    private Context           context;
    private ReviewListAdapter reviewListAdapter;
    private Intent            incomingIntent;
    private Intent            bookmarkResult;
    private Product           product;
    private boolean           bookmarkRemoved;
    private String            callingActivity;
    private DecimalFormat     priceFormat;

    private WeakReference<ViewProductActivity> viewProductActivity;

    // Views
    private ListView reviewList;

    protected ProgressBar mainProgressBar;
    private   ProgressBar reviewProgressMore;
    private   ProgressBar reviewProgress;

    private ProgressBar oneStarBar;
    private ProgressBar twoStarBar;
    private ProgressBar threeStarBar;
    private ProgressBar fourStarBar;
    private ProgressBar fiveStarBar;

    private TextView productTitle;
    private TextView productDescription;
    private TextView amazonPrice;
    private TextView walmartPrice;
    private TextView bestbuyPrice;
    private TextView ebayPrice;
    private TextView priceDiff;
    private TextView averageRating;
    private TextView numReviews;
    private TextView oneStarText;
    private TextView twoStarText;
    private TextView threeStarText;
    private TextView fourStarText;
    private TextView fiveStarText;
    private TextView noProductMessage;

    private RatingBar averageRatingBar;

    private ImageView productImage;
    private ImageView amazonLogo;
    private ImageView walmartLogo;
    private ImageView bestbuyLogo;
    private ImageView ebayLogo;

    private Button loadReviews;
    private Button loadMoreReviews;

    private ImageButton amazonBuyButton;
    private ImageButton walmartBuyButton;
    private ImageButton bestbuyButton;
    private ImageButton ebayBuyButton;

    private ImageButton addBookmark;
    private ImageButton removeBookmark;

    /**
     * Creates the ViewProductActivity and sets its members.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final int  PRODUCT_LOADER_ID     = 5;
        final int  REVIEW_LIST_LOADER_ID = 7;

        View header;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_listview);

        context             = getApplicationContext();
        reviewListAdapter   = new ReviewListAdapter(context);
        viewProductActivity = new WeakReference<>(this);
        priceFormat         = new DecimalFormat("$###,##0.00");

        header     = getLayoutInflater().inflate(R.layout.view_product, null);
        reviewList = findViewById(R.id.reviewList);

        reviewList.addHeaderView(header);

        bookmarkResult = new Intent();
        incomingIntent = getIntent();

        callingActivity = incomingIntent.getStringExtra(getResources().getString(R.string.calling_activity));

        // Sets the partially loaded product if the calling activity is the main activity.
        if (callingActivity.equals(getResources().getString(R.string.main_activity_class))
                || callingActivity.equals(getResources().getString(R.string.bookmark_activity_class)))
        {
            product = (Product) incomingIntent.getSerializableExtra(getResources().getString(R.string.product_data));
        }

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setMemberViews();

        reviewList.setAdapter(reviewListAdapter);
        reviewList.setHeaderDividersEnabled(false);

        // If this button is tapped, up to 25 reviews are loaded.
        loadMoreReviews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                getLoaderManager().restartLoader(REVIEW_LIST_LOADER_ID, null, reviewLoaderListener);
                reviewList.removeFooterView(loadMoreReviews);
                reviewList.addFooterView(reviewProgressMore);
            }
        });

        if (callingActivity.equals(getResources().getString(R.string.main_activity_class)) || callingActivity.equals(getResources().getString(R.string.bookmark_activity_class)))
        {
            getLoaderManager().initLoader(PRODUCT_LOADER_ID, null, productLoaderListener).forceLoad();
        }
        else if (callingActivity.equals(getResources().getString(R.string.barcode_capture_activity_class)))
        {
            getLoaderManager().initLoader(PRODUCT_LOADER_ID, null, productLoaderListener);
        }

        mainProgressBar.setVisibility(View.VISIBLE);
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
        switch (item.getItemId())
        {
            case android.R.id.home:
                // back icon in action bar clicked; goto parent activity, (main activity).
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // The product loader callback listeners are defined below.
    LoaderManager.LoaderCallbacks<Product> productLoaderListener = new LoaderManager.LoaderCallbacks<Product>()
    {
        /**
         * This method is called when the loader is created, it constructs the necessary attributes for the loader
         * to begin loading.
         *
         * @param id Loader ID.
         * @param args Bundle arguments.
         * @return Product Loader.
         */
        @Override public Loader<Product> onCreateLoader(int id, Bundle args)
        {
            ProductLoader productLoader = null;

            if (callingActivity.equals(getResources().getString(R.string.main_activity_class)) || callingActivity.equals(getResources().getString(R.string.bookmark_activity_class)))
            {
                productLoader = new ProductLoader(context, product, viewProductActivity);
            }
            else if (callingActivity.equals(getResources().getString(R.string.barcode_capture_activity_class)))
            {
                String upc = incomingIntent.getStringExtra("Barcode");

                productLoader = new ProductLoader(context, upc, viewProductActivity);
            }

            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return productLoader;
        }

        /**
         * This method is called when the loader has finished. All of the views are set with data here.
         *
         * @param productLoader The productLoader.
         * @param loadedProduct The loaded product.
         */
        @Override public void onLoadFinished(Loader<Product> productLoader, Product loadedProduct)
        {
            product = loadedProduct;

            mainProgressBar.setVisibility(View.GONE);

            if (!product.hasInfo())
            {
                noProductMessage.setVisibility(View.VISIBLE);
            }
            else
            {
                // The product image is set here, a "no image" image is set
                // if no image could be found
                if (product.getLargeImage() == null && product.getSmallImage() == null)
                {
                    productImage.setImageResource(R.drawable.no_image);
                }
                else if (product.getLargeImage() == null && product.getSmallImage() != null)
                {
                    productImage.setImageBitmap(product.getSmallImage().bitmap);
                }
                else
                {
                    productImage.setImageBitmap(product.getLargeImage());
                }

                productImage.setVisibility(View.VISIBLE);

                productTitle.setText(product.getAmazonProductInfo().getTitle());
                productTitle.setVisibility(View.VISIBLE);

                if (product.getDescription() != null && !product.getDescription().equals(""))
                {
                    productDescription.setText(product.getDescription());
                    productDescription.setVisibility(View.VISIBLE);
                }

                // Set All Views and Constraints.
                setRetailerConstraints();

                setRetailerView(product.getAmazonProductInfo(), amazonPrice, amazonBuyButton, amazonLogo);
                setRetailerView(product.getBestbuyProductInfo(), bestbuyPrice, bestbuyButton, bestbuyLogo);
                setRetailerView(product.getEbayProductInfo(), ebayPrice, ebayBuyButton, ebayLogo);
                setRetailerView(product.getWalmartProductInfo(), walmartPrice, walmartBuyButton, walmartLogo);

                setBookmarkViews();
                setReviewStatViews();
                setLoadReviews();
            }
        }

        /**
         * This method is called when the loader is reset.
         *
         * @param productLoader The product loader.
         */
        @Override public void onLoaderReset(Loader<Product> productLoader)
        {
            productLoader.forceLoad();
        }
    };

    // The review list loader callback listeners are defined below.
    LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Review>>()
    {
        /**
         * This method is called when the loader is created, it constructs the necessary attributes for the loader
         * to begin loading.
         *
         * @param id Loader ID.
         * @param args Bundle arguments.
         * @return The ReviewListLoader
         */
        @Override public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args)
        {
            // This is called when a new Loader needs to be created.
            return new ReviewListLoader(context, product);
        }

        /**
         * This method is called when the product loader is finished.
         *
         * @param reviewListLoader The review list loader.
         * @param reviews The loaded reviews.
         */
        @Override public void onLoadFinished(Loader<ArrayList<Review>> reviewListLoader, ArrayList<Review> reviews)
        {
            // Add the new data in the adapter.
            if (reviews != null)
            {
                reviewListAdapter.addAll(reviews);
            }

            reviewListAdapter.notifyDataSetChanged();

            // Adds space between this view and the review list.
            oneStarBar.setPadding(0,0,0,convertDPtoPX(16));

            reviewProgress.setVisibility(View.GONE);

            reviewList.removeFooterView(reviewProgressMore);

            // Determines if the the loadMoreReviews button should be added.
            if (!product.hasMoreReviews())
            {
                reviewList.removeFooterView(loadMoreReviews);
            }
            else if (reviewList.getFooterViewsCount() == 0)
            {
                reviewList.addFooterView(loadMoreReviews);
            }

            reviewList.setHeaderDividersEnabled(true);
            reviewList.setVisibility(View.VISIBLE);
        }

        /**
         * This method is called when the loader is reset.
         * @param reviewListLoader The review list loader.
         */
        @Override public void onLoaderReset(Loader<ArrayList<Review>> reviewListLoader)
        {
            reviewListLoader.forceLoad();
        }
    };

    /**
     * Sets all of the member views except reviewList because it needs to be set
     * before this method is called.
     */
    private void setMemberViews()
    {
        mainProgressBar    = findViewById(R.id.progressBarProduct);
        reviewProgressMore = new ProgressBar(context);
        reviewProgress     = findViewById(R.id.reviewProgress);

        oneStarBar   = findViewById(R.id.oneStarBar);
        twoStarBar   = findViewById(R.id.twoStarBar);
        threeStarBar = findViewById(R.id.threeStarBar);
        fourStarBar  = findViewById(R.id.fourStarBar);
        fiveStarBar  = findViewById(R.id.fiveStarBar);

        productTitle       = findViewById(R.id.productTitle);
        productDescription = findViewById(R.id.productDescription);
        amazonPrice        = findViewById(R.id.amazonPrice);
        walmartPrice       = findViewById(R.id.walmartPrice);
        bestbuyPrice       = findViewById(R.id.bestbuyPrice);
        ebayPrice          = findViewById(R.id.ebayPrice);
        priceDiff          = findViewById(R.id.productPriceDiff);
        averageRating      = findViewById(R.id.averageRating);
        numReviews         = findViewById(R.id.numReviews);
        oneStarText        = findViewById(R.id.oneStarText);
        twoStarText        = findViewById(R.id.twoStarText);
        threeStarText      = findViewById(R.id.threeStarText);
        fourStarText       = findViewById(R.id.fourStarText);
        fiveStarText       = findViewById(R.id.fiveStarText);
        noProductMessage   = findViewById(R.id.noProduct);

        averageRatingBar = findViewById(R.id.averageRatingBar);

        productImage = findViewById(R.id.imageViewProduct);
        amazonLogo   = findViewById(R.id.amazonLogo);
        walmartLogo  = findViewById(R.id.walmartLogo);
        bestbuyLogo  = findViewById(R.id.bestbuyLogo);
        ebayLogo     = findViewById(R.id.ebayLogo);

        loadReviews     = findViewById(R.id.loadReviews);
        loadMoreReviews = new Button(context);

        loadMoreReviews.setText(getResources().getString(R.string.load_more_reviews));
        loadMoreReviews.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        loadMoreReviews.setTextColor(Color.WHITE);

        amazonBuyButton  = findViewById(R.id.amazonBuyButton);
        walmartBuyButton = findViewById(R.id.walmartBuyButton);
        bestbuyButton    = findViewById(R.id.bestbuyButton);
        ebayBuyButton    = findViewById(R.id.ebayBuyButton);

        addBookmark    = findViewById(R.id.productAddBookmark);
        removeBookmark = findViewById(R.id.productBookmarkAdded);
    }

    /**
     * Sets the constraints for views relating to retailer logos, prices, and buy buttons.
     * This is necessary because not all retailers will have the product, therefore, different
     * adjustments need to made to their constraints when certain views are gone.
     */
    private void setRetailerConstraints()
    {
        ConstraintLayout constraintLayout;
        ConstraintSet constraintSet;

        constraintLayout = findViewById(R.id.viewProductLayout);
        constraintSet    = new ConstraintSet();

        final int SIXTEEN_DP = 16;
        final int EIGHT_DP   = 8;
        final int TWO_DP     = 2;

        constraintSet.clone(constraintLayout);

        // Set Amazon buy button top constraint to the bottom of the product image.
        if (product.getAmazonProductInfo().hasInfo())
        {
            constraintSet.connect(amazonBuyButton.getId(), ConstraintSet.TOP,
                                  productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(SIXTEEN_DP));
            constraintSet.applyTo(constraintLayout);
        }

        // Set Walmart buy button top constraint to the appropriate bottom constraint based on
        // if the Amazon buy button is visible or not.
        if (product.getWalmartProductInfo().hasInfo())
        {
            if (product.getAmazonProductInfo().hasInfo())
            {
                constraintSet.connect(walmartBuyButton.getId(), ConstraintSet.TOP,
                                      amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(TWO_DP));
                constraintSet.applyTo(constraintLayout);
            }
            else
            {
                constraintSet.connect(walmartBuyButton.getId(), ConstraintSet.TOP,
                                      productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
                constraintSet.applyTo(constraintLayout);
            }
        }

        // Set BestBuy buy button top constraint to the appropriate bottom constraint based on
        // what other buy buttons should exist.
        if (product.getBestbuyProductInfo().hasInfo())
        {
            if (product.getWalmartProductInfo().hasInfo())
            {
                constraintSet.connect(bestbuyButton.getId(), ConstraintSet.TOP,
                                      walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(TWO_DP));
                constraintSet.applyTo(constraintLayout);
            }
            else if (product.getAmazonProductInfo().hasInfo())
            {
                constraintSet.connect(bestbuyButton.getId(), ConstraintSet.TOP,
                                      amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(TWO_DP));
                constraintSet.applyTo(constraintLayout);
            }
            else
            {
                constraintSet.connect(bestbuyButton.getId(), ConstraintSet.TOP,
                                      productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(SIXTEEN_DP));
                constraintSet.applyTo(constraintLayout);
            }
        }

        // Set eBay buy button top constraint to the appropriate bottom constraint based on
        // what other buy buttons should exist.
        if (product.getEbayProductInfo().hasInfo())
        {
            if (product.getBestbuyProductInfo().hasInfo())
            {
                constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP,
                                      bestbuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(TWO_DP));
                constraintSet.applyTo(constraintLayout);
            }
            else if (product.getWalmartProductInfo().hasInfo())
            {
                constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP,
                                      walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(TWO_DP));
                constraintSet.applyTo(constraintLayout);
            }
            else if (product.getAmazonProductInfo().hasInfo())
            {
                constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP,
                                      amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(TWO_DP));
                constraintSet.applyTo(constraintLayout);
            }
            else
            {
                constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP,
                                      productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(SIXTEEN_DP));
                constraintSet.applyTo(constraintLayout);
            }
        }

        // Sets the proper constraints for bookmark buttons depending on what retailer information is visible.
        if (product.getEbayProductInfo().hasInfo())
        {
            constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP,
                                  ebayBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP,
                                  ebayBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.applyTo(constraintLayout);
        }
        else if (product.getBestbuyProductInfo().hasInfo())
        {
            constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP,
                                  bestbuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP, bestbuyButton.getId(),
                                  ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.applyTo(constraintLayout);
        }
        else if (product.getWalmartProductInfo().hasInfo())
        {
            constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP,
                                  walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP,
                                  walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.applyTo(constraintLayout);
        }
        else if (product.getAmazonProductInfo().hasInfo())
        {
            constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP,
                                  amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP,
                                  amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(EIGHT_DP));
            constraintSet.applyTo(constraintLayout);
        }
        else
        {
            constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP,
                                  productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(SIXTEEN_DP));
            constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP,
                                  productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(SIXTEEN_DP));
            constraintSet.applyTo(constraintLayout);
        }
    }

    /**
     * Sets the retailer logo, price, and buy button listener, and visibility of it's views.
     */
    private void setRetailerView(final ProductInfo productInfo, TextView priceTextView, ImageButton buyButton, ImageView logo)
    {
        if (productInfo.hasInfo())
        {
            priceTextView.setText(priceFormat.format(productInfo.getPrice()));

            // Sets the buy button and price text color green if the price is the lowest (or equal to the lowest price).
            if (productInfo.getPrice().compareTo(product.getLowestPrice()) == 0)
            {
                ViewCompat.setBackgroundTintList(buyButton, ContextCompat.getColorStateList(context, R.color.colorBookmarkAdded));
                priceTextView.setTextColor(getResources().getColor(R.color.colorBookmarkAdded));
            }

            buyButton.setVisibility(View.VISIBLE);
            logo.setVisibility(View.VISIBLE);
            priceTextView.setVisibility(View.VISIBLE);

            buyButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(productInfo.getProductURL()));
                    viewProductWebpage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Needed for older Android APIs.
                    context.startActivity(viewProductWebpage);
                }
            });
        }
    }

    /**
     * Sets all of the bookmark button views and determines which bookmark button is to be displayed along with the
     * difference in the product's lowest price since it was bookmarked.
     */
    private void setBookmarkViews()
    {
        final SharedPreferences sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        final SharedPreferences.Editor editor = sharedPref.edit();

        // Determines if the product is bookmarked or not.
        if (!sharedPref.contains(product.getUPC()))
        {
            removeBookmark.setVisibility(View.GONE);
            addBookmark.setVisibility(View.VISIBLE);
        }
        else
        {
            addBookmark.setVisibility(View.INVISIBLE);
            removeBookmark.setVisibility(View.VISIBLE);

            String json = sharedPref.getString(product.getUPC(), "");
            BookmarkedProduct bookmarkedProduct = gson.fromJson(json, BookmarkedProduct.class);

            bookmarkedProduct.setProduct(product);

            BigDecimal priceDiffVal = product.getLowestPrice().subtract(bookmarkedProduct.getPriceAdded());
            priceDiffVal = priceDiffVal.setScale(2, RoundingMode.DOWN);

            ProductListAdapter.setPriceDifference(context, priceDiffVal, priceDiff, priceFormat);
        }

        addBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BookmarkedProduct bookmarkedProduct = new BookmarkedProduct(product.getUPC(), new Date(), product.getLowestPrice());

                String json = gson.toJson(bookmarkedProduct);

                editor.putString(product.getUPC(), json);
                editor.apply();

                // The result of whether a bookmark was removed is sent to the MainActivity in order to update
                // it's product list view.
                bookmarkRemoved = false;

                bookmarkResult.removeExtra(getResources().getString(R.string.view_bookmark_result));
                bookmarkResult.putExtra(getResources().getString(R.string.view_bookmark_result), bookmarkRemoved);

                setResult(RESULT_OK, bookmarkResult);

                addBookmark.setVisibility(View.INVISIBLE);
                removeBookmark.setVisibility(View.VISIBLE);

                BigDecimal priceDiffVal = product.getLowestPrice().subtract(bookmarkedProduct.getPriceAdded());
                priceDiffVal = priceDiffVal.setScale(2, RoundingMode.DOWN);

                ProductListAdapter.setPriceDifference(context, priceDiffVal, priceDiff, priceFormat);


                Snackbar snackbar = Snackbar.make(view , getString(R.string.bookmark_added),
                        Snackbar.LENGTH_SHORT);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });

        removeBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor.remove(product.getUPC());
                editor.apply();

                addBookmark.setVisibility(View.VISIBLE);
                removeBookmark.setVisibility(View.GONE);
                priceDiff.setVisibility(View.INVISIBLE);

                // The result of whether a bookmark was removed is sent to the MainActivity in order to update
                // it's product list view.
                bookmarkRemoved = true;

                bookmarkResult.removeExtra(getResources().getString(R.string.view_bookmark_result));
                bookmarkResult.putExtra(getResources().getString(R.string.view_bookmark_result), bookmarkRemoved);

                setResult(RESULT_OK, bookmarkResult);

                Snackbar snackbar = Snackbar.make(view , getString(R.string.bookmark_removed),
                        Snackbar.LENGTH_SHORT);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });
    }

    /**
     * All of the views that are related to review statistics are set here.
     */
    private void setReviewStatViews()
    {
        DecimalFormat decimalFormatRating = new DecimalFormat("0.0");
        DecimalFormat numReviewsFormat    = new DecimalFormat("###,##0");

        averageRating.setText(decimalFormatRating.format(product.getReviewStats().getAverageRating()));
        averageRatingBar.setRating(product.getReviewStats().getAverageRating().floatValue());

        numReviews.setText(getString(R.string.num_reviews_value,
                numReviewsFormat.format(product.getReviewStats().getTotalStars())));

        if (!product.hasBasicReviewStats())
        {
            oneStarBar.setMax(product.getReviewStats().getMaxStarCount());
            twoStarBar.setMax(product.getReviewStats().getMaxStarCount());
            threeStarBar.setMax(product.getReviewStats().getMaxStarCount());
            fourStarBar.setMax(product.getReviewStats().getMaxStarCount());
            fiveStarBar.setMax(product.getReviewStats().getMaxStarCount());

            oneStarBar.setProgress(product.getReviewStats().getNumOneStars());
            twoStarBar.setProgress(product.getReviewStats().getNumTwoStars());
            threeStarBar.setProgress(product.getReviewStats().getNumThreeStars());
            fourStarBar.setProgress(product.getReviewStats().getNumFourStars());
            fiveStarBar.setProgress(product.getReviewStats().getNumFiveStars());

            oneStarBar.setPadding(0,0,0, convertDPtoPX(8));

            oneStarBar.setVisibility(View.VISIBLE);
            twoStarBar.setVisibility(View.VISIBLE);
            threeStarBar.setVisibility(View.VISIBLE);
            fourStarBar.setVisibility(View.VISIBLE);
            fiveStarBar.setVisibility(View.VISIBLE);

            oneStarText.setVisibility(View.VISIBLE);
            twoStarText.setVisibility(View.VISIBLE);
            threeStarText.setVisibility(View.VISIBLE);
            fourStarText.setVisibility(View.VISIBLE);
            fiveStarText.setVisibility(View.VISIBLE);
        }

        averageRating.setVisibility(View.VISIBLE);
        averageRatingBar.setVisibility(View.VISIBLE);
        numReviews.setVisibility(View.VISIBLE);
    }

    /**
     * The load reviews button's listener is set in this method.
     */
    private void setLoadReviews()
    {
        if (product.getReviewStats().getTotalStars() > 0)
        {
            loadReviews.setVisibility(View.VISIBLE);
            oneStarBar.setPadding(0,0,0,0);
        }

        loadReviews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                getLoaderManager().restartLoader(5, null, reviewLoaderListener);
                reviewList.removeFooterView(loadReviews);
                loadReviews.setVisibility(View.GONE);
                reviewProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Utility method that converts density independent pixels to ordinary pixels
     * @param dp The DP value to convert.
     * @return The equivalent pixel value.
     */
    private int convertDPtoPX(int dp)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
