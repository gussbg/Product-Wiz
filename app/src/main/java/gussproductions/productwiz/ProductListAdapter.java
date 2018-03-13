package gussproductions.productwiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Brendon on 2/13/2018.
 */

class ProductListAdapter extends ArrayAdapter<Product>
{
    private Context context;

    ProductListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        this.context = context;
    }

    public void setData(ArrayList<Product> products)
    {
        if (products != null)
        {
            addAll(products);
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
        final Gson gson = new Gson();



        final LayoutInflater mInflater = LayoutInflater.from(context);

        if (convertView == null)
        {
            view = mInflater.inflate(R.layout.row_product, parent, false);
        }
        else
        {
            view = convertView;
        }

        final Product product = getItem(position);



        DecimalFormat decimalFormat = new DecimalFormat("$#,##0.00");
        String formattedPrice = decimalFormat.format(product.getLowestPrice());

        TextView productTitle = view.findViewById(R.id.productListTitle);
        TextView productPrice = view.findViewById(R.id.productListPrice);
        //TextView lowestPriceRetailer = view.findViewById(R.id.productListLowestRetailer);


        productTitle.setText(product.getAmazonProductInfo().getTitle());
        productPrice.setText(formattedPrice);
        //lowestPriceRetailer.setText(product.getLowestPriceRetailer().toString());

        ImageView productImage = view.findViewById(R.id.productListImage);
        ImageView lowestPriceRetailerLogo = view.findViewById(R.id.lowestPriceRetailerImage);

        ImageButton buyButton = view.findViewById(R.id.listBuyButton);



        buyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent viewProductWebpage = new Intent (Intent.ACTION_VIEW, Uri.parse(product.getLowestPriceProductURL()));
                context.startActivity(viewProductWebpage);
            }
        });

        final ImageButton addBookmark = view.findViewById(R.id.addBookmark);
        final ImageButton bookmarkAddedBtn = view.findViewById(R.id.bookmarkAdded);

        final ImageView imageBuffer = view.findViewById(R.id.buttonBuffer);
        imageBuffer.setVisibility(View.VISIBLE);

        String value = sharedPref.getString(product.getUPC(), "-1");

        System.out.println(value);

        if (value.equals("-1"))
        {
            System.out.println("                    Product not bookmarked!");

            bookmarkAddedBtn.setVisibility(View.GONE);
            addBookmark.setVisibility(View.VISIBLE);

        }
        else
        {
            System.out.println("product bookmarked!!");

            addBookmark.setVisibility(View.GONE);
            bookmarkAddedBtn.setVisibility(View.VISIBLE);
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



                addBookmark.setVisibility(View.GONE);
                bookmarkAddedBtn.setVisibility(View.VISIBLE);

                Snackbar snackbar = Snackbar.make(view , "Bookmark Added",
                            Snackbar.LENGTH_LONG);

                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });

        bookmarkAddedBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor.remove(product.getUPC());
                editor.apply();

                addBookmark.setVisibility(View.VISIBLE);
                bookmarkAddedBtn.setVisibility(View.GONE);

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


        productImage.setImageBitmap(product.getImage());

        //TODO fix possible null pointer on no image!

        return view;
    }
}


