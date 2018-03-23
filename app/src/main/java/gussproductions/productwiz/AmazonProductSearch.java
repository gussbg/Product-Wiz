/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

/*
 * Jsoup is used for parsing Product and review information from the Amazon Product Advertising API
 * responses.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * The AmazonProductSearch class generates a set of Products with attributes such as price,
 * description, and upc. It also has methods that can add new product pages to the existing
 * set of Products.
 *
 * @author Brendon Guss
 * @since  01/03/2018
 */
class AmazonProductSearch
{
    private AmazonRequestHelper amazonRequestHelper;
    private Integer             totalPageNum;
    private String              responseURL;
    private int                 totalProductsScanned;
    private boolean             hasProducts;
    private WeakReference<MainActivity> mainActivity;

    final static int            PRODUCTS_PER_PAGE = 10;

    AmazonProductSearch(String searchText)
    {
        totalProductsScanned = 0;
        responseURL          = null;

        try
        {
            amazonRequestHelper = new AmazonRequestHelper(searchText, AmazonRequestMode.ITEM_SEARCH);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // The amazonRequestHelper generates the responseURL which links to XML data that can later be parsed.
        responseURL = amazonRequestHelper.getRequestURL();

        // Handles the case if no products are returned.
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

    AmazonProductSearch(String searchText, WeakReference<MainActivity> mainActivity)
    {
        this(searchText);
        this.mainActivity = mainActivity;
    }

    /**
     * The getMoreProducts method adds products to the Product set based on how many products have
     * already been added. It also keeps track of which product page it needs to be accessing.
     *
     * @param numProductsToParse The number of products to parse and add to the productSet.
     */
    ArrayList<Product> getMoreProducts(int numProductsToParse)
    {
        if (hasProducts)
        {
            ArrayList<Product> productSet     = new ArrayList<>();
            int productsParsed = 0;

            for (Integer productPageNum = totalProductsScanned / PRODUCTS_PER_PAGE + 1;
                 productPageNum <= totalPageNum && productsParsed < numProductsToParse; productPageNum++)
            {

                try
                {
                    amazonRequestHelper.setProductPageNum(productPageNum);

                    responseURL = amazonRequestHelper.getRequestURL();

                    Document productResultPage = Jsoup.connect(responseURL).userAgent("Mozilla")
                            .ignoreHttpErrors(true).ignoreContentType(true).get();
                    Elements unparsedProducts  = productResultPage.select("Item");

                    if (unparsedProducts != null)
                    {
                        for (int productIndex = totalProductsScanned % PRODUCTS_PER_PAGE;
                             productIndex < unparsedProducts.size() && productsParsed < numProductsToParse;
                             productIndex++, totalProductsScanned++)
                        {
                            String upc = unparsedProducts.get(productIndex).getElementsByTag("UPC").text();

                            // Ensures that only products that are new and have a price and UPC are parsed.
                            if (unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice")
                                    .select("FormattedPrice").hasText()
                                    && !unparsedProducts.get(productIndex).getElementsByTag("LowestNewPrice")
                                    .select("FormattedPrice").text().contains("display")
                                    && !upc.equals(""))
                            {
                                Element unparsedProduct = unparsedProducts.get(productIndex);
                                Product product         = new Product(new AmazonProductInfo(unparsedProduct), upc);

                                productSet.add(product);
                                mainActivity.get().mainProgressBar.incrementProgressBy(5);
                                productsParsed++;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            return productSet;
        }

        // No products are returned if none are parsed.
        return null;
    }

    /**
     * Calculates the total number of Amazon product search result pages that can be parsed and
     * stores it in the instance variable totalPageNum.
     */
    private void calcTotalPages()
    {
        if (hasProducts)
        {
            final int PRODUCT_PAGE_LIMIT = 5;
            totalPageNum = 0;

            try
            {
                Document productResultPage = Jsoup.connect(responseURL).userAgent("Mozilla")
                                                  .ignoreHttpErrors(true).ignoreContentType(true).get();
                Element  totalPagesElement = productResultPage.selectFirst("TotalPages");
                         totalPageNum      = Integer.parseInt(totalPagesElement.text());

                // The total number of product pages is often over 5, but 5 pages is the maximum
                // supported by the API.
                if (totalPageNum > PRODUCT_PAGE_LIMIT)
                {
                    totalPageNum = PRODUCT_PAGE_LIMIT;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean hasProducts()
    {
        return hasProducts;
    }
}
