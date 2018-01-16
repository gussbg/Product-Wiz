package gussproductions.reviewwiz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Brendon on 1/8/2018.
 */

public class WalmartProductInfo
{
    private BigDecimal price;
    private String itemID;
    private String productURL;

    public WalmartProductInfo(String upc)
    {
        generateInfo(upc);
    }

    private void generateInfo(String upc)
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper(upc);

        String requestURL = walmartRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla/5.0").ignoreHttpErrors(true).ignoreContentType(true).get();
            Element unparsedProduct = productResultPage.select("item").first();

            price = new BigDecimal(unparsedProduct.getElementsByTag("salePrice").text().replaceAll(",", ""));
            itemID = unparsedProduct.getElementsByTag("itemId").text();
            productURL = unparsedProduct.getElementsByTag("productUrl").text();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getItemID()
    {
        return itemID;
    }

    public String getProductURL()
    {
        return productURL;
    }
}
