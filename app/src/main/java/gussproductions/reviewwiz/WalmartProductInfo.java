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

public class WalmartProductInfo extends ProductInfo
{
    WalmartProductInfo(String upc)
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(upc);

        String requestURL = walmartRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Element unparsedProduct = productResultPage.select("item").first();

            if (unparsedProduct != null)
            {
                if (unparsedProduct.getElementsByTag("salePrice").hasText())
                {
                    itemID      = unparsedProduct.getElementsByTag("itemId").text();
                    price       = new BigDecimal(unparsedProduct.getElementsByTag("salePrice").text().replaceAll(",", ""));
                    title       = unparsedProduct.getElementsByTag("name").text();
                    description = unparsedProduct.getElementsByTag("shortDescription").text();
                    productURL  = unparsedProduct.getElementsByTag("productUrl").text();
                    imageURL    = unparsedProduct.getElementsByTag("largeImage").text();
                    reviews     = new ArrayList<>();
                    hasInfo     = true;

                    setReviewStats();
                }
                else
                {
                    hasInfo = false;
                }


            }
            else
            {
                hasInfo = false;
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private void setReviewStats()
    {
        if (hasInfo)
        {
            curReviewPageNum = 1;
            WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);

            curReviewURL = walmartRequestHelper.getRequestURL();

            Integer numStars[] = new Integer[5];

            try {
                Document reviewResultPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();

                if (reviewResultPage.getElementsByTag("title").text().equals("Error"))
                {
                    return;
                }

                Elements unparsedRatings = reviewResultPage.getElementsByTag("ratingCounts");

                for (int i = 0; i < unparsedRatings.size(); i++) {
                    String unparsedRatingCount = unparsedRatings.get(i).getElementsByTag("count").text();

                    if (!unparsedRatingCount.equals("")) {
                        numStars[i] = Integer.parseInt(unparsedRatings.get(i).text());
                    } else {

                        numStars[i] = 0;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            reviewStats = new ReviewStats(numStars);
        }
    }

    public void parseReviewPage() {
        if (hasInfo)
        {
            try
            {
                Document reviewResultPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();

                if (reviewResultPage.getElementsByTag("title").text().equals("Error"))
                {
                    return;
                }

                Elements unparsedReviews = reviewResultPage.getElementsByTag("review");

                for (Element unparsedReview : unparsedReviews)
                {
                    String reviewTitle = unparsedReview.getElementsByTag("title").text();
                    String reviewText = unparsedReview.getElementsByTag("reviewText").text();
                    StarRating starRating = StarRating.valueOf(Integer.parseInt(unparsedReview.getElementsByTag("rating").text()));
                    Integer numHelpful = Integer.parseInt(unparsedReview.getElementsByTag("upVotes").text());
                    Integer numUnhelpful = Integer.parseInt(unparsedReview.getElementsByTag("downVotes").text());

                    reviews.add(new Review(reviewTitle, reviewText, starRating, null, numHelpful, numUnhelpful));
                }

                curReviewPageNum++;
                WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);
                curReviewURL = walmartRequestHelper.getRequestURL();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
