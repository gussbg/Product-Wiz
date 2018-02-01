package gussproductions.reviewwiz;

import java.util.ArrayList;

/**
 * Created by Brendon on 1/8/2018.
 */

class ProductSearch
{
    private ArrayList<Product> productSet;

    private AmazonProductSearch amazonProductSearch;

    ProductSearch(String searchText)
    {
        amazonProductSearch = new AmazonProductSearch(searchText);

        productSet = amazonProductSearch.parseProducts(10);
    }
}
