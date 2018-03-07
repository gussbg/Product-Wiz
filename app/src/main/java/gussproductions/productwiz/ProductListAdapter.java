package gussproductions.productwiz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Brendon on 2/13/2018.
 */

class ProductListAdapter extends ArrayAdapter<Product>
{
    private Context context;
    private ArrayList<Boolean> animationStates;

    ProductListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        animationStates = new ArrayList<>();

        this.context    = context;
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


