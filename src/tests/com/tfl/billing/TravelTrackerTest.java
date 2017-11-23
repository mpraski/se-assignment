package tests.com.tfl.billing;

import com.tfl.billing.TravelTracker;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by marcin on 16.11.17.
 */

public class TravelTrackerTest {

    @Test
    public void travelTrackerOutputsCorrectLog() {
        PrintStream stdout = System.out;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stream));

        CustomerDatabase database = CustomerDatabase.getInstance();
        PaymentsSystem system = PaymentsSystem.getInstance();

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
