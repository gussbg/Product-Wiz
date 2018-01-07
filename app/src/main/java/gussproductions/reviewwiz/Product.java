package gussproductions.reviewwiz;

import java.math.BigDecimal;

public class Product {
    String isbn;
    String asin;
    String upc;
    String title;
    String imageURL;
    BigDecimal amazonPrice;
    String description;
    ReviewStats reviewStats;

    public Product(String isbn, String asin, String upc, String title, String imageURL, String description, BigDecimal amazonPrice, ReviewStats reviewStats)
    {
        this.isbn = isbn;
        this.asin = asin;
        this.upc = upc;
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.amazonPrice = amazonPrice;
        this.reviewStats = reviewStats;
    }
}
