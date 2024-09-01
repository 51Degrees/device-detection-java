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

package fiftyone.devicedetection.hash.engine.onpremise.data;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.*;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;

import java.io.IOException;

/**
 * Hash on-premise implementation of the {@link ValueMetaData} interface.
 * @see <a href="https://github.com/51Degrees/specifications/blob/main/data-model-specification/README.md#value">Specification</a>
 */
public class ValueMetaDataHash implements ValueMetaData {

    private final ValueMetaDataSwig source;

    private final DeviceDetectionHashEngine engine;

    /**
     * Construct a new instance.
     * @param engine the engine creating the instance
     * @param source the source metadata from the native engine
     */
    public ValueMetaDataHash(
        DeviceDetectionHashEngine engine,
        ValueMetaDataSwig source) {
        this.engine = engine;
        this.source = source;
    }

    @Override
    public FiftyOneAspectPropertyMetaData getProperty() {
        return new PropertyMetaDataHash(
            engine,
            engine.getMetaData().getPropertyForValue(source));
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

    @Override
    public String getUrl() {
        return source.getUrl();
    }

    @Override
    public int hashCode() {
        return getProperty().hashCode() ^ getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValueMetaData) {
            return equals((ValueMetaData) obj);
        }
        return super.equals(obj);
    }

    public boolean equals(ValueMetaData other) {
        return getProperty().equals(other.getProperty()) &&
            getName().equals(other.getName());
    }

    @Override
    public String toString() {
        return getProperty().getName() + "=>" + getName();
    }

    @Override
    public void close() throws IOException {
        source.delete();
    }
}