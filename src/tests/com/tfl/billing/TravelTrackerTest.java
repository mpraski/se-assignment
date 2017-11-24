package tests.com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.billing.Components.DefaultCustomerDatabase;
import com.tfl.billing.Components.DefaultPaymentSystem;
import com.tfl.billing.Components.ICustomerDatabase;
import com.tfl.billing.Components.IPaymentSystem;
import com.tfl.billing.TravelTracker;
import com.tfl.external.Customer;
import javafx.util.Pair;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static tests.com.tfl.billing.IsInListMatcher.oneFrom;

/**
 * Created by marcin on 16.11.17.
 */

public class TravelTrackerTest {

    private final static Customer DUMMY_CUSTOMER_1 = new Customer("Jimmy Page", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_2 = new Customer("Jimmy Page", new OysterCard("48400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_3 = new Customer("Jimmy Page", new OysterCard("58400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_4 = new Customer("Jimmy Page", new OysterCard("68400000-8cf0-11bd-b23e-10b96e4ef00d"));
    private final static Customer DUMMY_CUSTOMER_5 = new Customer("Jimmy Page", new OysterCard("78400000-8cf0-11bd-b23e-10b96e4ef00d"));
    Mockery context = new Mockery();

    @Test
    public void travelTrackerPerformsCorrectNumberOfOperations() {
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
