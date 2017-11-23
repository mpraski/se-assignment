package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.sun.istack.internal.NotNull;
import com.tfl.billing.helper.CardHelper;
import com.tfl.billing.helper.JourneyHelper;
import com.tfl.billing.helper.TotalHelper;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

public class TravelTracker {
    private final CustomerDatabase database;
    private final PaymentsSystem system;
    private final CardHelper cardHelper;

    public TravelTracker() {
        this.database = CustomerDatabase.getInstance();
        this.system = PaymentsSystem.getInstance();

        this.cardHelper = new CardHelper(database);
    }

    public TravelTracker(@NotNull CustomerDatabase database,
                         @NotNull PaymentsSystem system) {
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
