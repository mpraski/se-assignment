package com.tfl.billing.helper;

import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import com.tfl.billing.UnknownOysterCardException;
import com.tfl.billing.components.ICustomerDatabase;

import java.util.*;

/**
 * Created by marcin on 20.11.17.
 */
public final class DefaultCardHelper implements ICardHelper {

    private final List<JourneyEvent> events = new ArrayList<>();
    private final Set<UUID> travellers = new HashSet<>();
    private final ICustomerDatabase database;

    public DefaultCardHelper(ICustomerDatabase database) {
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

    @Override
    public List<JourneyEvent> getEvents() {
        return events;
    }
}
