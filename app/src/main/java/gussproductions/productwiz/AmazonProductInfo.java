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
 * Jsoup is used for parsing Product and review information from the Amazon Product Advertising API
 * responses.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * The AmazonProductInfo class encapsulates Amazon product information, reviews, and review
 * statistics. The product information is set within the constructor. Reviews and review statistics
 * are set in separate methods because of the extra time they each consume.
 *
 * @author Brendon Guss
 * @since  01/03/2018
 */
class AmazonProductInfo extends ProductInfo
{
    final static int REVIEW_PAGE_MAX_SIZE = 10;

    /**
     * Sets the Amazon product information given a UPC (if the product is listed on Amazon).
     */
    AmazonProductInfo(String upc)
    {
        curReviewPageNum = 1;

        try
        {
            AmazonRequestHelper amazonRequestHelper = new AmazonRequestHelper(upc, AmazonRequestMode.ITEM_LOOKUP_INFO);
            String              requestURL          = amazonRequestHelper.getRequestURL();
            Document            productResultPage   = Jsoup.connect(requestURL).userAgent("Mozilla")
                                                           .ignoreHttpErrors(true).ignoreContentType(true).get();

            // Only the first Item element is set in case multiple results are returned, although this is not expected.
            Element             unparsedProduct     = productResultPage.getElementsByTag("Item").first();

            // If an item is found on Amazon, then the info is parsed and set.
            if (unparsedProduct != null)
            {
                setProductInfo(unparsedProduct);
            }
            else
            {
                hasInfo = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Sets the Amazon product information given an unparsed product element. This constructor is used by the
     * AmazonProductSearch as it generates a list of multiple items.
     */
    AmazonProductInfo(Element unparsedProduct)
    {
        curReviewPageNum = 1;

        setProductInfo(unparsedProduct);
    }

    /**
     * Sets the Amazon product information given an unparsed Jsoup Element.
     *
     * @param unparsedProduct The unparsed Amazon product information.
     */
    private void setProductInfo(Element unparsedProduct)
    {
        String unparsedPrice = unparsedProduct.getElementsByTag("LowestNewPrice").select("FormattedPrice")
                                              .text().substring(1).replaceAll(",", "");

        itemID      = unparsedProduct.getElementsByTag("ASIN").text();
        price       = new BigDecimal(unparsedPrice);
        title       = unparsedProduct.getElementsByTag("Title").text();
        description = unparsedProduct.getElementsByTag("EditorialReview").select("Content").text();
        productURL  = unparsedProduct.getElementsByTag("DetailPageURL").text();

        // Not all Amazon products have images, so the lack of an image is handled here.
        if (unparsedProduct.getElementsByTag("LargeImage").first() != null)
        {
            imageURL = unparsedProduct.getElementsByTag("LargeImage").first().select("URL").text();
        }
        else
        {
            imageURL = null;
        }

        hasInfo = true;
    }

    /**
     * Amazon review statistics are set using this method. It is in a separate method because of the extra time it
     * takes to parse this information.
     */
    void setReviewStats()
    {
        String    reviewIframe;
        String    reviewURL;
        Document  reviewIFrame;
        Element   unparsedReviewStats;
        int       totalNumReviews;
        Integer[] numStars;

        reviewIframe    = getReviewIFrame(itemID);
        totalNumReviews = 0;
        numStars        = new Integer[5];

        try
        {
            reviewIFrame        = Jsoup.connect(reviewIframe).userAgent("Mozilla").ignoreHttpErrors(true)
                                       .ignoreContentType(true).get();
            unparsedReviewStats = reviewIFrame.getElementsByClass("crIFrameHeaderHistogram").first();

            // No review statistics are set if none can be found.
            if (unparsedReviewStats == null)
            {
                reviewStats  = null;
                curReviewURL = null;
            }
            else
            {
                reviewURL = reviewIFrame.getElementsByClass("asinReviewsSummary")
                                        .select("a").attr("abs:href");

                // Regex Pattern for the total number of Reviews.
                Pattern numReviewsPattern       = Pattern.compile("(.*?) Review(|s)");

                // Regex Pattern for the percentage of a particular star rating (1 - 5).
                Pattern ratingPercentagePattern = Pattern.compile(" star (.*?)%");

                Matcher numReviewsMatcher      = numReviewsPattern.matcher(unparsedReviewStats.text());
                Matcher ratingPercentageMatcher = ratingPercentagePattern.matcher(unparsedReviewStats.text());

                if (numReviewsMatcher.find())
                {
                    totalNumReviews = Integer.parseInt(numReviewsMatcher.group(1).replace(",", ""));
                }

                for (int i = 0; i < numStars.length && ratingPercentageMatcher.find(); i++)
                {
                    // Calculate the number of stars given the
                    numStars[i] = (int) Math.round((Double.parseDouble(ratingPercentageMatcher.group(1)) / 100.0)
                                                    * totalNumReviews);
                }

                curReviewURL = reviewURL;
                reviewStats  = new ReviewStats(numStars);
            }
        }

        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * A helper method for getReviewStats that returns a product's review IFrame URL.
     *
     * @param asin The ASIN of the product.
     * @return The review URL.
     */
    private String getReviewIFrame(String asin)
    {
        String   reviewIFrame = null;
        String   responseURL;
        Document reviewResultsPage;
        AmazonRequestHelper requestHelper;

        try
        {
            requestHelper     = new AmazonRequestHelper(asin, AmazonRequestMode.ITEM_LOOKUP_REVIEWS);
            responseURL       = requestHelper.getRequestURL();
            reviewResultsPage = Jsoup.connect(responseURL).userAgent("Mozilla").ignoreHttpErrors(true)
                                     .ignoreContentType(true).get();
            reviewIFrame      = reviewResultsPage.getElementsByTag("IFrameURL").text();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return reviewIFrame;
    }

    /**
     * Parses all of the reviews on the next review page of an Amazon product.
     */
    ArrayList<Review> getMoreReviews()
    {
        ArrayList<Review> reviews = new ArrayList<>();

        // If there are no reviews to parse, this method does nothing and no reviews are set.
        if (curReviewURL != null && hasInfo)
        {

            try
            {
                Document   reviewPage       = Jsoup.connect(curReviewURL).ignoreHttpErrors(true)
                                                .ignoreContentType(true).get();
                Elements   unparsedPageNums = reviewPage.getElementsByClass("page-button");
                Elements   unparsedReviews  = reviewPage.getElementsByClass("a-section celwidget");
                DateFormat dateFormat       = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

                for (Element unparsedReview : unparsedReviews)
                {
                    Element unparsedNumHelpful = unparsedReview.getElementsByClass("review-votes").first();

                    Integer    numHelpful  = DEFAULT_HELPFUL_NUM;
                    Date       reviewDate  = new Date();
                    String     reviewTitle = unparsedReview
                            .getElementsByClass("a-size-base a-link-normal review-title a-color-base a-text-bold")
                            .first().text();
                    StarRating starRating  = StarRating.valueOf(Integer.parseInt(unparsedReview
                            .getElementsByClass("a-row").first()
                            .getElementsByClass("a-link-normal").first()
                            .attr("title").substring(0, 1)));
                    String     reviewText  = unparsedReview.getElementsByClass("a-size-base review-text")
                                                           .first().text().replaceAll("<.*?>", "");

                    try
                    {
                        reviewDate = dateFormat.parse(unparsedReview
                                     .getElementsByClass("a-size-base a-color-secondary review-date")
                                     .first().text().substring(3));
                    }
                    catch (ParseException pe)
                    {
                        pe.printStackTrace();
                    }

                    if (unparsedNumHelpful == null)
                    {
                        numHelpful = DEFAULT_HELPFUL_NUM; // Handles the case of no helpful reviews.
                    }
                    else if (unparsedNumHelpful.text().substring(0, 3).equals("One"))
                    {
                        numHelpful = 1; // Handles the case of only one helpful review.
                    }
                    else
                    {
                        // Regex pattern for the number of helpful reviews.
                        Pattern numHelpfulPattern = Pattern.compile("(.*?) people found this helpful");

                        Matcher numHelpfulMatcher = numHelpfulPattern.matcher(unparsedNumHelpful.text());

                        if (numHelpfulMatcher.find())
                        {
                            numHelpful = Integer.parseInt(numHelpfulMatcher.group(1).replace(",", ""));
                        }
                    }

                    // Each parsed review datum is added to a new review and added to the product's reviews, the
                    // number of unhelpful reviews is always zero since Amazon reviews do not have this metric.
                    reviews.add(new Review(reviewTitle, reviewText, starRating, reviewDate, numHelpful,
                                           DEFAULT_UNHELPFUL_NUM));
                }

                String prevReviewPage = curReviewURL;

                // Set the current review URL to the next review URL for use in the next method call.
                for (Element unparsedPageNum : unparsedPageNums)
                {
                    if (Integer.parseInt(unparsedPageNum.text().replace(",", "")) == curReviewPageNum + 1)
                    {
                        curReviewURL = unparsedPageNum.select("a").attr("abs:href");
                    }
                }

                // The current review URL is set to null if there is no more pages of reviews to parse.
                if (curReviewURL.equals(prevReviewPage))
                {
                    curReviewURL = null;
                }

                curReviewPageNum++;
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

        return reviews;
    }
}
