/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;

/*
 * Jsoup is used for parsing Product and review information from the Amazon Product Advertising API
 * responses.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


/**
 * The AmazonProductSearch class generates a set of Products with attributes such as price,
 * description, and upc. It also has methods that can add new product pages to the existing
 * set of Products. It can also grab review statics for a given ASIN.
 *
 * @author Brendon Guss
 * @since  01/03/2018
 */
class AmazonProductSearch
{
    private AmazonRequestHelper requestHelper;
    private Integer totalPages;
    private String responseURL;
    private int totalProductsParsed;
    private boolean hasProducts;

    // Given a string of keywords, a ProductSet is generated.
    AmazonProductSearch(String searchText)
    {
        // Amazon Advertising API credentials.

        final String ITEM_KEYWORDS;

        ITEM_KEYWORDS = searchText;
        totalProductsParsed = 0;
        responseURL = null;

        try
        {
            requestHelper = new AmazonRequestHelper(ITEM_KEYWORDS, AmazonRequestMode.ITEM_SEARCH);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }


        // The requestHelper generates the responseURL which links to XML data that is later parsed.
        responseURL = requestHelper.getRequestURL();

        if (!responseURL.equals(""))
        {
            hasProducts = true;
            calcTotalPages();
        }
        else
        {
            hasProducts = false;
        }
    }

    /**
     * The parseProducts method adds products to the Product set based on how many products have
     * already been added. It also keeps track of which product page it needs to be accessing.
     *
     * @param numProductsToParse The number of products to parse and add to the productSet.
     */
    ArrayList<Product> parseProducts(int numProductsToParse)
    {
        if (hasProducts)
        {
            final int PRODUCTS_PER_PAGE = 10;


            if (numProductsToParse > totalPages * PRODUCTS_PER_PAGE)
            {
                numProductsToParse = totalPages * PRODUCTS_PER_PAGE;
            }

            ArrayList<Product> productSet = new ArrayList<>();
            int productsParsed = 0;


            for (Integer productPageNum = totalProductsParsed / PRODUCTS_PER_PAGE + 1; productPageNum <= totalPages && productsParsed < numProductsToParse; productPageNum++) {
                try {
                    requestHelper.setProductPageNum(productPageNum);
                    responseURL = requestHelper.getRequestURL();
                    Document productResultPage = Jsoup.connect(responseURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
                    Elements unparsedProducts = productResultPage.select("Item");

                    if (unparsedProducts != null) {
                        for (int productIndex = totalProductsParsed % (PRODUCTS_PER_PAGE * productPageNum); productIndex < unparsedProducts.size() && productsParsed < numProductsToParse; productIndex++) {

                            String upc = unparsedProducts.get(productIndex).getElementsByTag("UPC").text();

                            if (unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice").select("FormattedPrice").hasText() && !unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice").select("FormattedPrice").text().contains("display") && !upc.equals("")) {
                                Element unparsedProduct = unparsedProducts.get(productIndex);
                                Product product = new Product(new AmazonProductInfo(unparsedProduct), upc);
                                productSet.add(product);

                                productsParsed++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            totalProductsParsed += productsParsed;

            return productSet;
        }

        return null;
    }

    /**
     * Calculates the total number of Amazon product search result pages that can be parsed and
     * stores it in the instance variable totalPages.
     */
    private void calcTotalPages()
    {
        if (hasProducts)
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
    }
}
