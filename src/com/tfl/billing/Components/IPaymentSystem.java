package com.tfl.billing.Components;

import com.tfl.billing.Journey;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by marcin on 24.11.17.
 */
public interface IPaymentSystem {
    void charge(Customer customer, List<Journey> journeys, BigDecimal total);
}
