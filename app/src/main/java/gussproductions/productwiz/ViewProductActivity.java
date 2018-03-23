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
 * Created by Brendon on 2/18/2018.
 */

public class ViewProductActivity extends AppCompatActivity
{
    Context context;
    ProgressBar mainProgressBar;
    ProductLoader   productLoader;
    String callingActivity;
    Intent intent;
    ImageView productImage;
    TextView productTitle;
    ReviewListLoader reviewListLoader;
    ReviewListAdapter reviewListAdapter;
    Product product;
    Button loadReviews;
    Button loadMoreReviews;
    ProgressBar reviewProgress;

    ProgressBar oneStarBar;
    ProgressBar twoStarBar;
    ProgressBar threeStarBar;
    ProgressBar fourStarBar;
    ProgressBar fiveStarBar;

    TextView oneStarText;
    TextView twoStarText;
    TextView threeStarText;
    TextView fourStarText;
    TextView fiveStarText;
    TextView numReviews;

    TextView averageRating;

    TextView productDescription;

    RatingBar averageRatingBar;


    View header;

    ListView reviewList;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;

    TextView amazonPrice;
    TextView walmartPrice;
    TextView bestbuyPrice;
    TextView ebayPrice;

    ImageView amazonLogo;
    ImageView walmartLogo;
    ImageView bestbuyLogo;
    ImageView ebayLogo;

    ImageButton amazonBuyButton;
    ImageButton walmartBuyButton;
    ImageButton bestbuyButton;
    ImageButton ebayBuyButton;

    ImageButton addBookmark;
    ImageButton removeBookmark;

    TextView priceDiff;

    Intent bookmarkResult;
    boolean bookmarkRemoved;

    WeakReference<ViewProductActivity> viewProductActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_listview);

        context = getApplicationContext();

        viewProductActivity = new WeakReference<>(this);

        header = getLayoutInflater().inflate(R.layout.view_product, null);

        reviewList = findViewById(R.id.reviewList);

        reviewProgress = new ProgressBar(context);

        bookmarkResult = new Intent();

        reviewList.addHeaderView(header);

        loadMoreReviews = new Button(context);

        loadMoreReviews.setText(getResources().getString(R.string.load_more_reviews));
        loadMoreReviews.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        loadMoreReviews.setTextColor(Color.WHITE);

        /*
         * Listening to Load More button click event
         */
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

        if (callingActivity.equals(getResources().getString(R.string.main_activity_class)) || callingActivity.equals(getResources().getString(R.string.bookmark_activity_class)))
        {
            product = (Product) intent.getSerializableExtra(getResources().getString(R.string.product_data));
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context            = getApplicationContext();
        mainProgressBar    = findViewById(R.id.progressBarProduct);
        productImage = findViewById(R.id.imageViewProduct);
        productTitle       = findViewById(R.id.productTitle);

        loadReviews = findViewById(R.id.loadReviews);

        oneStarBar = findViewById(R.id.oneStarBar);
        twoStarBar = findViewById(R.id.twoStarBar);
        threeStarBar = findViewById(R.id.threeStarBar);
        fourStarBar = findViewById(R.id.fourStarBar);
        fiveStarBar = findViewById(R.id.fiveStarBar);

        oneStarText = findViewById(R.id.oneStarText);
        twoStarText = findViewById(R.id.twoStarText);
        threeStarText = findViewById(R.id.threeStarText);
        fourStarText = findViewById(R.id.fourStarText);
        fiveStarText = findViewById(R.id.fiveStarText);

        averageRating = findViewById(R.id.averageRating);
        averageRatingBar = findViewById(R.id.averageRatingBar);
        numReviews = findViewById(R.id.numReviews);

        productDescription = findViewById(R.id.productDescription);
        amazonPrice = findViewById(R.id.amazonPrice);
        walmartPrice = findViewById(R.id.walmartPrice);
        bestbuyPrice = findViewById(R.id.bestbuyPrice);
        ebayPrice = findViewById(R.id.ebayPrice);

        amazonLogo = findViewById(R.id.amazonLogo);
        walmartLogo = findViewById(R.id.walmartLogo);
        bestbuyLogo = findViewById(R.id.bestbuyLogo);
        ebayLogo = findViewById(R.id.ebayLogo);

        amazonBuyButton = findViewById(R.id.amazonBuyButton);
        walmartBuyButton = findViewById(R.id.walmartBuyButton);
        bestbuyButton = findViewById(R.id.bestbuyButton);
        ebayBuyButton = findViewById(R.id.ebayBuyButton);

        addBookmark = findViewById(R.id.productAddBookmark);
        removeBookmark = findViewById(R.id.productBookmarkAdded);

        priceDiff = findViewById(R.id.productPriceDiff);

        oneStarBar.setVisibility(View.GONE);
        twoStarBar.setVisibility(View.GONE);
        threeStarBar.setVisibility(View.GONE);
        fourStarBar.setVisibility(View.GONE);
        fiveStarBar.setVisibility(View.GONE);

        oneStarText.setVisibility(View.GONE);
        twoStarText.setVisibility(View.GONE);
        threeStarText.setVisibility(View.GONE);
        fourStarText.setVisibility(View.GONE);
        fiveStarText.setVisibility(View.GONE);

        averageRating.setVisibility(View.GONE);
        averageRatingBar.setVisibility(View.GONE);
        numReviews.setVisibility(View.GONE);

        productDescription.setVisibility(View.GONE);

        amazonPrice.setVisibility(View.GONE);
        walmartPrice.setVisibility(View.GONE);
        bestbuyPrice.setVisibility(View.GONE);
        ebayPrice.setVisibility(View.GONE);

        amazonLogo.setVisibility(View.GONE);
        walmartLogo.setVisibility(View.GONE);
        bestbuyLogo.setVisibility(View.GONE);
        ebayLogo.setVisibility(View.GONE);

        amazonBuyButton.setVisibility(View.GONE);
        walmartBuyButton.setVisibility(View.GONE);
        bestbuyButton.setVisibility(View.GONE);
        ebayBuyButton.setVisibility(View.GONE);

        addBookmark.setVisibility(View.GONE);
        removeBookmark.setVisibility(View.GONE);

        priceDiff.setVisibility(View.GONE);


        reviewListAdapter = new ReviewListAdapter(context);

        reviewList.setAdapter(reviewListAdapter);

        productTitle.setVisibility(View.GONE);
        productImage.setVisibility(View.GONE);
        loadReviews.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);

        getLoaderManager().initLoader(7, null, productLoaderListener).forceLoad();
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
            if (callingActivity.equals(getResources().getString(R.string.main_activity_class)) || callingActivity.equals(getResources().getString(R.string.bookmark_activity_class)))
            {
                productLoader = new ProductLoader(context, product, viewProductActivity);
            }
            else if (callingActivity.equals(getResources().getString(R.string.barcode_capture_activity_class)))
            {
                String upc = intent.getStringExtra("Barcode");

                productLoader = new ProductLoader(context, upc, viewProductActivity);
            }

            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return productLoader;
        }

        @Override public void onLoadFinished(Loader<Product> productLoader, Product loadedProduct)
        {
            product = loadedProduct;

            mainProgressBar.setVisibility(View.GONE);

            if (product.getLargeImage() == null)
            {
                productImage.setImageResource(R.drawable.no_image);
            }
            else
            {
                productImage.setImageBitmap(product.getLargeImage());
            }

            productImage.setVisibility(View.VISIBLE);

            productTitle.setText(product.getAmazonProductInfo().getTitle());
            productTitle.setVisibility(View.VISIBLE);

            if (product.getReviewStats().getTotalStars() > 0)
            {
                loadReviews.setVisibility(View.VISIBLE);
            }

            loadReviews.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    getLoaderManager().restartLoader(5, null, reviewLoaderListener);
                    reviewList.removeFooterView(loadReviews);
                }
            });

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

            DecimalFormat decimalFormatRating = new DecimalFormat("0.0");
            DecimalFormat decimalFormatPrice = new DecimalFormat("$###,##0.00");
            DecimalFormat numReviewsFormat = new DecimalFormat("###,##0");
            String formattedRating = decimalFormatRating.format(product.getReviewStats().getAverageRating());

            averageRating.setText(formattedRating);
            averageRatingBar.setRating(product.getReviewStats().getAverageRating().floatValue());

            numReviews.setText(numReviewsFormat.format(product.getReviewStats().getTotalStars()) + " Reviews");

            if (product.getDescription() != null && !product.getDescription().equals(""))
            {
                productDescription.setText(product.getDescription());
                productDescription.setVisibility(View.VISIBLE);
            }

            constraintLayout = findViewById(R.id.viewProductLayout);

            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            final SharedPreferences sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);

            TextView amazonPrice = findViewById(R.id.amazonPrice);
            TextView walmartPrice = findViewById(R.id.walmartPrice);
            TextView bestbuyPrice = findViewById(R.id.bestbuyPrice);
            TextView ebayPrice = findViewById(R.id.ebayPrice);

            ImageView amazonLogo = findViewById(R.id.amazonLogo);
            ImageView walmartLogo = findViewById(R.id.walmartLogo);
            ImageView bestbuyLogo = findViewById(R.id.bestbuyLogo);
            ImageView ebayLogo = findViewById(R.id.ebayLogo);

            if (product.getAmazonProductInfo().hasInfo())
            {
                amazonPrice.setText(decimalFormatPrice.format(product.getAmazonProductInfo().getPrice()));

                constraintSet.connect(amazonBuyButton.getId(), ConstraintSet.TOP, productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.applyTo(constraintLayout);
            }

            if (product.getWalmartProductInfo().hasInfo())
            {
                walmartPrice.setText(decimalFormatPrice.format(product.getWalmartProductInfo().getPrice()));

                if (product.getAmazonProductInfo().hasInfo())
                {
                    constraintSet.connect(walmartBuyButton.getId(), ConstraintSet.TOP, amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(2));
                    constraintSet.applyTo(constraintLayout);
                }
                else
                {
                    constraintSet.connect(walmartBuyButton.getId(), ConstraintSet.TOP, productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                    constraintSet.applyTo(constraintLayout);
                }
            }

            if (product.getBestbuyProductInfo().hasInfo())
            {
                bestbuyPrice.setText(decimalFormatPrice.format(product.getBestbuyProductInfo().getPrice()));

                if (product.getWalmartProductInfo().hasInfo())
                {
                    constraintSet.connect(bestbuyButton.getId(), ConstraintSet.TOP, walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(2));
                    constraintSet.applyTo(constraintLayout);
                }
                else if (product.getAmazonProductInfo().hasInfo())
                {
                    constraintSet.connect(bestbuyButton.getId(), ConstraintSet.TOP, amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(2));
                    constraintSet.applyTo(constraintLayout);
                }
                else
                {
                    constraintSet.connect(bestbuyButton.getId(), ConstraintSet.TOP, productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                    constraintSet.applyTo(constraintLayout);
                }
            }

            if (product.getEbayProductInfo().hasInfo())
            {
                ebayPrice.setText(decimalFormatPrice.format(product.getEbayProductInfo().getPrice()));

                if (product.getBestbuyProductInfo().hasInfo())
                {
                    constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP, bestbuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(2));
                    constraintSet.applyTo(constraintLayout);
                }
                else if (product.getWalmartProductInfo().hasInfo())
                {
                    constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP, walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(2));
                    constraintSet.applyTo(constraintLayout);
                }
                else if (product.getAmazonProductInfo().hasInfo())
                {
                    constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP, amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(2));
                    constraintSet.applyTo(constraintLayout);
                }
                else
                {
                    constraintSet.connect(ebayBuyButton.getId(), ConstraintSet.TOP, productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                    constraintSet.applyTo(constraintLayout);
                }
            }

            if (product.getEbayProductInfo().hasInfo())
            {
                constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP, ebayBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP, ebayBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.applyTo(constraintLayout);
            }
            else if (product.getBestbuyProductInfo().hasInfo())
            {
                constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP, bestbuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP, bestbuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.applyTo(constraintLayout);
            }
            else if (product.getWalmartProductInfo().hasInfo())
            {
                constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP, walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP, walmartBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.applyTo(constraintLayout);
            }
            else if (product.getAmazonProductInfo().hasInfo())
            {
                constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP, amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP, amazonBuyButton.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.applyTo(constraintLayout);
            }
            else
            {
                constraintSet.connect(removeBookmark.getId(), ConstraintSet.TOP, productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.connect(addBookmark.getId(), ConstraintSet.TOP, productImage.getId(), ConstraintSet.BOTTOM, convertDPtoPX(8));
                constraintSet.applyTo(constraintLayout);
            }

            final Gson gson = new Gson();
            final SharedPreferences.Editor editor = sharedPref.edit();

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

                DecimalFormat decimalFormat = new DecimalFormat("$#,##0.00");

                if (priceDiffVal.compareTo(BigDecimal.ZERO) == 0)
                {
                    priceDiff.setText("(±" + decimalFormat.format(priceDiffVal) + ")");
                    priceDiff.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                else if (priceDiffVal.compareTo(BigDecimal.ZERO) < 0)
                {
                    priceDiff.setText("(" + decimalFormat.format(priceDiffVal) + ")");
                    priceDiff.setTextColor(getResources().getColor(R.color.colorBookmarkAdded));
                }
                else
                {
                    priceDiff.setText("(+" + decimalFormat.format(priceDiffVal) + ")");
                    priceDiff.setTextColor(getResources().getColor(R.color.colorRed));
                }

                priceDiff.setVisibility(View.VISIBLE);
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

                    bookmarkRemoved = false;

                    bookmarkResult.removeExtra(getResources().getString(R.string.view_bookmark_result));

                    bookmarkResult.putExtra(getResources().getString(R.string.view_bookmark_result), bookmarkRemoved);

                    setResult(RESULT_OK, bookmarkResult);

                    addBookmark.setVisibility(View.INVISIBLE);
                    removeBookmark.setVisibility(View.VISIBLE);
                    priceDiff.setVisibility(View.VISIBLE);

                    Snackbar snackbar = Snackbar.make(view , "Bookmark Added",
                            Snackbar.LENGTH_LONG);

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
                    priceDiff.setVisibility(View.GONE);

                    bookmarkRemoved = true;

                    bookmarkResult.removeExtra(getResources().getString(R.string.view_bookmark_result));

                    bookmarkResult.putExtra(getResources().getString(R.string.view_bookmark_result), bookmarkRemoved);

                    setResult(RESULT_OK, bookmarkResult);

                    Snackbar snackbar = Snackbar.make(view , "Bookmark Removed",
                            Snackbar.LENGTH_LONG);

                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    snackbar.show();
                }
            });



            if (product.getAmazonProductInfo().hasInfo())
            {
                if (product.getAmazonProductInfo().getPrice().compareTo(product.getLowestPrice()) == 0)
                {
                    ViewCompat.setBackgroundTintList(amazonBuyButton, ContextCompat.getColorStateList(context, R.color.colorBookmarkAdded));
                    amazonPrice.setTextColor(getResources().getColor(R.color.colorBookmarkAdded));
                }

                amazonBuyButton.setVisibility(View.VISIBLE);
                amazonLogo.setVisibility(View.VISIBLE);
                amazonPrice.setVisibility(View.VISIBLE);

                amazonBuyButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(product.getAmazonProductInfo().getProductURL()));
                        context.startActivity(viewProductWebpage);
                    }
                });
            }

            if (product.getWalmartProductInfo().hasInfo())
            {
                if (product.getWalmartProductInfo().getPrice().compareTo(product.getLowestPrice()) == 0)
                {
                    ViewCompat.setBackgroundTintList(walmartBuyButton, ContextCompat.getColorStateList(context, R.color.colorBookmarkAdded));
                    walmartPrice.setTextColor(getResources().getColor(R.color.colorBookmarkAdded));
                }

                walmartBuyButton.setVisibility(View.VISIBLE);
                walmartLogo.setVisibility(View.VISIBLE);
                walmartPrice.setVisibility(View.VISIBLE);

                walmartBuyButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(product.getWalmartProductInfo().getProductURL()));
                        context.startActivity(viewProductWebpage);
                    }
                });
            }

            if (product.getBestbuyProductInfo().hasInfo())
            {
                if (product.getBestbuyProductInfo().getPrice().compareTo(product.getLowestPrice()) == 0)
                {
                    ViewCompat.setBackgroundTintList(bestbuyButton, ContextCompat.getColorStateList(context, R.color.colorBookmarkAdded));
                    bestbuyPrice.setTextColor(getResources().getColor(R.color.colorBookmarkAdded));
                }

                bestbuyButton.setVisibility(View.VISIBLE);
                bestbuyLogo.setVisibility(View.VISIBLE);
                bestbuyPrice.setVisibility(View.VISIBLE);

                bestbuyButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(product.getBestbuyProductInfo().getProductURL()));
                        context.startActivity(viewProductWebpage);
                    }
                });
            }

            if (product.getEbayProductInfo().hasInfo())
            {
                if (product.getEbayProductInfo().getPrice().compareTo(product.getLowestPrice()) == 0)
                {
                    ViewCompat.setBackgroundTintList(ebayBuyButton, ContextCompat.getColorStateList(context, R.color.colorBookmarkAdded));
                    ebayPrice.setTextColor(getResources().getColor(R.color.colorBookmarkAdded));
                }

                ebayBuyButton.setVisibility(View.VISIBLE);
                ebayLogo.setVisibility(View.VISIBLE);
                ebayPrice.setVisibility(View.VISIBLE);

                ebayBuyButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(product.getEbayProductInfo().getProductURL()));
                        context.startActivity(viewProductWebpage);
                    }
                });
            }


            averageRating.setVisibility(View.VISIBLE);

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

            averageRatingBar.setVisibility(View.VISIBLE);
            numReviews.setVisibility(View.VISIBLE);
        }

        @Override public void onLoaderReset(Loader<Product> productLoader)
        {
            productLoader.forceLoad();
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
            oneStarBar.setPadding(0,0,0,convertDPtoPX(16));

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

            reviewList.setHeaderDividersEnabled(true);
            reviewList.setVisibility(View.VISIBLE);
        }

        @Override public void onLoaderReset(Loader<ArrayList<Review>> reviewListLoader)
        {
            reviewListAdapter.setData(null);
        }
    };

    private int convertDPtoPX(int dp)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
