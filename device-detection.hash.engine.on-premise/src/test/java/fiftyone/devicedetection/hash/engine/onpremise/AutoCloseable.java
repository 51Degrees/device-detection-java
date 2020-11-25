package fiftyone.devicedetection.hash.engine.onpremise;

import fiftyone.devicedetection.hash.engine.onpremise.data.DeviceDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AutoCloseable {

    @Parameterized.Parameter(0)
    public Class clazz;
    @Parameterized.Parameter(1)
    public boolean isAuocloseable;
    @Parameterized.Parameters(name = "{0} AutoCloseable={1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
            // Classes which should be AutoCloseable.
            {ResultsHashSwig.class, true},
            {BoolValueSwig.class, true},
            {StringValueSwig.class, true},
            {IntegerValueSwig.class, true},
            {DoubleValueSwig.class, true},
            {VectorStringValuesSwig.class, true},
            {EvidenceDeviceDetectionSwig.class, true},
            {MapStringStringSwig.class, true},
            // Classes which should not be AutoCloseable.
            {Date.class, false},
            {EngineHashSwig.class, false},
            {DeviceDataHash.class, false},
            {MetaDataSwig.class, false},
            {CollectionConfigSwig.class, false},
            {RequiredPropertiesConfigSwig.class, false},
            {ComponentMetaDataCollectionSwig.class, false},
            {ComponentMetaDataSwig.class, false},
            {PropertyMetaDataCollectionSwig.class, false},
            {PropertyMetaDataSwig.class, false},
            {ProfileMetaDataCollectionSwig.class, false},
            {ProfileMetaDataSwig.class, false},
            {ValueMetaDataCollectionSwig.class, false},
            {ValueMetaDataSwig.class, false},
            {VectorStringSwig.class, false},
        };
        return Arrays.asList(data);
    }

    /**
     * Check that a class which is supposed to implement AutoCloseable does so,
     * and vice versa.
     */
    @Test
    public void IsAutoCloseable() {
        assertEquals(
            clazz.getSimpleName() + " should " + (isAuocloseable ? "" : "not ") +
                "implement AutoCloseable",
            isAuocloseable,
            java.lang.AutoCloseable.class.isAssignableFrom(clazz));
    }
}
