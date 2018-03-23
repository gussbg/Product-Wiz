package gussproductions.productwiz;

import android.widget.TextView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by Brendon on 3/12/2018.
 */

public class BookmarkedProduct implements Serializable
{
    private final String upc;
    private final Date dateAdded;
    private final BigDecimal priceAdded;
    private Product product;

    BookmarkedProduct(String upc, Date dateAdded, BigDecimal priceAdded)
    {
        this.upc = upc;
        this.dateAdded = dateAdded;
        this.priceAdded = priceAdded.setScale(2, RoundingMode.DOWN);
    }

    public Date getDateAdded()
    {
        return dateAdded;
    }

    public BigDecimal getPriceAdded()
    {
        return priceAdded;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public String getUPC()
    {
        return upc;
    }

    public Product getProduct()
    {
        return product;
    }
}
