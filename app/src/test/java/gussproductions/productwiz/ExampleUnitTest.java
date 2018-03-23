package gussproductions.productwiz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
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
        AmazonProductSearch amazonProductSearch = new AmazonProductSearch("dvd");

        //amazonProductSearch.getMoreProducts(10);
        amazonProductSearch.getMoreProducts(10);

        ArrayList<Product> products = amazonProductSearch.getMoreProducts(10);

        for (Product product : products)
        {
            System.out.println(product.getAmazonProductInfo().getTitle());
        }

        System.out.println(products.size());
    }

    @Test
    public void test_amazon_request()
    {
        AmazonRequestHelper amazonRequestHelper = null;

        try
        {
            amazonRequestHelper = new AmazonRequestHelper("841887035545", AmazonRequestMode.ITEM_LOOKUP_INFO);
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
    }

    @Test
    public void test_walmart_request()
    {
        WalmartRequestHelper walmartRequestHelper = new WalmartRequestHelper("035000521019");
    }

    @Test
    public void test_walmart_info()
    {
        WalmartProductInfo walmartProductInfo = new WalmartProductInfo("811571016587");

        walmartProductInfo.setReviewStats();

        ArrayList<Review> reviews = walmartProductInfo.getMoreReviews();

        System.out.println(walmartProductInfo.getProductURL());
    }

    @Test
    public void test_walmart_only_product()
    {
        Product product = new Product("811571016587");

        product.setReviewStats();

        ArrayList<Review> reviews = product.getMoreReviews();

        for (Review review : reviews)
        {
            System.out.println(review.getReviewTitle());
        }
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

        ArrayList<Product> productSet = amazonProductSearch.getMoreProducts(100);

        for (Product product : productSet)
        {
            product.getWalmartProductInfo().getReviewStats();
            product.getWalmartProductInfo().getMoreReviews();
            product.getWalmartProductInfo().getMoreReviews();

            product.getEbayProductInfo().setDescription();
            product.getEbayProductInfo().setReviewStats();
            product.getEbayProductInfo().getMoreReviews();
            product.getEbayProductInfo().getMoreReviews();

            product.getAmazonProductInfo().setReviewStats();
            product.getAmazonProductInfo().getMoreReviews();
            product.getAmazonProductInfo().getMoreReviews();

            product.getBestbuyProductInfo().getReviewStats();
        }
    }

    @Test
    public void test_ebay_request()
    {
        EbayRequestHelper ebayRequestHelper = new EbayRequestHelper("811571016587", EbayRequestMode.FIND_ITEMS_BY_PRODUCT);
    }

    @Test
    public void test_product_reviews()
    {
        Product product = new Product("846042007603"); //846042007603

        product.setReviewStats();

        System.out.println(product.getAmazonProductInfo().getTitle());

        ArrayList<Review> reviews = product.getMoreReviews();

        for (Review review : reviews)
        {
            System.out.println(review.getReviewTitle());
        }

        System.out.println(reviews.size());

        System.out.println(product.getBestbuyProductInfo().curReviewURL);
    }

    @Test
    public void test_jsoup() throws IOException {
        URL productURL = new URL("https://api.bestbuy.com/click/-/5878703/pdp");

        Document productPage       = Jsoup.connect("https://api.bestbuy.com/click/-/5878703/pdp").userAgent("Mozilla/5.0")
                .ignoreHttpErrors(true).ignoreContentType(true).get();
    }

    @Test
    public void test_ebay_reviews()
    {
        Product product = new Product("786936856255");

        System.out.println(product.getEbayProductInfo().productURL);

        product.setReviewStats();



        ArrayList<Review> reviews = product.getEbayProductInfo().getMoreReviews();


        for (Review review : reviews)
        {
            System.out.println(review.getReviewTitle());
        }

    }

    @Test
    public void test_review_stats()
    {
        Product product = new Product("024543327868");

        product.setImage(false);
        product.setLowestPriceInfo();

        product.setReviewStats();

        ReviewStats reviewStats = product.getAmazonProductInfo().getReviewStats();

        if (reviewStats == null)
        {
            System.out.println("Null!");
        }

        Integer numOneStars = reviewStats.getNumOneStars();
        Integer numTwoStars = reviewStats.getNumTwoStars();
        Integer numThreeStars = reviewStats.getNumThreeStars();
        Integer numFourStars = reviewStats.getNumFourStars();
        Integer numFiveStars = reviewStats.getNumFiveStars();

        System.out.println(numOneStars);
        System.out.println(numTwoStars);
        System.out.println(numThreeStars);
        System.out.println(numFourStars);
        System.out.println(numFiveStars);

        product.setDescription();

    }
}