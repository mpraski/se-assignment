package com.tfl.billing.helper;

import com.tfl.billing.Journey;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by marcin on 26.11.17.
 */
public interface ITotalHelper {
    BigDecimal getTotal(List<Journey> journeys);
}
