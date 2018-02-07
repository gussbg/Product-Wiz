/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;


/**
 * This request helper is used to generate the request URL that links to
 * XML data that can later be parsed into BestBuy product information.
 *
 * @author Brendon Guss
 * @since  01/09/2018
 */
class BestbuyRequestHelper extends RequestHelper
{
    BestbuyRequestHelper(String upc)
    {
        final String RESPONSE_FORMAT = "xml";
        final String ENDPOINT        = "https://api.bestbuy.com/v1/products";
        final String API_KEY         = "ej18EPj5rU43g6OpOxZ4Kwsi";

        requestURL = ENDPOINT;

        setUPC(upc);

        requestParams.put("apiKey", API_KEY);
        requestParams.put("format", RESPONSE_FORMAT);

        setResponseAttributes();

        requestURL += genQueryString();
    }

    /**
     * Sets the UPC for the product information request.
     *
     * @param upc The unique product identifier.
     */
    private void setUPC(String upc)
    {
        requestURL += "(upc=" + upc + ")?";
    }

    /**
     * Sets the attributes for the product data to be sent in the response
     * such as the price and description.
     */
    private void setResponseAttributes()
    {
        String responseAttributes = "image,"
                                  + "longDescription,"
                                  + "name,"
                                  + "salePrice,"
                                  + "sku,"
                                  + "customerReviewAverage,"
                                  + "customerReviewCount,"
                                  + "url";

        requestParams.put("show", responseAttributes);
    }

    /**
     * Gets the request URL that links to XML product data.
     *
     * @return The request URL.
     */
    String getRequestURL()
    {
        return requestURL;
    }
}
