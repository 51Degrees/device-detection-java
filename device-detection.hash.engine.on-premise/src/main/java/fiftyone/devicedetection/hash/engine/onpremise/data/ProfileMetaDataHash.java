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

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.interop.ValueIterable;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.*;
import fiftyone.pipeline.engines.fiftyone.data.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;

/**
 * Hash on-premise implementation of the {@link ProfileMetaData} interface.
 */
public class ProfileMetaDataHash implements ProfileMetaData {

    private final ProfileMetaDataSwig source;

    private final DeviceDetectionHashEngine engine;

    /**
     * Construct a new instance.
     * @param engine the engine creating the instance
     * @param source the source metadata from the native engine
     */
    public ProfileMetaDataHash(
        DeviceDetectionHashEngine engine,
        ProfileMetaDataSwig source) {
        this.engine = engine;
        this.source = source;
    }

    @Override
    public int getProfileId() {
        return (int) source.getProfileId();
    }

    @Override
    public CloseableIterable<ValueMetaData> getValues() {
        return new ValueIterable(engine,
                engine.getMetaData().getValuesForProfile(source));
    }

    @Override
    public CloseableIterable<ValueMetaData> getValues(String propertyName) {
        List<ValueMetaData> result = new ArrayList<>();

        ValueMetaDataCollectionSwig values =
                engine.getMetaData().getValuesForProfile(source);
        long size = values.getSize();
        for (long i = 0; i < size; i++) {
            ValueMetaDataSwig value = values.getByIndex(i);
            PropertyMetaDataSwig valueProperty = engine.getMetaData()
                .getPropertyForValue(value);
            if (propertyName.equalsIgnoreCase(valueProperty.getName())) {
                result.add(new ValueMetaDataHash(engine, value));
            } else {
                value.delete();
            }
            valueProperty.delete();
        }
        values.delete();
        return new CloseableIterableDefault<>(result);
    }

    @Override
    public ValueMetaData getValue(String propertyName, String valueName) {
        ValueMetaData result = null;
        ValueMetaDataCollectionSwig values =
                engine.getMetaData().getValuesForProfile(source);
        ValueMetaDataSwig value = values.getByKey(
            new ValueMetaDataKeySwig(propertyName, valueName));
        if (value != null) {
            result = new ValueMetaDataHash(engine, value);
        }
        values.delete();
        return result;
    }

    @Override
    public ComponentMetaData getComponent() {
        return new ComponentMetaDataHash(
                engine,
                engine.getMetaData().getComponentForProfile(source));
    }

    @Override
    public String getName() {
        List<ValueMetaData> values = new ArrayList<>();
        for (ValueMetaData value : getValues()) {
            if (value.getProperty().getDisplayOrder() > 0 &&
                    value.getName().equalsIgnoreCase("N/A") == false) {
                values.add(value);
            }
        }
        ValueMetaData[] valuesArray = new ValueMetaData[0];
        valuesArray = values.toArray(valuesArray);

        Arrays.sort(valuesArray, new Comparator<ValueMetaData>() {
            @Override
            public int compare(ValueMetaData o1, ValueMetaData o2) {
                return o1.getProperty().getDisplayOrder() -
                        o2.getProperty().getDisplayOrder();
            }
        });

        List<String> names = new ArrayList<>();
        for (ValueMetaData value : valuesArray) {
            if (names.contains(value.getName()) == false) {
                names.add(value.getName());
            }
        }

        if (names.isEmpty())
            return Integer.toString(getProfileId());
        else
            return stringJoin(names, "/");
    }

    @Override
    public int hashCode() {
        return getProfileId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProfileMetaData) {
            return equals((ProfileMetaData) obj);
        }
        return super.equals(obj);
    }

    public boolean equals(ProfileMetaData other) {
        return getProfileId() == other.getProfileId();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void close() throws IOException {
        source.delete();
    }
}