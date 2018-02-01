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
 * Created by Brendon on 1/17/2018.
 */

class BestbuyProductInfo extends ProductInfo
{
    private String baseReviewURL;

    BestbuyProductInfo(String upc)
    {
        BestbuyRequestHelper bestbuyRequestHelper = new BestbuyRequestHelper(upc);
        curReviewPageNum = 0;
        String requestURL = bestbuyRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            int numResults = Integer.parseInt(productResultPage.getElementsByTag("products").attr("total"));

            if (numResults == 1)
            {
                Element unparsedProduct = productResultPage.getElementsByTag("product").first();
                Integer numReviews;
                Double  averageStarRating;

                price             = new BigDecimal(unparsedProduct.getElementsByTag("salePrice").text().replaceAll(",", ""));
                itemID            = unparsedProduct.getElementsByTag("sku").text();
                productURL        = unparsedProduct.getElementsByTag("url").text();
                title             = unparsedProduct.getElementsByTag("name").text();
                imageURL          = unparsedProduct.getElementsByTag("image").text();
                description       = unparsedProduct.getElementsByTag("longDescription").text();
                numReviews        = Integer.parseInt(unparsedProduct.getElementsByTag("customerReviewCount").text());
                averageStarRating = Double.parseDouble(unparsedProduct.getElementsByTag("customerReviewAverage").text());
                reviewStats       = new ReviewStats(numReviews, averageStarRating);
                hasInfo           = true;
                reviews           = new ArrayList<>();
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

    public void parseReviewPage()
    {
        if (hasInfo) {
            setReviewPage();

            DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            Pattern numHelpfulPattern = Pattern.compile("Helpful \\((.*?)\\)");
            Pattern numUnhelpfulPattern = Pattern.compile("Unhelpful \\((.*?)\\)");

            Matcher numHelpfulMatcher;
            Matcher numUnhelpfulMatcher;


            try {
                Document reviewPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                Elements unparsedReviews = reviewPage.getElementsByClass("review-item-feedback");

                for (Element unparsedReview : unparsedReviews) {
                    Date reviewDate = new Date();
                    String reviewTitle = unparsedReview.getElementsByClass("col-md-9 col-sm-9 col-xs-12 title").text();
                    String reviewText = unparsedReview.getElementsByClass("pre-white-space").text();
                    StarRating starRating = StarRating.valueOf(Integer.parseInt(unparsedReview.getElementsByClass("c-review-average").text()));


                    Integer numHelpful = DEFAULT_HELPFUL_NUM;
                    Integer numUnhelpful = DEFAULT_UNHELPFUL_NUM;

                    numHelpfulMatcher = numHelpfulPattern.matcher(unparsedReview.getElementsByClass("pos-feedback no-margin-l false").text());
                    numUnhelpfulMatcher = numUnhelpfulPattern.matcher(unparsedReview.getElementsByClass("pos-feedback no-margin-l false").text());

                    if (numHelpfulMatcher.find()) {
                        numHelpful = Integer.parseInt(numHelpfulMatcher.group(1).replace(",", ""));
                    }

                    if (numUnhelpfulMatcher.find()) {
                        numUnhelpful = Integer.parseInt(numUnhelpfulMatcher.group(1).replace(",", ""));
                    }

                    try {
                        reviewDate = dateFormat.parse(unparsedReview.getElementsByClass("review-date").text());
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }

                    reviews.add(new Review(reviewTitle, reviewText, starRating, reviewDate, numHelpful, numUnhelpful));
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void setReviewPage()
    {
        if (hasInfo) {
            curReviewPageNum++;

            if (curReviewPageNum == 1) {
                try {
                    Document productPage = Jsoup.connect(productURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                    Element unparsedReviewURL = productPage.getElementsByClass("see-all-reviews-button-container").first().getElementsByClass("btn btn-default ").first();

                    baseReviewURL = unparsedReviewURL.attr("href");
                    curReviewURL = baseReviewURL + "?page=1";
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                curReviewURL = baseReviewURL + "?page=" + curReviewPageNum;
            }
        }
    }
}
