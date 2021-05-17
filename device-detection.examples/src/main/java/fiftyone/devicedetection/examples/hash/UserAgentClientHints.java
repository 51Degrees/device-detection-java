package fiftyone.devicedetection.examples.hash;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;

import static fiftyone.pipeline.core.Constants.*;

/**
 * @example hash/UserAgentClientHints.java
 *
 * @include{doc} example-user-agent-client-hints.txt
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/src/main/java/fiftyone/devicedetection/examples/hash/UserAgentClientHints.java).
 *
 * @include{doc} example-require-datafile.txt
 * 
 * Expected output:
 *
 * ---------------------------------------
 * This example demonstrates detection using user-agent client hints.
 * The sec-ch-ua value can be used to determine the browser of the connecting device, but not other components such as the hardware.
 * We show this by first performing detection with sec-ch-ua only.
 * We then repeat with the user-agent header only.
 * Finally, we use both sec-ch-ua and user-agent.Note that sec-ch-ua takes priority over the user-agent for detection of the browser.
 * ---------------------------------------
 * Sec-CH-UA = '"Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99"'
 * User-Agent = 'NOT_SET'
 *         Browser = Chrome 89
 *         IsMobile = No matching profiles could be found for the supplied evidence. A 'best guess' can be returned by configuring more lenient matching rules. See https://51degrees.com/documentation/_device_detection__features__false_positive_control.html
 * 
 * Sec-CH-UA = 'NOT_SET'
 * User-Agent = 'Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-G960U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/10.1 Chrome/71.0.3578.99 Mobile Safari/537.36'
 *         Browser = Samsung Browser 10.1
 *         IsMobile = true
 * 
 * Sec-CH-UA = '"Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99"'
 * User-Agent = 'Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-G960U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/10.1 Chrome/71.0.3578.99 Mobile Safari/537.36'
 *         Browser = Chrome 89
 *         IsMobile = true
 * Complete. Press enter to exit.
 * 
 */

/**
 * Getting started example.
 */
public class UserAgentClientHints extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV4.1.hash").getAbsolutePath();

        new Example(true).run(dataFile);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
    	private final String mobileUserAgent =
            "Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-G960U) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/10.1 " +
            "Chrome/71.0.3578.99 Mobile Safari/537.36";
        private final String secchua =
            "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"";

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile) throws Exception {
        	System.out.printf("Using data file at '%s'\n", dataFile);
        	System.out.println("---------------------------------------");
        	System.out.println("This example demonstrates detection " +
                "using user-agent client hints.");
            System.out.println("The sec-ch-ua value can be used to " +
                "determine the browser of the connecting device, " +
                "but not other components such as the hardware.");
            System.out.println("We show this by first performing " +
                "detection with sec-ch-ua only.");
            System.out.println("We then repeat with the user-agent " +
                "header only.");
            System.out.println("Finally, we use both sec-ch-ua and " +
                "user-agent. Note that sec-ch-ua takes priority " +
                "over the user-agent for detection of the browser.");
        	System.out.println("---------------------------------------");
            
            // Build the device detection pipeline using the builder that comes with the
            // fiftyone.devicedetection package and pass in the desired settings. Additional
            // flow elements / engines can be added before the build() method is called if
            // needed.
            try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile, false)
                //.setShareUsage(false)
                // Prefer low memory profile where all data streamed
                // from disk on-demand. Experiment with other profiles.
                //.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                .build()) {
            	// Try without a User-Agent first
                analyseClientHints(pipeline, false, true);
                System.out.println();
                // Now with just user-agent.
                analyseClientHints(pipeline, true, false);
                System.out.println();
                // Finally, perform detection with both.
                analyseClientHints(pipeline, true, true);
            }
        }
        
        private void analyseClientHints(
        	Pipeline pipeline, boolean setUserAgent, boolean setSecChUa) throws Exception{
            // Create the FlowData instance.
            try (FlowData data = pipeline.createFlowData()) {
                // Add a value for the user-agent client hints header
                // sec-ch-ua as evidence
                if (setSecChUa) {
                    data.addEvidence(EVIDENCE_QUERY_PREFIX +
                        EVIDENCE_SEPERATOR + "sec-ch-ua", secchua);
                }
                // Also add a standard user-agent if requested
                if (setUserAgent) {
                    data.addEvidence(EVIDENCE_QUERY_USERAGENT_KEY,
                        mobileUserAgent);
                }

                // Process the supplied evidence.
                data.process();
                // Get device data from the flow data.
                DeviceData device = data.get(DeviceData.class);

                AspectPropertyValue<String> browserName = device.getBrowserName();
                AspectPropertyValue<String> browserVersion = device.getBrowserVersion();
                AspectPropertyValue<Boolean> isMobile = device.getIsMobile();

                String displayCh = setSecChUa ? secchua : "NOT_SET";
                System.out.printf("Sec-CH-UA = '%s'\n", displayCh);
                String ua = setUserAgent ? mobileUserAgent : "NOT_SET";
                System.out.printf("User-Agent = '%s'\n", ua);

                // Output the Browser.
                if (browserName.hasValue() && browserVersion.hasValue()) {
                    System.out.printf("\tBrowser = %s %s\n", browserName.getValue(), browserVersion.getValue());
                }
                else if (browserName.hasValue()) {
                    System.out.printf("\tBrowser = %s (version unknown)\n", browserName.getValue());
                }
                else {
                    System.out.printf("\tBrowser = %s\n", browserName.getNoValueMessage());
                }
                
                // Output the value of the 'IsMobile' property.
                if (isMobile.hasValue()) {
                    System.out.printf("\tIsMobile = %b\n", isMobile.getValue());
                }
                else {
                    System.out.printf("\tIsMobile = %s\n", isMobile.getNoValueMessage());
                }
            }
        }
    }
}