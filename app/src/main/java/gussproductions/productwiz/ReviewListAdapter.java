package gussproductions.productwiz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Brendon on 2/22/2018.
 */

public class ReviewListAdapter extends ArrayAdapter<Review>
{
    private Context context;

    ReviewListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        this.context = context;
    }

    public void setData(ArrayList<Review> reviews)
    {
        if (reviews != null)
        {
            addAll(reviews);
        }
    }

    @Override public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;

        final LayoutInflater mInflater = LayoutInflater.from(context);

        if (convertView == null) {
            view = mInflater.inflate(R.layout.row_review, parent, false);
        } else {
            view = convertView;
        }

        final Review review = getItem(position);

        TextView reviewTitle = view.findViewById(R.id.reviewTitle);

        RatingBar ratingBar = view.findViewById(R.id.reviewRating);

        TextView reviewDate = view.findViewById(R.id.reviewDate);

        TextView reviewText = view.findViewById(R.id.reviewText);

        ImageView reviewRetailer = view.findViewById(R.id.reviewRetailer);

        if (review.getRetailer() == Retailer.AMAZON)
        {
            reviewRetailer.setImageResource(R.drawable.amazon_logo);
        }
        else if (review.getRetailer() == Retailer.WALMART)
        {
            reviewRetailer.setImageResource(R.drawable.walmart_logo);
        }
        else if (review.getRetailer() == Retailer.BEST_BUY)
        {
            reviewRetailer.setImageResource(R.drawable.bestbuy_logo);
        }
        else if (review.getRetailer() == Retailer.EBAY)
        {
            reviewRetailer.setImageResource(R.drawable.ebay_logo);
        }

        TextView thumbUpText = view.findViewById(R.id.thumbUpText);
        TextView thumbDownText = view.findViewById(R.id.thumbDownText);

        ratingBar.setIsIndicator(true);

        ratingBar.setRating(review.getStarRating().getValue());

        reviewTitle.setText(review.getReviewTitle());

        reviewText.setText(review.getReviewText());

        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

        thumbUpText.setText(review.getNumHelpful().toString());
        thumbDownText.setText(review.getNumUnhelpful().toString());

        if (review.getDate() != null)
        {
            reviewDate.setText(dateFormat.format(review.getDate()));
        }


        return view;

    }
}
