package com.tfl.billing.helper;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyType;
import com.tfl.external.Customer;

import java.util.List;

/**
 * Created by marcin on 26.11.17.
 */
public interface IJourneyHelper {
    List<Journey> getJourneys(Customer customer, List<JourneyEvent> events);

    JourneyType getJourneyType(Journey journey);
}
