/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import java.util.HashMap;
import java.util.Map;


/**
 * The StarRating enum is used to represent the star ratings that
 * are used to evaluate the quality of reviews by customers.
 *
 * @author Brendon Guss
 * @since  01/11/2018
 */
enum StarRating
{
    ONE_STAR(1), TWO_STAR(2), THREE_STAR(3), FOUR_STAR(4), FIVE_STAR(5);

    private Integer                         value;
    private static Map<Integer, StarRating> valueMap = new HashMap<>();

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

    /**
     * Given an Integer, the StarRating representation is returned.
     *
     * @param starRating The Integer to be converted to StarRating.
     * @return The StarRating value.
     */
    static StarRating valueOf(Integer starRating)
    {
        return valueMap.get(starRating);
    }

    Integer getValue()
    {
        return value;
    }
}
