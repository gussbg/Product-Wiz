package gussproductions.reviewwiz;
import java.util.Date;

/**
 * Created by Brendon on 1/11/2018.
 */

public class Review
{
    private String reviewTitle;
    private String reviewText;
    private StarRating starRating;
    private Date date;
    private Integer numHelpful;
    private Integer numUnhelpful;

    Review(String reviewTitle, String reviewText, StarRating starRating, Date date, Integer numHelpful, Integer numUnhelpful)
    {
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.starRating = starRating;
        this.date = date;
        this.numHelpful = numHelpful;
        this.numUnhelpful = numUnhelpful;
    }

    public String getReviewTitle()
    {
        return reviewTitle;
    }

    public String getReviewText()
    {
        return reviewText;
    }

    public StarRating getStarRating()
    {
        return starRating;
    }

    public Date getDate()
    {
        return date;
    }

    public Integer getNumHelpful()
    {
        return numHelpful;
    }

    public Integer getNumUnhelpful()
    {
        return numUnhelpful;
    }
}
