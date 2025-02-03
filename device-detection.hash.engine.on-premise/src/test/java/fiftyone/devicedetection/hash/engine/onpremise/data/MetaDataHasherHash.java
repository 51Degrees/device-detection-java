/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2025 51 Degrees Mobile Experts Limited, Davidson House,
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

package fiftyone.devicedetection.hash.engine.onpremise.data;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.Constants;
import fiftyone.devicedetection.shared.testhelpers.Wrapper;
import fiftyone.devicedetection.shared.testhelpers.data.MetaDataHasher;
import fiftyone.pipeline.engines.fiftyone.data.ComponentMetaData;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ProfileMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;


public class MetaDataHasherHash implements MetaDataHasher {

    @Override
    public int hashProperties(int hash, Wrapper wrapper) {
        int i = 0;
        Iterable<FiftyOneAspectPropertyMetaData> properties = wrapper.getProperties();
        for (FiftyOneAspectPropertyMetaData property : properties) {
            if (i % 10 == 0) {
                hash ^= property.hashCode();

                int j = 0;
                for (ValueMetaData value : property.getValues()) {
                    if (j % 10 == 0) {
                        hash ^= value.hashCode();
                    }
                    j++;
                }
                // Match metric property does not have component so don't include
                if (!property.getCategory().equals(Constants.MatchMetrics.CATEGORY)) {
                	hash ^= property.getComponent().hashCode();
                }
                if (property.getDefaultValue() != null) {
                    hash ^= property.getDefaultValue().hashCode();
                }
            }
            i++;
        }
        return hash;
    }

    @Override
    public int hashValues(int hash, Wrapper wrapper) {
        int i = 0;
        for (ValueMetaData value : wrapper.getValues()) {
            if (i % 100 == 0) {
                hash ^= value.hashCode();
                hash ^= value.getProperty().hashCode();
            }
            i++;
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
                }
                i++;
            }
            hash ^= component.getDefaultProfile().hashCode();
        }
        return hash;
    }

    @Override
    public int hashProfiles(int hash, Wrapper wrapper) {
        int i = 0;
        for (ProfileMetaData profile : wrapper.getProfiles()) {
            if (i % 100 == 0) {
                hash ^= profile.hashCode();
                hash ^= profile.getComponent().hashCode();
                int j = 0;
                for (ValueMetaData value : profile.getValues()) {
                    if (j % 10 == 0) {
                        hash ^= value.hashCode();
                    }
                    j++;
                }
            }
            i++;
        }
        return hash;
    }
}
