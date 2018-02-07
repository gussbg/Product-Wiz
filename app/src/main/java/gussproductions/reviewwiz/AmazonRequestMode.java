/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;

/**
 * Contains the three Request modes (that are relevant to this application)
 * that can be used with the Amazon Product Advertising API.
 *
 * @author Brendon Guss
 * @since  01/09/2018
 */
enum AmazonRequestMode
{
    // Item search is used to generate a list of items given a string of keywords.
    ITEM_SEARCH,

    // Item lookup info is used to get information on a specific item given a UPC.
    ITEM_LOOKUP_INFO,

    // Is used to get the review IFrame which is used to getting review statistics.
    ITEM_LOOKUP_REVIEWS
}
