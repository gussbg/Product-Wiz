package gussproductions.reviewwiz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

/**
 * Created by Brendon on 1/8/2018.
 */

public class AmazonProductInfo extends ProductInfo
{
    AmazonProductInfo(String upc)
    {
        curReviewPageNum = 1;

        try
        {
            AmazonRequestHelper amazonRequestHelper = new AmazonRequestHelper(upc, AmazonRequestMode.ITEM_LOOKUP_INFO);

            String requestURL = amazonRequestHelper.getRequestURL();
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Element  unparsedProduct = productResultPage.getElementsByTag("Item").first();

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

    AmazonProductInfo(Element unparsedProduct)
    {
        curReviewPageNum = 1;

        setProductInfo(unparsedProduct);
    }

    private void setProductInfo(Element unparsedProduct)
    {
        String unparsedPrice   = unparsedProduct.getElementsByTag("LowestNewPrice").select("FormattedPrice").text().substring(1).replaceAll(",", "");

        itemID      = unparsedProduct.getElementsByTag("ASIN").text();
        price       = new BigDecimal(unparsedPrice);
        title       = unparsedProduct.getElementsByTag("Title").text();
        description = unparsedProduct.getElementsByTag("EditorialReview").select("Content").text();
        productURL  = unparsedProduct.getElementsByTag("DetailPageURL").text();

        if (unparsedProduct.getElementsByTag("LargeImage").first() != null)
        {
            imageURL = unparsedProduct.getElementsByTag("LargeImage").first().select("URL").text();
        }
        else
        {
            imageURL = null;
        }

        reviews     = new ArrayList<>();
        hasInfo     = true;
    }

    void setReviewStats()
    {
        String reviewIframe;
        String reviewURL = null;
        Document reviewIFrame;
        Element unparsedReviewStats = null;
        int totalNumReviews = 0;
        Integer[] numStars;

        reviewIframe = getReviewIFrame(itemID);
        numStars = new Integer[5];

        try
        {
            reviewIFrame = Jsoup.connect(reviewIframe).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            unparsedReviewStats = reviewIFrame.getElementsByClass("crIFrameHeaderHistogram").first();

            if (unparsedReviewStats == null)
            {
                reviewStats = null;
                curReviewURL = null;
            }
            else
            {
                reviewURL = reviewIFrame.getElementsByClass("asinReviewsSummary").select("a").attr("abs:href");

                Pattern numReviewsPattern = Pattern.compile("(.*?) Review(|s)");
                Pattern numReviewsStarsPattern = Pattern.compile(" star (.*?)%");

                Matcher numReviewsMatcher = numReviewsPattern.matcher(unparsedReviewStats.text());
                Matcher numReviewsStarsMatcher = numReviewsStarsPattern.matcher(unparsedReviewStats.text());

                if (numReviewsMatcher.find())
                {
                    totalNumReviews = Integer.parseInt(numReviewsMatcher.group(1).replace(",", ""));
                }

                for (int i = 0; i < numStars.length && numReviewsStarsMatcher.find(); i++)
                {
                    numStars[i] = (int) Math.round((Double.parseDouble(numReviewsStarsMatcher.group(1)) / 100.0) * totalNumReviews);
                }

                curReviewURL = reviewURL;

                reviewStats = new ReviewStats(numStars);
            }
        }

        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }




    }

    /**
     * A helper method for getReviewStats that returns a product's review URL.
     *
     * @param asin The ASIN of the product.
     * @return The review URL.
     */
    private String getReviewIFrame(String asin)
    {
        String reviewURL = null;
        String responseURL;

        Document reviewResultsPage;

        try
        {
            AmazonRequestHelper requestHelper = new AmazonRequestHelper(asin, AmazonRequestMode.ITEM_LOOKUP_REVIEWS);
            responseURL = requestHelper.getRequestURL();
            reviewResultsPage = Jsoup.connect(responseURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            reviewURL = reviewResultsPage.getElementsByTag("IFrameURL").text();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return reviewURL;
    }

    public void parseReviewPage()
    {
        if (curReviewURL == null)
        {
            reviews = null;
        }
        else
        {
            try
            {
                Document reviewPage = Jsoup.connect(curReviewURL).ignoreHttpErrors(true).ignoreContentType(true).get();

                Elements unparsedPageNums = reviewPage.getElementsByClass("page-button");
                Elements unparsedReviews = reviewPage.getElementsByClass("a-section celwidget");

                DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

                for (Element unparsedReview : unparsedReviews)
                {
                    Integer numHelpful = 0;
                    Date reviewDate = new Date();
                    String reviewTitle = unparsedReview.getElementsByClass("a-size-base a-link-normal review-title a-color-base a-text-bold").first().text();
                    StarRating starRating = StarRating.valueOf(Integer.parseInt(unparsedReview.getElementsByClass("a-row").first().getElementsByClass("a-link-normal").first().attr("title").substring(0, 1)));
                    String reviewText = unparsedReview.getElementsByClass("a-size-base review-text").first().text().replaceAll("\\<.*?\\>", "");

                    Element unparsedNumHelpful = unparsedReview.getElementsByClass("review-votes").first();

                    try
                    {
                        reviewDate = dateFormat.parse(unparsedReview.getElementsByClass("a-size-base a-color-secondary review-date").first().text().substring(3));
                    }
                    catch (ParseException pe)
                    {
                        pe.printStackTrace();
                    }

                    if (unparsedNumHelpful == null)
                    {
                        numHelpful = DEFAULT_HELPFUL_NUM;
                    }
                    else if (unparsedNumHelpful.text().substring(0, 3).equals("One"))
                    {
                        numHelpful = 1;
                    }
                    else
                    {
                        Pattern numHelpfulPattern = Pattern.compile("(.*?) people found this helpful");
                        Matcher numHelpfulMatcher = numHelpfulPattern.matcher(unparsedNumHelpful.text());

                        if (numHelpfulMatcher.find())
                        {
                            numHelpful = Integer.parseInt(numHelpfulMatcher.group(1).replace(",", ""));
                        }
                    }

                    reviews.add(new Review(reviewTitle, reviewText, starRating, reviewDate, numHelpful, DEFAULT_UNHELPFUL_NUM));
                }

                String prevReviewPage = curReviewURL;

                for (Element unparsedPageNum : unparsedPageNums)
                {
                    if (Integer.parseInt(unparsedPageNum.text().replace(",", "")) == curReviewPageNum + 1)
                    {
                        curReviewURL = unparsedPageNum.select("a").attr("abs:href");
                    }
                }

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
    }
}
