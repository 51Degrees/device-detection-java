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

package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.devicedetection.cloud.data.DeviceDataCloud;
import fiftyone.pipeline.cloudrequestengine.data.CloudRequestData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.data.AspectPropertyMetaDataDefault;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import fiftyone.pipeline.engines.services.MissingPropertyReason;
import fiftyone.pipeline.engines.services.MissingPropertyResult;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MissingPropertyHandlingTests {

    protected static final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

    /**
     * Test that a cloud response which has a null value for a property is
     * mapped into an AspectPropertyValue with the 'no value reason' set from
     * the 'nullvaluereason' in the cloud response.
     *
     * @throws Exception
     */
    @Test
    public void PropertyInResource_NullValue() throws Exception {
        try (DeviceDetectionCloudEngine engine =
                     new DeviceDetectionCloudEngineBuilder(loggerFactory)
                             .build()) {

            FlowData flowData = mock(FlowData.class);
            addResponse(flowData, nullValueJson);

            DeviceDataCloud device = new DeviceDataCloudInternal(
                    loggerFactory.getLogger(DeviceDataCloud.class.getSimpleName()),
                    flowData,
                    engine,
                    mock(MissingPropertyService.class));

            engine.cloudRequestEngine = mock(CloudRequestEngine.class);
            engine.aspectProperties = properties;
            engine.processEngine(flowData, device);

            AspectPropertyValue<String> priceBand = device.getPriceBand();
            assertTrue(priceBand != null);
            assertFalse(priceBand.hasValue());
            assertEquals(
                    expectedNullReason,
                    priceBand.getNoValueMessage());
        }
    }

    /**
     * Test that a cloud response which has no value for a property throws a
     * PropertyMissingException.
     *
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @Test
    public void PropertyNotInResource() throws Exception {
        try (DeviceDetectionCloudEngine engine =
                     new DeviceDetectionCloudEngineBuilder(loggerFactory)
                             .build()) {

            FlowData flowData = mock(FlowData.class);
            addResponse(flowData, nullValueJson);
            MissingPropertyService missingPropertyService =
                    mock(MissingPropertyService.class);
            when(missingPropertyService.getMissingPropertyReason(
                    anyString(),
                    anyList()))
                    .thenReturn(new MissingPropertyResult(
                            MissingPropertyReason.Unknown,
                            expectedMissingPropertyReason));

            DeviceDataCloud device = new DeviceDataCloudInternal(
                    loggerFactory.getLogger(DeviceDataCloud.class.getSimpleName()),
                    flowData,
                    engine,
                    missingPropertyService);

            engine.cloudRequestEngine = mock(CloudRequestEngine.class);
            engine.aspectProperties = properties;
            engine.processEngine(flowData, device);

            try {
                AspectPropertyValue<String> deviceType = device.getDeviceType();
                fail("A PropertyMissingException should have been thrown");
            } catch (PropertyMissingException e) {
                assertEquals(
                        expectedMissingPropertyReason,
                        e.getMessage());
            }
        }
    }

    private static String expectedNullReason = "this is the null reason";
    private static String expectedMissingPropertyReason = "this is the missing reason";

    private static String nullValueJson =
            "{\n" +
                    "  'device': {\n" +
                    "    'platformname': 'Windows',\n" +
                    "    'platformversion': '10.0',\n" +
                    "    'priceband': null,\n" +
                    "    'pricebandnullreason': '" + expectedNullReason + "'\n" +
                    "  },\n" +
                    "  'javascriptProperties': []\n" +
                    "}\n";

    private static List<AspectPropertyMetaData> properties =
            new ArrayList<AspectPropertyMetaData>() {
                /**
                 * Serializable class version number, which is used during deserialization
                 */
                private static final long serialVersionUID = -819525441539357267L;

                {
                    add(new AspectPropertyMetaDataDefault("platformname", null, null, String.class, null, true));
                    add(new AspectPropertyMetaDataDefault("platformversion", null, null, String.class, null, true));
                }
            };

    private void addResponse(FlowData data, String json) {
        CloudRequestData cloudData = mock(CloudRequestData.class);
        when(cloudData.getJsonResponse()).thenReturn(json);
        when(data.getFromElement(any(CloudRequestEngine.class)))
                .thenReturn(cloudData);
    }
}
