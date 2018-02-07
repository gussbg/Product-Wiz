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
        AmazonProductSearch amazonProductSearch = new AmazonProductSearch("dvd");

        ArrayList<Product> productSet = amazonProductSearch.parseProducts(100);

        for (Product product : productSet)
        {
            product.getWalmartProductInfo().getReviewStats();
            product.getWalmartProductInfo().parseNextReviewPage();
            product.getWalmartProductInfo().parseNextReviewPage();

            product.getEbayProductInfo().setDescription();
            product.getEbayProductInfo().setReviewStats();
            product.getEbayProductInfo().parseNextReviewPage();
            product.getEbayProductInfo().parseNextReviewPage();

            product.getAmazonProductInfo().setReviewStats();
            product.getAmazonProductInfo().parseNextReviewPage();
            product.getAmazonProductInfo().parseNextReviewPage();

            product.getBestbuyProductInfo().getReviewStats();
            product.getBestbuyProductInfo().parseNextReviewPage();
            product.getBestbuyProductInfo().parseNextReviewPage();
        }
    }

    @Test
    public void test_ebay_request()
    {
        EbayRequestHelper ebayRequestHelper = new EbayRequestHelper("811571016587", EbayRequestMode.FIND_ITEMS_BY_PRODUCT);
    }
}