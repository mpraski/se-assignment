package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.sun.istack.internal.NotNull;
import com.tfl.billing.Components.DefaultCustomerDatabase;
import com.tfl.billing.Components.DefaultPaymentSystem;
import com.tfl.billing.Components.ICustomerDatabase;
import com.tfl.billing.Components.IPaymentSystem;
import com.tfl.billing.helper.CardHelper;
import com.tfl.billing.helper.JourneyHelper;
import com.tfl.billing.helper.TotalHelper;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public class TravelTracker {
    private final ICustomerDatabase database;
    private final IPaymentSystem system;
    private final CardHelper cardHelper;

    public TravelTracker() {
        this.database = new DefaultCustomerDatabase();
        this.system = new DefaultPaymentSystem();

        this.cardHelper = new CardHelper(database);
    }

    public TravelTracker(@NotNull ICustomerDatabase database,
                         @NotNull IPaymentSystem system) {
        this.database = database;
        this.system = system;

        this.cardHelper = new CardHelper(database);
    }

    public void chargeAccounts() {
        database.getCustomers().forEach(this::chargeCustomer);
    }

    private void chargeCustomer(Customer customer) {
        List<JourneyEvent> events = cardHelper.getEvents();
        List<Journey> journeys = JourneyHelper.getJourneys(customer, events);
        BigDecimal total = TotalHelper.getTotal(journeys);

        system.charge(customer, journeys, total);
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(cardHelper);
        }
    }
}
