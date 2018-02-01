package gussproductions.reviewwiz;

class Product
{
    private String             upc;
    private AmazonProductInfo  amazonProductInfo;
    private WalmartProductInfo walmartProductInfo;
    private BestbuyProductInfo bestbuyProductInfo;
    private EbayProductInfo    ebayProductInfo;

    Product(AmazonProductInfo amazonProductInfo, String upc)
    {
        this.upc = upc;

        this.amazonProductInfo = amazonProductInfo;
        this.walmartProductInfo = new WalmartProductInfo(upc);
        this.bestbuyProductInfo = new BestbuyProductInfo(upc);
        this.ebayProductInfo    = new EbayProductInfo(upc);

    }

    String             getUPC()                { return upc; }
    AmazonProductInfo  getAmazonProductInfo()  { return amazonProductInfo; }
    WalmartProductInfo getWalmartProductInfo()
    {
        return walmartProductInfo;
    }
    BestbuyProductInfo getBestbuyProductInfo() { return bestbuyProductInfo; }
    EbayProductInfo    getEbayProductInfo()    { return ebayProductInfo; }

}
