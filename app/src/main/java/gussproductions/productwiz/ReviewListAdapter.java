/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * The ReviewListAdapter class is an ArrayAdapter of Reviews for a product.
 * The ViewProductActivity utilizes it as it's source for review data and views.
 *
 * @author Brendon Guss
 * @since  02/22/2018
 */
class ReviewListAdapter extends ArrayAdapter<Review>
{
    private Context context;

    /**
     * Sets the context and the list item layout.
     *
     * @param context The application context
     */
    ReviewListAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_2);

        this.context = context;
    }

    /**
     * Adds and sets the review adapter data
     *
     * @param reviews The reviews to add to the adapter.
     */
    public void setData(ArrayList<Review> reviews)
    {
        if (reviews != null)
        {
            addAll(reviews);
        }
    }

    /**
     * Gives the view for each review for within the ViewProduct review ListView and
     * the sets of views contained within it and the processing necessary to do so.
     *
     * @param position The position in the adapter array of reviews for a product.
     * @param convertView The view that was last returned from this method.
     * @param parent The parent ViewGroup.
     * @return The updated view for a particular review within the ListView.
     */
    @NonNull @Override public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        final View   reviewView;
        final Review review;

        // Utility Variables
        final LayoutInflater layoutInflater;
        final DecimalFormat  numHelpfulFormat;
        final DateFormat     dateFormat;

        // Views
        final TextView reviewTitle;
        final TextView reviewDate;
        final TextView reviewText;
        final TextView thumbUpText;
        final TextView thumbDownText;

        final RatingBar ratingBar;
        final ImageView reviewRetailer;

        layoutInflater = LayoutInflater.from(context);

        // If the view has not been displayed then it is inflated, otherwise the previous view is used (convertView).
        if (convertView == null)
        {
            reviewView = layoutInflater.inflate(R.layout.row_review, parent, false);
        }
        else
        {
            reviewView = convertView;
        }

        numHelpfulFormat = new DecimalFormat("###,##0");
        dateFormat       = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

        review = getItem(position);

        reviewTitle   = reviewView.findViewById(R.id.reviewTitle);
        reviewDate    = reviewView.findViewById(R.id.reviewDate);
        reviewText    = reviewView.findViewById(R.id.reviewText);
        thumbUpText   = reviewView.findViewById(R.id.thumbUpText);
        thumbDownText = reviewView.findViewById(R.id.thumbDownText);

        ratingBar      = reviewView.findViewById(R.id.reviewRating);
        reviewRetailer = reviewView.findViewById(R.id.reviewRetailer);

        if (review.getReviewTitle() == null || review.getReviewTitle().equals(""))
        {
            reviewTitle.setVisibility(View.INVISIBLE);
        }
        else
        {
            reviewTitle.setVisibility(View.VISIBLE);
            reviewTitle.setText(review.getReviewText());
        }

        reviewTitle.setText(review.getReviewTitle());

        if (review.getDate() == null)
        {
            reviewDate.setVisibility(View.GONE);
        }
        else
        {
            reviewDate.setVisibility(View.VISIBLE);
            reviewDate.setText(dateFormat.format(review.getDate()));
        }

        if (review.getReviewText() == null || review.getReviewText().equals(""))
        {
            reviewText.setVisibility(View.GONE);
        }
        else
        {
            reviewText.setVisibility(View.VISIBLE);
            reviewText.setText(review.getReviewText());
        }

        thumbUpText.setText(numHelpfulFormat.format(review.getNumHelpful()));
        thumbDownText.setText(numHelpfulFormat.format(review.getNumUnhelpful()));

        ratingBar.setIsIndicator(true);
        ratingBar.setRating(review.getStarRating().getValue());

        ProductListAdapter.setRetailerLogo(review.getRetailer(), reviewRetailer);

        return reviewView;
    }
}
