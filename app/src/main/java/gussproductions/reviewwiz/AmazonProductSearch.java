/*
 * Copyright (c) 2017, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Jsoup is used for parsing Product and review information from the Amazon Product Advertising API
 * responses.
 */
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * The AmazonProductSearch class generates a set of Products with attributes such as price,
 * description, and upc. It also has methods that can add new product pages to the existing
 * set of Products. It can also grab review statics for a given ASIN.
 *
 * @author Brendon Guss
 * @since 01/03/2018
 */
class AmazonProductSearch
{
    private final int PRODUCTS_PER_PAGE = 10;

    private ArrayList<Product> productSet;
    private AmazonRequestHelper requestHelper;
    private HashMap<String, String> requestParams;
    private Integer totalPages;
    private String responseURL;
    private int totalProductsParsed;

    // Given a string of keywords, a ProductSet is generated.
    AmazonProductSearch(String searchText)
    {
        // Amazon Advertising API credentials.
        final String AWS_ACCESS_KEY_ID = "AKIAJIZXWDXYYXTUYMYQ";
        final String AWS_ASSOCIATE_TAG = "3997-6329-4456";
        final String AWS_SECRET_KEY = "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";
        final String ENDPOINT = "ecs.amazonaws.com";
        final String ITEM_KEYWORDS;

        ITEM_KEYWORDS = searchText;
        totalProductsParsed = 0;
        responseURL = null;
        productSet = new ArrayList<>();

        try
        {
            requestHelper = AmazonRequestHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_ASSOCIATE_TAG, AWS_SECRET_KEY);
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

        // The requestHelper generates the responseURL which links to XML data that is later parsed.
        responseURL = requestHelper.sign(requestParams);

        calcTotalPages();
        parseProducts(PRODUCTS_PER_PAGE);
    }

    /**
     * The parseProducts method adds products to the Product set based on how many products have
     * already been added. It also keeps track of which product page it needs to be accessing.
     *
     * @param numProductsToParse The number of products to parse and add to the productSet.
     */
    private void parseProducts(int numProductsToParse)
    {
        int productsParsed = 0;

        for (Integer productPageNum = totalProductsParsed / PRODUCTS_PER_PAGE + 1; productPageNum <= totalPages && productsParsed < numProductsToParse; productPageNum++)
        {
            try
            {
                requestParams.remove("ItemPage");
                requestParams.put("ItemPage", productPageNum.toString());

                responseURL = requestHelper.sign(requestParams);
                Document productResultPage = Jsoup.connect(responseURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                Elements unparsedProducts = productResultPage.select("Item");

                for (int productIndex = totalProductsParsed % (PRODUCTS_PER_PAGE * productPageNum); productIndex < unparsedProducts.size() && productsParsed < numProductsToParse; productIndex++)//(Element unparsedProduct : unparsedProducts)
                {
                    if (unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice").select("FormattedPrice").hasText())
                    {
                        String asin = unparsedProducts.get(productIndex).getElementsByTag("ASIN").text();

                        CommonProductInfo commonProductInfo =
                                new CommonProductInfo(unparsedProducts.get(productIndex).getElementsByTag("ISBN").text(),
                                                      unparsedProducts.get(productIndex).getElementsByTag("UPC").text(),
                                                      unparsedProducts.get(productIndex).getElementsByTag("EAN").text(),
                                                      unparsedProducts.get(productIndex).getElementsByTag("Title").text(),
                                                      unparsedProducts.get(productIndex).getElementsByTag("EditorialReview").select("Content").text());
                        AmazonProductInfo amazonProductInfo =
                                new AmazonProductInfo(asin,
                                        unparsedProducts.get(productIndex).getElementsByTag("DetailPageURL").text(),
                                        new BigDecimal(unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice").select("FormattedPrice").text().substring(1).replaceAll(",", "")));

                        Product product = new Product(commonProductInfo);
                        product.setAmazonProductInfo(amazonProductInfo);

                        //System.out.println(unparsedProducts.get(productIndex).getElementsByTag("DetailPageURL").text());
                        //System.out.println(getReviewIFrame(asin));

                        //Document reviewResultsPage = Jsoup.connect("https://www.amazon.com/Apple-iPhone-GSM-Unlocked-32GB/dp/B01N9YOF3R").userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();

                        productSet.add(product);

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

    /**
     * Calculates the total number of Amazon product search result pages that can be parsed and
     * stores it in the instance variable totalPages.
     */
    private void calcTotalPages()
    {
        final int PRODUCT_PAGE_LIMIT = 5;
        totalPages = 0;

        try
        {
            Document productResultPage = Jsoup.connect(responseURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Element totalPagesElement = productResultPage.selectFirst("TotalPages");
            totalPages = Integer.parseInt(totalPagesElement.text());

            /*
             * The total number of product pages is often over 5, but 5 pages is the maximum
             * supported by the API.
             */
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

    ReviewStats getReviewStats(String asin)
    {
        String reviewIframe;
        String reviewURL = null;
        Document reviewIFrame;
        String unparsedReviewStats;
        int totalNumReviews = 0;
        Integer[] numStars;

        reviewIframe = getReviewIFrame(asin);
        numStars = new Integer[5];

        try
        {
            reviewIFrame = Jsoup.connect(reviewIframe).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            unparsedReviewStats = reviewIFrame.select("div.crIFrameHeaderHistogram:matches( Reviews)").first().text();

            reviewURL = reviewIFrame.getElementsByClass("asinReviewsSummary").select("a").attr("abs:href");

            //System.out.println(reviewURL);

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

    /**
     * Sets the request parameters that are common to all Amazon Product Advertising API requests.
     */
    private void setCommonRequestParams()
    {
        requestParams = new HashMap<>();
        requestParams.put("Service", "AWSECommerceService");
        requestParams.put("Version", "2013-08-01");
        requestParams.put("Condition", "New");
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

            responseURL = requestHelper.sign(requestParams);

            reviewResultsPage = Jsoup.connect(responseURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            reviewURL = reviewResultsPage.getElementsByTag("IFrameURL").text();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return reviewURL;
    }

    public ArrayList<Product> getProductSet()
    {
        return productSet;
    }


}
