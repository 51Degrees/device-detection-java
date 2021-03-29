package fiftyone.devicedetection.hash.engine.onpremise.flowelements;

import fiftyone.common.testhelpers.TestLoggerFactory;
import fiftyone.pipeline.engines.configuration.CacheConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class Builder {
    protected static final TestLoggerFactory logger =
        new TestLoggerFactory(LoggerFactory.getILoggerFactory());

    private DeviceDetectionHashEngineBuilder builder;

    @Before
    public void init() {
        builder = new DeviceDetectionHashEngineBuilder(logger);
    }

    /**
     * Check that an exception is thrown when attempting to add a cache to the
     * on-premise engine.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void Builder_CacheDisabled() {
        builder.setCache(new CacheConfiguration(1000));
    }
}
