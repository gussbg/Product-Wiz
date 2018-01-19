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

        ArrayList<Product> products = amazonProductSearch.getProductSet();

        for (Product product : products)
        {
            product.setReviewStats(amazonProductSearch);
            System.out.println(product.getReviewStats().getTotalStars());
            //System.out.println(product.getAmazonProductInfo().getProductURL());
            break;
        }
    }

    @Test
    public void test_walmart_request()
    {
        WalmartProductInfo walmartProductInfo = new WalmartProductInfo("035000521019");

        //System.out.println(walmartRequestHelper.getRequestURL());
    }

    @Test
    public void test_bestbuy_request()
    {
        BestbuyProductInfo bestbuyProductInfo = new BestbuyProductInfo("811571016518");

        System.out.println(bestbuyProductInfo.getTitle());
        System.out.println(bestbuyProductInfo.getPrice());
        System.out.println(bestbuyProductInfo.getProductURL());
        System.out.println(bestbuyProductInfo.getImageURL());
        System.out.println(bestbuyProductInfo.getSku());
        System.out.println(bestbuyProductInfo.getDescription());

        bestbuyProductInfo.parseReviewPage();
    }
}