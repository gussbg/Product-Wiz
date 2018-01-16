package gussproductions.reviewwiz;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

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
            product.getAmazonProductInfo().setReviewStats(amazonProductSearch);
            break;
        }
    }

    @Test
    public void test_walmart_request()
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper("035000521019");

        System.out.println(walmartRequestHelper.getRequestURL());
    }
}