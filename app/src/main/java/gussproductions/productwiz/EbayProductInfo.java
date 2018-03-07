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

/*
 * Jsoup is used for parsing Product and review information from the BestBuy Open API
 * responses.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * The EbayProductInfo class encapsulates eBay product information, reviews, and review
 * statistics. The product information is set within the constructor. Reviews, review statistics,
 * and product description are set in separate methods because of the extra time they each consume.
 *
 * @author Brendon Guss
 * @since  01/27/2018
 */
class EbayProductInfo extends ProductInfo
{
    private String  baseReviewURL;
    private boolean hasReviews;

    EbayProductInfo(String upc)
    {
        EbayRequestHelper ebayRequestHelper = new EbayRequestHelper(upc, EbayRequestMode.FIND_ITEMS_BY_PRODUCT);
        String            requestURL        = ebayRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla")
                                              .ignoreHttpErrors(true).ignoreContentType(true).get();
            Element  unparsedProduct   = productResultPage.getElementsByTag("item").first();

            // Handles the case where no products are found.
            if (unparsedProduct != null)
            {
                itemID     = unparsedProduct.getElementsByTag("itemId").text();
                price      = new BigDecimal(unparsedProduct.getElementsByTag("currentPrice")
                                                           .text().replaceAll(",", ""));
                title      = unparsedProduct.getElementsByTag("title").text();
                productURL = unparsedProduct.getElementsByTag("viewItemURL").text();
                imageURL   = unparsedProduct.getElementsByTag("pictureURLSuperSize").text();
                hasInfo    = true;
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
     * Sets the product description. It is in a separate method because of the extra time it consumes.
     */
    void setDescription()
    {
        if (hasInfo)
        {
            EbayRequestHelper ebayRequestHelper = new EbayRequestHelper(itemID, EbayRequestMode.GET_ITEM);
            String            requestURL        = ebayRequestHelper.getRequestURL();

            try
            {
                Document itemInfoResultPage      = Jsoup.connect(requestURL).userAgent("Mozilla")
                                                        .ignoreHttpErrors(true).ignoreContentType(true).get();
                Element  unparsedItemDescription = itemInfoResultPage.getElementsByTag("Description").first();

                // Not all eBay products have descriptions so this case is handled here.
                if (unparsedItemDescription != null)
                {
                    description = unparsedItemDescription.text().replaceAll("\\<.*?\\>", "");
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * eBay review statistics are set using this method. It is in a separate method because of the extra time it
     * takes to parse this information.
     */
    void setReviewStats()
    {
        if (hasInfo)
        {
            try
            {
                Document reviewResultPage = Jsoup.connect(productURL).userAgent("Mozilla")
                                                 .ignoreHttpErrors(true).ignoreContentType(true).get();

                // Determines if there are review statistics to parse.
                if (reviewResultPage.getElementsByClass("ebay-review-list").first() != null)
                {
                    Elements unparsedRatingCounts = reviewResultPage.getElementsByClass("ebay-review-list")
                                                                    .first()
                                                                    .getElementsByClass("ebay-review-item-r");
                    Integer  numStars[]           = new Integer[5];

                    // Parses the rating counts for each rating.
                    for (int i = unparsedRatingCounts.size() - 1; i >= 0; i--)
                    {
                        numStars[i] = Integer.parseInt(unparsedRatingCounts.get(i).text());
                    }

                    reviewStats = new ReviewStats(numStars);
                    hasReviews = true;
                }
                else
                {
                    hasReviews = false;
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Parses all of the reviews on the next review page of an eBay product.
     */
    ArrayList<Review> getMoreReviews()
    {
        ArrayList<Review> reviews = new ArrayList<>();

        if (hasInfo && hasReviews)
        {
            System.out.println("ebay reviews!");

            setReviewPage();

            DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            try
            {
                Document reviewPage      = Jsoup.connect(curReviewURL).userAgent("Mozilla")
                                                .ignoreHttpErrors(true).ignoreContentType(true).get();
                Elements unparsedReviews = reviewPage.getElementsByClass("ebay-review-section");

                for (Element unparsedReview : unparsedReviews)
                {
                    Date       reviewDate   = new Date();
                    String     reviewTitle  = unparsedReview
                                                  .getElementsByClass("review-item-title rvw-nowrap-spaces")
                                                  .text();
                    String     reviewText   = unparsedReview
                                                  .getElementsByClass("review-item-content rvw-wrap-spaces")
                                                  .text();
                    StarRating starRating   = StarRating.valueOf(Integer.parseInt(unparsedReview
                                                  .getElementsByClass("ebay-star-rating")
                                                  .select("meta").attr("content")));
                    Integer    numHelpful   = Integer.parseInt(unparsedReview
                                                  .getElementsByClass("positive-h-c").text().trim()
                                                  .replaceAll(",", ""));
                    Integer    numUnhelpful = Integer.parseInt(unparsedReview
                                                  .getElementsByClass("negative-h-c").text().trim()
                                                  .replaceAll(",", ""));

                    try
                    {
                        reviewDate = dateFormat.parse(unparsedReview.getElementsByClass("review-item-date")
                                                                    .text());
                    }
                    catch (ParseException pe)
                    {
                        pe.printStackTrace();
                    }

                    // Each parsed review datum is added to a new review and added to the product's reviews.
                    reviews.add(new Review(reviewTitle, reviewText, starRating, reviewDate, numHelpful, numUnhelpful));
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

        return reviews;
    }

    /**
     * Sets the current review page to the next one.
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
                    Document productPage       = Jsoup.connect(productURL).userAgent("Mozilla")
                                                      .ignoreHttpErrors(true).ignoreContentType(true).get();
                    Element  unparsedReviewURL = productPage.getElementsByClass("sar-btn right").first();

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
                curReviewURL = baseReviewURL + "&pgn=" + curReviewPageNum;
            }
        }
    }
}
