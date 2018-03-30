package gussproductions.productwiz;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * The BookmarkedProduct class is used to store a bookmarked product's basic information
 * for later viewing. It contains a Product member that is used to provide the latest
 * product information upon later viewing the bookmarked product and is intended to be
 * set with the latest information every time the BookmarkedProduct is viewed.
 *
 * @author Brendon Guss
 * @since  03/12/2018
 */
class BookmarkedProduct implements Serializable
{
    private final String upc;
    private final Date dateAdded;
    private final BigDecimal priceAdded;
    private Product product;

    /**
     * Sets the basic information for a BookmarkedProduct
     *
     * @param upc The UPC of the bookmarked product.
     * @param dateAdded The date the product was bookmarked.
     * @param priceAdded The lowest price of the product when it was bookmarked.
     */
    BookmarkedProduct(String upc, Date dateAdded, BigDecimal priceAdded)
    {
        this.upc = upc;
        this.dateAdded = dateAdded;
        this.priceAdded = priceAdded.setScale(2, RoundingMode.DOWN);
    }

    // Getter methods.
    Date       getDateAdded()  { return dateAdded;  }
    BigDecimal getPriceAdded() { return priceAdded; }
    String     getUPC()        { return upc;        }
    Product    getProduct()    { return product;    }

    void setProduct(Product product) { this.product = product; }
}
