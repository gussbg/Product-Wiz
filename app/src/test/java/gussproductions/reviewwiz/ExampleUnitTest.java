package gussproductions.reviewwiz;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void test_amazon_search()
    {
        AmazonProductSearch amazonProductSearch = new AmazonProductSearch("iphone");

    }

    @Test
    public void test_amazon_request()
    {
        AmazonRequestHelper amazonRequestHelper = null;

        try
        {
            amazonRequestHelper = new AmazonRequestHelper("035000521019", AmazonRequestMode.ITEM_LOOKUP_INFO);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_amazon_info()
    {
        AmazonProductInfo amazonProductInfo = new AmazonProductInfo("035000521019");
    }

    @Test
    public void test_walmart_request()
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper("035000521019");
    }

    @Test
    public void test_walmart_info()
    {
        WalmartProductInfo walmartProductInfo = new WalmartProductInfo("035000521019");
    }

    @Test
    public void test_bestbuy_request()
    {
        BestbuyProductInfo bestbuyProductInfo = new BestbuyProductInfo("811571016587");
    }

    @Test
    public void test_product_search()
    {
        ProductSearch productSearch = new ProductSearch("phone");

        ArrayList<Product> productSet = productSearch.getProductSet();

        for (Product product : productSet)
        {
            product.getWalmartProductInfo().getReviewStats();
            product.getWalmartProductInfo().parseReviewPage();
            product.getWalmartProductInfo().parseReviewPage();

            product.getEbayProductInfo().getDescription();
            product.getEbayProductInfo().setReviewStats();
            product.getEbayProductInfo().parseReviewPage();
            product.getEbayProductInfo().parseReviewPage();

            product.getAmazonProductInfo().setReviewStats();
            product.getAmazonProductInfo().parseReviewPage();
            product.getAmazonProductInfo().parseReviewPage();

            product.getBestbuyProductInfo().getReviewStats();
            product.getBestbuyProductInfo().parseReviewPage();
            product.getBestbuyProductInfo().parseReviewPage();
        }
    }

    @Test
    public void test_ebay_request()
    {
        EbayRequestHelper ebayRequestHelper = new EbayRequestHelper("811571016587", EbayRequestMode.FIND_ITEMS_BY_PRODUCT);
    }
}