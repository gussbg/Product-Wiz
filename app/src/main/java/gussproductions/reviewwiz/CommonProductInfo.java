package gussproductions.reviewwiz;


/**
 * Created by Brendon on 1/8/2018.
 */

public class CommonProductInfo
{
    private String isbn;
    private String upc;
    private String ean;
    private String title;
    private String description;

    public CommonProductInfo(String isbn, String upc, String ean, String title, String description)
    {
        this.isbn = isbn;
        this.upc = upc;
        this.ean = ean;
        this.title = title;
        this.description = description;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public String getUPC()
    {
        return upc;
    }

    public String getEAN()
    {
        return ean;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }
}
