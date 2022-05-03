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

package fiftyone.devicedetection;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.devicedetection.shared.testhelpers.UserAgentGenerator;
import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.FlowError;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.HttpClientDefault;

import fiftyone.pipeline.util.FileFinder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static fiftyone.devicedetection.shared.testhelpers.FileUtils.UA_FILE_NAME;
import static fiftyone.pipeline.core.Constants.EVIDENCE_HTTPHEADER_PREFIX;
import static fiftyone.pipeline.core.Constants.EVIDENCE_SEPERATOR;
import static fiftyone.pipeline.engines.Constants.PerformanceProfiles.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeviceDetectionTests {

    private static final Logger logger = LoggerFactory.getLogger(DeviceDetectionTests.class);

    private static final String HASH_DATA_FILE_NAME = FileUtils.getHashFileName();

    private static UserAgentGenerator userAgents;
    TestConfig[] hashConfigs = {
        // ******** Hash with a single thread *********
        new TestConfig(HASH_DATA_FILE_NAME, HighPerformance, false, false, "Hash-HighPerformance-NoCache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, LowMemory, false, false, "Hash-LowMemory-NoCache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, Balanced, false, false, "Hash-Balanced-NoCache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, false, false, "Hash-HighPerformance-NoCache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, HighPerformance, true, false, "Hash-HighPerformance-Cache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, LowMemory, true, false, "Hash-LowMemory-Cache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, Balanced, true, false, "Hash-Balanced-Cache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, true, false, "Hash-BalancedTemp-Cache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, true, false, "Hash-BalancedTemp-Cache-SingleThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, true, false, "Hash-BalancedTemp-Cache-SingleThread"),
        // ******** Hash with multiple threads *********
        new TestConfig(HASH_DATA_FILE_NAME, HighPerformance, false, true, "Hash-HighPerformance-NoCache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, LowMemory, false, true, "Hash-LowMemory-NoCache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, Balanced, false, true, "Hash-Balanced-NoCache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, false, true, "Hash-HighPerformance-NoCache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, HighPerformance, true, true, "Hash-HighPerformance-Cache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, LowMemory, true, true, "Hash-LowMemory-Cache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, Balanced, true, true, "Hash-Balanced-Cache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, true, true, "Hash-BalancedTemp-Cache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, true, true, "Hash-BalancedTemp-Cache-MultiThread"),
        new TestConfig(HASH_DATA_FILE_NAME, BalancedTemp, true, true, "Hash-BalancedTemp-Cache-MultiThread")
    };

    @BeforeClass
    public static void initClass() throws IOException {
        userAgents = new UserAgentGenerator(
            FileFinder.getFilePath(UA_FILE_NAME));
    }

    private static String reportErrors(Collection<FlowError> errors) {
        StringBuilder result = new StringBuilder();
        for (FlowError error : errors) {
            result.append("Error in element '" +
                error.getFlowElement().getClass().getSimpleName() + "'\n");
            AddExceptionToMessage(result, error.getThrowable(), 0);
        }
        return result.toString();
    }

    private static void AddExceptionToMessage(
        StringBuilder message,
        Throwable ex,
        int depth) {
        AddToMessage(message, ex.getClass().getSimpleName() + " - " + ex.getMessage(), depth);
        AddToMessage(message, ex.getStackTrace().toString(), depth);
        if (ex.getSuppressed() != null) {
            AddExceptionToMessage(message, ex.getSuppressed()[0], depth++);
        }
    }

    private static void AddToMessage(StringBuilder message, String textToAdd, int depth) {
        for (int i = 0; i < depth; i++) {
            message.append("   ");
        }
        message.append(textToAdd + "\n");
    }

    @Test
    public void Hash_AllConfigurations_100_UserAgents() throws Exception {
        for (TestConfig config : hashConfigs) {
            logger.info("Testing '" + config.name + "'");
            TestOnPremise_AllConfigurations_100_UserAgents(
                config.dataFileName,
                config.performanceProfile,
                config.useCache,
                config.multiThreaded);
        }
    }

    public void TestOnPremise_AllConfigurations_100_UserAgents(
        String datafileName,
        Constants.PerformanceProfiles performanceProfile,
        boolean useCache,
        boolean multiThreaded) throws Exception {
        File datafile = FileFinder.getFilePath(datafileName);

        // Configure the pipeline builder based on the
        // parameters passed to this method.
        ILoggerFactory loggerFactory = mock(ILoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(loggerFactory.getLogger(anyString())).thenReturn(logger);
        DeviceDetectionOnPremisePipelineBuilder builder =
            new DeviceDetectionPipelineBuilder(loggerFactory)
                .useOnPremise(datafile.getAbsolutePath(), false)
                .setPerformanceProfile(performanceProfile)
                .setShareUsage(false)
                .setAutoUpdate(false);
        if (useCache) {
            builder.useResultsCache();
        }

        try (final Pipeline pipeline = builder.build()) {
            int threadCount = multiThreaded ? 8 : 1;
            ExecutorService service = Executors.newFixedThreadPool(threadCount);
            List<Callable<Void>> callables = new ArrayList<>(threadCount);
            for (int i = 0; i < threadCount; i++) {
                callables.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        for (String userAgent : userAgents.getRandomUserAgents(100)) {
                            try (FlowData flowData = pipeline.createFlowData()) {
                                flowData.addEvidence(
                                    EVIDENCE_HTTPHEADER_PREFIX +
                                        EVIDENCE_SEPERATOR + "User-Agent",
                                    userAgent)
                                    .process();
                                if (flowData.getErrors() != null) {
                                    assertEquals(
                                        "Expected no errors but got " +
                                            flowData.getErrors().size() + "\n" +
                                            reportErrors(flowData.getErrors()),
                                        0,
                                        flowData.getErrors().size());
                                }
                                DeviceData deviceData = flowData.get(DeviceData.class);
                                deviceData.getIsMobile();
                            }
                        }
                        return null;
                    }
                });
            }

            List<Future<Void>> futures = service.invokeAll(callables);

            for (Future<Void> future : futures) {
                future.get();
            }
        }
    }
    
    /**
     * This tests that the default constructor of the
     * DeviceDetectionPipelineBuilder does pass the DataUpdateService to the
     * resulting pipeline.
     * @throws Exception
     */
    @Test
    public void TestOnPremiseBuilder_DataUpdateService_Default() throws Exception {
    	// Configure the pipeline builder based on the
        // parameters passed to this method.
        ILoggerFactory loggerFactory = mock(ILoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(loggerFactory.getLogger(anyString())).thenReturn(logger);
    	DeviceDetectionOnPremisePipelineBuilder builder =
                new DeviceDetectionPipelineBuilder(loggerFactory,
                		new HttpClientDefault())
                    .useOnPremise(HASH_DATA_FILE_NAME, false)
                    .setPerformanceProfile(MaxPerformance)
                    .setShareUsage(false)
                    .setAutoUpdate(false);
    	try (Pipeline pipeline = builder.build()) {
    		assertEquals(1, pipeline.getServices().size());
    	}
    }
    
    /**
     * This tests that DataUpdateService is closed when its corresponding
     * pipeline created by DeviceDetectionPipelineBuilder is closed.
     * @throws Exception
     */
    @Test
    public void TestOnPremiseBuilder_DataUpdateService_Close() throws Exception {
    	// Configure the pipeline builder based on the
        // parameters passed to this method.
        ILoggerFactory loggerFactory = mock(ILoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(loggerFactory.getLogger(anyString())).thenReturn(logger);
        DataUpdateService updateService = mock(DataUpdateService.class);
    	DeviceDetectionOnPremisePipelineBuilder builder =
                new DeviceDetectionPipelineBuilder(loggerFactory,
                		new HttpClientDefault(), updateService)
                    .useOnPremise(HASH_DATA_FILE_NAME, false)
                    .setPerformanceProfile(MaxPerformance)
                    .setShareUsage(false)
                    .setAutoUpdate(false);
    	try (Pipeline pipeline = builder.build()) {
    		assertEquals(1, pipeline.getServices().size());
    	}
    	verify(updateService, times(1)).close();
    }

    private class TestConfig {
        final String dataFileName;
        final Constants.PerformanceProfiles performanceProfile;
        final boolean useCache;
        final boolean multiThreaded;
        final String name;
        TestConfig(
            String dataFileName,
            Constants.PerformanceProfiles performanceProfile,
            boolean useCache,
            boolean multiThreaded,
            String name) {
            this.dataFileName = dataFileName;
            this.performanceProfile = performanceProfile;
            this.useCache = useCache;
            this.multiThreaded = multiThreaded;
            this.name = name;
        }
    }
}
