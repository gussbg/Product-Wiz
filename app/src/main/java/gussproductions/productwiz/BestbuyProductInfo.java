/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;


import java.io.IOException;
import java.math.BigDecimal;


/*
 * Jsoup is used for parsing Product information from the BestBuy Open API
 * responses.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/**
 * The BestbuyProductInfo class encapsulates BestBuy product information, reviews, and review
 * statistics. The product information is set within the constructor.
 *
 * @author Brendon Guss
 * @since  01/09/2018
 */
class BestbuyProductInfo extends ProductInfo
{
    BestbuyProductInfo(String upc)
    {
        BestbuyRequestHelper bestbuyRequestHelper = new BestbuyRequestHelper(upc);
        String               requestURL           = bestbuyRequestHelper.getRequestURL();

        try
        {
            Document productResultPage = Jsoup.connect(requestURL).userAgent("Mozilla").followRedirects(true).referrer("http://www.google.com")
                    .ignoreHttpErrors(true).ignoreContentType(true).timeout(0).get();
            int      numResults        = Integer.parseInt(productResultPage.getElementsByTag("products")
                                                                           .attr("total"));

            // Ensures that only one product is parsed, it is not expected to have more than one result however.
            if (numResults == 1)
            {
                Element unparsedProduct       = productResultPage.getElementsByTag("product")
                                                                 .first();
                String  unparsedNumReviews    = unparsedProduct.getElementsByTag("customerReviewCount").text();
                String  unparsedAverageRating = unparsedProduct.getElementsByTag("customerReviewAverage").text();

                Integer numReviews;
                Double  averageStarRating;

                price             = new BigDecimal(unparsedProduct.getElementsByTag("salePrice")
                                                                  .text().replaceAll(",", ""));
                System.out.println("Price:                      " + price.toString());
                itemID            = unparsedProduct.getElementsByTag("sku").text();
                productURL        = unparsedProduct.getElementsByTag("url").text();
                title             = unparsedProduct.getElementsByTag("name").text();
                imageURL          = unparsedProduct.getElementsByTag("image").text();
                description       = unparsedProduct.getElementsByTag("longDescription").text();

                if (!unparsedNumReviews.equals("") && !unparsedAverageRating.equals(""))
                {
                    numReviews        = Integer.parseInt(unparsedNumReviews);
                    averageStarRating = Double.parseDouble(unparsedAverageRating);
                    reviewStats       = new ReviewStats(numReviews, averageStarRating);
                }

                hasInfo = true;
            }
            else
            {
                hasInfo = false;
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
