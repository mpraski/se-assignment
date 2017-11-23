package com.tfl.billing.helper;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyConstants;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by marcin on 20.11.17.
 */
public final class TotalHelper {

    // Prevent instantiation
    private TotalHelper() {
    }

    public static BigDecimal getTotal(List<Journey> journeys) {
        BigDecimal total = new BigDecimal(0);
        boolean isPeak = false;

        for (Journey journey : journeys) {
            BigDecimal journeyPrice = null;

            switch (JourneyHelper.getJourneyType(journey)) {
                case LONG_PEAK:
                    journeyPrice = JourneyConstants.LONG_PEAK_PRICE;
                    isPeak = true;
                    break;
                case LONG_OFF_PEAK:
                    journeyPrice = JourneyConstants.LONG_OFF_PEAK_PRICE;
                    break;
                case SHORT_PEAK:
                    journeyPrice = JourneyConstants.SHORT_PEAK_PRICE;
                    isPeak = true;
                    break;
                case SHORT_OFF_PEAK:
                    journeyPrice = JourneyConstants.SHORT_OFF_PEAK_PRICE;
                    break;
            }

            total = total.add(journeyPrice);
        }

        total = applyCap(total, isPeak);
        total = roundToNearestPenny(total);

        return total;
    }

    public static BigDecimal applyCap(BigDecimal total, boolean isPeak) {
        return isPeak ? total.min(JourneyConstants.PEAK_CAP) : total.min(JourneyConstants.OFF_PEAK_CAP);
    }

    public static BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
