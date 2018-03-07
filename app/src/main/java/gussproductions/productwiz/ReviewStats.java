package gussproductions.productwiz;

import java.io.Serializable;

/**
 * This ReviewStatics class encapsulates all the attributes regarding reviews such as
 * the average rating, the number of stars per star ranking, and the total number of stars.
 *
 * @author Brendon Guss
 * @since  01/4/2018
 */
class ReviewStats implements Serializable
{
    private Integer[] numStars;
    private Integer   totalStars;
    private Double    averageRating;

    /**
     * This constructor is the one most commonly used because it a numStars array which contains
     * all of the desired information.
     */
    ReviewStats(Integer numStars[])
    {
        this.numStars      = numStars;
        this.totalStars    = calcTotalStars();
        this.averageRating = calcAverageRating();
    }

    /**
     * This constructor is less desirable because it does not the counts of each star rating.
     */
    ReviewStats(Integer totalStars, Double averageRating)
    {
        this.totalStars    = totalStars;
        this.averageRating = averageRating;
    }

    /**
     * Calculates the average star rating of a product from product reviews.
     *
     * @return The average star rating.
     */
    private Double calcAverageRating()
    {
        return (numStars[0] * 1.0 + numStars[1] * 2.0 + numStars[2] * 3.0 + numStars[3] * 4.0 + numStars[4] * 5.0)
                / totalStars;
    }

    /**
     * Calculates the number of total starts.
     *
     * @return The total number of stars.
     */
    private Integer calcTotalStars()
    {
        totalStars = 0;

        for (Integer stars : numStars)
        {
            totalStars += stars;
        }

        return totalStars;
    }

    // Getter methods.
    public Integer getTotalStars()    { return totalStars;    }
    public Integer getNumOneStars()   { return numStars[0];   }
    public Integer getNumTwoStars()   { return numStars[1];   }
    public Integer getNumThreeStars() { return numStars[2];   }
    public Integer getNumFourStars()  { return numStars[3];   }
    public Integer getNumFiveStars()  { return numStars[4];   }
    public Double  getAverageRating() { return averageRating; }
}
