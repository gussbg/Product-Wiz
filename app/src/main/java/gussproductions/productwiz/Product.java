/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

/**
 * The Product class is an aggregation of all generated Product information
 * for one product.
 *
 * @author Brendon Guss
 * @since  01/28/2018
 */
class Product
{
    private String             upc;
    private AmazonProductInfo  amazonProductInfo;
    private WalmartProductInfo walmartProductInfo;
    private BestbuyProductInfo bestbuyProductInfo;
    private EbayProductInfo    ebayProductInfo;

    Product(AmazonProductInfo amazonProductInfo, String upc)
    {
        this.upc                = upc;
        this.amazonProductInfo  = amazonProductInfo;
        this.walmartProductInfo = new WalmartProductInfo(upc);
        this.bestbuyProductInfo = new BestbuyProductInfo(upc);
        this.ebayProductInfo    = new EbayProductInfo(upc);
    }

    // Getter methods.
    String             getUPC()                { return upc;                }
    AmazonProductInfo  getAmazonProductInfo()  { return amazonProductInfo;  }
    WalmartProductInfo getWalmartProductInfo()
    {
        return walmartProductInfo;
    }
    BestbuyProductInfo getBestbuyProductInfo() { return bestbuyProductInfo; }
    EbayProductInfo    getEbayProductInfo()    { return ebayProductInfo;    }
}
