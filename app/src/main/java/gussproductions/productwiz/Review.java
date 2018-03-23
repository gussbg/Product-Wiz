package gussproductions.productwiz;
import java.util.Date;

/**
 * This Review class encapsulates what datums are a part of a Review.
 * Some ProductInfo objects will leave some parameters as null or a default
 * value if they aren't present in the unparsed review, which is uncommon.
 *
 * @author Brendon Guss
 * @since  01/11/2018
 */
class Review
{
    private String     reviewTitle;
    private String     reviewText;
    private StarRating starRating;
    private Date       date;
    private Integer    numHelpful;
    private Integer    numUnhelpful;
    private Retailer   retailer;

    Review(String reviewTitle, String reviewText, StarRating starRating, Date date, Integer numHelpful
                             , Integer numUnhelpful, Retailer retailer)
    {
        this.reviewTitle  = reviewTitle;
        this.reviewText   = reviewText;
        this.starRating   = starRating;
        this.date         = date;
        this.numHelpful   = numHelpful;
        this.numUnhelpful = numUnhelpful;
        this.retailer     = retailer;
    }

    // Getter methods.
    String     getReviewTitle()  { return reviewTitle;  }
    String     getReviewText()   { return reviewText;   }
    StarRating getStarRating()   { return starRating;   }
    Date       getDate()         { return date;         }
    Integer    getNumHelpful()   { return numHelpful;   }
    Integer    getNumUnhelpful() { return numUnhelpful; }
    Retailer   getRetailer()     { return retailer;    }
}
