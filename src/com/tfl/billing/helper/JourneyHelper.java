package com.tfl.billing.helper;

import com.tfl.billing.*;
import com.tfl.external.Customer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marcin on 20.11.17.
 */
public final class JourneyHelper {

    // Prevent instantiation
    private JourneyHelper() {
    }

    public static List<Journey> getJourneys(Customer customer, List<JourneyEvent> events) {
        List<JourneyEvent> customerJourneyEvents = events.stream()
                .filter(event -> event.cardId().equals(customer.cardId()))
                .collect(Collectors.toList());

        final List<Journey> journeys = new ArrayList<>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }

        return journeys;
    }

    public static JourneyType getJourneyType(Journey journey) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(journey.startTime());
        int hourStart = calendar.get(Calendar.HOUR_OF_DAY);

        calendar.setTime(journey.endTime());
        int hourEnd = calendar.get(Calendar.HOUR_OF_DAY);

        if ((hourStart >= 6 && hourStart <= 9)
                || (hourStart >= 17 && hourStart <= 19)
                || (hourEnd >= 6 && hourEnd <= 9)
                || (hourEnd >= 17 && hourEnd <= 19)) {

            if (journey.durationMinutes() > 25) {
                return JourneyType.LONG_PEAK;
            } else {
                return JourneyType.SHORT_PEAK;
            }
        } else {
            if (journey.durationMinutes() > 25) {
                return JourneyType.LONG_OFF_PEAK;
            } else {
                return JourneyType.SHORT_OFF_PEAK;
            }
        }
    }
}
