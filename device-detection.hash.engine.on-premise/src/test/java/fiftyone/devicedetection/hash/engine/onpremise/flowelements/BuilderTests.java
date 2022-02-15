package fiftyone.devicedetection.hash.engine.onpremise.flowelements;

import fiftyone.pipeline.engines.configuration.CacheConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class BuilderTests {
    protected static final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

    private DeviceDetectionHashEngineBuilder builder;

    @Before
    public void init() {
        builder = new DeviceDetectionHashEngineBuilder(loggerFactory);
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
