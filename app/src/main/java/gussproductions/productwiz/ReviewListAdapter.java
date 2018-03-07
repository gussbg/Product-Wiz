package gussproductions.productwiz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

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

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        ratingBar.setIsIndicator(true);

        ratingBar.setRating(review.getStarRating().getValue());

        reviewTitle.setText(review.getReviewTitle());

        return view;

    }
}
