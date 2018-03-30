/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This request helper is implemented by other request helpers such as Amazon's that generate
 * the request URL that links to XML data that can later be parsed into product information.
 * Includes utility methods such as genQueryString that are in every RequestHelper subclass.
 *
 * @author Brendon Guss
 * @since  01/27/2018
 */
class RequestHelper
{
    String                  requestURL;
    HashMap<String, String> requestParams;

    RequestHelper()
    {
        requestParams = new HashMap<>();
    }

    String genQueryString()
    {
        // The parameters need to be processed in lexicographical order,
        // so a TreeMap implementation is used.
        SortedMap<String, String> sortedParamMap = new TreeMap<>(requestParams);

        // Generate and return the canonical form of the query string.
        return canonicalize(sortedParamMap);
    }

    // All strings are handled as UTF-8.
    final static String UTF8_CHARSET = "UTF-8";

    /**
     * Canonicalize the query string as required by Amazon.
     *
     * @param sortedParamMap    Parameter name-value pairs in lexicographical order.
     * @return                  Canonical form of query string.
     */
    private String canonicalize(SortedMap<String, String> sortedParamMap)
    {
        if (sortedParamMap.isEmpty())
        {
            return "";
        }

        StringBuilder                       stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter          = sortedParamMap.entrySet().iterator();

        while (iter.hasNext())
        {
            Map.Entry<String, String> kvpair = iter.next();

            stringBuilder.append(percentEncodeRfc3986(kvpair.getKey()));
            stringBuilder.append("=");
            stringBuilder.append(percentEncodeRfc3986(kvpair.getValue()));

            if (iter.hasNext())
            {
                stringBuilder.append("&");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Percent-encode values according the RFC 3986. The built-in Java
     * URLEncoder does not encode according to the RFC, so the extra
     * replacements are made.
     *
     * @param str Decoded string.
     * @return Encoded string per RFC 3986.
     */
    static String percentEncodeRfc3986(String str)
    {
        String out;

        try
        {
            out = URLEncoder.encode(str, UTF8_CHARSET)
                            .replace("+", "%20")
                            .replace("*", "%2A")
                            .replace("%7E", "~");
        }
        catch (UnsupportedEncodingException e)
        {
            out = str;
        }

        return out;
    }

    String getRequestURL() { return requestURL; }
}
