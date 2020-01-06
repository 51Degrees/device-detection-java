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

package fiftyone.devicedetection.examples;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngineBuilder;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.ElementPropertyMetaData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.core.flowelements.PipelineBuilder;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.flowelements.OnPremiseAspectEngine;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <!-- tutorial -->
 * <h3>Configuration</h3>
 * <p>
 * As standard the Hash provider is available by uncommenting the code starting at the FiftyOneDegreesHashTrieProvider method and then adding the compiled Hash JAR as a dependency which can be found <a href="https://github.com/51Degrees/Device-Detection/blob/master/java/trie/target/">here</a>.
 * <p>
 * You can build a new version of the dependency by cloning this repository and following these steps:
 * <p>
 * <i>NOTE: SWIG and and Maven are required to compile a new JAR dependency</i>
 * <pre class="prettyprint lang-sh">
 * <code>
 * git clone https://github.com/51Degrees/Device-Detection<br>
 * cd Device-Detection/java/<br>
 * build.sh (build.bat for Windows)<br>
 * cd trie<br>
 * mvn install<br>
 * </code>
 * </pre>
 * <p>
 * Add the compiled JAR file to your project and ensure you add a reference to your Hash Trie data file when you run the comparison.
 * <p>
 * Compares multiple device detection methods for accuracy and performance
 * outputting a single CSV file where each row contains the results from one
 * or more solutions for each of the target User-Agents provided.
 * <p>
 * <h3>Expected Results</h3>
 * <table class="dnnGrid" border="1">
 * <tr class="dnnGridHeader">
 * <th>Provider</th>
 * <td>Browscap</td>
 * <td>51Degrees</td>
 * </tr>
 * <tr class="dnnGridItem">
 * <th>Time to initialise provider (ms)</th>
 * <td>7,571.2</td>
 * <td>311.4</td>
 * </tr>
 * <tr class="dnnGridAltItem">
 * <th>Average time per detection per thread (ms)</th>
 * <td>0.0137752</td>
 * <td>0.0108086</td>
 * </tr>
 * <caption>
 * The comparison was performed using a single thread on a Lenovo G710
 * Laptop with a Quad Core 2.2GHz CPU and 8GB of main memory.
 * Input data: 51Degrees-Enterprise device data and a sample of
 * <a href="https://raw.githubusercontent.com/51Degrees/Java-Device-Detection/master/data/20000%20User%20Agents.csv">
 * 20,000 User-Agents</a>
 * </caption>
 * </table>
 * <p>
 * <p>
 * <a href="https://browscap.org/">Browscap</a>, or Browser Capabilities project
 * , is an open source project which maintains and offers free downloads of a
 * browscap.ini file, a browser capabilities database. It is a list of all known
 * browsers and bots, along with their default capabilities and limitations.
 * <p>
 * <p>
 * This example implements the Blueconic java library which is available on
 * <a href="https://github.com/blueconic/browscap-java">GitHub</a> and
 * <a href="https://mvnrepository.com/artifact/com.blueconic/browscap-java">Maven</a>.
 * <p>
 * <p>
 * Note: the implementations for WURFL and DeviceAtlas has not been tested
 * by the original author as 51Degrees do not have access to the associated
 * source code or data files. They have been generated theoretically from the
 * associated API documentation provided but are intended to be easy to modify.
 * The code marked "UNCOMMENT" will need to be uncommented and the associated
 * packages and data files obtained from ScientiaMobile and / or DeviceAtlas
 * to enable the associated providers.
 * <p>
 * <!-- tutorial -->
 */
public class Comparison extends ProgramBase {

    // the default number of threads if one is not provided.
    private static final int defaultNumberOfThreads = 1;
    // the maximum size of the queue of User-Agents for comparison.
    private static final int QUEUE_SIZE = 512;
    // multiplier used to determine the size of the provider's cache
    private static final float CACHE_MULTIPLIER = (float) 0.25;
    private static final String UNSUPPORTED_VALUE = "NOT AVAILABLE";
    // separator to use with the input data source
    private static final String CSV_INPUT_REGEX = "\\s\\|\\s";
    // separator to use in the CSV file.
    private static final char CSV_SEPARATER = ',';
    // string quote to use in the CSV file.
    private static final char CSV_QUOTE = '"';
    // single quote in a CSV string.
    private static final String CSV_SINGLE_QUOTE =
        new String(new char[]{CSV_QUOTE});
    // double quote to replace a single quote with in a string.
    private static final String CSV_DOUBLE_QUOTE =
        new String(new char[]{CSV_QUOTE, CSV_QUOTE});
    // report progress every X milliseconds
    private static final int PROGRESS_REPORT_INTERVAL = 5000;
    // number of User-Agents processed to determine the result.
    private final AtomicInteger count = new AtomicInteger();
    // processing time in millseconds from all threads
    private final AtomicLong elapsedNano = new AtomicLong();
    // queue of User-Agent strings for processing.
    private final LinkedBlockingQueue<Result> queue;
    // set to true when the queue has had all elements added to it.
    private boolean addingComplete = false;
    // provider to use for processing.
    private ComparisonProvider provider;

    public Comparison() {
        this.queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    }

    private static void runComparison(
        ComparisonProvider provider,
        LinkedList<Request> requests,
        int numberOfThreads) throws IOException, InterruptedException {
        new Comparison().run(provider, requests, numberOfThreads);
    }

    static LinkedList<Request> readUserAgents(String userAgentFile)
        throws IOException {
        String line;
        LinkedList<Request> requests = new LinkedList<Request>();
        BufferedReader bufferedReader = new BufferedReader(
            new FileReader(userAgentFile));
        while ((line = bufferedReader.readLine()) != null) {
            String[] values = line.split(CSV_INPUT_REGEX);
            Request request = new Request(
                values[0],
                values.length > 1 ? Integer.parseInt(values[1]) : 4);
            requests.add(request);
        }
        bufferedReader.close();
        return requests;
    }

    private static void writeFirstString(
        BufferedWriter bufferedWriter,
        String value)
        throws IOException {
        writeString(bufferedWriter, value, true);
    }

    private static void writeString(BufferedWriter bufferedWriter, String value)
        throws IOException {
        writeString(bufferedWriter, value, false);
    }

    private static void writeString(
        BufferedWriter bufferedWriter,
        String value,
        boolean isFirst) throws IOException {
        if (isFirst == false) {
            bufferedWriter.write(CSV_SEPARATER);
        }
        bufferedWriter.write(CSV_QUOTE);
        if (value != null) {
            bufferedWriter.write(value.replace(
                CSV_SINGLE_QUOTE,
                CSV_DOUBLE_QUOTE));
        }
        bufferedWriter.write(CSV_QUOTE);
    }

    private static void writeBoolean(
        BufferedWriter bufferedWriter,
        boolean value) throws IOException {
        bufferedWriter.write(CSV_SEPARATER);
        bufferedWriter.write(Boolean.toString(value));
    }

    private static void writeDouble(
        BufferedWriter bufferedWriter,
        double value) throws IOException {
        bufferedWriter.write(CSV_SEPARATER);
        bufferedWriter.write(Double.toString(value));
    }

    private static void writeInteger(
        BufferedWriter bufferedWriter,
        int value) throws IOException {
        bufferedWriter.write(CSV_SEPARATER);
        bufferedWriter.write(Integer.toString(value));
    }

    private static void writeResult(
        BufferedWriter bufferedWriter,
        Result result) throws IOException {
        try {
            result.setForWrite();
            for (Field field : Result.class.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    writeString(bufferedWriter, (String) field.get(result));
                } else if (field.getType() == int.class) {
                    writeInteger(bufferedWriter, field.getInt(result));
                } else if (field.getType() == boolean.class) {
                    writeBoolean(bufferedWriter, field.getBoolean(result));
                } else if (field.getType() == double.class) {
                    writeDouble(bufferedWriter, field.getDouble(result));
                } else if (field.getType() == float.class) {
                    writeDouble(bufferedWriter, field.getDouble(result));
                }
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Comparison.class.getName()).log(
                Level.SEVERE,
                null,
                ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Comparison.class.getName()).log(
                Level.SEVERE,
                null,
                ex);
        }
    }

    private static void writeHeaders(
        BufferedWriter bufferedWriter,
        ArrayList<String> providerNames) throws IOException {
        writeFirstString(bufferedWriter, "User-Agent");
        for (String providerName : providerNames) {
            for (Field field : Result.class.getDeclaredFields()) {
                if (field.getType() == String.class ||
                    field.getType() == int.class ||
                    field.getType() == boolean.class ||
                    field.getType() == double.class ||
                    field.getType() == float.class) {
                    writeString(
                        bufferedWriter,
                        providerName + "-" + field.getName());
                }
            }
        }
        bufferedWriter.newLine();
    }

    private static void writeResults(
        LinkedList<Request> requests,
        ArrayList<String> providerNames,
        String outputFile) throws IOException {
        System.out.printf("Writing comparison CSV file: %s\r\n",
            outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(
            new FileWriter(outputFile));
        writeHeaders(bufferedWriter, providerNames);
        for (Request request : requests) {
            writeFirstString(bufferedWriter, request.userAgentString);
            for (Result result : request.results) {
                writeResult(bufferedWriter, result);
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    private static void runFiftyOneDegreesPatternMemory(
        ArrayList<String> providerNames,
        LinkedList<Request> requests,
        int cacheSize,
        int numberOfThreads,
        String dataFile) throws Exception {
        if (dataFile != null && new File(dataFile).exists()) {
            providerNames.add("51D");
            System.out.printf("Processing 51Degrees file: %s\r\n",
                dataFile);
            long startTime = System.currentTimeMillis();
            ComparisonProvider fodf = new FiftyOneDegreesPatternMemoryProvider(
                dataFile,
                cacheSize);
            long endTime = System.currentTimeMillis();
            System.out.printf(
                "Initialised 51Degrees file provider in %d ms\r\n",
                endTime - startTime);
            try {
                runComparison(fodf, requests, numberOfThreads);
            } finally {
                fodf.close();
            }
        }
    }

    private static void runFiftyOneDegreesHashMemory(
        ArrayList<String> providerNames,
        LinkedList<Request> requests,
        int numberOfThreads,
        String dataFile) throws Exception {
        if (dataFile != null && new File(dataFile).exists()) {
            providerNames.add("51DHashTrie");
            System.out.printf("Processing 51Degrees Hash Trie File: %s\r\n",
                dataFile);
            long startTime = System.currentTimeMillis();
            ComparisonProvider fodhtf = new FiftyOneDegreesHashMemoryProvider(
                dataFile);
            long endTime = System.currentTimeMillis();
            System.out.printf(
                "Initialised 51Degrees Hash Trie file provider in %d ms\r\n",
                endTime - startTime);
            try {
                runComparison(fodhtf, requests, numberOfThreads);
            } finally {
                fodhtf.close();
            }
        }
    }

    private static void runWurfl(
        ArrayList<String> providerNames,
        LinkedList<Request> requests,
        int cacheSize,
        int numberOfThreads,
        String dataFile) throws IOException, InterruptedException {
        if (dataFile != null && new File(dataFile).exists()) {
            providerNames.add("WURFL");
            System.out.printf("Processing WURFL: %s\r\n",
                dataFile);
            long startTime = System.currentTimeMillis();
            ComparisonProvider wurfl = new WurflProvider(
                dataFile,
                cacheSize);
            long endTime = System.currentTimeMillis();
            System.out.printf(
                "Initialised WURFL in %d ms\r\n",
                endTime - startTime);
            try {
                runComparison(wurfl, requests, numberOfThreads);
            } finally {
                wurfl.close();
            }
        }
    }

    private static void runDeviceAtlas(
        ArrayList<String> providerNames,
        LinkedList<Request> requests,
        int cacheSize,
        int numberOfThreads,
        String dataFile) throws IOException, InterruptedException {
        if (dataFile != null && new File(dataFile).exists()) {
            providerNames.add("DA");
            System.out.printf("Processing DeviceAtlas: %s\r\n",
                dataFile);
            long startTime = System.currentTimeMillis();
            ComparisonProvider da = new DeviceAtlasProvider(
                dataFile,
                cacheSize);
            long endTime = System.currentTimeMillis();
            System.out.printf(
                "Initialised DeviceAtlas in %d ms\r\n",
                endTime - startTime);
            try {
                runComparison(da, requests, numberOfThreads);
            } finally {
                da.close();
            }
        }
    }

    private static void runBrowserCaps(
        ArrayList<String> providerNames,
        LinkedList<Request> requests,
        int cacheSize,
        int numberOfThreads) throws IOException, InterruptedException {
        providerNames.add("BC");
        System.out.println("Processing Browscap");
        ComparisonProvider bc = null;
        try {
            long startTime = System.currentTimeMillis();
            bc = new BrowsCapProvider(cacheSize);
            long endTime = System.currentTimeMillis();
            System.out.printf(
                "Initialised Browscap provider in %d ms\r\n",
                endTime - startTime);
            runComparison(bc, requests, numberOfThreads);
        } catch (Exception ex) {
            Logger.getLogger(Comparison.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bc.close();
        }
    }

    /**
     * Runs one of more comparison operations writing the results back out
     * to the list of requests.
     *
     * @param userAgentsFile      path to csv file containing User-Agents
     * @param fiftyOneDegreesFile path to 51Degrees provider data file
     * @param wurflFile           path to WURFL data file
     * @param deviceAtlasFile     path to DeviceAtlas data file
     * @param csvOutputFile       path to csv output file for storing results of comparison
     * @param numberOfThreads     number of concurrent threads
     */
    private static void runComparisons(
        String userAgentsFile,
        String fiftyOneDegreesFile,
        String fiftyoneDegreesHashTrieFile,
        String wurflFile,
        String deviceAtlasFile,
        String csvOutputFile,
        int numberOfThreads) throws Exception {

        // Check that the User-Agent file exists.
        if (new File(userAgentsFile).exists() == false) {
            throw new IllegalArgumentException(String.format(
                "File %s does not exist",
                userAgentsFile));
        }

        // Initialise the data for all the different providers.
        ArrayList<String> providerNames = new ArrayList<String>();
        LinkedList<Request> requests = readUserAgents(userAgentsFile);
        int cacheSize = (int) (requests.size() * CACHE_MULTIPLIER);

        // Call each of the providers in turn recording results and provider
        // names where a valid file has been provided.
        runBrowserCaps(
            providerNames,
            requests,
            cacheSize,
            numberOfThreads);
        runFiftyOneDegreesPatternMemory(
            providerNames,
            requests,
            cacheSize,
            numberOfThreads,
            fiftyOneDegreesFile);
        runFiftyOneDegreesHashMemory(
            providerNames,
            requests,
            numberOfThreads,
            fiftyoneDegreesHashTrieFile);
        runWurfl(
            providerNames,
            requests,
            cacheSize,
            numberOfThreads,
            wurflFile);
        runDeviceAtlas(
            providerNames,
            requests,
            cacheSize,
            numberOfThreads,
            deviceAtlasFile);

        // Write the results to a CSV file.
        writeResults(requests, providerNames, csvOutputFile);
    }

    /**
     * Instantiates this class and starts
     * {@link #runComparisons(String, String, String, String, String, String, int)}
     * with parameters from the command line. When run from the command line
     * the first and last file arguments must be a file of User-Agents and the
     * file that the comparison results will be written to. Files between the
     * first and last file are optional and provide the FiftyOneDegrees,
     * WURFL and DeviceAtlas data files. A final optional argument of type
     * integer can be provided to determine the number of concurrent threads
     * to use during processing if the provider supports multi threaded
     * operation.
     *
     * @param args command line arguments.
     * @throws IOException          if the data file cannot be accessed.
     * @throws InterruptedException if the comparison threads are interrupted.
     */
    public static void main(String[] args)
        throws Exception {
        int numberOfThreads = defaultNumberOfThreads;
        int existingFiles = 0;

        String hashFile = null;
        String patternFile = null;
        List<String> otherFiles = new ArrayList<>();
        for (String arg : args) {
            try {
                numberOfThreads = Integer.parseInt(arg);
            } catch (NumberFormatException ex) {
                existingFiles++;
                if (arg.contains("51Degrees")) {
                    if (arg.endsWith(".dat")) {
                        patternFile = arg;
                    } else if (arg.endsWith(".trie")) {
                        hashFile = arg;
                    }
                } else {
                    otherFiles.add(arg);
                }
            }
        }

        if (patternFile == null) {
            patternFile = getDefaultFilePath("51Degrees-LiteV3.2.dat").getAbsolutePath();
        }
        if (hashFile == null) {
            hashFile = getDefaultFilePath("51Degrees-LiteV3.4.trie").getAbsolutePath();
        }

        if (otherFiles.size() < 2) {
            throw new IllegalArgumentException(
                "At least 2 valid files need to be provided");
        }

        /**
         * Execute the comparison now the data has been gathered.
         *
         * NOTE: Modify the following code to test with Device Atlas,
         * WURFL, and 51Degrees Hash Trie, replace null with the path
         * to the corresponding data file.
         **/
        runComparisons(
            otherFiles.get(0), // The file containing User-Agents
            patternFile, // The 51Degrees data file
            hashFile, // The 51Degrees Hash Trie data file
            null, // The WURFL data file
            null, // The DA data file
            otherFiles.get(otherFiles.size() - 1), // The output CSV file
            numberOfThreads);
    }

    // the average time in milliseconds for a single thread.
    public double getAverageDetectionTimePerThread() {
        return (elapsedNano.doubleValue() / 1000000) / getCount();
    }

    // the number of User-Agents included in the test.
    public int getCount() {
        return count.intValue();
    }

    public void run(
        ComparisonProvider provider,
        LinkedList<Request> requests,
        int numberOfThreads)
        throws IOException, InterruptedException {

        // initialise the comparison variables
        this.provider = provider;
        this.elapsedNano.set(0);
        this.count.set(0);

        // get the User-Agent result iterator
        ResultIterator iterator = new ResultIterator(requests);

        System.out.printf(
            "Starting processing %.0f requests from %d User-Agents\r\n",
            iterator.total,
            requests.size());

        // start multiple threads in a fixed pool
        ExecutorService executor = Executors.newFixedThreadPool(
            numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(new ComparisonRunnable(this));
        }

        // add the detections to the queue until exhausted
        long next = System.currentTimeMillis() + PROGRESS_REPORT_INTERVAL;
        while (iterator.hasNext()) {
            this.queue.put(iterator.next());
            if (System.currentTimeMillis() > next) {
                System.out.printf("%.2f%% complete\r\n",
                    iterator.getPercentageComplete() * 100);
                next += PROGRESS_REPORT_INTERVAL;
            }
        }

        this.addingComplete = true;
        executor.shutdown();
        while (executor.isTerminated() == false) {
            // Do nothing.
        }

        // output the results from the comparison to the console
        System.out.printf(
            "Average millseconds per detection per thread: %f \r\n",
            getAverageDetectionTimePerThread());
        System.out.printf(
            "Concurrent threads: %d \r\n",
            numberOfThreads);
        System.out.printf(
            "User-Agents processed: %d \r\n",
            getCount());
    }

    /**
     * Common interface supported by each of the solution vendors.
     */
    interface ComparisonProvider extends Closeable {

        /**
         * Populates the result with data found from the User-Agent.
         *
         * @param userAgent target User-Agent
         * @param result    to be populated
         * @throws Exception
         */
        void calculateResult(String userAgent, Result result) throws Exception;
    }

    static class ResultIterator implements Iterator<Result> {

        private final LinkedList<LinkedList<Result>> queue;

        private final float total;

        private float fetches;

        ResultIterator(LinkedList<Request> requests) {
            long localTotal = 0;
            SortedMap<Float, LinkedList<Result>> map =
                new TreeMap<Float, LinkedList<Result>>();

            // calculate the total number of detections to perform
            for (Request request : requests) {
                localTotal += request.frequency;
            }
            this.total = localTotal;

            // build the map of results
            for (Request request : requests) {
                float weight = (float) request.frequency / (float) localTotal;
                Result result = new Result(request);
                request.results.add(result);
                if (!map.containsKey(weight)) {
                    map.put(weight, new LinkedList<Result>());
                }
                map.get(weight).add(result);
            }

            // set the queue
            this.queue = new LinkedList<LinkedList<Result>>();
            for (Entry<Float, LinkedList<Result>> entry : map.entrySet()) {
                queue.add(entry.getValue());
            }
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public Result next() {
            LinkedList<Result> current = queue.remove();
            Result result = current.remove();
            result.count++;
            fetches++;
            if (result.count < result.request.frequency) {
                current.addLast(result);
            }
            if (current.size() > 0) {
                queue.addLast(current);
            }
            return result;
        }

        @Override
        public void remove() {

        }

        float getPercentageComplete() {
            return this.fetches / this.total;
        }
    }

    static class Result {

        /**
         * The request the result relates to.
         */
        final Request request;

        /**
         * Nano seconds spent in device detection.
         */
        final AtomicLong totalDetectionTime = new AtomicLong(0);

        /**
         * The average detection time.
         */
        double averageDetectionTimeMs;

        /**
         * True if the device is a mobile, otherwise false. Solution vendors
         * may have subtly different meta data associated with the populating
         * value.
         */
        boolean isMobile;

        /**
         * The name of the device brand, manufacturer or vendor.
         */
        String hardwareVendor;

        /**
         * The model of the device.
         */
        String hardwareModel;

        /**
         * Vendor of the browser.
         */
        String browserName;

        /**
         * Version of the browser.
         */
        String browserVersion;

        /**
         * Type of the device as reported by the detection provider.
         */
        String deviceType;

        /**
         * When available the in the solution the difference between the result
         * found and the target User-Agent. Only 51Degrees supports this value.
         */
        int difference;

        /**
         * Number of detections carried out for the related User-Agent.
         */
        int count;

        Result(Request request) {
            this.request = request;
        }

        void setForWrite() {
            averageDetectionTimeMs = (double) totalDetectionTime.get() / (double) 1000000 / (double) count;
        }
    }

    /**
     * Includes the requesting User-Agent and a list of results from each
     * of the providers used.
     */
    static class Request {

        /**
         * Target User-Agent used for the comparison.
         */
        final String userAgentString;

        /**
         * Number of times the User-Agent should be repeated in the comparison.
         */
        final int frequency;

        /**
         * Results for each of the providers. Must be in the same order for
         * every request.
         */
        final LinkedList<Result> results;

        Request(String userAgentString, int frequency) {
            this.userAgentString = userAgentString;
            this.frequency = frequency;
            results = new LinkedList<Result>();
        }
    }

    /**
     * Uncomment the following code blocks to test with WURFL. The code will
     * need modification as the comparison has not been tested with the real
     * WURFL API. The code has been constructed based on the documentation
     * available on the ScientiaMobile web site at the following location.
     * <p>
     * https://docs.scientiamobile.com/documentation/onsite/onsite-java-api
     */
    static class WurflProvider implements ComparisonProvider {

        /**
         * UNCOMMENT FOR WURFL
         **/
        // private GeneralWURFLEngine wurfl;

        WurflProvider(String dataFile, int cacheSize) {
            /** UNCOMMENT FOR WURFL **/
            /**
             this.wurfl = new GeneralWURFLEngine(dataFile);
             this.wurfl.setEngineTarget(EngineTarget.accuracy);

             // More work may be needed to determine how to setup the cache
             // and pass in the cache size value.
             this.wurfl.setCacheProvider(new LRUMapCacheProvider());

             // load method is available on API version 1.8.1.0 and above
             wurfl.load();
             **/
        }

        @Override
        public void calculateResult(String userAgent, Result result)
            throws Exception {
            /** UNCOMMENT FOR WURFL **/
            /**
             Device device = this.wurfl.getDeviceForRequest(userAgent);
             result.isMobile = device.getCapabilityAsBool("is_wireless_device");
             result.hardwareVendor = device.getCapability("brand_name");
             result.hardwareModel = device.getCapability("model_name");
             result.deviceType = device.getCapability("form_factor");
             result.difference = -1;
             **/
        }

        @Override
        public void close() throws IOException {
            /** UNCOMMENT FOR WURFL **/
            //this.wurfl = null;
        }
    }

    /**
     * Uncomment the following code blocks to test with DeviceAtlas. The code
     * will need modification as the comparison has not been tested with the
     * real DeviceAtlas API. The code has been constructed based on the
     * documentation available on the DeviceAtlas web site at the following
     * location.
     * <p>
     * https://docs.deviceatlas.com/apis/enterprise/java/2.1.1/README.DeviceApi.html
     */
    static class DeviceAtlasProvider implements ComparisonProvider {

        /**
         * UNCOMMENT FOR DEVICE ATLAS
         **/
        //private DeviceApi da;

        DeviceAtlasProvider(String dataFile, int cacheSize) {
            /** UNCOMMENT FOR DEVICE ATLAS **/
            /**
             this.da = new DeviceApi();
             this.da.loadDataFromFile(dataFile);
             **/

            // Some configuration of a User-Agent cache may be possible to
            // improve performance in the second pass.
        }

        @Override
        public void calculateResult(String userAgent, Result result)
            throws Exception {
            /** UNCOMMENT FOR DEVICE ATLAS **/
            /**
             Properties properties = this.da.getProperties(userAgent);
             result.isMobile = properties.get("mobileDevice").asBoolean();
             result.hardwareVendor = properties.get("vendor").asString();
             result.hardwareModel = properties.get("model").asString();
             result.deviceType = properties.get("primaryHardwareType").asString();
             result.difference = -1;
             **/
        }

        @Override
        public void close() throws IOException {
            /** UNCOMMENT FOR DEVICE ATLAS **/
            //this.da = null;
        }
    }

    static abstract class FiftyOneDegreesBaseProvider<T extends DeviceData>
        implements ComparisonProvider {

        private final ElementPropertyMetaData isMobile;
        private final ElementPropertyMetaData hardwareVendor;
        private final ElementPropertyMetaData hardwareModel;
        private final ElementPropertyMetaData browserName;
        private final ElementPropertyMetaData browserVersion;
        private final ElementPropertyMetaData deviceType;
        private Pipeline pipeline;
        private OnPremiseAspectEngine<T, ?> engine;

        FiftyOneDegreesBaseProvider(OnPremiseAspectEngine<T, ?> engine)
            throws Exception {
            this.engine = engine;
            this.pipeline = new PipelineBuilder()
                .addFlowElement(engine)
                .setAutoCloseElements(true)
                .build();

            Map<String, ElementPropertyMetaData> availablePropertes =
                pipeline.getElementAvailableProperties().get(engine.getElementDataKey());

            isMobile = availablePropertes.get("ismobile");
            hardwareVendor = availablePropertes.get("hardwarevendor");
            hardwareModel = availablePropertes.get("hardwaremodel");
            browserName = availablePropertes.get("browsername");
            browserVersion = availablePropertes.get("browserversion");
            deviceType = availablePropertes.get("devicetype");

            System.out.printf("51Degrees '%s %s' published '%s'\r\n",
                engine.getClass().getSimpleName(),
                engine.getDataSourceTier(),
                engine.getDataFileMetaData().getDataPublishedDateTime().toString());
        }

        /**
         * @param userAgent
         * @return
         * @throws IOException
         */
        @Override
        public void calculateResult(String userAgent, Result result)
            throws Exception {
            FlowData flowData = pipeline.createFlowData();
            flowData.addEvidence("header.user-agent",
                userAgent)
                .process();
            DeviceData device = flowData.getFromElement(engine);
            result.isMobile = device.getIsMobile().getValue();
            result.browserName = browserName != null ?
                device.getBrowserName().getValue() : UNSUPPORTED_VALUE;
            result.browserVersion = browserVersion != null ?
                device.getBrowserVersion().getValue() : UNSUPPORTED_VALUE;
            result.hardwareVendor = hardwareVendor != null ?
                device.getHardwareVendor().getValue() : UNSUPPORTED_VALUE;
            result.hardwareModel = hardwareModel != null ?
                device.getHardwareModel().getValue() : UNSUPPORTED_VALUE;
            result.deviceType = deviceType != null ?
                device.getDeviceType().getValue() : UNSUPPORTED_VALUE;
            result.difference = device.getDifference().getValue();
        }

        @Override
        public void close() throws IOException {
            try {
                pipeline.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * Loads all the data into initialised data structures. Fast with a longer
     * initialisation time.
     */
    static class FiftyOneDegreesPatternMemoryProvider
        extends FiftyOneDegreesBaseProvider {

        FiftyOneDegreesPatternMemoryProvider(String dataFile, int cacheSize)
            throws Exception {
            super(new DeviceDetectionPatternEngineBuilder()
                .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                .setUpdateMatchedUserAgent(false)
                .setAutoUpdate(false)
                .setAllowUnmatched(true)
                .setDifference(-1)
                .setUserAgentCache(cacheSize)
                .build(dataFile, false));
            System.out.println(
                "Created 51Degrees memory provider");
        }
    }

    /**
     * Loads all the data into initialised data structures. Fast with a longer
     * initialisation time.
     */
    static class FiftyOneDegreesHashMemoryProvider
        extends FiftyOneDegreesBaseProvider {

        FiftyOneDegreesHashMemoryProvider(String dataFile)
            throws Exception {
            super(new DeviceDetectionHashEngineBuilder()
                .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                .setUpdateMatchedUserAgent(false)
                .setAllowUnmatched(true)
                .setAutoUpdate(false)
                .build(dataFile, false));
            System.out.println(
                "Created 51Degrees memory provider");
        }
    }

    /**
     * Uncomment the following code blocks to test with BrowsCap. The project
     * must be built with Java 8 to use the BrowsCap library. This code is not
     * left uncommented to ensure 51Degrees can be built and released with
     * support for Java 7.
     * An MIT licence implementation of BrowsCap project.
     * <p>
     * https://github.com/blueconic/browscap-java
     */
    static class BrowsCapProvider implements ComparisonProvider {

        /** UNCOMMENT FOR BROWSCAP **/
        /**
         * private LoadingCache<String, Capabilities> cache;
         * private UserAgentParser browscap;
         **/

        BrowsCapProvider(int cacheSize) throws Exception {
            /** UNCOMMENT FOR BROWSCAP **/
            /**
             this.cache = new LruLoadingCache<>(
             cacheSize,
             new ValueLoader<String, Capabilities>() {
            @Override public Capabilities load(String key) throws IOException {
            return browscap.parse(key);
            }
            });
             this.browscap = new UserAgentService().loadParser();
             **/
        }

        @Override
        public void calculateResult(String userAgent, Result result)
            throws Exception {
            /** UNCOMMENT FOR BROWSCAP **/
            /**
             Capabilities capabilities = this.cache.get(userAgent);
             String deviceType = capabilities.getDeviceType();
             result.isMobile = deviceType.equals("Mobile Phone") ||
             deviceType.equals("Tablet");
             result.browserName = capabilities.getBrowser();
             result.browserVersion = capabilities.getBrowserMajorVersion();
             result.deviceType = capabilities.getDeviceType();
             result.hardwareVendor = UNSUPPORTED_VALUE;
             result.hardwareModel = UNSUPPORTED_VALUE;
             result.difference = -1;
             **/
        }

        @Override
        public void close() throws IOException {
            /** UNCOMMENT FOR BROWSCAP **/
            /**
             this.cache = null;
             this.browscap = null;
             **/
        }
    }

    /**
     * Encapsulates the logic that is executed in each thread of the comparison.
     */
    private static class ComparisonRunnable implements Runnable {

        // the comparison that is being executed
        private final Comparison cp;

        ComparisonRunnable(
            Comparison cp)
            throws IOException {
            this.cp = cp;
        }

        @Override
        public void run() {
            try {
                Object guard = new Object();
                Result result = cp.queue.poll(1, TimeUnit.SECONDS);
                while (result != null ||
                    cp.addingComplete == false) {
                    if (result != null) {

                        // Capture the start time for the deteciton.
                        long start = System.nanoTime();

                        synchronized (guard) {
                            // Perform the detection and record the result.
                            cp.provider.calculateResult(
                                result.request.userAgentString,
                                result);
                        }

                        // Capture the end time for subsequent output.
                        long detectionTime = System.nanoTime() - start;
                        result.totalDetectionTime.addAndGet(detectionTime);

                        // Update the total elapsed time.
                        cp.elapsedNano.addAndGet(detectionTime);

                        // Increase the number of detection performed.
                        cp.count.incrementAndGet();
                    }
                    result = cp.queue.poll(1, TimeUnit.SECONDS);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Comparison.class.getName()).log(
                    Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Comparison.class.getName()).log(
                    Level.SEVERE,
                    null,
                    ex);
            }
        }
    }
}