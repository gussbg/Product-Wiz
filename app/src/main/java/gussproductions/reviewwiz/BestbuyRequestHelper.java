package gussproductions.reviewwiz;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Brendon on 1/17/2018.
 */

public class BestbuyRequestHelper
{
    private String requestURL;
    private HashMap<String, String> requestParams = new HashMap<>();

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
        addQuery();
    }

    private void setUPC(String upc)
    {
        requestURL += "(upc=" + upc + ")?";
    }

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

    private void addQuery()
    {
        SortedMap<String, String> sortedParamMap = new TreeMap<>(requestParams);
        String canonicalQS = RequestHelperTools.canonicalize(sortedParamMap);

        requestURL += canonicalQS;
    }

    public String getRequestURL()
    {
        return requestURL;
    }
}
