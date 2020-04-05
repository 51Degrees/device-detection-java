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
import fiftyone.devicedetection.hash.engine.onpremise.interop.Swig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.ValueIterable;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.PropertyMetaDataSwig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.ValueMetaDataCollectionSwig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.ValueMetaDataKeySwig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.ValueMetaDataSwig;
import fiftyone.pipeline.core.data.ElementPropertyMetaData;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.core.flowelements.FlowElement;
import fiftyone.pipeline.engines.fiftyone.data.ComponentMetaData;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;

import java.io.IOException;
import java.util.List;

public class PropertyMetaDataHash implements FiftyOneAspectPropertyMetaData {

    private final PropertyMetaDataSwig source;

    private final DeviceDetectionHashEngine engine;

    private final String url;
    private final byte displayOrder;
    private final boolean mandatory;
    private final boolean list;
    private final boolean obsolete;
    private final boolean show;
    private final boolean showValues;
    private final String description;
    private final boolean available;
    private final String name;
    private final String category;
    private final List<String> dataFilesWherePresent;
    private final String type;

    public PropertyMetaDataHash(
            DeviceDetectionHashEngine engine,
            PropertyMetaDataSwig source) {
        this.source = source;
        this.engine = engine;
        this.url = source.getUrl();
        this.displayOrder = (byte) source.getDisplayOrder();
        this.mandatory = source.getIsMandatory();
        this.list = source.getIsList();
        this.obsolete = source.getIsObsolete();
        this.show = source.getShow();
        this.showValues = source.getShowValues();
        this.description = source.getDescription();
        this.available = source.getAvailable();
        this.name = source.getName();
        this.category = source.getCategory();
        this.dataFilesWherePresent = Swig.asUnmodifiableList(source.getDataFilesWherePresent());
        type = source.getType();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public byte getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public boolean getMandatory() {
        return mandatory;
    }

    @Override
    public boolean getList() {
        return list;
    }

    @Override
    public boolean getObsolete() {
        return obsolete;
    }

    @Override
    public boolean getShow() {
        return show;
    }

    @Override
    public boolean getShowValues() {
        return showValues;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ComponentMetaData getComponent() {
        return new ComponentMetaDataHash(
                engine,
                engine.getMetaData().getComponentForProperty(source));
    }

    @Override
    public Iterable<ValueMetaData> getValues() {
        return new ValueIterable(
                engine,
                engine.getMetaData().getValuesForProperty(source));
    }

    @Override
    public ValueMetaData getValue(String valueName) {
        ValueMetaData result = null;

        ValueMetaDataCollectionSwig values =
                engine.getMetaData().getValuesForProperty(source);
        try {
            ValueMetaDataSwig value = values.getByKey(
                    new ValueMetaDataKeySwig(getName(), valueName));
            if (value != null) {
                result = new ValueMetaDataHash(engine, value);
            }
        } finally {
            values.delete();
        }
        return result;
    }

    @Override
    public ValueMetaData getDefaultValue() {
        ValueMetaDataSwig value =
                engine.getMetaData().getDefaultValueForProperty(source);
        return value == null ?
                null :
                new ValueMetaDataHash(engine, value);
    }

    @Override
    public List<String> getDataTiersWherePresent() {
        return dataFilesWherePresent;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public List<ElementPropertyMetaData> getItemProperties() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Class getType() {
        switch (type) {
            case "string":
                return String.class;
            case "int":
                return Integer.class;
            case "bool":
                return Boolean.class;
            case "double":
                return Double.class;
            case "javascript":
                return JavaScript.class;
            case "string[]":
                return List.class;
            default:
                return String.class;
        }
    }

    @Override
    public FlowElement getElement() {
        return engine;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FiftyOneAspectPropertyMetaData) {
            return equals((FiftyOneAspectPropertyMetaData) obj);
        }
        if (obj instanceof String) {
            return equals((String) obj);
        }
        return super.equals(obj);
    }

    public boolean equals(FiftyOneAspectPropertyMetaData other) {
        return getName().equals(other.getName());
    }

    public boolean equals(String other) {
        return getName().equals(other);
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