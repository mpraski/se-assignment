package com.tfl.billing.helper;

import com.oyster.ScanListener;
import com.tfl.billing.Components.ICustomerDatabase;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import com.tfl.billing.UnknownOysterCardException;

import java.util.*;

/**
 * Created by marcin on 20.11.17.
 */
public final class CardHelper implements ScanListener {

    private final List<JourneyEvent> events = new ArrayList<>();
    private final Set<UUID> travellers = new HashSet<>();
    private final ICustomerDatabase database;

    public CardHelper(ICustomerDatabase database) {
        this.database = database;
    }

    @Override
    public void cardScanned(UUID customer, UUID scanner) {
        if (travellers.contains(customer)) {
            events.add(new JourneyEnd(customer, scanner));
            travellers.remove(customer);
        } else if (database.isRegisteredId(customer)) {
            travellers.add(customer);
            events.add(new JourneyStart(customer, scanner));
        } else {
            throw new UnknownOysterCardException(customer);
        }
    }

    public List<JourneyEvent> getEvents() {
        return events;
    }
}
