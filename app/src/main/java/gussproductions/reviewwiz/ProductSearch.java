package gussproductions.reviewwiz;

import java.util.ArrayList;

/**
 * Created by Brendon on 1/8/2018.
 */

public class ProductSearch
{
    private ArrayList<Product> productSet;

    AmazonProductSearch amazonProductSearch;

    public ProductSearch(String searchText)
    {
        amazonProductSearch = new AmazonProductSearch(searchText);

        productSet = amazonProductSearch.getProductSet();

        for (Product product : productSet)
        {
            WalmartProductInfo walmartProductInfo = new WalmartProductInfo(product.getCommonProductInfo().getUPC());
            product.setWalmartProductInfo(walmartProductInfo);
        }
    }
}
