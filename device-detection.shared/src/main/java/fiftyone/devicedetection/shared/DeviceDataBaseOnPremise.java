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

package fiftyone.devicedetection.shared;

import fiftyone.pipeline.core.data.ElementPropertyMetaData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.TryGetResult;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static fiftyone.pipeline.util.Types.getPrimativeTypeMap;

public abstract class DeviceDataBaseOnPremise extends DeviceDataBase {

    protected final Map<Class<?>, Class<?>> primativeTypes;
    private Object dataLock = new Object();
    private Object getLock = new Object();
    private boolean mapPopulated = false;

    protected DeviceDataBaseOnPremise(
        Logger logger,
        FlowData flowData,
        AspectEngine engine,
        MissingPropertyService missingPropertyService) {
        super(logger, flowData, engine, missingPropertyService);
        primativeTypes = getPrimativeTypeMap();
    }

    protected abstract boolean propertyIsAvailable(String propertyName);

    public abstract AspectPropertyValue<List<String>> getValues(String propertyName);

    protected abstract AspectPropertyValue<String> getValueAsString(String propertyName);

    protected abstract AspectPropertyValue<Integer> getValueAsInteger(String propertyName);

    protected abstract AspectPropertyValue<Boolean> getValueAsBool(String propertyName);

    protected abstract AspectPropertyValue<Double> getValueAsDouble(String propertyName);

    protected abstract AspectPropertyValue<JavaScript> getValueAsJavaScript(String propertyName);

    @Override
    public Map<String, Object> asKeyMap() {
        if (mapPopulated == false) {
            synchronized (dataLock) {
                if (mapPopulated == false) {
                    Map<String, Object> map = new TreeMap<>(
                        String.CASE_INSENSITIVE_ORDER);
                    for (ElementPropertyMetaData property : getPipline()
                        .getElementAvailableProperties()
                        .get(getEngines().get(0).getElementDataKey()).values()) {
                        map.put(property.getName().toLowerCase(),
                            getAs(property.getName(), AspectPropertyValue.class, property.getType()));
                    }
                    populateFromMap(map);
                    mapPopulated = true;
                }
            }
        }
        // Now that the base map has been populated,
        // we can return it.
        return super.asKeyMap();
    }

    protected Class getPropertyType(String propertyName) {
        Class type = Object.class;
        Map<String, ElementPropertyMetaData> properties =
            getPipline().getElementAvailableProperties()
                .get(getEngines().get(0).getElementDataKey());
        if (properties != null) {
            ElementPropertyMetaData property = properties.get(propertyName);
            if (property != null) {
                type = property.getType();
            }
        }
        return type;
    }

    @Override
    protected <T> TryGetResult<T> tryGetValue(String key, Class<T> type, Class<?>... parameterisedTypes) {
        TryGetResult<T> result = new TryGetResult<>();
        if (mapPopulated == true) {
            // If the complete set of values has been populated
            // then we can use the base implementation to get
            // the value from the dictionary.
            return super.tryGetValue(key, type);
        } else {
            // If the complete set of values has not been populated
            // then we don't want to retrieve values for all
            // properties so just get the one we want.
            if (propertyIsAvailable(key)) {
                if (type.equals(Object.class)) {
                    type = (Class)AspectPropertyValue.class;
                    parameterisedTypes = new Class[]{ getPropertyType(key) };
                }
                synchronized (getLock) {
                    Object obj = null;
                    if (type.equals(AspectPropertyValue.class)){
                        if (parameterisedTypes.length > 0) {
                            Class<?> innerType = parameterisedTypes[0];
                            if (innerType.equals(String.class)) {
                                obj = getValueAsString(key);
                            } else if (innerType.equals(Boolean.class)) {
                                obj = getValueAsBool(key);
                            } else if (innerType.equals(Integer.class)) {
                                obj = getValueAsInteger(key);
                            } else if (innerType.equals(Double.class)) {
                                obj = getValueAsDouble(key);
                            } else if (innerType.equals(List.class)) {
                                obj = getValues(key);
                            } else if (innerType.equals(JavaScript.class)) {
                                obj = getValueAsJavaScript(key);
                            } else {
                                obj = getValueAsString(key);
                            }
                        }
                        else {
                            obj = getValueAsString(key);
                        }
                    }
                    try {
                        T value;
                        if (type.isPrimitive()) {
                            value = (T) primativeTypes.get(type).cast(obj);
                        } else {
                            value = type.cast(obj);
                        }
                        result.setValue(value);
                    } catch (ClassCastException e) {
                        throw new ClassCastException(
                            "Expected property '" + key + "' to be of " +
                                "type '" + type.getSimpleName() + "' but it is " +
                                "'" + obj.getClass().getSimpleName() + "'");
                    }
                }
            }
        }
        return result;
    }

}