package tests.com.tfl.billing.util;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by marcin on 20.11.17.
 */
public class TestUtils {

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

    private static void setFinalLong(Object obj, Field field, long l) throws Exception {
        field.setAccessible(true);
        field.setLong(obj, l);
    }
}
