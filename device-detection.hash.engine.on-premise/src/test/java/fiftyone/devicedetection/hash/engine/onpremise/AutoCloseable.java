/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2026 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 * (EUPL) v.1.2 and is subject to its terms as set out below.
 *
 * If a copy of the EUPL was not distributed with this file, You can obtain
 * one at https://opensource.org/licenses/EUPL-1.2.
 *
 * The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 * amended by the European Commission) shall be deemed incompatible for
 * the purposes of the Work and the provisions of the compatibility
 * clause in Article 5 of the EUPL shall not apply.
 *
 * If using the Work as, or as part of, a network application, by
 * including the attribution notice(s) required under Article 5 of the EUPL
 * in the end user terms of the application under an appropriate heading,
 * such notice(s) shall fulfill the requirements of that article.
 * ********************************************************************* */

package fiftyone.devicedetection.hash.engine.onpremise;

import fiftyone.devicedetection.hash.engine.onpremise.data.DeviceDataHash;
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
    public Class<?> clazz;
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
            {EngineHashSwig.class, true},
            {EvidenceDeviceDetectionSwig.class, true},
            {MapStringStringSwig.class, true},
            {VectorStringSwig.class, true},
            // Classes which should not be AutoCloseable.
            {Date.class, false},
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
