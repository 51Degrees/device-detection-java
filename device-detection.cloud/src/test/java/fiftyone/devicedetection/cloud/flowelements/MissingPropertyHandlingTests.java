package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.common.testhelpers.TestLoggerFactory;
import fiftyone.devicedetection.cloud.data.DeviceDataCloud;
import fiftyone.pipeline.cloudrequestengine.data.CloudRequestData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.data.AspectPropertyMetaDataDefault;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import fiftyone.pipeline.engines.services.MissingPropertyReason;
import fiftyone.pipeline.engines.services.MissingPropertyResult;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MissingPropertyHandlingTests {

    protected static final TestLoggerFactory logger =
        new TestLoggerFactory(LoggerFactory.getILoggerFactory());

    /**
     * Test that a cloud response which has a null value for a property is
     * mapped into an AspectPropertyValue with the 'no value reason' set from
     * the 'nullvaluereason' in the cloud response.
     * @throws Exception
     */
    @Test
    public void PropertyInResource_NullValue() throws Exception {
        DeviceDetectionCloudEngine engine =
            new DeviceDetectionCloudEngineBuilder(logger)
            .build();

        FlowData flowData = mock(FlowData.class);
        addResponse(flowData, nullValueJson);

        DeviceDataCloud device = new DeviceDataCloudInternal(
            logger.getLogger(DeviceDataCloud.class.getSimpleName()),
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

    /**
     * Test that a cloud response which has no value for a property throws a
     * PropertyMissingException.
     * @throws Exception
     */
    @Test
    public void PropertyNotInResource() throws Exception {
        DeviceDetectionCloudEngine engine =
            new DeviceDetectionCloudEngineBuilder(logger)
                .build();

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
            logger.getLogger(DeviceDataCloud.class.getSimpleName()),
            flowData,
            engine,
            missingPropertyService);

        engine.cloudRequestEngine = mock(CloudRequestEngine.class);
        engine.aspectProperties = properties;
        engine.processEngine(flowData, device);

        try {
            AspectPropertyValue<String> deviceType = device.getDeviceType();
            fail("A PropertyMissingException should have been thrown");
        }
        catch (PropertyMissingException e) {
            assertEquals(
                expectedMissingPropertyReason,
                e.getMessage());
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
        new ArrayList<AspectPropertyMetaData>(){
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
