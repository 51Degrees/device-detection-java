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

package fiftyone.devicedetection.examples.hash;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Benchmark extends ProgramBase {
    // the default number of threads if one is not provided.
    private static final int defaultNumberOfThreads =
            Runtime.getRuntime().availableProcessors();
    // the maximum size of the queue of User-Agents to benchmark.
    private static final int QUEUE_SIZE = 512;

    /**
     * Runs three different benchmarks for popular configurations.
     *
     * @param deviceDataFile  path to the 51Degrees device data file for testing
     * @param userAgentFile   path to a text file of User-Agents
     * @param numberOfThreads number of concurrent threads
     * @throws Exception as a catch all
     */
    private static void runBenchmarks(
            String deviceDataFile,
            String userAgentFile,
            int numberOfThreads) throws Exception {

        Example example = new Example(true);

        System.out.printf("Benchmarking in memory dataset: %s\r\n", deviceDataFile);
        byte[] fileContent = Files.readAllBytes(new File(userAgentFile).toPath());
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(fileContent)
                .setAutoUpdate(false)
                .setShareUsage(false)
                .setConcurrency(numberOfThreads)
                .build()) {

            example.run(
                    pipeline,
                    userAgentFile,
                    numberOfThreads);
        }

        System.out.printf("Benchmarking LowMemory dataset: %s\r\n", deviceDataFile);

        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(deviceDataFile, false)
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                .setAutoUpdate(false)
                .setShareUsage(false)
                .setConcurrency(numberOfThreads)
                .build()) {

            example.run(pipeline,
                    userAgentFile,
                    numberOfThreads);
        }

        System.out.printf("Benchmarking HighPerformance dataset: %s\r\n", deviceDataFile);
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(deviceDataFile, false)
                .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                .setAutoUpdate(false)
                .setShareUsage(false)
                .setConcurrency(numberOfThreads)
                .build()) {

            example.run(pipeline,
                    userAgentFile,
                    numberOfThreads);
        }

        System.out.printf("Benchmarking Balanced dataset: %s\r\n", deviceDataFile);

        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(deviceDataFile, false)
                .setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                .setAutoUpdate(false)
                .setShareUsage(false)
                .setConcurrency(numberOfThreads)
                .build()) {

            example.run(pipeline,
                    userAgentFile,
                    numberOfThreads);
        }
    }

    /**
     * Instantiates this class and starts
     * {@link #runBenchmarks(String, String, int)} with parameters from the
     * command line.
     *
     * @param args command line arguments.
     * @throws IOException          if the data file cannot be accessed.
     * @throws InterruptedException if the benchmark threads are interrupted.
     */
    public static void main(String[] args)
            throws Exception {
        int numberOfThreads = defaultNumberOfThreads;

        String dataFile = null;
        List<String> otherFiles = new ArrayList<>();
        for (String arg : args) {
            try {
                numberOfThreads = Integer.parseInt(arg);
            } catch (NumberFormatException ex) {
                if (arg.contains("51Degrees") && arg.endsWith(".trie")) {
                    dataFile = arg;
                } else {
                    otherFiles.add(arg);
                }
            }
        }
        if (dataFile == null) {
            dataFile = getDefaultFilePath("51Degrees-LiteV4.1.hash").getAbsolutePath();
        }

        // execute the benchmarks now the data has been gathered
        runBenchmarks(
                dataFile,
                otherFiles.size() > 0 ?
                        otherFiles.get(0) :
                        getDefaultFilePath("20000 User Agents.csv").getAbsolutePath(),
                numberOfThreads);
    }

    /**
     * Encapsulates the logic that is executed in each thread of the benchmark.
     */
    private static class BenchmarkRunnable implements Runnable {

        // the benchmark that is being executed
        private final Example bm;

        BenchmarkRunnable(Example bm) {
            this.bm = bm;
        }

        @Override
        public void run() {
            try {
                int workerCheckSum = 0;
                String userAgentString = bm.queue.poll(1, TimeUnit.SECONDS);
                while (userAgentString != null ||
                        bm.addingComplete == false) {
                    if (userAgentString != null) {
                        // the benchmark is for detection time only
                        long start = System.nanoTime();

                        // A try-with-resource block MUST be used for the
                        // FlowData instance. This ensures that native resources
                        // created by the device detection engine are freed.
                        try (FlowData flowData = bm.pipeline.createFlowData()) {
                            flowData
                                    .addEvidence("header.user-agent", userAgentString)
                                    .process();
                            bm.elapsedNano.addAndGet(
                                    System.nanoTime() - start);

                            // Calculate a checksum to compare different runs on
                            // the same data. Retrieve all the values for all the
                            // profiles to simulate a the most stressfull detection
                            // scenario.
                            DeviceData device = flowData.get(DeviceData.class);
                            if (device != null) {
                                for (Object value : device.asKeyMap().values()) {
                                    if (value != null) {
                                        workerCheckSum += value.hashCode();
                                    }
                                }
                            }
                            bm.count.incrementAndGet();
                        } catch (Exception e) {
                            Logger.getLogger(Benchmark.class.getName()).log(
                                    Level.SEVERE, null, e);
                        }
                    }
                    userAgentString = bm.queue.poll(1, TimeUnit.SECONDS);
                }
                synchronized (bm) {
                    bm.checkSum += workerCheckSum;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Benchmark.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
    }

    static class Example extends ExampleBase {

        // number of User-Agents processed to determine the result.
        private final AtomicInteger count = new AtomicInteger();

        // processing time in millseconds from all threads
        private final AtomicLong elapsedNano = new AtomicLong();

        // used to ensure compiler optimiser doesn't optimise out the very
        // method that the benchmark is testing.
        private int checkSum;

        // set to true when the queue has had all elements added to it.
        private boolean addingComplete = false;

        // queue of User-Agent strings for processing.
        private LinkedBlockingQueue<String> queue;

        // pipeline to use for processing.
        private Pipeline pipeline;

        public Example(boolean printOutput) {
            super(printOutput);
        }

        // the average time in milliseconds for a single thread.
        public double getAverageDetectionTimePerThread() {
            return (elapsedNano.doubleValue() / 1000000) / getCount();
        }

        // the number of User-Agents included in the test.
        public int getCount() {
            return count.intValue();
        }

        void run(
                Pipeline pipeline,
                String userAgentFile,
                int numberOfThreads) throws Exception {
            // A try-with-resource block MUST be used for the FlowData instance.
            // This ensures that native resources created by the device
            // detection engine are freed.
            try (FlowData f = pipeline.createFlowData()) {
                f.addEvidence("header.user-agent", "test");
                f.process();
                DeviceData d = f.get(DeviceData.class);
                d.getIsMobile();
            }

            if (new File(userAgentFile).exists() == false) {
                throw new IllegalArgumentException(String.format(
                        "File '%s' does not exist.",
                        userAgentFile));
            }
            this.queue = new LinkedBlockingQueue<>(QUEUE_SIZE);

            String userAgentString;

            // initialise the benchmark variables
            this.pipeline = pipeline;
            this.elapsedNano.set(0);
            this.count.set(0);
            this.checkSum = 0;

            // start multiple threads in a fixed pool
            ExecutorService executor = Executors.newFixedThreadPool(
                    numberOfThreads);
            for (int i = 0; i < numberOfThreads; i++) {
                executor.execute(new BenchmarkRunnable(this));
            }

            // read each line of the User-Agents source file adding them to the
            // shared queue for each thread
            try (BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(userAgentFile))) {
                int userAgentsRead = 0;
                while ((userAgentString = bufferedReader.readLine()) != null) {
                    userAgentsRead++;
                    this.queue.put(userAgentString);
                    if (userAgentsRead % 50000 == 0) {
                        print("+");
                    }
                }
                print("\r\n");
                this.addingComplete = true;
                executor.shutdown();
                while (executor.isTerminated() == false) {
                    // Do nothing.
                }
            }

            // output the results from the benchmark to the console
            println("");
            printf(
                    "Average millseconds per detection per thread: %f \r\n",
                    getAverageDetectionTimePerThread());
            printf(
                    "Concurrent threads: %d \r\n",
                    numberOfThreads);
            printf(
                    "User-Agents processed: %d \r\n",
                    getCount());
            printf(
                    "Checksum: %d \r\n",
                    checkSum);
        }
    }
}
