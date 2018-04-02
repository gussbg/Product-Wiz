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
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * The BookmarkListAdapter class is an ArrayAdapter of BookmarkedProducts that the ListView in the
 * ViewBookmarksActivity utilizes as it's data and view source.
 *
 * @author Brendon Guss
 * @since  03/14/2018
 */
class BookmarkListAdapter extends ArrayAdapter<BookmarkedProduct>
{
    private Context context;

    /**
     * Sets the context and the list item layout.
     *
     * @param context The application context
     */
    BookmarkListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        this.context = context;
    }

    /**
     * Adds and sets the adapter data with bookmarked products.
     *
     * @param bookmarkedProducts The bookmarked products to add and set.
     */
    public void setData(ArrayList<BookmarkedProduct> bookmarkedProducts)
    {
        if (bookmarkedProducts != null)
        {
            addAll(bookmarkedProducts);
        }
    }

    /**
     * Gives the view for each bookmarked product within the ViewBookmarksActivity ListView and
     * the sets of views contained within it and the processing necessary to do so.
     *
     * @param position The position in the adapter array of bookmarked products.
     * @param convertView The view that was last returned from this method.
     * @param parent The parent ViewGroup.
     * @return The updated view for a particular bookmarked product within the ListView.
     */
    @NonNull @Override public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        final View bookmarkView;

        // Needed for removing bookmarks.
        final SharedPreferences        sharedPref;
        final SharedPreferences.Editor editor;

        final BookmarkedProduct bookmarkedProduct;
        final Product           product;

        // Utility Variables
        final LayoutInflater layoutInflater;
        final DecimalFormat  priceFormat;

        // Views
        final ViewGroup viewBookmarks;

        final TextView productTitle;
        final TextView productPrice;
        final TextView noBookmarksMessage;
        final TextView priceDifference;

        final ImageView productImage;
        final ImageView lowestPriceRetailerLogo;
        final ImageView imageBuffer; // Used to add space underneath the buy and remove bookmark buttons.

        final ImageButton btnBuy;
        final ImageButton btnRemoveBookmark;
        final ImageButton btnAddBookmark;

        final ListView bookmarkList;

        BigDecimal priceDiffVal;

        sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);
        editor     = sharedPref.edit();

        bookmarkedProduct = getItem(position);
        product           = bookmarkedProduct.getProduct();

        layoutInflater = LayoutInflater.from(context);

        // If the view has not been displayed then it is inflated, otherwise the previous view is used (convertView).
        if (convertView == null)
        {
            bookmarkView = layoutInflater.inflate(R.layout.row_product, parent, false);
        }
        else
        {
            bookmarkView = convertView;
        }

        priceFormat = new DecimalFormat("$#,##0.00");

        viewBookmarks = (ViewGroup) parent.getParent();

        productTitle       = bookmarkView.findViewById(R.id.productListTitle);
        productPrice       = bookmarkView.findViewById(R.id.productListPrice);
        noBookmarksMessage = viewBookmarks.findViewById(R.id.noBookmarks);
        priceDifference    = bookmarkView.findViewById(R.id.priceDifference);

        productImage            = bookmarkView.findViewById(R.id.productListImage);
        lowestPriceRetailerLogo = bookmarkView.findViewById(R.id.lowestPriceRetailerImage);
        imageBuffer             = bookmarkView.findViewById(R.id.buttonBuffer);

        btnBuy            = bookmarkView.findViewById(R.id.listBuyButton);
        btnRemoveBookmark = bookmarkView.findViewById(R.id.bookmarkAdded);
        btnAddBookmark    = bookmarkView.findViewById(R.id.addBookmark);

        bookmarkList = parent.findViewById(R.id.bookmarkList);

        productTitle.setText(product.getTitle());
        productPrice.setText(priceFormat.format(product.getLowestPrice()));

        priceDiffVal = product.getLowestPrice().subtract(bookmarkedProduct.getPriceAdded());
        priceDiffVal = priceDiffVal.setScale(2, RoundingMode.DOWN);

        ProductListAdapter.setPriceDifference(context, priceDiffVal, priceDifference, priceFormat);

        if (product.getSmallImage() == null)
        {
            productImage.setImageResource(R.drawable.no_image);
        }
        else
        {
            productImage.setImageBitmap(product.getSmallImage().bitmap);
        }

        ProductListAdapter.setRetailerLogo(product.getLowestPriceRetailer(), lowestPriceRetailerLogo);

        btnAddBookmark.setVisibility(View.GONE);
        btnRemoveBookmark.setVisibility(View.VISIBLE);
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

        // Removes the bookmark and displays a snackbar message stating this.
        btnRemoveBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor.remove(product.getUPC());
                editor.apply();

                btnRemoveBookmark.setVisibility(View.GONE);

                remove(bookmarkedProduct);

                // If this is the last bookmark and it is removed, then a message is displayed stating there are
                // no bookmarks.
                if (bookmarkList.getCount() == 0)
                {
                    bookmarkList.setVisibility(View.GONE);
                    noBookmarksMessage.setVisibility(View.VISIBLE);
                }

                // Refreshes the bookmark ListView.
                notifyDataSetChanged();

                Snackbar snackbar = Snackbar.make(view , R.string.bookmark_removed,
                        Snackbar.LENGTH_SHORT);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });

        return bookmarkView;
    }
}
