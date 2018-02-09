/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

/**
 * Contains the two Request modes (that are relevant to this application)
 * that can be used with the eBay Product API.
 *
 * @author Brendon Guss
 * @since  01/27/2018
 */
enum EbayRequestMode
{
    // Mode used for finding products from a UPC,
    FIND_ITEMS_BY_PRODUCT,

    // Used for getting information on one item on eBay. This mode is only used to get the description.
    GET_ITEM
}
