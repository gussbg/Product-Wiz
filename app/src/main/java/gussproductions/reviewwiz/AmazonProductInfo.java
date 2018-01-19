package gussproductions.reviewwiz;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * Created by Brendon on 1/8/2018.
 */

public class AmazonProductInfo
{
    private String asin;
    private String productURL;
    private BigDecimal price;

    private ArrayList<Review> reviews;
    private String curReviewPage;
    private int curPageNum;
    private final int NUM_UNHELPFUL = 0;

    public AmazonProductInfo(String asin, String productURL, BigDecimal price)
    {
        this.asin = asin;
        this.productURL = productURL;
        this.price = price;
        this.reviews = new ArrayList<>();

        curPageNum = 1;
    }

    public String getASIN()
    {
        return asin;
    }

    public String getProductURL()
    {
        return productURL;
    }

    public BigDecimal getPrice()
    {
        return price;
    }


    public ReviewStats updateReviewStats(AmazonProductSearch amazonProductSearch)
    {
        ReviewStats reviewStats;

        reviewStats = amazonProductSearch.getReviewStats(asin);
        curReviewPage = reviewStats.getAmazonReviewURL();
        parseReviewPage();

        return reviewStats;
    }

    public void parseReviewPage()
    {
        try
        {
            Document reviewPage = Jsoup.connect(curReviewPage).ignoreHttpErrors(true).ignoreContentType(true).get();

            Elements unparsedReviewTexts = reviewPage.getElementsByClass("a-size-base review-text");
            Elements unparsedPageNums    = reviewPage.getElementsByClass("page-button");
            Elements unparsedReviews     = reviewPage.getElementsByClass("a-section celwidget");

            DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            ArrayList<String>     reviewTexts  = new ArrayList<>();
            ArrayList<String>     reviewTitles = new ArrayList<>();
            ArrayList<Date>       reviewDates  = new ArrayList<>();
            ArrayList<Integer>    helpfulVotes = new ArrayList<>();
            ArrayList<StarRating> starRatings  = new ArrayList<>();

            for (Element unparsedReviewText : unparsedReviewTexts)
            {
                reviewTexts.add(unparsedReviewText.text().replaceAll("\\<.*?\\>", ""));
            }

            for (Element unparsedReview : unparsedReviews)
            {
                Element unparsedStarRating  = unparsedReview.getElementsByClass("a-row").first().getElementsByClass("a-link-normal").first();
                Element unparsedReviewDate  = unparsedReview.getElementsByClass("a-size-base a-color-secondary review-date").first();
                Element unparsedNumHelpful  = unparsedReview.getElementsByClass("review-votes").first();
                Element unparsedReviewTitle = unparsedReview.getElementsByClass("a-size-base a-link-normal review-title a-color-base a-text-bold").first();

                reviewTitles.add(unparsedReviewTitle.text());

                starRatings.add(StarRating.valueOf(Integer.parseInt(unparsedStarRating.attr("title").substring(0,1))));

                if (unparsedNumHelpful.text().substring(0,3).equals("One"))
                {
                    helpfulVotes.add(1);
                }
                else
                {
                    Pattern numHelpfulPattern = Pattern.compile("(.*?) people found this helpful");
                    Matcher numHelpfulMatcher = numHelpfulPattern.matcher(unparsedNumHelpful.text());

                    if (numHelpfulMatcher.find())
                    {
                        helpfulVotes.add(Integer.parseInt(numHelpfulMatcher.group(1).replace(",", "")));
                    }
                    else
                    {
                        helpfulVotes.add(0);
                    }
                }

                try
                {
                    reviewDates.add(dateFormat.parse(unparsedReviewDate.text().substring(3)));
                }
                catch (ParseException pe)
                {
                    pe.printStackTrace();
                }
            }

            String prevReviewPage = curReviewPage;

            for (Element unparsedPageNum : unparsedPageNums)
            {
                if (Integer.parseInt(unparsedPageNum.text().replace(",", "")) == curPageNum + 1)
                {
                    curReviewPage = unparsedPageNum.select("a").attr("abs:href");
                }
            }

            if (curReviewPage.equals(prevReviewPage))
            {
                curReviewPage = null;
            }

            for (int i = 0; i < reviewDates.size(); i++)
            {
                reviews.add(new Review(reviewTitles.get(i), reviewTexts.get(i), starRatings.get(i), reviewDates.get(i), helpfulVotes.get(i), NUM_UNHELPFUL));
            }

            curPageNum++;
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
