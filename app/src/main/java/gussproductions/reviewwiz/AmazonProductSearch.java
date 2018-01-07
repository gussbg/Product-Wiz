package gussproductions.reviewwiz;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Created by Brendon on 8/23/2017.
 */

public class AmazonProductSearch {

    private final String AWS_ACCESS_KEY_ID = "AKIAJIZXWDXYYXTUYMYQ";
    private final String AWS_ASSOCIATE_TAG = "3997-6329-4456";


    private final String AWS_SECRET_KEY = "V9ybVhcilaWjqDFixxvJMh+dKX+kevCY2kbZUdZR";

    private final String ENDPOINT = "ecs.amazonaws.com";

    private String ITEM_KEYWORDS;
    private final int PRODUCTS_PER_PAGE = 10;
    private final int PRODUCT_PAGE_LIMIT = 5;
    private ArrayList<Product> productSet;
    private SignedRequestsHelper requestsHelper;
    private HashMap<String, String> requestParams;
    private Integer totalPages;
    private String requestURL = null;
    private int totalProductsParsed;

    public AmazonProductSearch(String searchText)
    {
        ITEM_KEYWORDS = searchText;
        totalProductsParsed = 0;

        /*
         * Set up the signed requests requestsHelper
         */
        try
        {
            requestsHelper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_ASSOCIATE_TAG, AWS_SECRET_KEY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        setCommonRequestParams();

        requestParams.put("Operation", "ItemSearch");
        requestParams.put("Keywords", ITEM_KEYWORDS);
        requestParams.put("ResponseGroup", "Medium");
        requestParams.put("SearchIndex","All");

        requestURL = requestsHelper.sign(requestParams);
        System.out.println("Signed Request is \"" + requestURL + "\"");
        productSet = new ArrayList<Product>();

        calcTotalPages();
        parseProducts(PRODUCTS_PER_PAGE);
    }

    private void parseProducts(int numProductsToParse)
    {
        int productsParsed = 0;

        for (Integer productPageNum = totalProductsParsed / PRODUCTS_PER_PAGE + 1; productPageNum <= PRODUCT_PAGE_LIMIT && productsParsed < numProductsToParse; productPageNum++)
        {
            try
            {
                requestParams.remove("ItemPage");
                requestParams.put("ItemPage", productPageNum.toString());

                requestURL = requestsHelper.sign(requestParams);
                Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                Elements unparsedProducts = productResultPage.select("Item");

                for (int productIndex = totalProductsParsed % (PRODUCTS_PER_PAGE * productPageNum); productIndex < unparsedProducts.size() && productsParsed < numProductsToParse; productIndex++)//(Element unparsedProduct : unparsedProducts)
                {
                    if (unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice").select("FormattedPrice").hasText())
                    {
                        String asin = unparsedProducts.get(productIndex).getElementsByTag("ASIN").text();
                        ReviewStats reviewStats;

                        reviewStats = getReviewStats(asin);

                        productSet.add(
                                new Product(unparsedProducts.get(productIndex).getElementsByTag("ISBN").text(),
                                        asin,
                                        unparsedProducts.get(productIndex).getElementsByTag("UPC").text(),
                                        unparsedProducts.get(productIndex).getElementsByTag("Title").text(),
                                        unparsedProducts.get(productIndex).getElementsByTag("EditorialReview").select("Content").text(),
                                        unparsedProducts.get(productIndex).getElementsByTag("LargeImage").first().select("URL").text(),
                                        new BigDecimal(unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice").select("FormattedPrice").text().substring(1).replaceAll(",", "")),
                                        reviewStats));

                        productsParsed++;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void calcTotalPages()
    {
        totalPages = 0;

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Element totalPagesElement = productResultPage.selectFirst("TotalPages");
            totalPages = Integer.parseInt(totalPagesElement.text());

            // Only up to 5 pages as this is the API limit
            if (totalPages > PRODUCT_PAGE_LIMIT)
            {
                totalPages = PRODUCT_PAGE_LIMIT;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private ReviewStats getReviewStats(String asin)
    {
        String reviewURL;
        Document reviewIFrame;
        String unparsedReviewStats;
        int totalNumReviews = 0;
        Integer[] numStars;

        reviewURL = getReviewURL(asin);
        numStars = new Integer[5];

        try
        {
            reviewIFrame = Jsoup.connect(reviewURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            unparsedReviewStats = reviewIFrame.select("div.crIFrameHeaderHistogram:matches( Reviews)").first().text();

            Pattern numReviewsPattern = Pattern.compile("(.*?) Reviews");
            Pattern numReviewsStarsPattern = Pattern.compile(" star (.*?)%");

            Matcher numReviewsMatcher = numReviewsPattern.matcher(unparsedReviewStats);
            Matcher numReviewsStarsMatcher = numReviewsStarsPattern.matcher(unparsedReviewStats);

            if (numReviewsMatcher.find())
            {
                totalNumReviews = Integer.parseInt(numReviewsMatcher.group(1).replace(",", ""));
            }

            for (int i = 0; i < numStars.length && numReviewsStarsMatcher.find(); i++)
            {
                numStars[i] = (int) Math.round((Double.parseDouble(numReviewsStarsMatcher.group(1)) / 100.0) * totalNumReviews);
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        return new ReviewStats(numStars, reviewURL);
    }

    private void setCommonRequestParams()
    {
        requestParams = new HashMap<String, String>();
        requestParams.put("Service", "AWSECommerceService");
        requestParams.put("Version", "2013-08-01");
        requestParams.put("Condition", "New");
    }

    private String getReviewURL(String asin)
    {
        String reviewURL = null;

        requestParams.remove("ResponseGroup");
        requestParams.remove("Operation");
        requestParams.remove("Keywords");
        requestParams.remove("SearchIndex");
        requestParams.put("ResponseGroup", "Reviews");
        requestParams.put("Operation", "ItemLookup");

        try
        {
            Document reviewResultsPage;

            requestParams.remove("ItemId");
            requestParams.put("ItemId", asin);

            requestURL = requestsHelper.sign(requestParams);

            reviewResultsPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            reviewURL = reviewResultsPage.getElementsByTag("IFrameURL").text();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return reviewURL;
    }
}
