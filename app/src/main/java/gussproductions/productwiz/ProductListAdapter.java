/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * The ProductListAdapter class is an ArrayAdapter of Products that the ListView in the
 * MainActivity utilizes as it's data and view source.
 *
 * @author Brendon Guss
 * @since  02/13/2018
 */
class ProductListAdapter extends ArrayAdapter<Product>
{
    private Context context;

    /**
     * Sets the context and the list item layout.
     *
     * @param context The application context
     */
    ProductListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        this.context = context;
    }

    /**
     * Adds and sets the adapter data with bookmarked products.
     *
     * @param products The products to add and set.
     */
    public void setData(ArrayList<Product> products)
    {
        if (products != null)
        {
            addAll(products);
        }
    }

    /**
     * Gives the view for each product within the Main Activity ListView and
     * the sets of views contained within it and the processing necessary to do so.
     *
     * @param position The position in the adapter array of products.
     * @param convertView The view that was last returned from this method.
     * @param parent The parent ViewGroup.
     * @return The updated view for a particular product within the ListView.
     */
    @NonNull @Override public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        final View productView;

        // Needed for adding and removing bookmarks.
        final SharedPreferences sharedPref;
        final SharedPreferences.Editor editor;

        final Product product;

        // Utility Variables
        final LayoutInflater layoutInflater;
        final DecimalFormat priceFormat;
        final Gson gson;

        // Views
        final TextView productTitle;
        final TextView productPrice;
        final TextView priceDifference;

        final ImageView productImage;
        final ImageView lowestPriceRetailerLogo;
        final ImageView imageBuffer; // Used to add space underneath the buy and add/remove bookmark buttons.

        final ImageButton btnBuy;
        final ImageButton btnAddBookmark;
        final ImageButton btnRemoveBookmark;

        BigDecimal priceDiffVal;

        sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);
        editor     = sharedPref.edit();

        product = getItem(position);

        gson = new Gson();

        layoutInflater = LayoutInflater.from(context);

        // If the view has not been displayed then it is inflated, otherwise the previous view is used (convertView).
        // The other conditions fix display issues when scrolling.
        if (convertView == null || convertView.findViewById(R.id.priceDifference).getVisibility() == View.VISIBLE
                || position == 0 || position == 1)
        {
            productView = layoutInflater.inflate(R.layout.row_product, parent, false);
        }
        else
        {
            productView = convertView;
        }

        priceFormat = new DecimalFormat("$#,##0.00");

        productTitle    = productView.findViewById(R.id.productListTitle);
        productPrice    = productView.findViewById(R.id.productListPrice);
        priceDifference = productView.findViewById(R.id.priceDifference);

        productImage            = productView.findViewById(R.id.productListImage);
        lowestPriceRetailerLogo = productView.findViewById(R.id.lowestPriceRetailerImage);
        imageBuffer             = productView.findViewById(R.id.buttonBuffer);

        btnBuy            = productView.findViewById(R.id.listBuyButton);
        btnAddBookmark    = productView.findViewById(R.id.addBookmark);
        btnRemoveBookmark = productView.findViewById(R.id.bookmarkAdded);

        productTitle.setText(product.getAmazonProductInfo().getTitle());
        productPrice.setText(priceFormat.format(product.getLowestPrice()));

        if (product.getSmallImage() == null)
        {
            productImage.setImageResource(R.drawable.no_image);
        }
        else
        {
            productImage.setImageBitmap(product.getSmallImage().bitmap);
        }

        setRetailerLogo(product.getLowestPriceRetailer(), lowestPriceRetailerLogo);

        // Determines if the product is bookmarked and displays the needed views if it is.
        if (!sharedPref.contains(product.getUPC()))
        {
            btnRemoveBookmark.setVisibility(View.GONE);
            btnAddBookmark.setVisibility(View.VISIBLE);
            priceDifference.setVisibility(View.GONE);
        }
        else
        {
            String            json              = sharedPref.getString(product.getUPC(), "");
            BookmarkedProduct bookmarkedProduct = gson.fromJson(json, BookmarkedProduct.class);

            bookmarkedProduct.setProduct(product);

            priceDiffVal = product.getLowestPrice().subtract(bookmarkedProduct.getPriceAdded());
            priceDiffVal = priceDiffVal.setScale(2, RoundingMode.DOWN);

            setPriceDifference(context, priceDiffVal, priceDifference, priceFormat);

            btnAddBookmark.setVisibility(View.GONE);
            btnRemoveBookmark.setVisibility(View.VISIBLE);
        }

        imageBuffer.setVisibility(View.VISIBLE);

        // Launches Internet browser to view product webpage that has the lowest price when this button is tapped.
        btnBuy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW,
                                                        Uri.parse(product.getLowestPriceProductURL()));
                viewProductWebpage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Needed for older Android APIs.
                context.startActivity(viewProductWebpage);
            }
        });

        // Adds a bookmark and displays a snackbar message stating this.
        btnAddBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BookmarkedProduct bookmarkedProduct = new BookmarkedProduct(product.getUPC(), new Date(),
                                                                            product.getLowestPrice());
                String            json              = gson.toJson(bookmarkedProduct);

                editor.putString(product.getUPC(), json);
                editor.apply();

                btnAddBookmark.setVisibility(View.GONE);
                btnRemoveBookmark.setVisibility(View.VISIBLE);

                // Refreshes the product ListView.
                notifyDataSetChanged();

                Snackbar snackbar = Snackbar.make(view , R.string.bookmark_added,
                            Snackbar.LENGTH_SHORT);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });

        // Removes the bookmark and displays a snackbar message stating this.
        btnRemoveBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor.remove(product.getUPC());
                editor.apply();

                btnAddBookmark.setVisibility(View.VISIBLE);
                btnRemoveBookmark.setVisibility(View.GONE);

                // Refreshes the product ListView.
                notifyDataSetChanged();

                Snackbar snackbar = Snackbar.make(view , R.string.bookmark_removed,
                        Snackbar.LENGTH_SHORT);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });

        return productView;
    }

    /**
     * Sets the price difference view given its value and price format.
     *
     * @param context The Application context.
     * @param priceDiffVal The value of the price difference since being bookmarked.
     * @param priceDiffView The view that displays the price difference.
     * @param priceFormat The price format.
     */
    static void setPriceDifference(Context context, BigDecimal priceDiffVal, TextView priceDiffView,
                                   DecimalFormat priceFormat)
    {
        if (priceDiffVal.compareTo(BigDecimal.ZERO) == 0)
        {
            priceDiffView.setText(context.getResources().getString(R.string.no_price_difference,
                    priceFormat.format(priceDiffVal)));
            priceDiffView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        else if (priceDiffVal.compareTo(BigDecimal.ZERO) < 0)
        {
            priceDiffView.setText(context.getResources().getString(R.string.negative_price_difference,
                    priceFormat.format(priceDiffVal)));
            priceDiffView.setTextColor(context.getResources().getColor(R.color.colorBookmarkAdded));
        }
        else
        {
            priceDiffView.setText(context.getResources().getString(R.string.positive_price_difference,
                    priceFormat.format(priceDiffVal)));
            priceDiffView.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        priceDiffView.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the retailer logo given the retailer and a retailer logo ImageView.
     *
     * @param retailer The retailer.
     * @param retailerImageView The retailer logo ImageView.
     */
    static void setRetailerLogo(Retailer retailer, ImageView retailerImageView)
    {
        if (retailer.equals(Retailer.AMAZON))
        {
            retailerImageView.setImageResource(R.drawable.amazon_logo);
        }
        else if (retailer.equals(Retailer.EBAY))
        {
            retailerImageView.setImageResource(R.drawable.ebay_logo);
        }
        else if (retailer.equals(Retailer.BEST_BUY))
        {
            retailerImageView.setImageResource(R.drawable.bestbuy_logo);
        }
        else if (retailer.equals(Retailer.WALMART))
        {
            retailerImageView.setImageResource(R.drawable.walmart_logo);
        }
    }
}
