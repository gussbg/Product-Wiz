/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    final static int REVIEW_PAGE_MAX_SIZE = 5;

    WalmartProductInfo(String upc)
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(upc);
        String               requestURL           = walmartRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla")
                                              .ignoreHttpErrors(true).ignoreContentType(true).get();
            Element unparsedProduct    = productResultPage.select("item").first();

            if (unparsedProduct != null)
            {
                if (unparsedProduct.getElementsByTag("salePrice").hasText())
                {
                    itemID      = unparsedProduct.getElementsByTag("itemId").text();
                    price       = new BigDecimal(unparsedProduct.getElementsByTag("salePrice")
                                                     .text().replaceAll(",", ""));
                    price       = price.setScale(2, RoundingMode.DOWN);
                    title       = unparsedProduct.getElementsByTag("name").text();
                    description = unparsedProduct.getElementsByTag("shortDescription").text().replaceAll("<.*?>", "");
                    productURL  = unparsedProduct.getElementsByTag("productUrl").text();

                    // Gets the highest quality image available.
                    if (unparsedProduct.getElementsByTag("largeImage").first() == null)
                    {
                        if (unparsedProduct.getElementsByTag("mediumImage").first() != null)
                        {
                            imageURL = unparsedProduct.getElementsByTag("mediumImage").first().text();
                        }
                        else
                        {
                            imageURL = unparsedProduct.getElementsByTag("thumbnailImage").first().text();
                        }
                    }

                    hasInfo     = true;
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
    void setReviewStats()
    {
        if (hasInfo)
        {
            curReviewPageNum = 1;
            WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(itemID, curReviewPageNum);

            curReviewURL     = walmartRequestHelper.getRequestURL();

            Integer numStars[] = new Integer[5];

            for (int i = 0; i < 5; i++)
            {
                numStars[i] = 0;
            }

            try
            {
                Document reviewResultPage = Jsoup.connect(curReviewURL).userAgent("Mozilla")
                                                 .ignoreHttpErrors(true).ignoreContentType(true).get();

                // Handles the rare case where the API gives an error.
                if (reviewResultPage.getElementsByTag("title").text().equals("Error"))
                {
                    return;
                }

                Elements unparsedRatings = reviewResultPage.getElementsByTag("ratingCounts");

                // Review statistics are only generated if ratings can be found.
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
                else
                {
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
    ArrayList<Review> getMoreReviews()
    {
        ArrayList<Review> reviews = new ArrayList<>();

        if (hasInfo && curReviewURL != null && !curReviewURL.equals(""))
        {
            try
            {
                Document reviewResultPage = Jsoup.connect(curReviewURL)
                                                 .userAgent("Mozilla/5.0").ignoreHttpErrors(true)
                                                 .ignoreContentType(true).get();

                // Handles the rare case where the API gives an error.
                if (reviewResultPage.getElementsByTag("title").text().equals("Error"))
                {
                    return reviews;
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
                                                      , numHelpful, numUnhelpful, Retailer.WALMART));
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

        return reviews;
    }
}
