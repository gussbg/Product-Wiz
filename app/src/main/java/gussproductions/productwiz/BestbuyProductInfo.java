/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Jsoup is used for parsing Product and review information from the BestBuy Open API
 * responses.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * The BestbuyProductInfo class encapsulates BestBuy product information, reviews, and review
 * statistics. The product information is set within the constructor.
 *
 * @author Brendon Guss
 * @since  01/09/2018
 */
class BestbuyProductInfo extends ProductInfo
{
    private String  baseReviewURL;
    private boolean hasReviews;

    BestbuyProductInfo(String upc)
    {
        BestbuyRequestHelper bestbuyRequestHelper = new BestbuyRequestHelper(upc);
        String               requestURL           = bestbuyRequestHelper.getRequestURL();

        curReviewPageNum = 0;

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0")
                                              .ignoreHttpErrors(true).ignoreContentType(true).get();
            int      numResults        = Integer.parseInt(productResultPage.getElementsByTag("products")
                                                                           .attr("total"));

            // Ensures that only one product is parsed, it is not expected to have more than one result however.
            if (numResults == 1)
            {
                Element unparsedProduct       = productResultPage.getElementsByTag("product")
                                                                 .first();
                String  unparsedNumReviews    = unparsedProduct.getElementsByTag("customerReviewCount").text();
                String  unparsedAverageRating = unparsedProduct.getElementsByTag("customerReviewAverage").text();

                Integer numReviews;
                Double  averageStarRating;

                price             = new BigDecimal(unparsedProduct.getElementsByTag("salePrice")
                                                                  .text().replaceAll(",", ""));
                itemID            = unparsedProduct.getElementsByTag("sku").text();
                productURL        = unparsedProduct.getElementsByTag("url").text();
                title             = unparsedProduct.getElementsByTag("name").text();
                imageURL          = unparsedProduct.getElementsByTag("image").text();
                description       = unparsedProduct.getElementsByTag("longDescription").text();
                hasReviews        = !unparsedNumReviews.equals("") && !unparsedAverageRating.equals("");

                if (hasReviews)
                {
                    numReviews        = Integer.parseInt(unparsedNumReviews);
                    averageStarRating = Double.parseDouble(unparsedAverageRating);
                    reviewStats       = new ReviewStats(numReviews, averageStarRating);
                    reviews           = new ArrayList<>();
                }

                hasInfo = true;
            }
            else
            {
                hasInfo    = false;
                hasReviews = false;
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * Parses the next review page. This method has no effect if there are no reviews or product information.
     */
    void parseNextReviewPage()
    {
        if (hasInfo && hasReviews)
        {
            setReviewPage();

            DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            Pattern numHelpfulPattern   = Pattern.compile("Helpful \\((.*?)\\)"); // Regex for helpful votes.
            Pattern numUnhelpfulPattern = Pattern.compile("Unhelpful \\((.*?)\\)"); // Regex for unhelpful votes.

            Matcher numHelpfulMatcher;
            Matcher numUnhelpfulMatcher;

            try
            {
                Document reviewPage      = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0")
                                                .ignoreHttpErrors(true).ignoreContentType(true).get();
                Elements unparsedReviews = reviewPage.getElementsByClass("review-item-feedback");

                for (Element unparsedReview : unparsedReviews)
                {
                    Date       reviewDate  = new Date();
                    String     reviewTitle = unparsedReview.getElementsByClass("col-md-9 col-sm-9 col-xs-12 title")
                                                          .text();
                    String     reviewText  = unparsedReview.getElementsByClass("pre-white-space").text();
                    StarRating starRating  = StarRating.valueOf(Integer.parseInt(
                                                 unparsedReview.getElementsByClass("c-review-average").text()));

                    Integer numHelpful   = DEFAULT_HELPFUL_NUM;
                    Integer numUnhelpful = DEFAULT_UNHELPFUL_NUM;

                    numHelpfulMatcher   = numHelpfulPattern.matcher(
                                              unparsedReview
                                              .getElementsByClass("pos-feedback no-margin-l false").text());
                    numUnhelpfulMatcher = numUnhelpfulPattern.matcher(
                                              unparsedReview
                                              .getElementsByClass("pos-feedback no-margin-l false").text());

                    if (numHelpfulMatcher.find())
                    {
                        numHelpful = Integer.parseInt(numHelpfulMatcher.group(1).replace(",", ""));
                    }

                    if (numUnhelpfulMatcher.find())
                    {
                        numUnhelpful = Integer.parseInt(numUnhelpfulMatcher.group(1).replace(",", ""));
                    }

                    try
                    {
                        reviewDate = dateFormat.parse(unparsedReview.getElementsByClass("review-date").text());
                    }
                    catch (ParseException pe)
                    {
                        pe.printStackTrace();
                    }

                    // Each parsed review datum is added to a new review and added to the product's
                    // reviews
                    reviews.add(new Review(reviewTitle, reviewText, starRating, reviewDate, numHelpful, numUnhelpful));
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Sets the current review page URL to the next review page.
     */
    private void setReviewPage()
    {
        if (hasInfo && hasReviews)
        {
            curReviewPageNum++;

            if (curReviewPageNum == 1)
            {
                try
                {
                    Document productPage       = Jsoup.connect(productURL).userAgent("Mozilla/5.0")
                                                .ignoreHttpErrors(true).ignoreContentType(true).get();
                    Element  unparsedReviewURL = productPage
                                                     .getElementsByClass("see-all-reviews-button-container")
                                                     .first().getElementsByClass("btn btn-default ")
                                                     .first();

                    baseReviewURL = unparsedReviewURL.attr("href");
                    curReviewURL  = baseReviewURL + "?page=1";
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
            else
            {
                curReviewURL = baseReviewURL + "?page=" + curReviewPageNum;
            }
        }
    }
}
