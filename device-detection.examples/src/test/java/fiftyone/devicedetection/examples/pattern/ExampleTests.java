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

package fiftyone.devicedetection.examples.pattern;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.devicedetection.shared.testhelpers.Utils;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.junit.Test;

import java.io.File;

import static fiftyone.common.testhelpers.ClassPath.addToClassPath;
import static fiftyone.devicedetection.shared.testhelpers.Constants.PATTERN_DATA_FILE_NAME;
import static fiftyone.devicedetection.shared.testhelpers.Constants.UA_FILE_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExampleTests {
    private static final String dataFile =
        Utils.getFilePath(PATTERN_DATA_FILE_NAME).getAbsolutePath();

    @Test
    public void AllProfiles_Pattern() throws Exception {
        String outputFile = "testoutput";
        try {
            AllProfiles.Example allProfiles = new AllProfiles.Example(false);
            allProfiles.run(dataFile, outputFile);
            assertNotNull(allProfiles.hardwareProperties);
            assertNotNull(allProfiles.hardwareComponent);
            assertNotNull(allProfiles.hardwareProfiles);
            assertTrue(allProfiles.hardwareProfiles.size() > 0);
        } finally {
            File file = new File(outputFile);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    public void Benchmark_Pattern_SingleThreaded() throws Exception {
        Benchmark.Example benchmark = new Benchmark.Example(false);
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
            .useOnPremise(dataFile, false)
            .setShareUsage(false)
            .setAutoUpdate(false)
            .build()) {
            benchmark.run(
                pipeline,
                Utils.getFilePath(UA_FILE_NAME).getAbsolutePath(),
                1);
        }
    }

    @Test
    public void Benchmark_Pattern_MultiThreaded() throws Exception {
        Benchmark.Example benchmark = new Benchmark.Example(false);
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
            .useOnPremise(dataFile, false)
            .setShareUsage(false)
            .setAutoUpdate(false)
            .build()) {
            benchmark.run(
                pipeline,
                Utils.getFilePath(UA_FILE_NAME).getAbsolutePath(),
                8);
        }
    }

    @Test
    public void ConfigureCache_Pattern() throws Exception {
        ConfigureCache.Example configureCache = new ConfigureCache.Example(false);
        configureCache.run(dataFile);
    }

    @Test
    public void ConfigureFromFile_Pattern() throws Exception {
        addToClassPath(DeviceDetectionPatternEngine.class);
        addToClassPath(ConfigureFromFile.class);
        ConfigureFromFile.Example configureFromFile = new ConfigureFromFile.Example(false);
        configureFromFile.run();
    }

    @Test
    public void DynamicFilters_Pattern() throws Exception {
        DynamicFilters.Example dynamicFilters = new DynamicFilters.Example(false);
        dynamicFilters.run(dataFile);
    }

    @Test
    public void GettingStarted_Pattern() throws Exception {
        GettingStarted.Example gettingStarted = new GettingStarted.Example(false);
        gettingStarted.run(dataFile);
    }

    @Test
    public void MatchForProfileId_Pattern() throws Exception {
        MatchForProfileId.Example matchForProfileId = new MatchForProfileId.Example(false);
        matchForProfileId.run(dataFile);
    }

    @Test
    public void MatchMetrics_Pattern() throws Exception {
        MatchMetrics.Example matchMetrics = new MatchMetrics.Example(false);
        matchMetrics.run(dataFile);
    }

    @Test
    public void MetaData_Pattern() throws Exception {
        MetaData.Example metaData = new MetaData.Example(false);
        metaData.run(dataFile);
    }

    @Test
    public void OfflineProcessing_Pattern() throws Exception {
        String outputFile = "testoutput";
        try {
            OfflineProcessing.Example offlineProcessing = new OfflineProcessing.Example(false);
            offlineProcessing.run(
                dataFile,
                Utils.getFilePath(UA_FILE_NAME).getAbsolutePath(),
                outputFile);
        } finally {
            File file = new File(outputFile);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    public void Performance_Pattern() throws Exception {
        Performance.Example performance = new Performance.Example(false);
        performance.run(
            dataFile,
            Utils.getFilePath(UA_FILE_NAME).getAbsolutePath(),
            1000);
    }

    @Test
    public void StronglyTyped_Pattern() throws Exception {
        StronglyTyped.Example stronglyTyped = new StronglyTyped.Example(false);
        stronglyTyped.run(dataFile);
    }
}
