package gussproductions.reviewwiz;

/**
 * Created by Brendon on 1/8/2018.
 */

public class WalmartRequestHelper
{
    private String requestURL;

    public WalmartRequestHelper (String upc)
    {
        final String COMMON_URL_PREFIX = "http://api.walmartlabs.com/v1/items?";
        final String RESPONSE_FORMAT = "xml";

        requestURL = COMMON_URL_PREFIX;
        setAPIKey();
        setUPC(upc);
        setResponseFormat(RESPONSE_FORMAT);

    }

    private void setAPIKey()
    {
        final String apiKey = "r9gupp7gu9kbbfyuzkgssbjy";

        requestURL += "apiKey=" + apiKey;
    }

    private void setUPC(String upc)
    {
        requestURL += "&upc=" + upc;
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

