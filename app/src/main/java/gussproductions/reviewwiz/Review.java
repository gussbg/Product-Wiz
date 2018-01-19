package gussproductions.reviewwiz;
import java.util.Date;

/**
 * Created by Brendon on 1/11/2018.
 */

public class Review
{
    String reviewTitle;
    String reviewText;
    StarRating starRating;
    Date date;
    Integer numHelpful;
    Integer numUnhelpful;

    public Review(String reviewTitle, String reviewText, StarRating starRating, Date date, Integer numHelpful, Integer numUnhelpful)
    {
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.starRating = starRating;
        this.date = date;
        this.numHelpful = numHelpful;
        this.numUnhelpful = numUnhelpful;
    }
}
