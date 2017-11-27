package com.tfl.billing.helper;

import com.oyster.ScanListener;
import com.tfl.billing.JourneyEvent;

import java.util.List;

/**
 * Created by marcin on 26.11.17.
 */
public interface ICardHelper extends ScanListener {
    List<JourneyEvent> getEvents();
}
