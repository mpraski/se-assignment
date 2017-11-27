package com.tfl.billing.components;

import com.tfl.billing.Journey;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by marcin on 24.11.17.
 */
public class DefaultPaymentSystem implements IPaymentSystem {
    private final PaymentsSystem system;

    public DefaultPaymentSystem() {
        this.system = PaymentsSystem.getInstance();
    }

    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal total) {
        system.charge(customer, journeys, total);
    }
}
