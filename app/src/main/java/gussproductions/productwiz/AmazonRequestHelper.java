/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class contains all the logic for generating and signing requests
 * to the Amazon Product Advertising API.
 *
 * @author Brendon Guss
 * @since  01/03/2018
 */
class AmazonRequestHelper extends RequestHelper
{
    // The HMAC algorithm required by Amazon.
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    // The URI for the service.
    private static final String REQUEST_URI = "/onca/xml";

    // The HTTP request method to fetch the response.
    private static final String REQUEST_METHOD = "GET";

    private Mac mac;

    private AmazonRequestMode amazonRequestMode;

    /**
     * Constructs the AmazonRequestHelper given an Amazon request mode and an Amazon searchID.
     *
     * @param searchID The Amazon search ID.
     * @param amazonRequestMode The Amazon request mode.
     * @throws IllegalArgumentException If an illegal argument is given.
     * @throws UnsupportedEncodingException If the character encoding mode is not supported.
     * @throws NoSuchAlgorithmException If the algorithm does not exist.
     * @throws InvalidKeyException If the key is invalid.
     */
    AmazonRequestHelper(String searchID, AmazonRequestMode amazonRequestMode)
            throws IllegalArgumentException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException
    {
        final String        AWS_SECRET_KEY  = "V9ybVhcilaWjqDFixxvJMh+dKX+kevCY2kbZUdZR";
        final byte[]        secretyKeyBytes = AWS_SECRET_KEY.getBytes(UTF8_CHARSET);
        final SecretKeySpec secretKeySpec   = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);

        this.amazonRequestMode = amazonRequestMode;
        mac                    = Mac.getInstance(HMAC_SHA256_ALGORITHM);

        mac.init(secretKeySpec); // Initialize MAC.

        setCommonRequestParams(); // Set the common request parameters.

        if (amazonRequestMode.equals(AmazonRequestMode.ITEM_SEARCH)) // For generating a list of items.
        {
            requestParams.put("Operation"    , "ItemSearch");
            requestParams.put("Keywords"     , searchID);
            requestParams.put("ResponseGroup", "Medium");
            requestParams.put("SearchIndex"  , "All");
        }
        else if (amazonRequestMode.equals(AmazonRequestMode.ITEM_LOOKUP_REVIEWS)) // For getting reviews of one item.
        {
            requestParams.put("Operation"    , "ItemLookup");
            requestParams.put("IdType"       , "ASIN");
            requestParams.put("ItemId"       , searchID);
            requestParams.put("ResponseGroup", "Reviews");
        }
        else if (amazonRequestMode.equals(AmazonRequestMode.ITEM_LOOKUP_INFO)) // For getting information on one item.
        {
            requestParams.put("Operation"    , "ItemLookup");
            requestParams.put("IdType"       , "UPC");
            requestParams.put("ItemId"       , searchID);
            requestParams.put("ResponseGroup", "Medium");
            requestParams.put("SearchIndex"  , "All");
        }
    }

    /**
     * Sets the page number of product results. Can only be used when using the item search
     * request mode.
     *
     * @param productPageNum The product page number.
     */
    void setProductPageNum(Integer productPageNum)
    {
        if (amazonRequestMode.equals(AmazonRequestMode.ITEM_SEARCH))
        {
            requestParams.remove("ItemPage");
            requestParams.put("ItemPage", productPageNum.toString());
        }
    }

    /**
     * Sets the request parameters that are common to all Amazon Product Advertising API requests.
     */
    private void setCommonRequestParams()
    {
        final String AWS_ACCESS_KEY_ID = "AKIAJIZXWDXYYXTUYMYQ";
        final String AWS_ASSOCIATE_TAG = "3997-6329-4456";

        requestParams = new HashMap<>();
        requestParams.put("Service"       , "AWSECommerceService");
        requestParams.put("Version"       , "2013-08-01");
        requestParams.put("Condition"     , "New");
        requestParams.put("AWSAccessKeyId", AWS_ACCESS_KEY_ID);
        requestParams.put("AssociateTag"  , AWS_ASSOCIATE_TAG);
    }

    /**
     * This method signs requests in hashmap form. It returns a URL that should
     * be used to fetch the response. The URL returned should not be modified in
     * any way, doing so will invalidate the signature and Amazon will reject
     * the request.
     */
    String getRequestURL()
    {
        final String ENDPOINT = "ecs.amazonaws.com";

        String canonicalQS;

        requestParams.remove("Timestamp");
        requestParams.put("Timestamp", timestamp());

        canonicalQS = genQueryString();

        // Create the string upon which the signature is calculated.
        String toSign =
                REQUEST_METHOD + "\n"
                        + ENDPOINT + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;

        // Generate the signature.
        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);

        // Construct the URL.
        requestURL = "http://" + ENDPOINT + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig;

        return requestURL;
    }

    /**
     * Compute the HMAC.
     *
     * @param stringToSign  String to compute the HMAC over.
     * @return              base64-encoded hmac value.
     */
    private String hmac(String stringToSign)
    {
        String signature;
        byte[] data;
        byte[] rawHmac;

        try
        {
            data      = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac   = mac.doFinal(data);
            signature = new String(Base64.encodeBase64(rawHmac));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }

        return signature;
    }

    /**
     * Generate a ISO-8601 format timestamp as required by Amazon.
     *
     * @return  ISO-8601 format timestamp.
     */
    private String timestamp()
    {
        String timestamp;

        Calendar cal   = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));

        timestamp = dfm.format(cal.getTime());

        return timestamp;
    }
}
