package tests.com.tfl.billing.helper;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyConstants;
import com.tfl.billing.helper.DefaultJourneyHelper;
import com.tfl.billing.helper.DefaultTotalHelper;
import com.tfl.billing.helper.ITotalHelper;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Assert;
import org.junit.Test;
import tests.com.tfl.billing.util.TestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by marcin on 20.11.17.
 */
public class TotalHelperTest {

    private final static UUID READER_START = OysterReaderLocator.atStation(Station.EUSTON).id();
    private final static UUID READER_END = OysterReaderLocator.atStation(Station.GOODGE_STREET).id();
    private final static UUID DUMMY_CUSTOMER_UUID = UUID.fromString("1e138616-ce25-11e7-abc4-cec278b6b50a");

    @Test
    public void areJourneyPricesCapped() {
        DefaultTotalHelper totalHelper = new DefaultTotalHelper(new DefaultJourneyHelper());

        Assert.assertEquals("Cap is applied to peak journey", totalHelper.applyCap(new BigDecimal(12.0), true), JourneyConstants.PEAK_CAP);
        Assert.assertEquals("Cap is applied to off peak journey", totalHelper.applyCap(new BigDecimal(12.0), false), JourneyConstants.OFF_PEAK_CAP);
    }

    @Test
    public void arePricesCorrectlyRounded() {
        DefaultTotalHelper totalHelper = new DefaultTotalHelper(new DefaultJourneyHelper());

        BigDecimal price = new BigDecimal("12.005");
        BigDecimal rounded = new BigDecimal("12.01");

        BigDecimal price2 = new BigDecimal("12.004");
        BigDecimal rounded2 = new BigDecimal("12.00");

        Assert.assertTrue("Properly rounded", totalHelper.roundToNearestPenny(price).compareTo(rounded) == 0);
        Assert.assertTrue("Properly rounded", totalHelper.roundToNearestPenny(price2).compareTo(rounded2) == 0);
    }

    @Test
    public void isCorrectTotalReturned() {
        ITotalHelper totalHelper = new DefaultTotalHelper(new DefaultJourneyHelper());

        List<Journey> journeys = new ArrayList<>(Arrays.asList(
                TestUtils.mockJourney(DUMMY_CUSTOMER_UUID, READER_START, READER_END, 7, 0, 10),
                TestUtils.mockJourney(DUMMY_CUSTOMER_UUID, READER_END, READER_START, 8, 0, 10),
                TestUtils.mockJourney(DUMMY_CUSTOMER_UUID, READER_START, READER_END, 12, 0, 10),
                TestUtils.mockJourney(DUMMY_CUSTOMER_UUID, READER_END, READER_START, 13, 0, 10)
        ));

        BigDecimal total = new BigDecimal("9.00");

        Assert.assertTrue("Properly computed total price", totalHelper.getTotal(journeys).compareTo(total) == 0);
    }
}
