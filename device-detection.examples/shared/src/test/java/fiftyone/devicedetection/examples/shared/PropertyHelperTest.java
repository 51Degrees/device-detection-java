package fiftyone.devicedetection.examples.shared;

import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PropertyHelperTest {

    @Test
    public void testAsStringBoolean() {
        AspectPropertyValue<Boolean> test = new AspectPropertyValueDefault<>(true);
        assertEquals("true", PropertyHelper.asString(test));
    }

    @Test
    public void testAsStringStringArray() {
        List<String> strings = new ArrayList<>();
        strings.add("one"); strings.add("two");
        AspectPropertyValue<List<String>> test = new AspectPropertyValueDefault<>(strings);
        assertEquals("one, two", PropertyHelper.asString(test));
    }

    @Test
    public void testAsBooleanArray() {
        List<Boolean> bools = new ArrayList<>();
        bools.add(true); bools.add(false);
        AspectPropertyValue<List<Boolean>> test = new AspectPropertyValueDefault<>(bools);
        assertEquals("true, false", PropertyHelper.asString(test));
    }

    @Test
    public void testAsNoValue() {
        AspectPropertyValue<List<Boolean>> test = new AspectPropertyValueDefault<>();
        assertTrue(PropertyHelper.asString(test).startsWith("Unknown"));
    }
}