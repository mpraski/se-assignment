package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.sun.istack.internal.NotNull;
import com.tfl.billing.components.DefaultCustomerDatabase;
import com.tfl.billing.components.DefaultPaymentSystem;
import com.tfl.billing.components.ICustomerDatabase;
import com.tfl.billing.components.IPaymentSystem;
import com.tfl.billing.helper.*;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public class TravelTracker {
    private final ICustomerDatabase database;
    private final IPaymentSystem system;

    private final ICardHelper cardHelper;
    private final IJourneyHelper journeyHelper;
    private final ITotalHelper totalHelper;

    public TravelTracker() {
        this.database = new DefaultCustomerDatabase();
        this.system = new DefaultPaymentSystem();

        this.cardHelper = new DefaultCardHelper(database);
        this.journeyHelper = new DefaultJourneyHelper();
        this.totalHelper = new DefaultTotalHelper(journeyHelper);
    }

    public TravelTracker(@NotNull ICustomerDatabase database,
                         @NotNull IPaymentSystem system) {
        this.database = database;
        this.system = system;

        this.cardHelper = new DefaultCardHelper(database);
        this.journeyHelper = new DefaultJourneyHelper();
        this.totalHelper = new DefaultTotalHelper(journeyHelper);
    }

    public TravelTracker(@NotNull ICustomerDatabase database,
                         @NotNull IPaymentSystem system,
                         @NotNull ICardHelper cardHelper,
                         @NotNull IJourneyHelper journeyHelper,
                         @NotNull ITotalHelper totalHelper) {
        this.database = database;
        this.system = system;

        this.cardHelper = cardHelper;
        this.journeyHelper = journeyHelper;
        this.totalHelper = totalHelper;
    }

    public void chargeAccounts() {
        database.getCustomers().forEach(this::chargeCustomer);
    }

    private void chargeCustomer(Customer customer) {
        List<JourneyEvent> events = cardHelper.getEvents();
        List<Journey> journeys = journeyHelper.getJourneys(customer, events);
        BigDecimal total = totalHelper.getTotal(journeys);

        system.charge(customer, journeys, total);
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(cardHelper);
        }
    }
}
