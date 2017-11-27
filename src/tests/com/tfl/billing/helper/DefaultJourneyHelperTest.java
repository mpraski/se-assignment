package tests.com.tfl.billing.helper;

import com.oyster.OysterCard;
import com.tfl.billing.*;
import com.tfl.billing.helper.DefaultJourneyHelper;
import com.tfl.billing.helper.IJourneyHelper;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Assert;
import org.junit.Test;
import tests.com.tfl.billing.util.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marcin on 20.11.17.
 */
public class DefaultJourneyHelperTest {

    private final static UUID READER_START = OysterReaderLocator.atStation(Station.EUSTON).id();
    private final static UUID READER_END = OysterReaderLocator.atStation(Station.GOODGE_STREET).id();
    private final static Customer DUMMY_CUSTOMER = new Customer("Jimmy Page", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));

    private final static int JOURNEY_NUMBER = 4;

    @Test
    public void isCorrectJourneyTypeReturned() {
        IJourneyHelper journeyHelper = new DefaultJourneyHelper();

        Journey peak_long = TestUtils.mockJourney(DUMMY_CUSTOMER.cardId(), READER_START, READER_END, 7, 0, 30);
        Journey peak_short = TestUtils.mockJourney(DUMMY_CUSTOMER.cardId(), READER_START, READER_END, 7, 0, 15);
        Journey off_peak_long = TestUtils.mockJourney(DUMMY_CUSTOMER.cardId(), READER_START, READER_END, 13, 0, 30);
        Journey off_peak_short = TestUtils.mockJourney(DUMMY_CUSTOMER.cardId(), READER_START, READER_END, 13, 0, 15);

        Assert.assertEquals("Properly recognized as peak long", journeyHelper.getJourneyType(peak_long), JourneyType.LONG_PEAK);
        Assert.assertEquals("Properly recognized as peak short", journeyHelper.getJourneyType(peak_short), JourneyType.SHORT_PEAK);
        Assert.assertEquals("Properly recognized as off peak long", journeyHelper.getJourneyType(off_peak_long), JourneyType.LONG_OFF_PEAK);
        Assert.assertEquals("Properly recognized as off peak short", journeyHelper.getJourneyType(off_peak_short), JourneyType.SHORT_OFF_PEAK);
    }

    @Test
    public void isCorrectNumberOfJourneysCalculated() {
        IJourneyHelper journeyHelper = new DefaultJourneyHelper();

        List<JourneyEvent> events = new ArrayList<>();

        for (int i = 0; i < JOURNEY_NUMBER; i++) {
            events.add(new JourneyStart(DUMMY_CUSTOMER.cardId(), READER_START));
            events.add(new JourneyEnd(DUMMY_CUSTOMER.cardId(), READER_END));
        }

        // Check, if journeys are properly constructed from a series of start and end events
        Assert.assertEquals("Number of journeys is correct", journeyHelper.getJourneys(DUMMY_CUSTOMER, events).size(), JOURNEY_NUMBER);
    }
}
