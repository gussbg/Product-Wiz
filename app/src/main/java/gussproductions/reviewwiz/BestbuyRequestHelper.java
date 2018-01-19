package gussproductions.reviewwiz;

/**
 * Created by Brendon on 1/17/2018.
 */

public class BestbuyRequestHelper
{
    private String requestURL;
    private final String COMMON_URL_PREFIX = "https://api.bestbuy.com/v1/products";
    private final String RESPONSE_FORMAT = "xml";

    public BestbuyRequestHelper(String upc)
    {
        requestURL = COMMON_URL_PREFIX;
        setUPC(upc);
        setApiKey();
        setResponseAttributes();
        setResponseFormat();
    }

    private void setUPC(String upc)
    {
        requestURL += "(upc=" + upc + ")";
    }

    private void setApiKey()
    {
        final String apiKey = "ej18EPj5rU43g6OpOxZ4Kwsi";

        requestURL += "?apiKey=" + apiKey;

    }

    private void setResponseAttributes()
    {
        requestURL += "&show="
                   +  "image,"
                   +  "longDescription,"
                   +  "name,"
                   +  "salePrice,"
                   +  "sku,"
                   +  "customerReviewAverage,"
                   +  "customerReviewCount,"
                   +  "url";
    }

    private void setResponseFormat()
    {
        requestURL += "&format=" + RESPONSE_FORMAT;
    }

    public String getRequestURL()
    {
        return requestURL;
    }
}
