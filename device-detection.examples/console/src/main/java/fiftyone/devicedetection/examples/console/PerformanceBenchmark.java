/*
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2022 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 *  (EUPL) v.1.2 and is subject to its terms as set out below.
 *
 *  If a copy of the EUPL was not distributed with this file, You can obtain
 *  one at https://opensource.org/licenses/EUPL-1.2.
 *
 *  The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 *  amended by the European Commission) shall be deemed incompatible for
 *  the purposes of the Work and the provisions of the compatibility
 *  clause in Article 5 of the EUPL shall not apply.
 *
 *   If using the Work as, or as part of, a network application, by
 *   including the attribution notice(s) required under Article 5 of the EUPL
 *   in the end user terms of the application under an appropriate heading,
 *   such notice(s) shall fulfill the requirements of that article.
 */

package fiftyone.devicedetection.examples.console;

import fiftyone.common.testhelpers.LogbackHelper;
import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.shared.DataFileHelper;
import fiftyone.devicedetection.examples.shared.EvidenceHelper;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.util.FileFinder;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

import static fiftyone.devicedetection.examples.shared.DataFileHelper.getDataFileLocation;
import static fiftyone.devicedetection.examples.shared.DataFileHelper.getEvidenceFile;
import static fiftyone.pipeline.engines.Constants.PerformanceProfiles.*;

/**
 * The example illustrates the flexibility with which the 51Degrees pipeline can be configured
 * to achieve a range of outcomes relating to speed, accuracy, predictive power, memory usage.
 * <p>
 * Please see <a href="//51degrees.com/documentation/_device_detection__features__performance_options.html">performance options</a>)
 * and <a href="//51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance">hash dataset options</a>
 * for more information about adjusting performance.
 */
public class PerformanceBenchmark {
    // the default number of threads if one is not provided.
    public static final int DEFAULT_NUMBER_OF_THREADS = 4;
    // the number of tests to execute.
    public static final int TESTS_PER_THREAD = 10000;

    public static final Logger logger = LoggerFactory.getLogger(PerformanceBenchmark.class);

    // where the results of the tests are gathered
    private List<Future<BenchmarkResult>> resultList;
    private int numberOfThreads = DEFAULT_NUMBER_OF_THREADS;
    private List<Map<String, String>> evidence;
    private String dataFileLocation;
    private PrintWriter writer;

    // a default set of configurations: (profile, allProperties, performanceGraph, predictiveGraph)
    public static PerformanceConfiguration [] DEFAULT_PERFORMANCE_CONFIGURATIONS = {
            new PerformanceConfiguration(MaxPerformance, false, false, true),
            new PerformanceConfiguration(MaxPerformance, false, true, false),
            new PerformanceConfiguration(MaxPerformance, true, true, false)
    };


    public static void main(String[] args) throws Exception {
        LogbackHelper.configureLogback(FileFinder.getFilePath("logback.xml"));

        String dataFilename = args.length > 0 ? args[0] : null;
        String evidenceFilename = args.length > 1 ? args[1] : null;
        int numberOfThreads = DEFAULT_NUMBER_OF_THREADS;
        if (args.length > 2) {
            numberOfThreads = Integer.parseInt(args[2]);
        }

        new PerformanceBenchmark().runBenchmarks(DEFAULT_PERFORMANCE_CONFIGURATIONS,
                dataFilename,
                evidenceFilename,
                numberOfThreads,
                new PrintWriter(System.out,true));
    }

    /**
     * Runs benchmarks for various configurations.
     *
     * @param dataFilename     path to the 51Degrees device data file for testing
     * @param evidenceFilename path to a text file of evidence
     * @param numberOfThreads  number of concurrent threads
     * @throws Exception as a catch all
     */
    protected void runBenchmarks(PerformanceConfiguration[] performanceConfigurations,
                                 String dataFilename,
                                 String evidenceFilename,
                                 int numberOfThreads,
                                 PrintWriter writer) throws Exception {

        logger.info("Running Performance example");

        this.dataFileLocation = getDataFileLocation(dataFilename);

        File evidenceFile = getEvidenceFile(evidenceFilename);
        this.evidence = Collections.unmodifiableList(
                EvidenceHelper.getEvidenceList(evidenceFile, 20000));
        this.numberOfThreads = numberOfThreads;
        this.writer = writer;

        // run "from memory" benchmarks - the only profiles that really make sense
        // are maxPerformance
        for (PerformanceConfiguration config: performanceConfigurations){
            if (config.profile.equals(MaxPerformance)) {
                executeBenchmark(false, config);
            }
        }

        // run the selected benchmarks from disk
        for (PerformanceConfiguration config: performanceConfigurations){
            executeBenchmark(true, config);
        }

        logger.info("Finished Performance example");
    }

    /**
     * Set up and execute a benchmark test
     * @param configureFromDisk configure the pipeline from disk or from buffer
     * @param config the configuration to use for this benchmark
     * @throws Exception to satisfy undelying calls
     */
    private void executeBenchmark(boolean configureFromDisk,
                                  PerformanceConfiguration config) throws Exception {
        logger.info(MarkerFactory.getMarker(config.profile.name() + " " +
                        config.allProperties + " " +
                        config.performanceGraph + " " +
                        config.predictiveGraph),
                "Benchmarking with profile: {} AllProperties: {}, " +
                        "performanceGraph: {}, predictiveGraph {}",
                config.profile,
                config.allProperties,
                config.performanceGraph,
                config.predictiveGraph);

        Pipeline pipeline = null;
        try {
            if (configureFromDisk) {
                logger.info("Load from disk");
                DeviceDetectionOnPremisePipelineBuilder builder = new DeviceDetectionPipelineBuilder()
                        // load from disk
                        .useOnPremise(dataFileLocation, false);

                setPipelinePerformanceProperties(builder, config);
                pipeline = builder.build();

                DataFileHelper.logDataFileInfo(pipeline.getElement(DeviceDetectionHashEngine.class));
            } else {
                logger.info("Load memory from {}", dataFileLocation);
                byte[] fileContent = Files.readAllBytes(new File(dataFileLocation).toPath());
                logger.info("Memory loaded");

                // create a pipeline builder
                DeviceDetectionOnPremisePipelineBuilder builder = new DeviceDetectionPipelineBuilder()
                        // load from buffer
                        .useOnPremise(fileContent);

                setPipelinePerformanceProperties(builder, config);
                pipeline = builder.build();
            }

            // run the benchmarks twice, once to warm up the JVM
            logger.info("Warming up");
            long warmpupTime = runTests(pipeline);
            System.gc();
            Thread.sleep(300);

            logger.info("Running");
            long executionTime = runTests(pipeline);
            long adjustedExecutionTime = executionTime - warmpupTime;
            logger.info("Finished - Execution time was {} ms, adjustment from warm-up {} ms",
                    executionTime, adjustedExecutionTime);
        } finally {
            if (Objects.nonNull(pipeline)) {
                pipeline.close();
            }
        }
        doReport();
    }

    /**
     * Helper to set the critical performance settings of the pipeline, shared between memory and
     * disk data source pipeline creation, to ensure consistency.
     * <p>
     * Please see <a href="//51degrees.com/documentation/_device_detection__features__performance_options.html">performance options</a>)
     * and <a href="//51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance">hash dataset options</a>
     * for more information about adjusting performance.
     * @param builder          the builder to configure
     * @param config benchmark configuration
     */
    private void setPipelinePerformanceProperties(
            DeviceDetectionOnPremisePipelineBuilder builder,
            PerformanceConfiguration config) {
        // the different profiles provide for trading off memory usage
        builder.setPerformanceProfile(config.profile)
        // set this to false for testing
        .setAutoUpdate(false)
        // set this to false for testing
        .setShareUsage(false)
        // hint for cache concurrency
        .setConcurrency(numberOfThreads);
        // performance is improved by selecting only the properties you intend to use
        // Requesting properties from a single component
        // reduces detection time compared with requesting properties from multiple components.
        // If you don't specify any properties to detect, then all properties are detected,
        // here we choose "all properties" by specifying none, or just "isMobile"
        if (BooleanUtils.isFalse(config.allProperties)) {
            builder.setProperty("isMobile");
        }
        // choose performanceGraph or predictiveGraph, choosing both runs uses
        // performance first then predictive, if the result was not found in performance
        builder.setUsePerformanceGraph(config.performanceGraph);
        builder.setUsePredictiveGraph(config.predictiveGraph);
    }

    /**
     * Report per thread and overall detection performance
     * @throws Exception to satisfy needs of called APIs
     */
    private void doReport() throws Exception {
        long totalMillis = 0;
        long totalChecks = 0;
        int checksum = 0;
        for (Future<BenchmarkResult> result : resultList) {
            BenchmarkResult bmr = result.get();

            writer.format("Thread:  %,d detections, elapsed %f seconds, %,d Detections per second%n",
                    bmr.count,
                    bmr.elapsedMillis/1000.0,
                    (Math.round(1000.0 * bmr.count/ bmr.elapsedMillis)));

            totalMillis += bmr.elapsedMillis;
            totalChecks += bmr.count;
            checksum += bmr.checkSum;
        }

        // output the results from the benchmark to the console
<<<<<<< Updated upstream
        double millisPerTest = ((double) totalMillis / (resultList.size() * totalChecks));
=======
        double millisPerTest = ((double) totalMillis / (numberOfThreads * totalChecks));
>>>>>>> Stashed changes
        writer.format("Overall: %,d detections, Average millisecs per detection: %f, Detections per second: %,d\n",
                totalChecks, millisPerTest, Math.round(1000.0/millisPerTest));
        writer.format("Overall: Concurrent threads: %d, Checksum: %x \n", numberOfThreads, checksum);
        writer.println();
    }

    /**
     * Execute detections on specified number of threads
     * @param pipeline the pipeline to use
     * @return elapsed millis
     * @throws Exception to satisfy called APIs
     */
    private long runTests(Pipeline pipeline) throws Exception {

        // create a list of callables
        List<Callable<BenchmarkResult>> callables = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            callables.add(new BenchmarkRunnable(pipeline, evidence));
        }
        // start multiple threads in a fixed pool
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        long start = System.currentTimeMillis();
        // start all the threads
        resultList = service.invokeAll(callables);
        // wait for all the threads to complete
        for (Future<BenchmarkResult> result : resultList) {
            result.get();
        }
        long duration = System.currentTimeMillis() - start;
        service.shutdown();
        return duration;
    }


    /**
     * Callable that implements the logic of the test for each thread
     */
    private static class BenchmarkRunnable implements Callable<BenchmarkResult> {

        // the benchmark that is being executed
        private final BenchmarkResult result;
        private final List<Map<String, String>> testList;
        private final Pipeline pipeline;

        BenchmarkRunnable(Pipeline pipeline, List<Map<String, String>> evidence) {
            this.testList = evidence;
            // initialise the benchmark variables
            this.pipeline = pipeline;
            this.result = new BenchmarkResult();

            result.elapsedMillis = 0;
            result.count = 0;
            result.checkSum = 0;
        }


        @Override
        public BenchmarkResult call() {
            result.checkSum = 0;
            long start = System.currentTimeMillis();
            for (Map<String, String> evidence : testList) {
                // the benchmark is for detection time only

                // A try-with-resource block MUST be used for the
                // FlowData instance. This ensures that native resources
                // created by the device detection engine are freed.
                try (FlowData flowData = pipeline.createFlowData()) {
                    flowData
                            .addEvidence(evidence)
                            .process();

                    // Calculate a checksum to compare different runs on
                    // the same data.
                    DeviceData device = flowData.get(DeviceData.class);
                    if (device != null) {
                        if (device.getIsMobile().hasValue()) {
                            Object value = device.getIsMobile().getValue();
                            if (value != null) {
                                result.checkSum += value.hashCode();
                            }
                        }
                    }
                    result.count++;
                    if (result.count >= TESTS_PER_THREAD) {
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Exception getting flow data", e);
                }
            }
            result.elapsedMillis += System.currentTimeMillis() - start;
            return result;
        }
    }


    static class BenchmarkResult {

        // number of device evidence processed to determine the result.
        private long count;

        // processing time in millis this thread
        private long elapsedMillis;

        // used to ensure compiler optimiser doesn't optimise out the very
        // method that the benchmark is testing.
        private int checkSum;


    }

    public static class PerformanceConfiguration {
        Constants.PerformanceProfiles profile;
        boolean allProperties;
        boolean performanceGraph;
        boolean predictiveGraph;

        public PerformanceConfiguration(Constants.PerformanceProfiles profile,
                                        boolean allProperties, boolean performanceGraph,
                                        boolean predictiveGraph) {
            this.profile = profile;
            this.allProperties = allProperties;
            this.performanceGraph = performanceGraph;
            this.predictiveGraph = predictiveGraph;
        }
    }
}

/*!
 * @example PerformanceBenchmark.java
 * The example illustrates a "clock-time" benchmark for assessing detection speed.
 *
 * Using a YAML formatted evidence file - "20000 Evidence Records.yml" - supplied with the
 * distribution or can be obtained from the (data repository on Github)[https://github.com/51Degrees/device-detection-data/blob/master/20000%20Evidence%20Records.yml].
 *
 * It's important to understand the trade-offs between performance, memory usage and accuracy, that
 * the 51Degrees pipeline configuration makes available, and this example shows a range of
 * different configurations to illustrate the difference in performance.
 *
 * Requesting properties from a single component
 * reduces detection time compared with requesting properties from multiple components. If you
 * don't specify any properties to detect, then all properties are detected.
 *
 * Please review (performance options)[https://51degrees.com/documentation/_device_detection__features__performance_options.html]
 * and (hash dataset options)[https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance]
 * for more information about adjusting performance.
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-dotnet/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/Performance.java).
 *
 * @include{doc} example-require-datafile.txt
 */

