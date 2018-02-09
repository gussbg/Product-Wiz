/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;


/**
 * This request helper is used to generate the request URL that links to
 * XML data that can later be parsed into Walmart product or review information.
 *
 * @author Brendon Guss
 * @since  01/08/2018
 */
class WalmartRequestHelper extends RequestHelper
{
    private final String ENDPOINT = "http://api.walmartlabs.com/v1/";

    /**
     * Generates a request URL that links to product information.
     */
    WalmartRequestHelper (String upc)
    {
        requestURL = ENDPOINT + "items?";

        setCommonParams();

        requestParams.put("upc", upc);

        requestURL += genQueryString();
    }

    /**
     * Generates a request URL that links to reviews given an ItemID.
     */
    WalmartRequestHelper(String itemID, Integer reviewPageNum)
    {
        requestURL = ENDPOINT + "reviews/";

        setItemID(itemID);

        requestURL += "?";

        setCommonParams();

        requestParams.put("page", reviewPageNum.toString());

        requestURL += genQueryString();
    }

    /**
     * Sets the parameters common to both product and review requests.
     */
    private void setCommonParams()
    {
        final String RESPONSE_FORMAT = "xml";
        final String API_KEY = "r9gupp7gu9kbbfyuzkgssbjy";

        requestParams.put("apiKey", API_KEY);
        requestParams.put("format", RESPONSE_FORMAT);
    }

    /**
     * Sets the itemID, a unique identifier for Walmart products.
     *
     * @param itemID The itemID to set.
     */
    private void setItemID(String itemID)
    {
        requestURL += itemID;
    }

    String getRequestURL()
    {
        return requestURL;
    }
}

