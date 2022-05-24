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

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.shared.DataFileHelper;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.util.FileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

/**
 * Example provides a means of measuring the detections per second in a multi-threaded
 * environment. Measurement is done against clock time, so varies quite a bit.
 */
public class Performance {
    private static final Logger logger = LoggerFactory.getLogger(Performance.class);
    public static final int THREADS = 4;
    public static final int TESTS_PER_THREAD = 10000;

    private static List<Future<TestResult>> results;

     public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String dataFilename = args.length > 0 ? args[0] : null;
        String evidenceFilename = args.length > 1 ? args[1] : null;
        run(dataFilename, evidenceFilename, THREADS, TESTS_PER_THREAD, System.out);
    }

    public static void run(String dataFilename, String evidenceFilename,
                           int threadCount, int testsPerThread, OutputStream out) throws Exception {
        logger.info("Running Performance example");

        String dataFileLocation = getDataFileLocation(dataFilename);

        File evidenceFile = getEvidenceFile(evidenceFilename);

        // Build and run a new on-premise Hash engine with the max
        // performance profile which loads all the available device data into memory.
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFileLocation, false)
                .setAutoUpdate(false)
                .setShareUsage(false)
                // Highest performance profile.
                .setPerformanceProfile(Constants.PerformanceProfiles.MaxPerformance)
                .setUsePredictiveGraph(false)
                .setUsePerformanceGraph(true)
                .setProperty("isMobile")
                .setConcurrency(threadCount)
                .build()) {

            DataFileHelper.logDataFileInfo(pipeline.getElement(DeviceDetectionHashEngine.class));
            PrintWriter writer = new PrintWriter(out, true);

            logger.info("Warming up");
            long warmpupTime = runTests(evidenceFile, pipeline, threadCount, testsPerThread);
            System.gc();
            Thread.sleep(300);
            logger.info("Running");
            long executionTime = runTests(evidenceFile, pipeline, threadCount, testsPerThread);
            long adjustedExecutionTime = executionTime - warmpupTime;
            logger.info("Finished - Execution time was {} ms, adjustment from warm-up {} ms",
                    executionTime,
                    adjustedExecutionTime);

            int totalTests = 0;
            for (Future<TestResult> future: results){
                TestResult result = future.get();
                writer.format("Thread: %,d iterations, elapsed %d millis, %,d detections / sec%n",
                        result.iterations,
                        result.duration,
                        (Math.round(1000.0 * result.iterations/ result.duration)));
                totalTests += result.iterations;
            }

            double timePerTest = ((double)executionTime/totalTests);
            writer.format("%,d detections in %d ms using %d threads. %f ms per detection, %,d " +
                            "detections / sec%n",
                    totalTests, executionTime, threadCount, timePerTest,
                    Math.round(1000.0/timePerTest));

            sumResult(writer, TestResult.Value.TRUE);
            sumResult(writer, TestResult.Value.FALSE);
            sumResult(writer, TestResult.Value.UNKNOWN);

            logger.info("finished example");
        }
    }


    /**
     * Add up the various counts of detections and output
     * @param writer the writer to use for output
     * @param key which result to report on
     * @throws Exception if there was a problem getting result from task
     */
    private static void sumResult(PrintWriter writer, TestResult.Value key) throws Exception {
        int total = 0;
        for (Future<TestResult> result : results) {
            total += result.get().get(key);
        }
        writer.println("IsMobile = " + key + ": " + total);
    }

    /**
     * Start multiple threads to process a set of User-Agents, making a note of the clock time taken
     *
     * @param evidenceFile the evidence being tested
     * @param pipeline     the pipeline
     * @return total execution time from scheduling the threads to their completion
     */
    private static long runTests(File evidenceFile, Pipeline pipeline, int threadCount,
                                 int tests) throws Exception {
        //
        List<Callable<TestResult>> callables = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            callables.add(new TestRunner(
                    getEvidenceList(evidenceFile, tests),
                    pipeline
            ));
        }
        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        long start = System.currentTimeMillis();

        // Wait for all processing to finish, and make a note of the time
        // elapsed since the processing was started.
        results = service.invokeAll(callables);
        for (Future<TestResult> result : results) {
            result.get();
        }
        service.shutdown();
        return System.currentTimeMillis() - start;
    }

    /**
     * Run the detection tests and return the results - to be run on a separate thread
     */
    private static class TestRunner implements Callable<TestResult> {

        private final Iterable<Map<String, String>> evidence;
        private final Pipeline pipeline;

        private TestResult result;

        public TestRunner(
                Iterable<Map<String, String>> evidence,
                Pipeline pipeline) {
            this.evidence = evidence;
            this.pipeline = pipeline;
            this.result = new TestResult();
            this.result.isMobileTrue = 0;
            this.result.isMobileFalse = 0;
            this.result.isMobileUnknown = 0;
        }

        @Override
        public TestResult call() throws Exception {
            // Create a new flow data to add evidence to and get
            // device data back again.
            this.result = new TestResult();
            long startTime = System.currentTimeMillis();
            for (Map<String, String> evidence : evidence) {

                // A try-with-resource block MUST be used for  the
                // FlowData instance. This ensures that native resources
                // created by the device detection engine are freed.
                try (FlowData data = pipeline.createFlowData()) {

                    // Add the evidence to the flow data.
                    data.addEvidence(evidence)
                            .process();

                    // Get the device from the engine.
                    DeviceData device = data.get(DeviceData.class);
                    // Update the counters depending on the isMobile result.
                    result.update(device.getIsMobile());
                 }
            }
            result.duration = System.currentTimeMillis() - startTime;
            return result;
        }
    }

    /**
     * Records the outcome of the test runner
     */
    static class TestResult {
        int isMobileTrue;
        int isMobileFalse;
        int isMobileUnknown;

        long duration;
        int iterations;

        enum Value {TRUE, FALSE, UNKNOWN, SUM}
        int get(Value key) {
            switch (key) {
                case TRUE: return isMobileTrue;
                case FALSE: return isMobileFalse;
                case UNKNOWN: return isMobileUnknown;
                default: return isMobileUnknown + isMobileFalse + isMobileTrue;
            }
        }

        void update(AspectPropertyValue<Boolean> isMobile){
            if (isMobile.hasValue()) {
                if (isMobile.getValue()) {
                    isMobileTrue++;
                } else {
                    isMobileFalse++;
                }
            } else {
                isMobileUnknown++;
            }
            iterations++;
        }
    }

    /**
     * Load a Yaml file as a list of documents (each being a Map containing evidence)
     * @param yamlFile a yaml file
     * @param max maximum entries
     * @return a List
     * @throws IOException in case of error
     */
    private static List<Map<String, String>> getEvidenceList(File yamlFile, int max) throws IOException {
        return StreamSupport.stream(getEvidenceIterable(yamlFile).spliterator(), false)
                .limit(max)
                .collect(Collectors.toList());
    }

    /**
     * Create an Iterable<Map<String, String>> for reading documents from the passed yamlFile
     * @param yamlFile a yamlFile
     * @return an Iterable
     * @throws IOException for file errors
     */
    @SuppressWarnings("unchecked")
    private static Iterable<Map<String, String>> getEvidenceIterable(File yamlFile) throws IOException {
        final Iterator<Object> objectIterator =
                new Yaml().loadAll(Files.newInputStream(yamlFile.toPath())).iterator();
        return () -> new Iterator<Map<String, String>>() {
            @Override
            public boolean hasNext() {
                return objectIterator.hasNext();
            }

/*  current evidence file doesn't have "header." leading the name of the evidence
            @Override
            public Map<String, String> next() {
                return (Map<String, String>) objectIterator.next();
            }
*/
            @Override
            public Map<String, String> next() {
                Map<String, String> result = new HashMap<>();
                ((Map<String, String>) objectIterator.next())
                        .forEach((k,v) -> result.put(k.startsWith("header")? k : "header." + k, v));
                return result;
            }
        };
    }

    /**
     * Tries to find the passed file, or if null a default file
     * @param evidenceFilename a filename to find
     * @return a File object
     * @throws Exception if the file was not found
     */

    @SuppressWarnings("RedundantThrows")
    private static File getEvidenceFile(String evidenceFilename) throws Exception {
        if (Objects.isNull(evidenceFilename)) {
            evidenceFilename = FileUtils.EVIDENCE_FILE_NAME;
        }

        File evidenceFile;
        try {
            evidenceFile = FileFinder.getFilePath(evidenceFilename);
        } catch (Exception e) {
            logger.error("Could not find evidence file {}", evidenceFilename);
            throw e;
        }
        return evidenceFile;
    }

    /**
     * Tries to find the passed file, or if null a default file
     * @param dataFilename a filename to find
     * @return a full pathname
     * @throws Exception if the file was not found
     */
    @SuppressWarnings("RedundantThrows")
    private static String getDataFileLocation(String dataFilename) throws Exception {
        if (Objects.isNull(dataFilename)) {
            dataFilename = FileUtils.getHashFileName();
        }
        String dataFileLocation;
        try {
            dataFileLocation = getFilePath(dataFilename).getAbsolutePath();
        } catch (Exception e) {
            DataFileHelper.cantFindDataFile(dataFilename);
            throw e;
        }
        return dataFileLocation;
    }
}
/*!
 * @example Performance.java
 * The example illustrates a "clock-time" benchmark for assessing detection speed.
 *
 * Using a YAML formatted evidence file - "20000 Evidence Records.yml" is supplied with the
 * distribution or can be obtained from the (data repository on Github)[https://github.com/51Degrees/device-detection-data/blob/master/20000%20Evidence%20Records.yml].
 *
 * It can be instructive to adjust the various parameters with which the pipeline is configured,
 * to observe the difference in performance. Requesting properties from a single component
 * reduces detection time compared with requesting properties from multiple components. If you
 * don't specify any properties to detect, then all properties are detected, for example.
 *
 * Please review (performance options)[https://51degrees.com/documentation/_device_detection__features__performance_options.html]
 * and (hash dataset options)[https://51degrees.com/documentation/4.4/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance]
 * for more information about adjusting performance.
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-dotnet/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/Performance.java).
 *
 * @include{doc} example-require-datafile.txt
 */
