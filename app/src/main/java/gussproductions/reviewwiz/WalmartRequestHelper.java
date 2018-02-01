package gussproductions.reviewwiz;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Brendon on 1/8/2018.
 */

public class WalmartRequestHelper
{
    private String requestURL;
    private HashMap<String, String> requestParams = new HashMap<>();


    private final String ENDPOINT = "http://api.walmartlabs.com/v1/";

    WalmartRequestHelper (String upc)
    {
        requestURL = ENDPOINT + "items?";

        setCommonParams();

        requestParams.put("upc", upc);

        addQuery();
    }

    WalmartRequestHelper(String itemID, Integer reviewPageNum)
    {
        requestURL = ENDPOINT + "reviews/";

        setItemID(itemID);

        requestURL += "?";

        setCommonParams();

        requestParams.put("page", reviewPageNum.toString());

        addQuery();
    }

    private void setCommonParams()
    {
        final String RESPONSE_FORMAT = "xml";
        final String API_KEY = "r9gupp7gu9kbbfyuzkgssbjy";

        requestParams.put("apiKey", API_KEY);
        requestParams.put("format", RESPONSE_FORMAT);
    }

    private void addQuery()
    {
        SortedMap<String, String> sortedParamMap = new TreeMap<>(requestParams);
        String canonicalQS = RequestHelperTools.canonicalize(sortedParamMap);

        requestURL += canonicalQS;
    }

    private void setItemID(String itemID)
    {
        requestURL += itemID;
    }

    String getRequestURL()
    {
        return requestURL;
    }
}

