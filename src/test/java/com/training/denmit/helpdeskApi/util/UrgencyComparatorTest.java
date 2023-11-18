package com.training.denmit.helpdeskApi.util;

import com.training.denmit.helpdeskApi.model.enums.Urgency;
import com.training.denmit.helpdeskApi.util.comparator.UrgencyComparator;
import org.junit.Assert;
import org.junit.Test;

public class UrgencyComparatorTest {

    private final UrgencyComparator comparator = new UrgencyComparator();

    @Test
    public void getOrderOfUrgencyTest_ifUrgencyIsLow() {
        int expected = 4;

        int actual = comparator.getOrderOfUrgency(Urgency.LOW);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getOrderOfUrgencyTest_ifUrgencyIsHigh() {
        int expected = 3;

        int actual = comparator.getOrderOfUrgency(Urgency.HIGH);

        Assert.assertNotEquals(expected, actual);
    }

    @Test
    public void compareTestForLowAndAverageUrgency() {
        int expected = 1;

        int actual = comparator.compare(Urgency.LOW, Urgency.AVERAGE);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void compareTestForCriticalAndLowUrgency() {
        int expected = -3;

        int actual = comparator.compare(Urgency.CRITICAL, Urgency.LOW);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void compareTestForHighAndAverageUrgency() {
        int order1 = comparator.getOrderOfUrgency(Urgency.HIGH);
        int order2 = comparator.getOrderOfUrgency(Urgency.LOW);

        Assert.assertFalse(order1 > order2);
    }

    @Test
    public void compareTest_IfStatusesAreEquals() {
        int expected = 0;

        int actual = comparator.compare(Urgency.LOW, Urgency.LOW);

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareNegativeTest_IfUnknownStatus() {
        comparator.compare(Urgency.LOW, Urgency.valueOf("UNKNOWN"));
    }
}
