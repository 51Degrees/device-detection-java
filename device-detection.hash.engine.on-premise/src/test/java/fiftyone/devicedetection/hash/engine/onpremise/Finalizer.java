/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2023 51 Degrees Mobile Experts Limited, Davidson House,
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
public class Finalizer {

    @Parameterized.Parameter(0)
    public Class<?> clazz;
    @Parameterized.Parameter(1)
    public boolean hasFinalizer;
    @Parameterized.Parameters(name = "{0} Finalizer={1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
            // Classes which should have a finalizer.
            {EngineHashSwig.class, true},
            {CollectionConfigSwig.class, true},
            {RequiredPropertiesConfigSwig.class, true},
            {ComponentMetaDataSwig.class, true},
            {PropertyMetaDataSwig.class, true},
            {ProfileMetaDataSwig.class, true},
            {ValueMetaDataSwig.class, true},
            {Date.class, true},
            {ComponentMetaDataCollectionSwig.class, true},
            {PropertyMetaDataCollectionSwig.class, true},
            {ProfileMetaDataCollectionSwig.class, true},
            {ValueMetaDataCollectionSwig.class, true},
            {MetaDataSwig.class, true},
            // Classes which should not have a finalizer.
            {ResultsHashSwig.class, false},
            {BoolValueSwig.class, false},
            {StringValueSwig.class, false},
            {IntegerValueSwig.class, false},
            {DoubleValueSwig.class, false},
            {VectorStringValuesSwig.class, false},
            {EvidenceDeviceDetectionSwig.class, false},
            {EvidenceBaseSwig.class, false},
            {MapStringStringSwig.class, false},
            {VectorStringSwig.class, false},
        };
        return Arrays.asList(data);
    }

    /**
     * Check that a class which is supposed to implement a finalizer does so, and
     * vice versa.
     */
    @Test
    public void HasFinalizer() throws ClassNotFoundException {
        check(clazz);
    }

    /**
     * Carry out the check for a finalizer on the class, and all of it's
     * superclasses which exist under the fiftyone namespace (no need to look at
     * the finalizer in things like Object and AbstractMap).
     * @param currentClass
     * @throws ClassNotFoundException
     */
    private void check(Class<?> currentClass) throws ClassNotFoundException {
        boolean result = false;
        try {
            if (currentClass.getDeclaredMethod("finalize") != null) {
                result = true;
            }
        } catch (NoSuchMethodException e) {
            // Method does not exist, so results is false.
        }

        assertEquals(
            currentClass.getSimpleName() + " should " + (hasFinalizer ? "" : "not ") +
                "implement a finalizer",
            hasFinalizer,
            result);

        // Check any superclasses.
        if (currentClass.getAnnotatedSuperclass() != null) {
            String superName = currentClass
                .getAnnotatedSuperclass().getType().getTypeName();
            // Don't go outside of the fiftyone namspace.
            if (superName.startsWith("fiftyone")) {
                check(Class.forName(superName));
            }
        }
    }
}
