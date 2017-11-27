package tests.com.tfl.billing.util;

import com.oyster.OysterCard;
import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import com.tfl.billing.components.ICustomerDatabase;
import com.tfl.external.Customer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marcin on 20.11.17.
 */
public class TestUtils {

    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static <T> T randomItem(List<T> items) {
        return items.get(new Random().nextInt(items.size() - 1));
    }

    public static <T> List<T> randomItems(List<T> items, int n) {
        return new Random()
                .ints(0, items.size() - 1)
                .distinct()
                .limit(n)
                .mapToObj(value -> items.get(value))
                .collect(Collectors.toList());
    }

    public static Journey mockJourney(UUID customer, UUID reader_start, UUID reader_end, int hour, int minute, int duration) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, hour);
        now.set(Calendar.MINUTE, minute);
        now.set(Calendar.SECOND, 0);

        JourneyEvent start = new JourneyStart(customer, reader_start);
        try {
            setFinalLong(start, start.getClass().getSuperclass().getDeclaredField("time"), now.getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        now.add(Calendar.MINUTE, duration);

        JourneyEvent end = new JourneyEnd(customer, reader_end);
        try {
            setFinalLong(end, end.getClass().getSuperclass().getDeclaredField("time"), now.getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Journey(start, end);
    }

    public static ICustomerDatabase mockCustomerDatabase(int size) {
        final List<Customer> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            customers.add(new Customer(generateRandomString(5) + " " + generateRandomString(10), new OysterCard(UUID.randomUUID().toString())));
        }

        return new ICustomerDatabase() {
            @Override
            public List<Customer> getCustomers() {
                return customers;
            }

            @Override
            public boolean isRegisteredId(UUID customer) {
                return customers.stream().anyMatch(cust -> cust.cardId().equals(customer));
            }
        };
    }

    private static void setFinalLong(Object obj, Field field, long l) throws Exception {
        field.setAccessible(true);
        field.setLong(obj, l);
    }

    private static int getRandomNumber() {
        int randomInt;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    private static String generateRandomString(int length) {
        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }
}
