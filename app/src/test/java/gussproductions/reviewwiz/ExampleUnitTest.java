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

        System.out.println(amazonRequestHelper.getRequestURL());
    }

    @Test
    public void test_amazon_info()
    {
        AmazonProductInfo amazonProductInfo = new AmazonProductInfo("035000521019");

        System.out.println(amazonProductInfo.title);
    }

    @Test
    public void test_walmart_request()
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper("035000521019");

        System.out.println(walmartRequestHelper.getRequestURL());
    }

    @Test
    public void test_walmart_info()
    {
        WalmartProductInfo walmartProductInfo = new WalmartProductInfo("035000521019");
        walmartProductInfo.parseReviewPage();
    }

    @Test
    public void test_bestbuy_request()
    {
        BestbuyProductInfo bestbuyProductInfo = new BestbuyProductInfo("811571016587");

        System.out.println(bestbuyProductInfo.getTitle());
        System.out.println(bestbuyProductInfo.getPrice());
        System.out.println(bestbuyProductInfo.getProductURL());
        System.out.println(bestbuyProductInfo.getImageURL());
        //System.out.println(bestbuyProductInfo.getSku());
        System.out.println(bestbuyProductInfo.getDescription());

        //bestbuyProductInfo.parseReviewPage();
    }

    @Test
    public void test_product_search()
    {
        ProductSearch productSearch = new ProductSearch("iphone");
    }

    @Test
    public void test_ebay_request()
    {


        EbayProductInfo ebayProductInfo = new EbayProductInfo("811571016587");

        //System.out.println(ebayProductInfo.getDescription());

        //ebayProductInfo.setReviewStats();
        //ebayProductInfo.setReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();
        ebayProductInfo.parseReviewPage();

        //System.out.println();

        //EbayRequestHelper ebayRequestHelper = new EbayRequestHelper("811571016587", EbayRequestMode.FIND_ITEMS_BY_PRODUCT);

       // System.out.println("Ebay request: " + ebayRequestHelper.getRequestURL());
    }
}