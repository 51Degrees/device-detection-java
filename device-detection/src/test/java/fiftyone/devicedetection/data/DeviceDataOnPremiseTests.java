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

package fiftyone.devicedetection.data;

import fiftyone.devicedetection.shared.DeviceDataBaseOnPremise;
import fiftyone.pipeline.core.data.ElementPropertyMetaData;
import fiftyone.pipeline.core.data.ElementPropertyMetaDataDefault;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.exceptions.NoValueException;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeviceDataOnPremiseTests {

    private static String testPropertyName = "testproperty";
    private Logger logger;
    private FlowData flowData;
    private AspectEngine engine;
    private Pipeline pipeline;
    private MissingPropertyService missingPropertyService;

    private void setupElementProperties(Class type) {
        Map<String, ElementPropertyMetaData> properties = new HashMap<>();
        ElementPropertyMetaData property = new ElementPropertyMetaDataDefault(
            testPropertyName,
            engine,
            "category",
            type,
            true);
        properties.put(testPropertyName, property);
        Map<String, Map<String, ElementPropertyMetaData>> elementProperties =
            new HashMap<>();
        elementProperties.put(engine.getElementDataKey(), properties);
        when(flowData.getPipeline()).thenReturn(pipeline);
        when(pipeline.getElementAvailableProperties())
            .thenReturn(elementProperties);
    }

    @Before
    public void init() {
        logger = mock(Logger.class);
        missingPropertyService = mock(MissingPropertyService.class);
        engine = mock(AspectEngine.class);
        when(engine.getElementDataKey()).thenReturn("test");
        flowData = mock(FlowData.class);
        pipeline = mock(Pipeline.class);
    }

    @Test
    public void getList() throws NoValueException {
        setupElementProperties(List.class);
        List<String> expected = new ArrayList<>();
        TestResults<List<String>> results =
            new TestResults<>(
                logger,
                flowData,
                engine,
                missingPropertyService,
                expected);

        Object value = results.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(value.getClass()));
        assertEquals(expected, ((AspectPropertyValue)value).getValue());
        Map<String, Object> map = results.asKeyMap();
        assertTrue(map.containsKey(testPropertyName));
        Object mapValue = map.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(mapValue.getClass()));
        assertEquals(expected, ((AspectPropertyValue)mapValue).getValue());
    }

    @Test
    public void getString() throws NoValueException {
        setupElementProperties(String.class);
        String expected = "string";
        TestResults<String> results =
            new TestResults<>(
                logger,
                flowData,
                engine,
                missingPropertyService,
                expected);

        Object value = results.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(value.getClass()));
        assertEquals(expected, ((AspectPropertyValue)value).getValue());
        Map<String, Object> map = results.asKeyMap();
        assertTrue(map.containsKey(testPropertyName));
        Object mapValue = map.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(mapValue.getClass()));
        assertEquals(expected, ((AspectPropertyValue)mapValue).getValue());
    }

    @Test
    public void getBool() throws NoValueException {
        setupElementProperties(Boolean.class);
        Boolean expected = true;
        TestResults<Boolean> results =
            new TestResults<>(
                logger,
                flowData,
                engine,
                missingPropertyService,
                expected);

        Object value = results.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(value.getClass()));
        assertEquals(expected, ((AspectPropertyValue)value).getValue());
        Map<String, Object> map = results.asKeyMap();
        assertTrue(map.containsKey(testPropertyName));
        Object mapValue = map.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(mapValue.getClass()));
        assertEquals(expected, ((AspectPropertyValue)mapValue).getValue());
    }

    @Test
    public void getInt() throws NoValueException {
        setupElementProperties(Integer.class);
        int expected = 1;
        TestResults<Integer> results =
            new TestResults<Integer>(
                logger,
                flowData,
                engine,
                missingPropertyService,
                expected);

        Object value = results.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(value.getClass()));
        assertEquals(expected, ((AspectPropertyValue)value).getValue());
        Map<String, Object> map = results.asKeyMap();
        assertTrue(map.containsKey(testPropertyName));
        Object mapValue = map.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(mapValue.getClass()));
        assertEquals(expected, ((AspectPropertyValue)mapValue).getValue());
    }

    @Test
    public void getDouble() throws NoValueException {
        setupElementProperties(Double.class);
        double expected = 1;
        TestResults<Double> results =
            new TestResults<Double>(
                logger,
                flowData,
                engine,
                missingPropertyService,
                expected);

        Object value = results.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(value.getClass()));
        assertEquals(expected, ((AspectPropertyValue)value).getValue());
        Map<String, Object> map = results.asKeyMap();
        assertTrue(map.containsKey(testPropertyName));
        Object mapValue = map.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(mapValue.getClass()));
        assertEquals(expected, ((AspectPropertyValue)mapValue).getValue());
    }

    @Test
    public void getJavaScript() throws NoValueException {
        setupElementProperties(JavaScript.class);
        String expectedString = "javascript";
        JavaScript expected = new JavaScript(expectedString);
        TestResults<JavaScript> results =
            new TestResults<JavaScript>(
                logger,
                flowData,
                engine,
                missingPropertyService,
                expected);

        Object value = results.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(value.getClass()));
        assertEquals(expected, ((AspectPropertyValue)value).getValue());
        Map<String, Object> map = results.asKeyMap();
        assertTrue(map.containsKey(testPropertyName));
        Object mapValue = map.get(testPropertyName);
        assertTrue(AspectPropertyValue.class.isAssignableFrom(mapValue.getClass()));
        assertEquals(expected, ((AspectPropertyValue)mapValue).getValue());
    }

    private class TestResults<T> extends DeviceDataBaseOnPremise {
        private Object value;

        TestResults(
            Logger logger,
            FlowData flowData,
            AspectEngine engine,
            MissingPropertyService missingPropertyService,
            Object value) {
            super(logger, flowData, engine, missingPropertyService);
            this.value = value;
        }

        @Override
        public AspectPropertyValue<List<String>> getUserAgents() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AspectPropertyValue<String> getDeviceId() {
            throw new UnsupportedOperationException();
        }


        @Override
        protected AspectPropertyValue<Boolean> getValueAsBool(String propertyName) {
            if (propertyName.equals(testPropertyName)) {
                return new AspectPropertyValueDefault<>((Boolean)value);
            } else {
                throw new PropertyMissingException();
            }
        }

        @Override
        protected AspectPropertyValue<Double> getValueAsDouble(String propertyName) {
            if (propertyName.equals(testPropertyName)) {
                return new AspectPropertyValueDefault<>((Double)value);
            } else {
                throw new PropertyMissingException();
            }
        }

        @Override
        protected AspectPropertyValue<Integer> getValueAsInteger(String propertyName) {
            if (propertyName.equals(testPropertyName)) {
                return new AspectPropertyValueDefault<>((Integer)value);
            } else {
                throw new PropertyMissingException();
            }
        }

        @Override
        protected AspectPropertyValue<String> getValueAsString(String propertyName) {
            if (propertyName.equals(testPropertyName)) {
                return new AspectPropertyValueDefault<>((String)value);
            } else {
                throw new PropertyMissingException();
            }
        }

        @Override
        protected AspectPropertyValue<JavaScript> getValueAsJavaScript(String propertyName) {
            if (propertyName.equals(testPropertyName)) {
                return new AspectPropertyValueDefault<>((JavaScript) value);
            } else {
                throw new PropertyMissingException();
            }
        }

        @Override
        public AspectPropertyValue<List<String>> getValues(String propertyName) {
            if (propertyName.equals(testPropertyName)) {
                return new AspectPropertyValueDefault<>((List<String>)value);
            } else {
                throw new PropertyMissingException();
            }
        }

        @Override
        protected boolean propertyIsAvailable(String propertyName) {
            return propertyName.equals(testPropertyName);
        }
    }
}
