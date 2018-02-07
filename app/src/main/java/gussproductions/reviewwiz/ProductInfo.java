/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.reviewwiz;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * This class encapsulates product information that is common to all
 * retailers. It contains all of the variables and methods that subclass
 * implementations will require.
 *
 * @author Brendon Guss
 * @since  01/27/2018
 */
abstract class ProductInfo
{
    String            itemID;
    BigDecimal        price;
    String            title;
    String            description;
    String            productURL;
    String            imageURL;
    ReviewStats       reviewStats;
    ArrayList<Review> reviews;
    String            curReviewURL;
    int               curReviewPageNum;
    boolean           hasInfo;

    final int DEFAULT_HELPFUL_NUM   = 0;
    final int DEFAULT_UNHELPFUL_NUM = 0;

    abstract void parseNextReviewPage();

    // Getter Methods.
    String            getItemID()      { return itemID;      }
    BigDecimal        getPrice()       { return price;       }
    String            getTitle()       { return title;       }
    String            getDescription() { return description; }
    ReviewStats       getReviewStats() { return reviewStats; }
    ArrayList<Review> getReviews()     { return reviews;     }
    String            getProductURL()  { return productURL;  }
    String            getImageURL()    { return imageURL;    }
    boolean           hasInfo()        { return hasInfo;     }
}
