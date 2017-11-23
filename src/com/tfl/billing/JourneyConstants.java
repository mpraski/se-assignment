package com.tfl.billing;

import java.math.BigDecimal;

/**
 * Created by marcin on 20.11.17.
 */
public final class JourneyConstants {
    public static final BigDecimal LONG_OFF_PEAK_PRICE = new BigDecimal(2.70);
    public static final BigDecimal LONG_PEAK_PRICE = new BigDecimal(3.80);
    public static final BigDecimal SHORT_OFF_PEAK_PRICE = new BigDecimal(1.60);
    public static final BigDecimal SHORT_PEAK_PRICE = new BigDecimal(2.90);

    public static final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    public static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);
}
