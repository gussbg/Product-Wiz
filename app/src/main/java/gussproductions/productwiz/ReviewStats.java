package gussproductions.productwiz;

import java.io.Serializable;

/**
 * This ReviewStats class encapsulates all the attributes regarding reviews such as
 * the average rating, the number of stars per star ranking, and the total number of stars.
 *
 * @author Brendon Guss
 * @since  01/4/2018
 */
class ReviewStats implements Serializable
{
    private Integer[] numStars;
    private Integer   totalStars;
    private Integer   maxStarCount;
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
        this.maxStarCount  = calcMaxStarCount();
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
        Double averageRating;

        averageRating = (numStars[0] * 1.0 + numStars[1] * 2.0 + numStars[2] * 3.0 + numStars[3] * 4.0 + numStars[4] * 5.0)
                / totalStars;

        if (averageRating.equals(Double.NaN))
        {
            averageRating = 0.0;
        }

        return averageRating;
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

    /**
     * Adds review statistics to the current instance. This is used for
     * the overall product review statistics added from individual retailers.
     *
     * @param reviewStats Review statistics to be added.
     */
    void add(ReviewStats reviewStats)
    {
        if (reviewStats != null)
        {
            numStars[0] += reviewStats.getNumOneStars();
            numStars[1] += reviewStats.getNumTwoStars();
            numStars[2] += reviewStats.getNumThreeStars();
            numStars[3] += reviewStats.getNumFourStars();
            numStars[4] += reviewStats.getNumFiveStars();
        }

        this.totalStars    = calcTotalStars();
        this.averageRating = calcAverageRating();
        this.maxStarCount  = calcMaxStarCount();
    }

    /**
     * Calculates which star rating (1-5) has the highest count.
     *
     * @return The high star rating count.
     */
    private Integer calcMaxStarCount()
    {
        Integer maxStarCount = 0;

        for (int i = 0; i < 5; i++)
        {
            if (maxStarCount < numStars[i])
            {
                maxStarCount = numStars[i];
            }
        }

        return maxStarCount;
    }

    // Getter methods.
    Integer getTotalStars()    { return totalStars;    }
    Integer getNumOneStars()   { return numStars[0];   }
    Integer getNumTwoStars()   { return numStars[1];   }
    Integer getNumThreeStars() { return numStars[2];   }
    Integer getNumFourStars()  { return numStars[3];   }
    Integer getNumFiveStars()  { return numStars[4];   }
    Integer getMaxStarCount()  { return maxStarCount;  }
    Double  getAverageRating() { return averageRating; }
}
