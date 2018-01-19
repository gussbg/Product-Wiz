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
 * Created by Brendon on 1/17/2018.
 */

public class BestbuyProductInfo
{
    private BigDecimal        price;
    private String            sku;
    private String            productURL;
    private String            title;
    private String            imageURL;
    private String            description;
    private ReviewStats       reviewStats;
    private ArrayList<Review> reviews;
    private String            baseReviewURL;
    private String            curReviewURL;
    private int               curReviewPageNum;
    private boolean           hasInfo;

    public BestbuyProductInfo(String upc)
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
                sku               = unparsedProduct.getElementsByTag("sku").text();
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
        setReviewPage();

        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        try
        {
            Document reviewPage = Jsoup.connect(curReviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Elements unparsedReviews = reviewPage.getElementsByClass("review-item-feedback");

            for (Element unparsedReview : unparsedReviews)
            {
                Date       reviewDate   = new Date();
                String     reviewTitle  = unparsedReview.getElementsByClass("col-md-9 col-sm-9 col-xs-12 title").text();
                String     reviewText   = unparsedReview.getElementsByClass("pre-white-space").text();
                StarRating starRating   = StarRating.valueOf(Integer.parseInt(unparsedReview.getElementsByClass("reviewer-score").text()));
                Integer    numHelpful   = Integer.parseInt(unparsedReview.getElementsByClass("pos-display").text());
                Integer    numUnhelpful = Integer.parseInt(unparsedReview.getElementsByClass("neg-display").text());

                try
                {
                    reviewDate = dateFormat.parse(unparsedReview.getElementsByClass("review-date").text());
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

    private void setReviewPage()
    {
        curReviewPageNum++;

        if (curReviewPageNum == 1)
        {
            try
            {
                Document productPage = Jsoup.connect(productURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                Element  unparsedReviewURL = productPage.getElementsByClass("see-all-reviews-button-container").first().getElementsByClass("btn btn-default ").first();

                baseReviewURL = unparsedReviewURL.attr("href");
                curReviewURL = baseReviewURL + "?page=1";

                System.out.println(curReviewURL);
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

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getSku()
    {
        return sku;
    }

    public String getProductURL()
    {
        return productURL;
    }

    public String getTitle()
    {
        return title;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public String getDescription()
    {
        return description;
    }

    public ReviewStats getReviewStats()
    {
        return reviewStats;
    }

    public boolean hasInfo()
    {
        return hasInfo;
    }
}
