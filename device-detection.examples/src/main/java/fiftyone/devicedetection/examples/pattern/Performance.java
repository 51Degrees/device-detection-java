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
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @example pattern/Performance.java
 *
 * Performance example of using 51Degrees device detection.
 *
 * The example shows how to:
 *
 * 1. Build a new on-premise Pattern engine with the high performance profile.
 * ```
 * DeviceDetectionPatternEngine engine = new DeviceDetectionPatternEngineBuilder()
 *     .setAutoUpdate(false)
 *     .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
 *     .build("51Degrees-LiteV3.2.dat", false);
 * ```
 *
 * 2. Start multiple threads to process a set of User-Agents, making a note of
 * the time at which processing was started.
 * ```
 * ReportIterable iterable = new ReportIterable(list, count, maxDistinctUAs, 40);
 * List<Callable<Void>> callables = new ArrayList<>();
 * for (int i = 0; i < threadCount; i++) {
 *     callables.add(new PerformanceCallable(iterable, pipeline, isMobileTrue, isMobileFalse));
 * }
 * ExecutorService service = Executors.newFixedThreadPool(threadCount);
 * long start = System.currentTimeMillis();
 * List<Future<Void>> results = service.invokeAll(callables);
 * ```
 *
 * 3. Wait for all processing to finish, and make a note of the time elapsed
 * since the processing was started.
 * ```
 * List<Future<Void>> results = service.invokeAll(callables);
 * for (Future<Void> result : results) {
 *     result.get();
 * }
 * long time = System.currentTimeMillis() - start;
 * ```
 *
 * 4. Output the average time to process a single User-Agent.
 * ```
 * println("Average " + (double) time / (double) count + "ms per User-Agent");
 * ```
 */
public class Performance extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV3.2.dat").getAbsolutePath();
        String uaFile = args.length > 1 ? args[1] :
            getDefaultFilePath("20000 User Agents.csv").getAbsolutePath();
        new Example(true).run(dataFile, uaFile, 10000);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile, String uaFile, int count) throws Exception {
            println("Constructing pipeline with engine " +
                "from file " + dataFile);
            // Create a simple pipeline to access the engine with.
            Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile, false)
                .setAutoUpdate(false)
                // Prefer low memory profile where all data streamed
                // from disk on-demand. Experiment with other profiles.
                .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                //.setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                .setShareUsage(false)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                .build();
            run(uaFile, count, pipeline);
        }

        private String uaFile;
        private int count;
        private Pipeline pipeline;
        private AtomicInteger isMobileTrue;
        private AtomicInteger isMobileFalse;
        private AtomicInteger isMobileUnknown;
        private int maxDistinctUAs = 10000;
        private int threadCount = 4;

        private void run(String uaFile, int count, Pipeline pipeline) throws Exception {
            this.uaFile = uaFile;
            this.count = count;
            this.pipeline = pipeline;
            try {
                println("Processing " + count + " User-Agents from " + uaFile);
                println("The " + count + " process calls will use a " +
                    "maximum of " + maxDistinctUAs + " distinct User-Agents");

                println("Callibrating");
                long calibrationTime = runThreads(true);
                println();
                println("Processing");
                long time = runThreads(false);
                double detectionsPerSecond = (double) (count * threadCount * 1000) / (double) (time - calibrationTime);
                println();
                printf("Average %.2f detections per second using %d threads (%2f per thread)\n",
                    detectionsPerSecond,
                    threadCount,
                    detectionsPerSecond / threadCount);
                printf("%4f ms per User-Agent effective (%4f actual)\n",
                    1000 / detectionsPerSecond,
                    (1000 * threadCount) / detectionsPerSecond);
                println("IsMobile = True  : " + isMobileTrue.get());
                println("IsMobile = False : " + isMobileFalse.get());
                println("IsMobile = Unknown : " + isMobileUnknown.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private long runThreads(boolean calibration) throws IOException, InterruptedException, ExecutionException {
            isMobileTrue = new AtomicInteger(0);
            isMobileFalse = new AtomicInteger(0);
            isMobileUnknown = new AtomicInteger(0);
            List<String> userAgents = new ArrayList<>();
            for (String userAgent : getUserAgents(uaFile, count)) {
                userAgents.add(userAgent);
            }
            List<Callable<Void>> callables = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                callables.add(new PerformanceCallable(
                    new ReportIterable(userAgents, count, maxDistinctUAs, 40 / threadCount),
                    pipeline,
                    isMobileTrue,
                    isMobileFalse,
                    isMobileUnknown,
                    calibration));
            }
            ExecutorService service = Executors.newFixedThreadPool(threadCount);
            long start = System.currentTimeMillis();
            List<Future<Void>> results = service.invokeAll(callables);
            for (Future<Void> result : results) {
                result.get();
            }
            service.shutdown();
            return System.currentTimeMillis() - start;

        }

        private static class PerformanceCallable implements Callable<Void> {

            private final Iterable<String> userAgents;
            private final Pipeline pipeline;
            AtomicInteger isMobileTrue;
            AtomicInteger isMobileFalse;
            AtomicInteger isMobileUnknown;
            private final boolean calibration;

            public PerformanceCallable(
                Iterable<String> userAgents,
                Pipeline pipeline,
                AtomicInteger isMobileTrue,
                AtomicInteger isMobileFalse,
                AtomicInteger isMobileUnknown,
                boolean calibration) {
                this.userAgents = userAgents;
                this.pipeline = pipeline;
                this.isMobileTrue = isMobileTrue;
                this.isMobileFalse = isMobileFalse;
                this.isMobileUnknown = isMobileUnknown;
                this.calibration = calibration;
            }

            @Override
            public Void call() throws Exception {
                // Create a new flow data to add evidence to and get
                // device data back again.
                Iterator<String> userAgentsIterator = userAgents.iterator();
                while (userAgentsIterator.hasNext()) {
                    String userAgent = userAgentsIterator.next();
                    if (calibration) {
                        isMobileFalse.incrementAndGet();
                    }
                    else {
                        FlowData data = pipeline.createFlowData();
                        // Add the User-Agent as evidence to the flow data.
                        data.addEvidence("header.User-Agent", userAgent)
                            .process();

                        // Get the device from the engine.
                        DeviceData device = data.get(DeviceData.class);
                        // Update the counters depending on the IsMobile
                        // result.
                        AspectPropertyValue<Boolean> isMobile =
                            device.getIsMobile();
                        if (isMobile.hasValue()) {
                            if (isMobile.getValue()) {
                                isMobileTrue.incrementAndGet();
                            } else {
                                isMobileFalse.incrementAndGet();
                            }
                        }
                        else {
                            isMobileUnknown.incrementAndGet();
                        }
                    }
                }
                return null;
            }
        }
    }
}
