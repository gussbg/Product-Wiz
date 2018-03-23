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
 * Created by Brendon on 3/14/2018.
 */

public class BookmarkListAdapter extends ArrayAdapter<BookmarkedProduct>
{
    private Context context;

    BookmarkListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        this.context = context;
    }

    public void setData(ArrayList<BookmarkedProduct> bookmarkedProducts)
    {
        if (bookmarkedProducts != null)
        {
            addAll(bookmarkedProducts);
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View view;
        final SharedPreferences sharedPref;
        final SharedPreferences.Editor editor;

        sharedPref = context.getSharedPreferences("gussproductions.productwiz", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        final LayoutInflater mInflater = LayoutInflater.from(context);

        if (convertView == null)
        {
            view = mInflater.inflate(R.layout.row_product, parent, false);
        }
        else
        {
            view = convertView;
        }

        final BookmarkedProduct bookmarkedProduct = getItem(position);
        final Product product = bookmarkedProduct.getProduct();

        DecimalFormat decimalFormat = new DecimalFormat("$#,##0.00");
        String formattedPrice = decimalFormat.format(product.getLowestPrice());

        TextView productTitle = view.findViewById(R.id.productListTitle);
        TextView productPrice = view.findViewById(R.id.productListPrice);

        productTitle.setText(product.getAmazonProductInfo().getTitle());
        productPrice.setText(formattedPrice);

        ImageView productImage = view.findViewById(R.id.productListImage);
        ImageView lowestPriceRetailerLogo = view.findViewById(R.id.lowestPriceRetailerImage);

        ImageButton buyButton = view.findViewById(R.id.listBuyButton);

        TextView priceDifference = view.findViewById(R.id.priceDifference);

        buyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(product.getLowestPriceProductURL()));
                context.startActivity(viewProductWebpage);
            }
        });

        final ImageButton removeBookmark = view.findViewById(R.id.bookmarkAdded);
        final ImageButton addBookmark = view.findViewById(R.id.addBookmark);

        addBookmark.setVisibility(View.GONE);

        removeBookmark.setVisibility(View.VISIBLE);

        final ImageView imageBuffer = view.findViewById(R.id.buttonBuffer);
        imageBuffer.setVisibility(View.VISIBLE);

        BigDecimal priceDiffVal = product.getLowestPrice().subtract(bookmarkedProduct.getPriceAdded());

        priceDiffVal = priceDiffVal.setScale(2, RoundingMode.DOWN);

        if (priceDiffVal.compareTo(BigDecimal.ZERO) == 0)
        {
            priceDifference.setText("(±" + decimalFormat.format(priceDiffVal) + ")");
            priceDifference.setTextColor(view.getResources().getColor(R.color.colorAccent));
        }
        else if (priceDiffVal.compareTo(BigDecimal.ZERO) < 0)
        {
            priceDifference.setText("(" + decimalFormat.format(priceDiffVal) + ")");
            priceDifference.setTextColor(view.getResources().getColor(R.color.colorBookmarkAdded));
        }
        else
        {
            priceDifference.setText("(+" + decimalFormat.format(priceDiffVal) + ")");
            priceDifference.setTextColor(view.getResources().getColor(R.color.colorRed));
        }

        priceDifference.setVisibility(View.VISIBLE);
        final ListView bookmarkList = parent.findViewById(R.id.bookmarkList);

        ViewGroup viewBookmarks = (ViewGroup) parent.getParent();

        final TextView noBookmarksMessage = viewBookmarks.findViewById(R.id.noBookmarks);

        removeBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor.remove(product.getUPC());
                editor.apply();

                removeBookmark.setVisibility(View.GONE);


                remove(bookmarkedProduct);

                if (bookmarkList.getCount() == 0)
                {
                    bookmarkList.setVisibility(View.GONE);
                    noBookmarksMessage.setVisibility(View.VISIBLE);
                }

                notifyDataSetChanged();






                Snackbar snackbar = Snackbar.make(view , "Bookmark Removed",
                        Snackbar.LENGTH_LONG);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });

        if (product.getLowestPriceRetailer().equals(Retailer.AMAZON))
        {
            lowestPriceRetailerLogo.setImageResource(R.drawable.amazon_logo);
        }
        else if (product.getLowestPriceRetailer().equals(Retailer.EBAY))
        {
            lowestPriceRetailerLogo.setImageResource(R.drawable.ebay_logo);
        }
        else if (product.getLowestPriceRetailer().equals(Retailer.BEST_BUY))
        {
            lowestPriceRetailerLogo.setImageResource(R.drawable.bestbuy_logo);
        }
        else if (product.getLowestPriceRetailer().equals(Retailer.WALMART))
        {
            lowestPriceRetailerLogo.setImageResource(R.drawable.walmart_logo);
        }

        if (product.getSmallImage() == null)
        {
            productImage.setImageResource(R.drawable.no_image);
        }
        else
        {
            productImage.setImageBitmap(product.getSmallImage().bitmap);
        }

        return view;
    }
}
