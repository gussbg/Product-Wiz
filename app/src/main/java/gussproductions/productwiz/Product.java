/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The Product class is an aggregation of all generated Product information
 * for one product.
 *
 * @author Brendon Guss
 * @since  01/28/2018
 */
class Product implements Serializable
{
    private String             upc;
    private SerializableBitmap image;
    private Retailer           lowestPriceRetailer;
    private BigDecimal         lowestPrice;
    private String             lowestPriceProductURL;
    private AmazonProductInfo  amazonProductInfo;
    private WalmartProductInfo walmartProductInfo;
    private BestbuyProductInfo bestbuyProductInfo;
    private EbayProductInfo    ebayProductInfo;
    private BookmarkedProduct  bookmarkedProduct;
    private int                amazonReviewsDelivered;
    private int                ebayReviewsDelivered;
    private int                walmartReviewsDelivered;
    private boolean            hasMoreReviews;

    Product(AmazonProductInfo amazonProductInfo, String upc)
    {
        this.upc                = upc;
        this.amazonProductInfo  = amazonProductInfo;
        this.walmartProductInfo = new WalmartProductInfo(upc);
        this.bestbuyProductInfo = new BestbuyProductInfo(upc);
        this.ebayProductInfo    = new EbayProductInfo(upc);
    }

    Product(String upc)
    {
        this.upc                = upc;
        this.amazonProductInfo  = new AmazonProductInfo(upc);
        this.walmartProductInfo = new WalmartProductInfo(upc);
        this.bestbuyProductInfo = new BestbuyProductInfo(upc);
        this.ebayProductInfo    = new EbayProductInfo(upc);
    }

    Product(String upc, BookmarkedProduct bookmarkedProduct)
    {
        this.upc                = upc;
        this.bookmarkedProduct  = bookmarkedProduct;
        this.amazonProductInfo  = new AmazonProductInfo(upc);
        this.walmartProductInfo = new WalmartProductInfo(upc);
        this.bestbuyProductInfo = new BestbuyProductInfo(upc);
        this.ebayProductInfo    = new EbayProductInfo(upc);
    }

    // Getter methods.
    String             getUPC()                   { return upc;                   }
    Bitmap             getImage()                 { return image.bitmap;          }
    BigDecimal         getLowestPrice()           { return lowestPrice;           }
    Retailer           getLowestPriceRetailer()   { return lowestPriceRetailer;   }
    String             getLowestPriceProductURL() { return lowestPriceProductURL; }
    AmazonProductInfo  getAmazonProductInfo()     { return amazonProductInfo;     }
    WalmartProductInfo getWalmartProductInfo()    { return walmartProductInfo;    }
    BestbuyProductInfo getBestbuyProductInfo()    { return bestbuyProductInfo;    }
    EbayProductInfo    getEbayProductInfo()       { return ebayProductInfo;       }
    BookmarkedProduct  getBookmarkedProduct()     { return bookmarkedProduct;     }
    boolean            hasMoreReviews()           { return hasMoreReviews;        }

    void setImage()
    {
        if (amazonProductInfo.hasInfo() && amazonProductInfo.imageURL != null && !amazonProductInfo.imageURL.equals(""))
        {
            image = new SerializableBitmap(loadImage(amazonProductInfo.imageURL));
        }
        else if (bestbuyProductInfo.hasInfo() && bestbuyProductInfo.imageURL != null && !bestbuyProductInfo.imageURL.equals(""))
        {
            image = new SerializableBitmap(loadImage(bestbuyProductInfo.imageURL));
        }
        else if (walmartProductInfo.hasInfo() && walmartProductInfo.imageURL != null && !walmartProductInfo.imageURL.equals(""))
        {
            image = new SerializableBitmap(loadImage(walmartProductInfo.imageURL));
        }
        else if (ebayProductInfo.hasInfo() && ebayProductInfo.imageURL != null && !ebayProductInfo.imageURL.equals(""))
        {
            image = new SerializableBitmap(loadImage(ebayProductInfo.imageURL));
        }
    }

    private Bitmap loadImage(String imageURL)
    {
        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mIcon11;
    }

    void setLowestPriceInfo()
    {
        final BigDecimal MAX_PRICE = new BigDecimal(999999999);

        if (amazonProductInfo.hasInfo())
        {
            lowestPrice           = amazonProductInfo.getPrice();
            lowestPriceRetailer   = Retailer.AMAZON;
            lowestPriceProductURL = amazonProductInfo.getProductURL();
        }
        else
        {
            lowestPrice = MAX_PRICE;
        }

        if (bestbuyProductInfo.hasInfo() && lowestPrice.compareTo(bestbuyProductInfo.getPrice()) == 1)
        {
            lowestPrice           = bestbuyProductInfo.getPrice();
            lowestPriceRetailer   = Retailer.BEST_BUY;
            lowestPriceProductURL = bestbuyProductInfo.getProductURL();
        }
        else if (walmartProductInfo.hasInfo() && lowestPrice.compareTo(walmartProductInfo.getPrice()) == 1)
        {
            lowestPrice           = walmartProductInfo.getPrice();
            lowestPriceRetailer   = Retailer.WALMART;
            lowestPriceProductURL = walmartProductInfo.getProductURL();
        }
        else if (ebayProductInfo.hasInfo() && lowestPrice.compareTo(ebayProductInfo.getPrice()) == 1)
        {
            lowestPrice           = ebayProductInfo.getPrice();
            lowestPriceRetailer   = Retailer.EBAY;
            lowestPriceProductURL = ebayProductInfo.getProductURL();
        }
    }

    void setReviewStats()
    {
        if (amazonProductInfo.hasInfo())
        {
            amazonProductInfo.setReviewStats();
        }

        if (ebayProductInfo.hasInfo())
        {
            ebayProductInfo.setReviewStats();
        }

        if (walmartProductInfo.hasInfo())
        {
            walmartProductInfo.setReviewStats();
        }
    }

    void setEbayDescription()
    {
        if (!amazonProductInfo.hasInfo() && !bestbuyProductInfo.hasInfo()
            && !walmartProductInfo.hasInfo() && ebayProductInfo.hasInfo())
        {
            ebayProductInfo.setDescription();
        }
    }

    ArrayList<Review> getMoreReviews()
    {
        ArrayList<Review> combinedReviews = new ArrayList<>();
        ArrayList<Review> amazonReviews;
        ArrayList<Review> ebayReviews;
        ArrayList<Review> walmartReviews;

        amazonReviews  = amazonProductInfo.getMoreReviews();
        ebayReviews    = ebayProductInfo.getMoreReviews();
        walmartReviews = walmartProductInfo.getMoreReviews();

        combinedReviews.addAll(amazonReviews);
        combinedReviews.addAll(ebayReviews);
        combinedReviews.addAll(walmartReviews);

        amazonReviewsDelivered  = amazonReviews.size();
        ebayReviewsDelivered    = ebayReviews.size();
        walmartReviewsDelivered = walmartReviews.size();

        Collections.shuffle(combinedReviews);

        hasMoreReviews = hasMoreReviews(combinedReviews);

        return combinedReviews;
    }

    private boolean hasMoreReviews(ArrayList<Review> deliveredReviews)
    {
        if (walmartProductInfo.hasInfo() && amazonReviewsDelivered == 0 && ebayReviewsDelivered == 0
                && walmartReviewsDelivered < WalmartProductInfo.REVIEW_PAGE_MAX_SIZE)
        {
            return false;
        }

        if (amazonProductInfo.hasInfo() && ebayReviewsDelivered == 0 && walmartReviewsDelivered == 0
                && amazonReviewsDelivered < AmazonProductInfo.REVIEW_PAGE_MAX_SIZE)
        {
            return false;
        }

        if (ebayProductInfo.hasInfo() && amazonReviewsDelivered == 0 && walmartReviewsDelivered == 0
                && ebayReviewsDelivered < EbayProductInfo.REVIEW_PAGE_MAX_SIZE)
        {
            return false;
        }

        return deliveredReviews.size() != 0;
    }
}
