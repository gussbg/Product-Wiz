package gussproductions.reviewwiz;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brendon on 1/11/2018.
 */

public enum StarRating
{
    ONE_STAR(1), TWO_STAR(2), THREE_STAR(3), FOUR_STAR(4), FIVE_STAR(5);

    private Integer value;
    private static Map valueMap = new HashMap<>();

    StarRating(Integer value)
    {
        this.value = value;
    }

    static
    {
        for (StarRating starRating : StarRating.values())
        {
            valueMap.put(starRating.value, starRating);
        }
    }

    static StarRating valueOf(Integer starRating)
    {
        return (StarRating) valueMap.get(starRating);
    }

    Integer getValue()
    {
        return value;
    }
}
