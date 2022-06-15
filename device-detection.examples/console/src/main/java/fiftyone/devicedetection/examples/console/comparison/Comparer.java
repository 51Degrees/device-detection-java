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

package fiftyone.devicedetection.examples.console.comparison;

import fiftyone.common.testhelpers.LogbackHelper;
import fiftyone.devicedetection.examples.console.comparison.Detection.BenchmarkResult;
import fiftyone.devicedetection.examples.console.comparison.Detection.Request;
import fiftyone.devicedetection.examples.console.comparison.Detection.Solution;
import fiftyone.devicedetection.examples.shared.EvidenceHelper;
import fiftyone.pipeline.util.FileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

import static fiftyone.devicedetection.examples.shared.DataFileHelper.getEvidenceFile;

/**
 * This class provides a framework and runner for comparing different device detection solutions.
 * It is provided in a basic form to compare detection time. Different solutions may be compared
 * by implementing the interfaces in {@link Detection}. In this example we have provided
 * such implementations for 51Degrees and BrowsCap, and skeleton commented-out implementations
 * for ScientiaMobile (WURFL) and DeviceAtlas. We don't have access to those APIs so the
 * code has not been tested and it is left to those interested to uncomment and get working.
 */
public class Comparer {
    public static final int DEFAULT_NUMBER_OF_THREADS = 4;
    public static final int DEFAULT_NUMBER_OF_RESULTS = 2500;
    @SuppressWarnings("resource")
    public static final Solution[] DEFAULT_SOLUTIONS = {
            new DetectionImplFiftyOneDegrees.FiftyOneSolution(),
            new DetectionImplBrowsCap.BrowsCapSolution()};
    private int numberOfThreads;
    private int numberOfResults;
    private List<Map<String, String>> evidenceList;

    private static final Logger logger = LoggerFactory.getLogger(Comparer.class);

    public static void main(String[] args) throws Exception {
        LogbackHelper.configureLogback(FileFinder.getFilePath("logback.xml"));
        new Comparer().compare(Arrays.asList(DEFAULT_SOLUTIONS),
                DEFAULT_NUMBER_OF_THREADS,
                DEFAULT_NUMBER_OF_RESULTS,
                new Reporting.Minimal(),
                new PrintWriter(System.out, true));
    }

    /**
     * Execute the benchmarks using the solutions provided
     * @param solutions some solutions
     * @param numberOfThreads how many threads to run for each solution
     * @param numberOfResults how many detections to do per thread
     * @param reportingModel how and what to report
     * @param writer where to report it
     * @throws Exception to satisfy underlying APIs
     */
    public void compare(List<Solution> solutions,
                        int numberOfThreads,
                        int numberOfResults,
                        Reporting reportingModel,
                        PrintWriter writer) throws Exception {

        logger.info("Starting comparison");
        this.numberOfThreads = numberOfThreads;
        this.numberOfResults = numberOfResults;

        // load the evidence as a list, using the default which comes with the distribution
        File evidenceFile = getEvidenceFile(null);
        this.evidenceList = Collections.unmodifiableList(
                EvidenceHelper.getEvidenceList(evidenceFile, numberOfResults));

        // iterate over the solutions
        List<ExecutionResult> executionResults = new ArrayList<>(solutions.size());
        for (Solution solution : solutions) {
            logger.info("Benchmarking {}", solution.getVendorId());
            // init
            solution.initialise(numberOfThreads);
            try {
                // time how long it takes to execute all threads
                long timeNow = System.currentTimeMillis();
                List<BenchmarkResult> result = runBenchmarks(solution);
                long elapsedMillis = System.currentTimeMillis() - timeNow;
                // add to results for this vendor
                executionResults.add(new ExecutionResult(solution.getVendorId(),
                        result, elapsedMillis));
            } finally {
                solution.close();
            }
        }
        logger.info("Preparing reports");
        reportingModel.report(executionResults, writer);
        logger.info("Comparison done");
    }

    /**
     * Initiate the benchmark threads
     * @param solution the solution being benchmarked
     * @return result of the benchmark
     * @throws Exception to satisfy called APIs
     */
    public List<BenchmarkResult> runBenchmarks(Solution solution) throws Exception {
        List<BenchmarkResult> results = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        try {
            // create a list of tasks to be run
            List<Callable<BenchmarkResult>> callables = new ArrayList<>();
            for (int i = 0; i < numberOfThreads; i++) {
                callables.add(new Benchmark(solution, evidenceList, numberOfResults));
            }
            // run the tasks
            List<Future<BenchmarkResult>> futures = service.invokeAll(callables);
            // wait for the tasks to finish
            for (Future<BenchmarkResult> future : futures) {
                results.add(future.get());
            }
        } finally {
            service.shutdown();
        }
        return results;
    }

    /**
     * Implementation of the benchmark for a single thread
     */
    static class Benchmark implements Callable<BenchmarkResult> {
        private final Solution solution;
        private final BenchmarkResult result;
        private final List<Map<String, String>> evidenceList;
        private final int count;

        public Benchmark(Solution solution, List<Map<String, String>> evidence, int count) {
            this.solution = solution;
            this.evidenceList = evidence;
            this.result = new BenchmarkResult(count);
            this.count = count;
        }

        @Override
        public BenchmarkResult call() throws Exception {
            int counter = 0;
            long startTime = System.currentTimeMillis();
            // iterate over the evidence and carry out detection
            for (Map<String, String> evidence : evidenceList) {
                // do the detection
                result.properties.add(solution.detect(new Request(evidence)));
                counter++;
                if (counter >= count) {
                    break;
                }
            }
            // record elapsed time
            result.elapsedMillis = System.currentTimeMillis() - startTime;
            result.count = count;
            return result;
        }
    }

    /**
     * A list of {@link BenchmarkResult}, one per thread, and the elapsed time
     * between starting the threads and them all completing
     */
    static class ExecutionResult {
        final List<BenchmarkResult> benchmarkResults;
        final String solutionId;
        final long elapsedMillis;
        public ExecutionResult(String solutionId,
                               List<BenchmarkResult> benchmarkResults,
                               long elapsedMillis) {
            this.benchmarkResults = benchmarkResults;
            this.solutionId = solutionId;
            this.elapsedMillis = elapsedMillis;
        }
    }
}
