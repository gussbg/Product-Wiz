package gussproductions.productwiz;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Brendon on 3/12/2018.
 */

public class BookmarkedProduct
{
    private final String upc;
    private final Date dateAdded;
    private final BigDecimal priceAdded;

    BookmarkedProduct(String upc, Date dateAdded, BigDecimal priceAdded)
    {
        this.upc = upc;
        this.dateAdded = dateAdded;
        this.priceAdded = priceAdded;
    }

    public String getUPC()
    {
        return upc;
    }

    public Date getDateAdded()
    {
        return dateAdded;
    }

    public BigDecimal getPriceAdded()
    {
        return priceAdded;
    }
}
