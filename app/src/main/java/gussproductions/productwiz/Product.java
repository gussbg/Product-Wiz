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
    private Bitmap             largeImage;
    private SerializableBitmap smallImage;
    private Retailer           lowestPriceRetailer;
    private BigDecimal         lowestPrice;
    private String             lowestPriceProductURL;
    private AmazonProductInfo  amazonProductInfo;
    private WalmartProductInfo walmartProductInfo;
    private BestbuyProductInfo bestbuyProductInfo;
    private EbayProductInfo    ebayProductInfo;
    private BookmarkedProduct  bookmarkedProduct;
    private ReviewStats        reviewStats;
    private String             description;
    private int                amazonReviewsDelivered;
    private int                ebayReviewsDelivered;
    private int                walmartReviewsDelivered;
    private boolean            hasMoreReviews;
    private boolean            basicReviewStats;

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

    // Getter methods.
    String             getUPC()                   { return upc;                   }
    Bitmap             getLargeImage()            { return largeImage;            }
    SerializableBitmap getSmallImage()            { return smallImage;            }
    BigDecimal         getLowestPrice()           { return lowestPrice;           }
    Retailer           getLowestPriceRetailer()   { return lowestPriceRetailer;   }
    String             getLowestPriceProductURL() { return lowestPriceProductURL; }
    AmazonProductInfo  getAmazonProductInfo()     { return amazonProductInfo;     }
    WalmartProductInfo getWalmartProductInfo()    { return walmartProductInfo;    }
    BestbuyProductInfo getBestbuyProductInfo()    { return bestbuyProductInfo;    }
    EbayProductInfo    getEbayProductInfo()       { return ebayProductInfo;       }
    ReviewStats        getReviewStats()           { return reviewStats;           }
    String             getDescription()           { return description;           }
    boolean            hasMoreReviews()           { return hasMoreReviews;        }
    boolean            hasBasicReviewStats()      { return basicReviewStats;      }

    void setLargeImage()
    {
        int largestHeight = 0;
        Bitmap largestImage = null;

        if (amazonProductInfo.hasInfo() && amazonProductInfo.imageURL != null && !amazonProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(amazonProductInfo.imageURL);

            if (largeImage != null)
            {
                largestImage = largeImage;
                largestHeight = largeImage.getHeight();
            }
        }
        if (bestbuyProductInfo.hasInfo() && bestbuyProductInfo.imageURL != null && !bestbuyProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(bestbuyProductInfo.imageURL);

            if (largeImage != null && largestHeight < largeImage.getHeight())
            {
                largestImage = largeImage;
                largestHeight = largeImage.getHeight();
            }
        }
        if (walmartProductInfo.hasInfo() && walmartProductInfo.imageURL != null && !walmartProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(walmartProductInfo.imageURL);

            if (largeImage != null && largestHeight < largeImage.getHeight())
            {
                largestImage = largeImage;
                largestHeight = largeImage.getHeight();
            }
        }
        if (ebayProductInfo.hasInfo() && ebayProductInfo.imageURL != null && !ebayProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(ebayProductInfo.imageURL);

            if (largeImage != null && largestHeight < largeImage.getHeight())
            {
                largestImage = largeImage;
            }
        }

        largeImage = largestImage;
    }

    void setSmallImage()
    {
        if (!amazonProductInfo.hasInfo() || amazonProductInfo.smallImageURL == null || amazonProductInfo.smallImageURL.equals(""))
        {
            setLargeImage();
            smallImage = new SerializableBitmap(largeImage);
            largeImage = null;
        }
        else
        {
            smallImage = new SerializableBitmap(loadImage(amazonProductInfo.smallImageURL));
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
        if (walmartProductInfo.hasInfo() && lowestPrice.compareTo(walmartProductInfo.getPrice()) == 1)
        {
            lowestPrice           = walmartProductInfo.getPrice();
            lowestPriceRetailer   = Retailer.WALMART;
            lowestPriceProductURL = walmartProductInfo.getProductURL();
        }
        if (ebayProductInfo.hasInfo() && lowestPrice.compareTo(ebayProductInfo.getPrice()) == 1)
        {
            lowestPrice           = ebayProductInfo.getPrice();
            lowestPriceRetailer   = Retailer.EBAY;
            lowestPriceProductURL = ebayProductInfo.getProductURL();
        }
    }

    void setReviewStats()
    {
        Integer numStars[] = new Integer[5];

        for (int i = 0; i < 5; i++)
        {
            numStars[i] = 0;
        }

        reviewStats = new ReviewStats(numStars);

        if (amazonProductInfo.hasInfo() && reviewStats != null)
        {
            amazonProductInfo.setReviewStats();
            reviewStats.add(amazonProductInfo.getReviewStats());
        }

        if (ebayProductInfo.hasInfo())
        {
            ebayProductInfo.setReviewStats();
            reviewStats.add(ebayProductInfo.getReviewStats());
        }

        if (walmartProductInfo.hasInfo())
        {
            walmartProductInfo.setReviewStats();
            reviewStats.add(walmartProductInfo.reviewStats);
        }

        if (!amazonProductInfo.hasInfo() && !ebayProductInfo.hasInfo() && !walmartProductInfo.hasInfo() && bestbuyProductInfo.hasInfo())
        {
            reviewStats      = bestbuyProductInfo.getReviewStats();
            basicReviewStats = true;
        }

    }

    void setDescription()
    {
        if (amazonProductInfo.hasInfo() && amazonProductInfo.getDescription() != null && !amazonProductInfo.getDescription().equals(""))
        {
            description = amazonProductInfo.getDescription();
        }
        else if (walmartProductInfo.hasInfo() && walmartProductInfo.getDescription() != null && !walmartProductInfo.getDescription().equals(""))
        {
            description = walmartProductInfo.getDescription();
        }
        else if (bestbuyProductInfo.hasInfo() && bestbuyProductInfo.getDescription() != null && !bestbuyProductInfo.getDescription().equals(""))
        {
            description = bestbuyProductInfo.getDescription();
        }
        else if (ebayProductInfo.hasInfo())
        {
            ebayProductInfo.setDescription();

            description = ebayProductInfo.getDescription();
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
