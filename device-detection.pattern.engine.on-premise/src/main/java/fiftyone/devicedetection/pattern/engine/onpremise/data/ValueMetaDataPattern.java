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

package fiftyone.devicedetection.pattern.engine.onpremise.data;

import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.ValueMetaDataSwig;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;

import java.io.IOException;

public class ValueMetaDataPattern implements ValueMetaData {

    private final ValueMetaDataSwig source;

    private final DeviceDetectionPatternEngine engine;

    public ValueMetaDataPattern(
        DeviceDetectionPatternEngine engine,
        ValueMetaDataSwig source) {
        this.engine = engine;
        this.source = source;
    }

    @Override
    public FiftyOneAspectPropertyMetaData getProperty() {
        return new PropertyMetaDataPattern(
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
