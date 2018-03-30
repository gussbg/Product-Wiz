/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class encapsulates product information that is common to all
 * retailers. It contains all of the variables and methods that subclass
 * implementations will require.
 *
 * @author Brendon Guss
 * @since  01/27/2018
 */
abstract class ProductInfo implements Serializable
{
    String            itemID;
    BigDecimal        price;
    String            title;
    String            description;
    String            productURL;
    String            imageURL;
    ReviewStats       reviewStats;
    String            curReviewURL;
    int               curReviewPageNum;
    boolean           hasInfo;

    final int DEFAULT_HELPFUL_NUM   = 0;
    final int DEFAULT_UNHELPFUL_NUM = 0;

    // Getter Methods.
    String            getItemID()      { return itemID;      }
    BigDecimal        getPrice()       { return price;       }
    String            getTitle()       { return title;       }
    String            getDescription() { return description; }
    ReviewStats       getReviewStats() { return reviewStats; }
    String            getProductURL()  { return productURL;  }
    String            getImageURL()    { return imageURL;    }
    boolean           hasInfo()        { return hasInfo;     }
}
