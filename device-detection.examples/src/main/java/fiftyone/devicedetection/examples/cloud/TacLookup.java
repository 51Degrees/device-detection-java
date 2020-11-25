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

package fiftyone.devicedetection.examples.cloud;

import fiftyone.devicedetection.cloud.data.MultiDeviceDataCloud;
import fiftyone.devicedetection.cloud.flowelements.HardwareProfileCloudEngine;
import fiftyone.devicedetection.cloud.flowelements.HardwareProfileCloudEngineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineBuilder;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.core.flowelements.PipelineBuilder;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.exceptions.NoValueException;
import fiftyone.pipeline.engines.services.HttpClient;
import fiftyone.pipeline.engines.services.HttpClientDefault;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.List;

import static fiftyone.devicedetection.shared.Constants.EVIDENCE_QUERY_TAC_KEY;
import static fiftyone.pipeline.util.StringManipulation.stringJoin;

/**
 * @example cloud/TacLookup.java
 *
 * @include{doc} example-tac-lookup-cloud.txt
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/src/main/java/fiftyone/devicedetection/examples/cloud/TacLookup.java).
 *
 * @include{doc} example-require-resourcekey.txt
 * Make sure to include the HardwareVendor, HardwareModel and HardwareName
 * properties as they are used by this example.
 *
 * Example output:
 *
 * ```
 * This example shows the details of devices associated with a given 'Type Allocation Code' or 'TAC'.
 * More background information on TACs can be found through various online sources such as Wikipedia: https://en.wikipedia.org/wiki/Type_Allocation_Code
 * ----------------------------------------
 * Which devices are associated with the TAC '86386802'?
 * Coolpad 5217 (5217)
 * Coolpad 5200 (5200)
 * Coolpad 5310 (5310)
 * Coolpad 5311 (5311)
 * Coolpad 5315 (5315)
 * Coolpad CoolPad Unknown (CoolPad Unknown)
 * Which devices are associated with the TAC '35925406'?
 * Apple iPhone 6 (A1586)
 * ```
 */

/**
 * TAC lookup example.
 */
public class TacLookup extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String resourceKey = args.length > 0 ? args[0] :
            // Obtain a resource key for free at https://configure.51degrees.com
            // Make sure to include the 'HardwareVendor' and 'HardwareModel'
            // properties as they are used by this example.
            "!!YOUR_RESOURCE_KEY!!";
        new TacLookup.Example(true).run(resourceKey);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
        private static final String TAC = "35925406";
        private static final String TAC2 = "86386802";

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String resourceKey) throws Exception {
            if (resourceKey.startsWith("!!")) {
                println("You need to create a resource key at " +
                    "https://configure.51degrees.com and paste it into this example.");
                println("Make sure to include the 'HardwareVendor', " +
                    "'HardwareName' and 'HardwareModel' properties as they " +
                    "are used by this example.");
            }
            else {
                println("This example shows the details of devices " +
                    "associated with a given 'Type Allocation Code' or 'TAC'.");
                println("More background information on TACs can be " +
                    "found through various online sources such as Wikipedia: " +
                    "https://en.wikipedia.org/wiki/Type_Allocation_Code");
                println("----------------------------------------");

                ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
                HttpClient httpClient = new HttpClientDefault();

                // Create a cloud request engine. This will make the HTTP calls to the
                // 51Degrees cloud service.
                // Add your resource key here.
                try (CloudRequestEngine cloudEngine =
                        new CloudRequestEngineBuilder(loggerFactory, httpClient)
                        .setResourceKey(resourceKey)
                        .build();
                    // Create the 'hardware-profile' cloud engine.
                    // This will expose the response from received by the cloud request engine
                    // in a more user-friendly format.
                    HardwareProfileCloudEngine hardwareProfileEngine =
                        new HardwareProfileCloudEngineBuilder(loggerFactory)
                    .build();
                    // Create the pipeline using the engines.
                    Pipeline pipeline = new PipelineBuilder(loggerFactory)
                        .addFlowElement(cloudEngine)
                        .addFlowElement(hardwareProfileEngine)
                        .build()) {
                    // Pass a TAC into the pipeline and list the matching devices.
                    analyseTac(TAC, pipeline);
                    analyseTac(TAC2, pipeline);
                }
            }
        }

        void analyseTac(String tac, Pipeline pipeline) throws Exception {
            // Create the FlowData instance. A try-with-resource block MUST be
            // used for the FlowData instance. This ensures that native resources
            // created by the device detection engine are freed.
            try (FlowData data = pipeline.createFlowData()) {
                // After creating a flowdata instance, add the TAC as evidence.
                data.addEvidence(EVIDENCE_QUERY_TAC_KEY, tac);
                // Process the supplied evidence.
                data.process();
                // The result is an array containing the details of any devices that match
                // the specified TAC.
                // The code in this example iterates through this array, outputting the
                // vendor and model of each matching device.
                MultiDeviceDataCloud result = data.get(MultiDeviceDataCloud.class);
                printf("Which devices are associated with the TAC '%s'?\n", tac);
                for (DeviceData device : result.getProfiles()) {
                    AspectPropertyValue<String> vendor = device.getHardwareVendor();
                    AspectPropertyValue<List<String>> name = device.getHardwareName();
                    AspectPropertyValue<String> model = device.getHardwareModel();

                    if (vendor.hasValue() &&
                        model.hasValue() &&
                        name.hasValue()) {
                        println("\t" + vendor.getValue() +
                            " " + stringJoin(name.getValue(), ",") +
                            " (" + model.getValue() + ")");
                    } else {
                        println(vendor.getNoValueMessage());
                    }
                }
            }
        }
    }
}
