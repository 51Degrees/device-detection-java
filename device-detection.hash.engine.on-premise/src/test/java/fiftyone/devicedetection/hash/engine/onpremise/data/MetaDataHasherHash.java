/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2019 51 Degrees Mobile Experts Limited, 5 Charlotte Close,
 * Caversham, Reading, Berkshire, United Kingdom RG4 7BY.
 *
 * This Original Work is licensed under the European Union Public Licence (EUPL) 
 * v.1.2 and is subject to its terms as set out below.
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

package fiftyone.devicedetection.hash.engine.onpremise.data;

import fiftyone.devicedetection.shared.testhelpers.Wrapper;
import fiftyone.devicedetection.shared.testhelpers.data.MetaDataHasher;
import fiftyone.pipeline.engines.fiftyone.data.ComponentMetaData;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;

import static org.junit.Assert.fail;

public class MetaDataHasherHash implements MetaDataHasher {

    @Override
    public int hashProperties(int hash, Wrapper wrapper) {
        int i = 0;
        for (FiftyOneAspectPropertyMetaData property : wrapper.getProperties()) {
            if (i % 10 == 0) {
                hash ^= property.hashCode();
                hash ^= property.getComponent().hashCode();
                try {
                    Object values = property.getValues();
                    fail();
                } catch (UnsupportedOperationException e) {
                }
                try {
                    Object value = property.getDefaultValue();
                    fail();
                } catch (UnsupportedOperationException e) {
                }
            }
            i++;
        }
        return hash;
    }

    @Override
    public int hashValues(int hash, Wrapper wrapper) {
        try {
            Object profiles = wrapper.getEngine().getValues();
            fail();
        } catch (UnsupportedOperationException e) {
        }
        return hash;
    }

    @Override
    public int hashComponents(int hash, Wrapper wrapper) {
        for (ComponentMetaData component : wrapper.getComponents()) {
            hash ^= component.hashCode();
            int i = 0;
            for (FiftyOneAspectPropertyMetaData property : component.getProperties()) {
                if (i % 10 == 0) {
                    hash ^= property.hashCode();
                    try {
                        Object profile = component.getDefaultProfile();
                        fail();
                    } catch (UnsupportedOperationException e) {
                    }
                }
                i++;
            }
        }
        return hash;
    }

    @Override
    public int hashProfiles(int hash, Wrapper wrapper) {
        try {
            Object profiles = wrapper.getEngine().getProfiles();
            fail();
        } catch (UnsupportedOperationException e) {
        }
        return hash;
    }
}
