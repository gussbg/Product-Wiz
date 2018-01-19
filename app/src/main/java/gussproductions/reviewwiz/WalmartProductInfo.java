package gussproductions.reviewwiz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Brendon on 1/8/2018.
 */

public class WalmartProductInfo
{
    private BigDecimal price;
    private String itemID;
    private String productURL;
    private ReviewStats reviewStats;
    private ArrayList<Review> reviews;
    private String curReviewURL;
    private int curReviewPageNum;

    WalmartProductInfo(String upc)
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(upc);
        reviews = new ArrayList<>();

        String requestURL = walmartRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Element unparsedProduct = productResultPage.select("item").first();

            price = new BigDecimal(unparsedProduct.getElementsByTag("salePrice").text().replaceAll(",", ""));
            itemID = unparsedProduct.getElementsByTag("itemId").text();
            productURL = unparsedProduct.getElementsByTag("productUrl").text();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        setReviewStats();
        parseReviewPage();
    }

    public void setReviewStats()
    {
        curReviewPageNum = 1;
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);

        curReviewURL = walmartRequestHelper.getRequestURL();

        Integer numStars[] = new Integer[5];

        try
        {
            Document reviewResultPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Elements unparsedRatingCounts = reviewResultPage.getElementsByTag("count");

            for (int i = 0; i < unparsedRatingCounts.size(); i++)
            {
                numStars[i] = Integer.parseInt(unparsedRatingCounts.get(i).text());
            }

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        reviewStats = new ReviewStats(numStars, null);
    }

    public void parseReviewPage()
    {
        try
        {
            Document reviewResultPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Elements unparsedReviews  = reviewResultPage.getElementsByTag("review");

            for (Element unparsedReview : unparsedReviews)
            {
                String     reviewTitle  = unparsedReview.getElementsByTag("title").text();
                String     reviewText   = unparsedReview.getElementsByTag("reviewText").text();
                StarRating starRating   = StarRating.valueOf(Integer.parseInt(unparsedReview.getElementsByTag("rating").text()));
                Integer    numHelpful   = Integer.parseInt(unparsedReview.getElementsByTag("upVotes").text());
                Integer    numUnhelpful = Integer.parseInt(unparsedReview.getElementsByTag("downVotes").text());

                reviews.add(new Review(reviewTitle, reviewText, starRating, null, numHelpful, numUnhelpful));
            }

            curReviewPageNum++;
            WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);
            curReviewURL = walmartRequestHelper.getRequestURL();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public ReviewStats getReviewStats()
    {
        return reviewStats;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getItemID()
    {
        return itemID;
    }

    public String getProductURL()
    {
        return productURL;
    }
}
