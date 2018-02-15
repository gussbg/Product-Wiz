package gussproductions.productwiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brendon on 2/13/2018.
 */

public class ProductListAdapter extends ArrayAdapter<Product>
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
    @Override public View getView(int position, View convertView, ViewGroup parent)
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

        Product product = getItem(position);

        TextView textView = view.findViewById(R.id.textView);
        textView.setText(product.getAmazonProductInfo().getTitle());

        return textView;
    }
}
