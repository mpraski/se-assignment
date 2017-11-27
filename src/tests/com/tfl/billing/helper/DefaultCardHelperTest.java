package tests.com.tfl.billing.helper;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import com.tfl.billing.UnknownOysterCardException;
import com.tfl.billing.components.ICustomerDatabase;
import com.tfl.billing.helper.DefaultCardHelper;
import com.tfl.billing.helper.ICardHelper;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Assert;
import org.junit.Test;
import tests.com.tfl.billing.util.TestUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marcin on 20.11.17.
 */
public class DefaultCardHelperTest {

    private final static int CUSTOMER_NUMBER = 50;
    private final static int RANDOM_SAMPLE = 10;
    private final static int EVENT_NUMBER = 100;
    private final static Customer NONEXISTENT_CUSTOMER = new Customer("Chris Squire", new OysterCard("00000000-0000-0000-0000-000000000000"));
    private final static OysterCardReader READER_START = OysterReaderLocator.atStation(Station.EUSTON);
    private final static OysterCardReader READER_END = OysterReaderLocator.atStation(Station.GOODGE_STREET);

    @Test
    public void cardHelperRecordsCorrectNumberEvents() {
        ICustomerDatabase database = TestUtils.mockCustomerDatabase(50);

        List<Customer> customers = TestUtils.randomItems(database.getCustomers(), RANDOM_SAMPLE);

        ICardHelper cardHelper = new DefaultCardHelper(database);

        for (Customer customer : customers) {
            cardHelper.cardScanned(customer.cardId(), READER_START.id());
            cardHelper.cardScanned(customer.cardId(), READER_END.id());
        }

        Assert.assertEquals("Number of recorded events", cardHelper.getEvents().size() / 2, RANDOM_SAMPLE);
    }

    @Test
    public void cardHelperRecordsEventsInCorrectOrder() {
        ICustomerDatabase database = TestUtils.mockCustomerDatabase(CUSTOMER_NUMBER);

        List<Customer> customers = TestUtils.randomItems(database.getCustomers(), RANDOM_SAMPLE);

        ICardHelper cardHelper = new DefaultCardHelper(database);

        for (int i = 0; i < EVENT_NUMBER; i++) {
            Customer customer = TestUtils.randomItem(customers);
            cardHelper.cardScanned(customer.cardId(), READER_START.id());
        }

        Customer customer = TestUtils.randomItem(customers);

        List<JourneyEvent> events = cardHelper.getEvents().stream()
                .filter(event -> event.cardId().equals(customer.cardId()))
                .collect(Collectors.toList());

        JourneyEvent last = null;
        for (JourneyEvent event : events) {
            if (last == null) {
                last = event;
                continue;
            }

            if (event instanceof JourneyStart && last instanceof JourneyStart
                    || event instanceof JourneyEnd && last instanceof JourneyEnd) {
                Assert.fail("Events are not in correct order");
            } else {
                last = event;
            }
        }
    }

    @Test
    public void cardHelperRecordsCorrectEvents() {
        ICustomerDatabase database = TestUtils.mockCustomerDatabase(CUSTOMER_NUMBER);

        List<Customer> customers = TestUtils.randomItems(database.getCustomers(), RANDOM_SAMPLE);

        Set<UUID> customerIds = customers.stream()
                .map(customer -> customer.cardId())
                .collect(Collectors.toSet());

        Set<UUID> readerIds = new HashSet<>(Arrays.asList(
                READER_START.id(),
                READER_END.id()
        ));

        DefaultCardHelper cardHelper = new DefaultCardHelper(database);

        for (Customer customer : customers) {
            cardHelper.cardScanned(customer.cardId(), READER_START.id());
            cardHelper.cardScanned(customer.cardId(), READER_END.id());
        }

        Set<UUID> scannedCustomerIds = cardHelper.getEvents()
                .stream()
                .map((journeyEvent -> journeyEvent.cardId()))
                .collect(Collectors.toSet());

        Set<UUID> scannedReaderIds = cardHelper.getEvents()
                .stream()
                .map((journeyEvent -> journeyEvent.readerId()))
                .collect(Collectors.toSet());

        Assert.assertEquals("Scanned customer IDs", customerIds, scannedCustomerIds);
        Assert.assertEquals("Scanned reader IDs", readerIds, scannedReaderIds);
    }


    @Test(expected = UnknownOysterCardException.class)
    public void throwsExceptionWhenOrderOfEventsIsIncorrect() {
        ICustomerDatabase database = TestUtils.mockCustomerDatabase(CUSTOMER_NUMBER);

        DefaultCardHelper cardHelper = new DefaultCardHelper(database);

        cardHelper.cardScanned(NONEXISTENT_CUSTOMER.cardId(), READER_START.id());
    }
}
