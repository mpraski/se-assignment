package tests.com.tfl.billing.util;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

/**
 * Created by marcin on 24.11.17.
 */
public class IsInListMatcher<T> extends TypeSafeMatcher<T> {
    private final List<T> list;

    public IsInListMatcher(List<T> list) {
        this.list = list;
    }

    @Factory
    public static <T> Matcher<T> oneFrom(List<T> list) {
        return new IsInListMatcher<>(list);
    }

    @Override
    protected boolean matchesSafely(T t) {
        return list.contains(t);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("object is contained withing the list");
    }
}