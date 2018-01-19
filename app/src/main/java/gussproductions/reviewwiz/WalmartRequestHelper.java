package gussproductions.reviewwiz;

/**
 * Created by Brendon on 1/8/2018.
 */

public class WalmartRequestHelper
{
    private String requestURL;
    final String COMMON_URL_PREFIX = "http://api.walmartlabs.com/v1/";
    final String RESPONSE_FORMAT = "xml";

    public WalmartRequestHelper (String upc)
    {
        requestURL = COMMON_URL_PREFIX + "items";
        setAPIKey();
        setUPC(upc);
        setResponseFormat(RESPONSE_FORMAT);
    }

    public WalmartRequestHelper(String itemID, int reviewPageNum)
    {
        requestURL = COMMON_URL_PREFIX + "reviews/";
        setItemID(itemID);
        setAPIKey();
        setReviewPageNum(reviewPageNum);
        setResponseFormat(RESPONSE_FORMAT);
    }

    private void setReviewPageNum(int reviewPageNum)
    {
        requestURL += "&page=" + reviewPageNum;
    }

    private void setAPIKey()
    {
        final String apiKey = "r9gupp7gu9kbbfyuzkgssbjy";

        requestURL += "?apiKey=" + apiKey;
    }

    private void setUPC(String upc)
    {
        requestURL += "&upc=" + upc;
    }

    private void setItemID(String itemID)
    {
        requestURL += itemID;
    }

    private void setResponseFormat(String format)
    {
        requestURL += "&format=" + format;
    }

    public String getRequestURL()
    {
        return requestURL;
    }

}

