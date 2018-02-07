/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * The WalmartProductInfo class encapsulates Walmart product information, reviews, and review
 * statistics. The product information is set within the constructor. The review statistics are
 * set in a separate method for readability.
 *
 * @author Brendon Guss
 * @since  01/08/2018
 */
class WalmartProductInfo extends ProductInfo
{
    WalmartProductInfo(String upc)
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(upc);
        String               requestURL           = walmartRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0")
                                              .ignoreHttpErrors(true).ignoreContentType(true).get();
            Element unparsedProduct    = productResultPage.select("item").first();

            if (unparsedProduct != null)
            {
                if (unparsedProduct.getElementsByTag("salePrice").hasText())
                {
                    itemID      = unparsedProduct.getElementsByTag("itemId").text();
                    price       = new BigDecimal(unparsedProduct.getElementsByTag("salePrice")
                                                     .text().replaceAll(",", ""));
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

    /**
     * Walmart review statistics are set using this method.
     */
    private void setReviewStats()
    {
        if (hasInfo)
        {
            WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);

            curReviewURL     = walmartRequestHelper.getRequestURL();
            curReviewPageNum = 1;

            Integer numStars[] = new Integer[5];

            try
            {
                Document reviewResultPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0")
                                                 .ignoreHttpErrors(true).ignoreContentType(true).get();

                // Handles the rare case where the API gives an error.
                if (reviewResultPage.getElementsByTag("title").text().equals("Error"))
                {
                    return;
                }

                Elements unparsedRatings = reviewResultPage.getElementsByTag("ratingCounts");

                // Review statistics are only generating if ratings can be found.
                if (unparsedRatings.size() != 0)
                {
                    for (int i = 0; i < unparsedRatings.size(); i++)
                    {
                        String unparsedRatingCount = unparsedRatings.get(i).getElementsByTag("count").text();

                        if (!unparsedRatingCount.equals(""))
                        {
                            numStars[i] = Integer.parseInt(unparsedRatings.get(i).text());
                        }
                        else
                        {
                            // No ratings could be found
                            numStars[i] = 0;
                        }
                    }

                    reviewStats = new ReviewStats(numStars);
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Parses the next review page. This method has no effect if there are no reviews or product information.
     */
    void parseNextReviewPage()
    {
        if (hasInfo)
        {
            try
            {
                Document reviewResultPage = Jsoup.connect(curReviewURL)
                                                 .userAgent("Mozilla/5.0").ignoreHttpErrors(true)
                                                 .ignoreContentType(true).get();

                // Handles the rare case where the API gives an error.
                if (reviewResultPage.getElementsByTag("title").text().equals("Error"))
                {
                    return;
                }

                Elements unparsedReviews = reviewResultPage.getElementsByTag("review");

                for (Element unparsedReview : unparsedReviews)
                {
                    String reviewTitle    = unparsedReview.getElementsByTag("title").text();
                    String reviewText     = unparsedReview.getElementsByTag("reviewText").text();
                    StarRating starRating = StarRating.valueOf(Integer.parseInt(unparsedReview
                                                                                    .getElementsByTag("rating")
                                                                                    .text()));
                    Integer numHelpful    = Integer.parseInt(unparsedReview.getElementsByTag("upVotes").text());
                    Integer numUnhelpful  = Integer.parseInt(unparsedReview.getElementsByTag("downVotes").text());

                    // Each parsed review datum is added to a new review and added to the product's
                    // reviews, the date is always null since Walmart reviews do not have dates.
                    reviews.add(new Review(reviewTitle, reviewText, starRating, null
                                                      , numHelpful, numUnhelpful));
                }

                curReviewPageNum++;

                // Set the current review URL to the next page.
                WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);
                curReviewURL                              = walmartRequestHelper.getRequestURL();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }
}
