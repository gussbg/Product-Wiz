/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;

import java.util.HashMap;


/**
 * This request helper is used to generate the request URL that links to
 * XML data that can later be parsed into eBay product information.
 *
 * @author Brendon Guss
 * @since  01/27/2018
 */
class EbayRequestHelper extends RequestHelper
{
    private final EbayRequestMode ebayRequestMode;

    EbayRequestHelper(String productID, EbayRequestMode ebayRequestMode)
    {
        this.ebayRequestMode = ebayRequestMode;

        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            // Constants necessary for eBay request.
            final String  RESPONSE_FORMAT                = "XML";
            final String  AFFILIATE_NETWORK_ID           = "9";
            final String  AFFILIATE_TRACKING_ID          = "5338248994";
            final String  NEW_CONDITION_ID               = "1000";
            final String  FILTER_NAME                    = "Condition";
            final int     TRACKING_CONDITION_PARAM_INDEX = 0;
            final String  LISTING_FILTER_NAME            = "ListingType";
            final String  LISTING_TYPE                   = "FixedPrice";
            final int     LISTING_CONDITION_PARAM_INDEX  = 1;
            final String  IMAGE_OUTPUT_SELECT            = "PictureURLSuperSize";
            final String  SORT_ORDER                     = "PricePlusShippingLowest";
            final Integer RESULT_SIZE                    = 1;

            setCommonParams();

            requestParams.put("RESPONSE-DATA-FORMAT", RESPONSE_FORMAT);

            requestURL += genQueryString();

            setRestPayload();

            requestParams = new HashMap<>();

            requestParams.put("affiliate.networkId" , AFFILIATE_NETWORK_ID);
            requestParams.put("affiliate.trackingId", AFFILIATE_TRACKING_ID);
            requestParams.put("itemFilter(" + TRACKING_CONDITION_PARAM_INDEX + ").name"
                                                    , FILTER_NAME);
            requestParams.put("itemFilter(" + TRACKING_CONDITION_PARAM_INDEX + ").value"
                                                    , NEW_CONDITION_ID);
            requestParams.put("itemFilter(" + LISTING_CONDITION_PARAM_INDEX + ").name"
                                                    , LISTING_FILTER_NAME);
            requestParams.put("itemFilter(" + LISTING_CONDITION_PARAM_INDEX + ").value"
                                                    , LISTING_TYPE);
            requestParams.put("outputSelector"      , IMAGE_OUTPUT_SELECT);

            setProductID(productID);

            requestParams.put("sortOrder"                     , SORT_ORDER);
            requestParams.put("paginationInput.entriesPerPage", RESULT_SIZE.toString());

            requestURL += genQueryString();
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            final String INCLUDE_SELECTOR = "TextDescription";

            setCommonParams();

            requestParams.put("IncludeSelector", INCLUDE_SELECTOR);

            setProductID(productID);

            requestURL += genQueryString();
        }
    }

    /**
     * Sets the parameters that are common to every eBay API request
     * such as the endpoint and global ID.
     */
    private void setCommonParams()
    {
        setEndpoint();
        setOperation();
        setApiVersion();
        setAppID();
        setGlobalID();
    }

    /**
     * Sets the product ID in the eBay API request.
     *
     * @param productID The product ID to set, this can be either a UPC or eBay's ItemID
     */
    private void setProductID(String productID)
    {
        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            final String PRODUCT_ID_TYPE = "UPC";

            requestParams.put("productId.@type", PRODUCT_ID_TYPE);
            requestParams.put("productId", productID);
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            requestParams.put("ItemID", productID);
        }
    }

    /**
     * Sets the endpoint in the eBay API request.
     */
    private void setEndpoint()
    {
        String endpoint = null;

        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            endpoint = "http://svcs.ebay.com/services/search/FindingService/v1?";
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            endpoint = "http://open.api.ebay.com/shopping?";
        }

        requestURL = endpoint;
    }

    /**
     * Sets the operation in the eBay API request.
     */
    private void setOperation()
    {
        final String OPERATION;

        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            OPERATION = "findItemsByProduct";

            requestParams.put("OPERATION-NAME", OPERATION);
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            OPERATION = "GetSingleItem";

            requestParams.put("callname", OPERATION);
        }
    }

    /**
     * Sets the API Version in the eBay API request.
     */
    private void setApiVersion()
    {
        final String SERVICE_VERSION;

        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            SERVICE_VERSION = "1.13.0";

            requestParams.put("SERVICE-VERSION", SERVICE_VERSION);
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            SERVICE_VERSION = "863";

            requestParams.put("version", SERVICE_VERSION);
        }
    }

    /**
     * Sets the Application ID in the eBay API request.
     */
    private void setAppID()
    {
        final String APP_NAME = "BrendonG-ReviewWi-PRD-b5d80d3bd-f95d9b10";

        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            requestParams.put("SECURITY-APPNAME", APP_NAME);
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            requestParams.put("appid", APP_NAME);
        }
    }

    /**
     * Sets the global ID in the eBay API request.
     */
    private void setGlobalID()
    {
        final String GLOBAL_ID;

        if (ebayRequestMode.equals(EbayRequestMode.FIND_ITEMS_BY_PRODUCT))
        {
            GLOBAL_ID = "EBAY-US";

            requestParams.put("GLOBAL-ID", GLOBAL_ID);
        }
        else if (ebayRequestMode.equals(EbayRequestMode.GET_ITEM))
        {
            GLOBAL_ID = "0";

            requestParams.put("siteid", GLOBAL_ID);
        }
    }

    /**
     * Sets the rest payload in the eBay API request.
     */
    private void setRestPayload()
    {
        requestURL += "&REST-PAYLOAD=";
    }

    /**
     * Gets the request URL that links to XML product data.
     *
     * @return The request URL.
     */
    String getRequestURL() { return requestURL; }
}
