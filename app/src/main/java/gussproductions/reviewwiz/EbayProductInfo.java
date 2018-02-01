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

/**
 * Created by Brendon on 1/27/2018.
 */

public class EbayProductInfo extends ProductInfo
{
    private String baseReviewURL;
    private boolean hasReviews;

    EbayProductInfo(String upc)
    {
        EbayRequestHelper ebayRequestHelper = new EbayRequestHelper(upc, EbayRequestMode.FIND_ITEMS_BY_PRODUCT);

        String requestURL = ebayRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();

            Element unparsedProduct = productResultPage.getElementsByTag("item").first();

            if (unparsedProduct != null)
            {
                itemID     = unparsedProduct.getElementsByTag("itemId").text();
                price      = new BigDecimal(unparsedProduct.getElementsByTag("currentPrice").text().replaceAll(",", ""));
                title      = unparsedProduct.getElementsByTag("title").text();
                productURL = unparsedProduct.getElementsByTag("viewItemURL").text();
                imageURL   = unparsedProduct.getElementsByTag("pictureURLSuperSize").text();
                reviews    = new ArrayList<>();
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

    @Override
    String getDescription()
    {
        if (hasInfo)
        {
            EbayRequestHelper ebayRequestHelper = new EbayRequestHelper(itemID, EbayRequestMode.GET_ITEM);

            String requestURL = ebayRequestHelper.getRequestURL();

            try {
                Document itemInfoResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();

                Element unparsedItemDescription = itemInfoResultPage.getElementsByTag("Description").first();

                if (unparsedItemDescription != null)
                {
                    description = unparsedItemDescription.text().replaceAll("\\<.*?\\>", "");
                    hasReviews  = true;
                }
                else
                {
                    hasReviews = false;
                }


            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            return description;
        }
        else
        {
            return null;
        }
    }

    void setReviewStats()
    {
        if (hasInfo && hasReviews)
        {
            try {
                Document reviewResultPage = Jsoup.connect(productURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                System.out.println(productURL);
                //Elements unparsedRatingCounts = reviewResultPage.getElementsByClass("ebay-review-list").first().getElementsByClass("ebay-review-item-r");

                if (reviewResultPage.getElementsByClass("ebay-review-list").first() != null)
                {
                    Elements unparsedRatingCounts = reviewResultPage.getElementsByClass("ebay-review-list").first().getElementsByClass("ebay-review-item-r");
                    Integer numStars[] = new Integer[5];

                    for (int i = unparsedRatingCounts.size() - 1; i >= 0; i--) {
                        numStars[i] = Integer.parseInt(unparsedRatingCounts.get(i).text());
                    }

                    reviewStats = new ReviewStats(numStars);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    void parseReviewPage()
    {
        if (hasInfo && hasReviews)
        {
            setReviewPage();

            DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            try
            {
                Document reviewPage      = Jsoup.connect(curReviewURL)
                        .userAgent("Mozilla/5.0")
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .get();

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
                    StarRating starRating   = StarRating
                            .valueOf(
                                    Integer.parseInt(
                                            unparsedReview
                                                    .getElementsByClass("ebay-star-rating")
                                                    .select("meta")
                                                    .attr("content")));
                    Integer    numHelpful   = Integer.parseInt(
                            unparsedReview
                                    .getElementsByClass("positive-h-c")
                                    .text()
                                    .trim()
                                    .replaceAll(",", ""));
                    Integer    numUnhelpful = Integer.parseInt(unparsedReview.getElementsByClass("negative-h-c").text().trim().replaceAll(",", ""));

                    try
                    {
                        reviewDate = dateFormat.parse(unparsedReview.getElementsByClass("review-item-date").text());
                    }
                    catch (ParseException pe)
                    {
                        pe.printStackTrace();
                    }

                    reviews.add(new Review(reviewTitle, reviewText, starRating, reviewDate, numHelpful, numUnhelpful));
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    private void setReviewPage()
    {
        if (hasInfo && hasReviews)
        {
            curReviewPageNum++;

            if (curReviewPageNum == 1)
            {
                try
                {
                    Document productPage       = Jsoup.connect(productURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
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
