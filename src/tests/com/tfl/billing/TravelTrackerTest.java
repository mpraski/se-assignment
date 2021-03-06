package tests.com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.TravelTracker;
import com.tfl.billing.components.DefaultCustomerDatabase;
import com.tfl.billing.components.DefaultPaymentSystem;
import com.tfl.billing.components.ICustomerDatabase;
import com.tfl.billing.components.IPaymentSystem;
import com.tfl.billing.helper.ICardHelper;
import com.tfl.billing.helper.IJourneyHelper;
import com.tfl.billing.helper.ITotalHelper;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import javafx.util.Pair;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static tests.com.tfl.billing.util.IsInListMatcher.oneFrom;

/**
 * Created by marcin on 16.11.17.
 */

public class TravelTrackerTest {

    private final static OysterCardReader READER_ONE = OysterReaderLocator.atStation(Station.EUSTON);
    private final static OysterCardReader READER_TWO = OysterReaderLocator.atStation(Station.GOODGE_STREET);
    private final static OysterCard DUMMY_OYSTER = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private final static Customer DUMMY_CUSTOMER_1 = new Customer("Jimmy Page", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_2 = new Customer("Jimmy Page", new OysterCard("48400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_3 = new Customer("Jimmy Page", new OysterCard("58400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_4 = new Customer("Jimmy Page", new OysterCard("68400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_5 = new Customer("Jimmy Page", new OysterCard("78400000-8cf0-11bd-b23e-10b96e4ef00d"));
    Mockery context = new Mockery();

    @Test
    public void travelTrackerCorrectlyUsesDatabaseAndBillingSystem() {
        ICustomerDatabase database = context.mock(ICustomerDatabase.class);
        IPaymentSystem system = context.mock(IPaymentSystem.class);

        final List<Customer> customers = new ArrayList<>(Arrays.asList(
                DUMMY_CUSTOMER_1,
                DUMMY_CUSTOMER_2,
                DUMMY_CUSTOMER_3,
                DUMMY_CUSTOMER_4,
                DUMMY_CUSTOMER_5
        ));
        final int size = customers.size();

        context.checking(new Expectations() {{
            oneOf(database).getCustomers();
            will(returnValue(customers));

            exactly(1).of(database).getCustomers();
            exactly(size).of(system).charge(with(oneFrom(customers)), with(aNonNull(List.class)), with(aNonNull(BigDecimal.class)));
        }});

        TravelTracker travelTracker = new TravelTracker(database, system);
        travelTracker.chargeAccounts();
    }

    @Test
    public void travelTrackerCorrectlyUsesHelpers() {
        ICustomerDatabase database = context.mock(ICustomerDatabase.class);
        IPaymentSystem system = context.mock(IPaymentSystem.class);

        ICardHelper cardHelper = context.mock(ICardHelper.class);
        IJourneyHelper journeyHelper = context.mock(IJourneyHelper.class);
        ITotalHelper totalHelper = context.mock(ITotalHelper.class);

        final List<Customer> customers = new ArrayList<>(Arrays.asList(
                DUMMY_CUSTOMER_1,
                DUMMY_CUSTOMER_2,
                DUMMY_CUSTOMER_3,
                DUMMY_CUSTOMER_4,
                DUMMY_CUSTOMER_5
        ));
        final int size = customers.size();

        final List<JourneyEvent> emptyEventList = Collections.emptyList();
        final List<Journey> emptyJourneyList = Collections.emptyList();

        final BigDecimal zero = new BigDecimal("0.00");

        context.checking(new Expectations() {{
            oneOf(database).getCustomers();
            will(returnValue(customers));

            exactly(size).of(cardHelper).getEvents();
            will(returnValue(emptyEventList));

            exactly(size).of(journeyHelper).getJourneys(with(oneFrom(customers)), with(emptyEventList));
            will(returnValue(emptyJourneyList));

            exactly(size).of(totalHelper).getTotal(emptyJourneyList);
            will(returnValue(zero));

            exactly(1).of(database).getCustomers();

            exactly(size).of(system).charge(with(oneFrom(customers)), with(emptyJourneyList), with(zero));
        }});

        TravelTracker travelTracker = new TravelTracker(database, system, cardHelper, journeyHelper, totalHelper);
        travelTracker.chargeAccounts();
    }

    @Test
    public void travelTrackerRegistersItselfWithCardReaders() {
        ICustomerDatabase database = context.mock(ICustomerDatabase.class);
        IPaymentSystem system = context.mock(IPaymentSystem.class);

        ICardHelper cardHelper = context.mock(ICardHelper.class);
        IJourneyHelper journeyHelper = context.mock(IJourneyHelper.class);
        ITotalHelper totalHelper = context.mock(ITotalHelper.class);

        context.checking(new Expectations() {{
            allowing(database).isRegisteredId(DUMMY_OYSTER.id());
            will(returnValue(true));

            ignoring(system);

            ignoring(journeyHelper);
            ignoring(totalHelper);

            exactly(1).of(cardHelper).cardScanned(DUMMY_CUSTOMER_1.cardId(), READER_ONE.id());
            exactly(1).of(cardHelper).cardScanned(DUMMY_CUSTOMER_1.cardId(), READER_TWO.id());
        }});

        TravelTracker travelTracker = new TravelTracker(database, system, cardHelper, journeyHelper, totalHelper);
        travelTracker.connect(READER_ONE, READER_TWO);

        READER_ONE.touch(DUMMY_OYSTER);
        READER_TWO.touch(DUMMY_OYSTER);
    }

    // This is probably not an elegant way to write a test, however I wanted to check the output generated by payment system of the legacy library
    @Test
    @Ignore
    public void travelTrackerOutputsCorrectLog() {
        PrintStream stdout = System.out;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stream));

        ICustomerDatabase database = new DefaultCustomerDatabase();
        IPaymentSystem system = new DefaultPaymentSystem();

        List<Pair<String, UUID>> expected = database.getCustomers()
                .stream()
                .map((customer -> new Pair<>(customer.fullName(), customer.cardId())))
                .collect(Collectors.toList());

        TravelTracker travelTracker = new TravelTracker(database, system);
        travelTracker.chargeAccounts();

        System.setOut(stdout);

        List<Pair<String, UUID>> outputted = new ArrayList<>();

        Pattern pattern = Pattern.compile("Customer: (.*?) - (.*?)");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stream.toByteArray())));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (m.matches())
                    outputted.add(new Pair<>(m.group(1), UUID.fromString(m.group(2))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("Every customer in the database was charged", outputted, expected);
    }
}
