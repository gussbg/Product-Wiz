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
    private ReviewStats        reviewStats;
    private String             description;
    private int                amazonReviewsDelivered;
    private int                ebayReviewsDelivered;
    private int                walmartReviewsDelivered;
    private boolean            hasMoreReviews;
    private boolean            hasBasicReviewStats;

    /**
     * Constructs a Product given it's Amazon product information and it's UPC.
     * This constructor is used when the product is partially loaded from the
     * Amazon product search.
     *
     * @param amazonProductInfo The Amazon product information.
     * @param upc The product's UPC.
     */
    Product(AmazonProductInfo amazonProductInfo, String upc)
    {
        this.upc                = upc;
        this.amazonProductInfo  = amazonProductInfo;
        this.walmartProductInfo = new WalmartProductInfo(upc);
        this.bestbuyProductInfo = new BestbuyProductInfo(upc);
        this.ebayProductInfo    = new EbayProductInfo(upc);
    }

    /**
     * Constructs a Product given it's UPC.
     *
     * @param upc The product's UPC.
     */
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

    /**
     * Sets the product's large image, which is intended to be viewed in the ViewProductActivity.
     */
    void setLargeImage()
    {
        int largestHeight   = 0;
        Bitmap largestImage = null;

        if (amazonProductInfo.hasInfo() && amazonProductInfo.imageURL != null
                && !amazonProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(amazonProductInfo.imageURL);

            if (largeImage != null)
            {
                largestImage  = largeImage;
                largestHeight = largeImage.getHeight();
            }
        }
        if (bestbuyProductInfo.hasInfo() && bestbuyProductInfo.imageURL != null
                && !bestbuyProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(bestbuyProductInfo.imageURL);

            if (largeImage != null && largestHeight < largeImage.getHeight())
            {
                largestImage  = largeImage;
                largestHeight = largeImage.getHeight();
            }
        }
        if (walmartProductInfo.hasInfo() && walmartProductInfo.imageURL != null
                && !walmartProductInfo.imageURL.equals(""))
        {
            largeImage = loadImage(walmartProductInfo.imageURL);

            if (largeImage != null && largestHeight < largeImage.getHeight())
            {
                largestImage  = largeImage;
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

    /**
     * Sets the product's small image, which is intended to be viewed in the main activity in the product result list.
     */
    void setSmallImage()
    {
        if (!amazonProductInfo.hasInfo() || amazonProductInfo.smallImageURL == null
                || amazonProductInfo.smallImageURL.equals(""))
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

    /**
     * Loads the raw bitmap of the product's image URL.
     *
     * @param imageURL The product's image URL.
     * @return The raw image bitmap.
     */
    private Bitmap loadImage(String imageURL)
    {
        InputStream inputStream;
        Bitmap imageBitmap = null;

        try
        {
            inputStream = new java.net.URL(imageURL).openStream();
            imageBitmap = BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return imageBitmap;
    }

    /**
     * Sets the lowest price, it's associated retailer, and product URL.
     */
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

    /**
     * Sets the product's review statistics.
     */
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

        if (!amazonProductInfo.hasInfo() && !ebayProductInfo.hasInfo() && !walmartProductInfo.hasInfo()
                && bestbuyProductInfo.hasInfo())
        {
            reviewStats = bestbuyProductInfo.getReviewStats();
            hasBasicReviewStats = true;
        }

    }

    /**
     * Sets the product's description.
     */
    void setDescription()
    {
        if (amazonProductInfo.hasInfo() && amazonProductInfo.getDescription() != null
                && !amazonProductInfo.getDescription().equals(""))
        {
            description = amazonProductInfo.getDescription();
        }
        else if (walmartProductInfo.hasInfo() && walmartProductInfo.getDescription() != null
                && !walmartProductInfo.getDescription().equals(""))
        {
            description = walmartProductInfo.getDescription();
        }
        else if (bestbuyProductInfo.hasInfo() && bestbuyProductInfo.getDescription() != null
                && !bestbuyProductInfo.getDescription().equals(""))
        {
            description = bestbuyProductInfo.getDescription();
        }
        else if (ebayProductInfo.hasInfo())
        {
            ebayProductInfo.setDescription();

            description = ebayProductInfo.getDescription();
        }
    }

    String getTitle()
    {
        if (amazonProductInfo.hasInfo)
        {
            return amazonProductInfo.getTitle();
        }
        else if (walmartProductInfo.hasInfo)
        {
            return walmartProductInfo.getTitle();
        }
        else if (bestbuyProductInfo.hasInfo)
        {
            return bestbuyProductInfo.getTitle();
        }
        else if (ebayProductInfo.hasInfo)
        {
            return ebayProductInfo.getTitle();
        }
        else
        {
            return null;
        }
    }

    /**
     * Gives more reviews for a product from every retailer where it is available except for BestBuy.
     * The reviews are shuffled so reviews from different retailers are mixed.
     *
     * @return The products reviews from the first page of a product's reviews from every retailer that carries it.
     */
    ArrayList<Review> getMoreReviews()
    {
        ArrayList<Review> combinedReviews;
        ArrayList<Review> amazonReviews;
        ArrayList<Review> ebayReviews;
        ArrayList<Review> walmartReviews;

        combinedReviews = new ArrayList<>();
        amazonReviews   = amazonProductInfo.getMoreReviews();
        ebayReviews     = ebayProductInfo.getMoreReviews();
        walmartReviews  = walmartProductInfo.getMoreReviews();

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

    /**
     * Determines if there are more reviews for the product.
     *
     * @param deliveredReviews The most recently displayed reviews.
     * @return If the product has more reviews, this returns true.
     */
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

        return (!ebayProductInfo.hasInfo() || amazonReviewsDelivered != 0 || walmartReviewsDelivered != 0
                || ebayReviewsDelivered >= EbayProductInfo.REVIEW_PAGE_MAX_SIZE) && deliveredReviews.size() != 0;
    }

    /**
     * Determines if the product has any information from any retailer.
     *
     * @return If the product has any information, this returns true.
     */
    boolean hasInfo()
    {
        return amazonProductInfo.hasInfo() || ebayProductInfo.hasInfo() || bestbuyProductInfo.hasInfo()
                || walmartProductInfo.hasInfo();
    }

    /**
     * Determines if the product has only basic review statistics, this only occurs if BestBuy is the only retailer in
     * which the product is available.
     *
     * @return If the product has basic review statistics, this returns true.
     */
    boolean hasBasicReviewStats()
    {
        return hasBasicReviewStats;
    }
}
