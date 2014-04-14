package de.aw.xray.agent;

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author armin.weisser
 */
public class XrayEventTest {

    @Test
    public void testNotSameButEquals() {
        XrayEvent xrayEvent1 = new XrayEvent("type1", "result1");
        XrayEvent xrayEvent2 = new XrayEvent("type1", "result1");
        Assert.assertNotSame(xrayEvent1, xrayEvent2);
        Assert.assertTrue(xrayEvent1.equals(xrayEvent2));
    }


    @Test
    public void testTimestampNotEqual() throws InterruptedException {
        XrayEvent xrayEvent1 = new XrayEvent("type1", "result1");
        Thread.sleep(1000);
        XrayEvent xrayEvent2 = new XrayEvent("type1", "result1");
        Assert.assertFalse(xrayEvent1.equals(xrayEvent2));
    }

    @Test
    public void testTypeNotEqual() throws InterruptedException {
        XrayEvent xrayEvent1 = new XrayEvent("type1", "result1");
        XrayEvent xrayEvent2 = new XrayEvent("type2", "result1");
        Assert.assertFalse(xrayEvent1.equals(xrayEvent2));
    }

    @Test
    public void testResultNotEqual() throws InterruptedException {
        XrayEvent xrayEvent1 = new XrayEvent("type1", "result1");
        XrayEvent xrayEvent2 = new XrayEvent("type1", "result2");
        Assert.assertFalse(xrayEvent1.equals(xrayEvent2));
    }

    @Test
    public void testEqualXrayEventsInAHashSet() throws InterruptedException {
        XrayEvent xrayEvent1 = new XrayEvent("type1", "result1");
        XrayEvent xrayEvent2 = new XrayEvent("type1", "result1");
        Assert.assertTrue(xrayEvent1.equals(xrayEvent2));

        Set<XrayEvent> set = new HashSet<XrayEvent>(2);
        set.add(xrayEvent1);
        set.add(xrayEvent2);

        Assert.assertEquals(1, set.size());
    }

}
