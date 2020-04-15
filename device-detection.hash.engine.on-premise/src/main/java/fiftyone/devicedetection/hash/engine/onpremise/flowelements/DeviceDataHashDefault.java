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

package fiftyone.devicedetection.hash.engine.onpremise.flowelements;

import fiftyone.devicedetection.hash.engine.onpremise.Enums;
import fiftyone.devicedetection.hash.engine.onpremise.data.DeviceDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.interop.Swig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.*;
import fiftyone.devicedetection.shared.DeviceDataBaseOnPremise;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.TryGetResult;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;

public class DeviceDataHashDefault
    extends DeviceDataBaseOnPremise
    implements DeviceDataHash {
    // Pre-populate this to avoid calling the method every time.
    private static final Enums.MatchMethods[] matchMethods =
            Enums.MatchMethods.values();

    private final List<ResultsHashSwig> resultsList = new ArrayList<>();

    DeviceDataHashDefault(
        Logger logger,
        FlowData flowData,
        AspectEngine engine,
        MissingPropertyService missingPropertyService) {
        super(logger, flowData, engine, missingPropertyService);
    }

    void setResults(ResultsHashSwig results) {
        resultsList.add(results);
    }

    private ResultsHashSwig getResultsContainingProperty(String propertyName) {
        for (ResultsHashSwig results : resultsList) {
            if (results.containsProperty(propertyName, propertyName.length())) {
                return results;
            }
        }
        return null;
    }

    private AspectPropertyValue<String> getDeviceIdInternal() {
        if (resultsList.size() == 1) {
            // Only one Engine has added results, so return the device
            // id from those results.
            return new AspectPropertyValueDefault<>(
                    resultsList.get(0).getDeviceId());
        } else {
            // Multiple Engines have added results, so construct a device
            // id from the results.
            List<String> result = new ArrayList<>();
            List<String[]> deviceIds = new ArrayList<>();
            for (ResultsHashSwig results : resultsList) {
                deviceIds.add(results.getDeviceId().split("-"));
            }
            int max = 0;
            for (String[] deviceId : deviceIds) {
                if (deviceId.length > max) {
                    max = deviceId.length;
                }
            }
            for (int i = 0; i < max; i++) {
                String profileId = "0";
                for (String[] deviceId : deviceIds) {
                    if (deviceId.length > i && deviceId[i].equals("0") == false) {
                        profileId = deviceId[i];
                    }
                }
                result.add(profileId);
            }
            return new AspectPropertyValueDefault<>(
                    stringJoin(result, "-"));
        }
    }

    private AspectPropertyValue<Integer> getDifferenceInternal() {
        int total = 0;
        for (ResultsHashSwig results : resultsList) {
            total += results.getDifference();
        }
        return new AspectPropertyValueDefault<>(total);
    }

    private AspectPropertyValue<Integer> getDriftInternal() {
        int result = Integer.MAX_VALUE;
        for (ResultsHashSwig results : resultsList) {
            if (results.getDrift() < result) {
                result = results.getDrift();
            }
        }
        return new AspectPropertyValueDefault<>(result);
    }

    private AspectPropertyValue<Integer> getIterationsInternal() {
        int result = 0;
        for (ResultsHashSwig results : resultsList) {
            result += results.getIterations();
        }
        return new AspectPropertyValueDefault<>(result);
    }

    private AspectPropertyValue<Integer> getMatchedNodesInternal() {
        int result = 0;
        for (ResultsHashSwig results : resultsList) {
            result += results.getMatchedNodes();
        }
        return new AspectPropertyValueDefault<>(result);
    }

    private AspectPropertyValue<String> getMethodInternal() {
        int result = 0;
        for (ResultsHashSwig results : resultsList) {
            if (results.getMethod() > result) {
                result = results.getMethod();
            }
        }
        return new AspectPropertyValueDefault<>(matchMethods[result].name());
    }

    private AspectPropertyValue<List<String>> getUserAgentsInternal() {
        List<String> result = new ArrayList<>();
        for (ResultsHashSwig results : resultsList) {
            for (int i = 0; i < results.getUserAgents(); i++) {
                String userAgent = results.getUserAgent(i);
                if (result.contains(userAgent) == false) {
                    result.add(userAgent);
                }
            }
        }
        return new AspectPropertyValueDefault<>(result);
    }

    @Override
    public AspectPropertyValue<Integer> getDrift() {
        return getAs("Drift", AspectPropertyValue.class, Integer.class);
    }

    @Override
    public AspectPropertyValue<Integer> getIterations() {
        return getAs("Iterations", AspectPropertyValue.class, Integer.class);
    }

    @Override
    public AspectPropertyValue<Integer> getMatchedNodes() {
        return getAs("MatchedNodes", AspectPropertyValue.class, Integer.class);
    }

    @Override
    public AspectPropertyValue<String> getMethod() {
        return getAs("Method", AspectPropertyValue.class, String.class);
    }

    @Override
    protected boolean propertyIsAvailable(String propertyName) {
        for (ResultsHashSwig results : resultsList) {
            if (results.containsProperty(propertyName, propertyName.length())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AspectPropertyValue<List<String>> getValues(String propertyName) {
        AspectPropertyValue<List<String>> result = new AspectPropertyValueDefault<>();
        ResultsHashSwig results = getResultsContainingProperty(propertyName);
        if (results != null) {
            VectorStringValuesSwig value = results.getValues(propertyName, propertyName.length());
            if (value.hasValue()) {
                result.setValue(Collections.unmodifiableList(Swig.asList(value.getValue())));
            }
            else {
                result.setNoValueMessage(value.getNoValueMessage());
            }
        }
        return result;
    }

    @Override
    protected AspectPropertyValue<String> getValueAsString(String propertyName) {
        AspectPropertyValue<String> result = new AspectPropertyValueDefault<>();
        ResultsHashSwig results = getResultsContainingProperty(propertyName);
        if (results != null) {
            StringValueSwig value = results.getValueAsString(propertyName, propertyName.length());
            if (value.hasValue()) {
                result.setValue(value.getValue());
            }
            else {
                result.setNoValueMessage(value.getNoValueMessage());
            }
        }
        return result;
    }

    @Override
    protected AspectPropertyValue<JavaScript> getValueAsJavaScript(String propertyName) {
        AspectPropertyValue<JavaScript> result = new AspectPropertyValueDefault<>();
        ResultsHashSwig results = getResultsContainingProperty(propertyName);
        if (results != null) {
            StringValueSwig value = results.getValueAsString(propertyName, propertyName.length());
            if (value.hasValue()) {
                result.setValue(new JavaScript(value.getValue()));
            }
            else {
                result.setNoValueMessage(value.getNoValueMessage());
            }
        }
        return result;
    }

    @Override
    protected AspectPropertyValue<Integer> getValueAsInteger(String propertyName) {
        AspectPropertyValue<Integer> result = new AspectPropertyValueDefault<>();
        ResultsHashSwig results = getResultsContainingProperty(propertyName);
        if (results != null) {
            IntegerValueSwig value = results.getValueAsInteger(propertyName, propertyName.length());
            if (value.hasValue()) {
                result.setValue(value.getValue());
            }
            else {
                result.setNoValueMessage(value.getNoValueMessage());
            }
        }
        return result;
    }

    @Override
    protected AspectPropertyValue<Boolean> getValueAsBool(String propertyName) {
        AspectPropertyValue<Boolean> result = new AspectPropertyValueDefault<>();
        ResultsHashSwig results = getResultsContainingProperty(propertyName);
        if (results != null) {
            BoolValueSwig value = results.getValueAsBool(propertyName, propertyName.length());
            if (value.hasValue()) {
                result.setValue(value.getValue());
            }
            else {
                result.setNoValueMessage(value.getNoValueMessage());
            }
        }
        return result;
    }

    @Override
    protected AspectPropertyValue<Double> getValueAsDouble(String propertyName) {
        AspectPropertyValue<Double> result = new AspectPropertyValueDefault<>();
        ResultsHashSwig results = getResultsContainingProperty(propertyName);
        if (results != null) {
            DoubleValueSwig value = results.getValueAsDouble(propertyName, propertyName.length());
            if (value.hasValue()) {
                result.setValue(value.getValue());
            }
            else {
                result.setNoValueMessage(value.getNoValueMessage());
            }
        }
        return result;
    }

    @Override
    protected <T> TryGetResult<T> tryGetValue(String key, Class<T> type, Class<?>... parameterisedTypes) {
        TryGetResult<T> result = super.tryGetValue(key, type, parameterisedTypes);

        if (result.hasValue() == false) {
            boolean objSet = false;
            Object obj = null;
            if (key.equalsIgnoreCase("DeviceId")) {
                obj = getDeviceIdInternal();
                objSet = true;
            } else if (key.equalsIgnoreCase("Difference")) {
                obj = getDifferenceInternal();
                objSet = true;
            } else if (key.equalsIgnoreCase("UserAgents")) {
                obj = getUserAgentsInternal();
                objSet = true;
            } else if (key.equalsIgnoreCase("Drift")) {
                obj = getDriftInternal();
                objSet = true;
            } else if (key.equalsIgnoreCase("Iterations")) {
                obj = getIterationsInternal();
                objSet = true;
            }
            else if (key.equalsIgnoreCase("MatchedNodes")) {
                obj = getMatchedNodesInternal();
                objSet = true;
            }
            else if (key.equalsIgnoreCase("Method")){
                obj = getMethodInternal();
                objSet = true;
            }
            if (objSet == true) {
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
        return result;
    }

    @Override
    protected void finalize() throws Throwable {
        for (ResultsHashSwig results : resultsList) {
            if (results != null) {
                results.delete();
            }
        }
    }
}
