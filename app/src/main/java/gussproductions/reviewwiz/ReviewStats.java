package gussproductions.reviewwiz;

/**
 * Created by Brendon on 1/4/2018.
 */

public class ReviewStats
{
    private Integer[] numStars;
    private Integer totalStars = 0;
    private Double averageRating;
    private String amazonReviewURL;

    public ReviewStats(Integer numStars[], String amazonReviewURL)
    {
        this.numStars = numStars;
        this.totalStars = calcTotalStars();
        this.averageRating = calcAverageRating();
        this.amazonReviewURL = amazonReviewURL;
    }

    private Double calcAverageRating()
    {
        return (numStars[0] * 1.0 + numStars[1] * 2.0 + numStars[2] * 3.0 + numStars[3] * 4.0 + numStars[4] * 5.0);
    }

    private Integer calcTotalStars()
    {
        for (Integer integer : numStars)
        {
            totalStars += integer;
        }

        return totalStars;
    }

    public void addOneStars(Integer numStars)
    {
        this.numStars[0] += numStars;
    }

    public void addTwoStars(Integer numStars)
    {
        this.numStars[1] += numStars;
    }

    public void addThreeStars(Integer numStars)
    {
        this.numStars[2] += numStars;
    }

    public void addFourStars(Integer numStars)
    {
        this.numStars[3] += numStars;
    }

    public void addFiveStars(Integer numStars)
    {
        this.numStars[4] += numStars;
    }

    public Integer getTotalStars()
    {
        return totalStars;
    }

    public Integer getNumOneStars()
    {
        return numStars[0];
    }
    public Integer getNumTwoStars()
    {
        return numStars[1];
    }

    public Integer getNumThreeStars()
    {
        return numStars[2];
    }

    public Integer getNumFourStars()
    {
        return numStars[3];
    }

    public Integer getNumFiveStars()
    {
        return numStars[4];
    }

    public Double getAverageRating()
    {
        return averageRating;
    }

    public String getAmazonReviewURL()
    {
        return amazonReviewURL;
    }
}
