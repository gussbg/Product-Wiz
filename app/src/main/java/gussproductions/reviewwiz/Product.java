package gussproductions.reviewwiz;

public class Product
{
    private CommonProductInfo commonProductInfo;
    private AmazonProductInfo amazonProductInfo;
    private WalmartProductInfo walmartProductInfo;
    private ReviewStats reviewStats;

    public Product(CommonProductInfo commonProductInfo)
    {
        this.commonProductInfo = commonProductInfo;
    }

    public void setAmazonProductInfo(AmazonProductInfo amazonProductInfo)
    {
        this.amazonProductInfo = amazonProductInfo;
    }

    public void setWalmartProductInfo(WalmartProductInfo walmartProductInfo)
    {
        this.walmartProductInfo = walmartProductInfo;
    }
    public void setReviewStats(AmazonProductSearch amazonProductSearch)
    {
        reviewStats = amazonProductInfo.updateReviewStats(amazonProductSearch);
    }

    public ReviewStats getReviewStats()
    {
        return reviewStats;
    }

    public CommonProductInfo getCommonProductInfo()
    {
        return commonProductInfo;
    }

    public AmazonProductInfo getAmazonProductInfo()
    {
        return amazonProductInfo;
    }

    public WalmartProductInfo getWalmartProductInfo()
    {
        return walmartProductInfo;
    }
}
