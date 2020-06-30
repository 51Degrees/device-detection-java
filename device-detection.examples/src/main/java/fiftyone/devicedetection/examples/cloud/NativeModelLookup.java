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

import static fiftyone.devicedetection.shared.Constants.EVIDENCE_QUERY_NATIVE_MODEL_KEY;
import static fiftyone.pipeline.util.StringManipulation.stringJoin;

/**
 * @example cloud/NativeModelLookup.java
 *
 * Example of using the 51Degrees cloud service to lookup details of a device
 * based on its native model name.
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/src/main/java/fiftyone/devicedetection/examples/cloud/NativeModelLookup.java).
 *
 * To run this example, you will need to create a **resource key**.
 * The resource key is used as short-hand to store the particular set of
 * properties you are interested in as well as any associated license keys
 * that entitle you to increased request limits and/or paid-for properties.
 *
 * You can create a resource key using the 51Degrees [Configurator](https://configure.51degrees.com).
 * Make sure to include the HardwareVendor, HardwareModel and HardwareName
 * properties as they are used by this example.
 *
 * Create a cloud request engine. This will make the HTTP calls to the
 * 51Degrees cloud service.
 * Add your resource key here.
 *
 * ```
 *
 * CloudRequestEngine cloudEngine =
 *     new CloudRequestEngineBuilder(loggerFactory, httpClient)
 *     .setResourceKey(resourceKey)
 *     .build();
 *
 * ```
 *
 * Create the 'hardware-profile' cloud engine.
 * This will expose the response from received by the cloud request engine
 * in a more user-friendly format.
 *
 * ```
 *
 * HardwareProfileCloudEngine hardwareProfileEngine =
 *     new HardwareProfileCloudEngineBuilder(loggerFactory)
 *     .build();
 *
 * ```
 *
 * Build a pipeline with engines that we've created
 *
 * ```
 *
 * Pipeline pipeline = new PipelineBuilder(loggerFactory)
 *     .addFlowElement(cloudEngine)
 *     .addFlowElement(hardwareProfileEngine)
 *     .build();
 *
 * ```
 *
 * After creating a flowdata instance, add the native model name as evidence.
 *
 * ```
 *
 * flowData.addEvidence("query.nativemodel", nativemodel);
 *
 * ```
 *
 * The result is an array containing the details of any devices that match
 * the specified native model name.
 * The code in this example iterates through this array, outputting the
 * vendor and model of each matching device.
 *
 * ```
 *
 * for (DeviceData device : result.getProfiles()) {
 *     AspectPropertyValue<String> vendor = device.getHardwareVendor();
 *     AspectPropertyValue<List<String>> name = device.getHardwareName();
 *     AspectPropertyValue<String> model = device.getHardwareModel();
 *
 *     if (vendor.hasValue() &&
 *         model.hasValue() &&
 *         name.hasValue()) {
 *         println("\t" + vendor.getValue() +
 *         " " + stringJoin(name.getValue(), ",") +
 *         " (" + model.getValue() + ")");
 *     }
 *     else {
 *         println(vendor.getNoValueMessage());
 *     }
 * }
 *
 * ```
 *
 * Example output:
 *
 * ```
 * This example finds the details of devices from the 'native model name'.
 * The native model name can be retrieved by code running on the device (For example, a mobile app).
 * For Android devices, see https://developer.android.com/reference/android/os/Build#MODEL
 * For iOS devices, see https://gist.github.com/soapyigu/c99e1f45553070726f14c1bb0a54053b#file-machinename-swift
 * ----------------------------------------
 * Which devices are associated with the native model name 'SC-03L'?
 * Samsung Galaxy S10 (SC-03L)
 * Which devices are associated with the native model name 'iPhone11,8'?
 * Apple iPhone XR (iPhone XR)
 * Apple iPhone XR (A1984)
 * Apple iPhone XR (A2105)
 * Apple iPhone XR (A2106)
 * Apple iPhone XR (A2107)
 * Apple iPhone XR (A2108)
 * ```
 */
public class NativeModelLookup extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String resourceKey = args.length > 0 ? args[0] :
            // Obtain a resource key for free at https://configure.51degrees.com
            // Make sure to include the 'HardwareVendor' and 'HardwareModel'
            // properties as they are used by this example.
            "!!YOUR_RESOURCE_KEY!!";
        new NativeModelLookup.Example(true).run(resourceKey);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
        private static final String nativemodel1 = "SC-03L";
        private static final String nativemodel2 = "iPhone11,8";

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
                    "associated with a given 'native model name'.");
                println("The native model name can be retrieved by " +
                    "code running on the device (For example, a mobile app).");
                println("For Android devices, see " +
                    "https://developer.android.com/reference/android/os/Build#MODEL");
                println("For iOS devices, see " +
                    "https://gist.github.com/soapyigu/c99e1f45553070726f14c1bb0a54053b#file-machinename-swift");
                println("----------------------------------------");

                ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
                HttpClient httpClient = new HttpClientDefault();

                // Create the cloud request engine
                try (CloudRequestEngine cloudEngine =
                        new CloudRequestEngineBuilder(loggerFactory, httpClient)
                        .setResourceKey(resourceKey)
                        .build();
                     // Create the hardware profile engine to process the
                     // response from the request engine.
                     HardwareProfileCloudEngine hardwareProfileEngine =
                         new HardwareProfileCloudEngineBuilder(loggerFactory)
                     .build();
                     // Create the pipeline using the engines.
                     Pipeline pipeline = new PipelineBuilder(loggerFactory)
                        .addFlowElement(cloudEngine)
                        .addFlowElement(hardwareProfileEngine)
                        .build()) {
                    // Pass a TAC into the pipeline and list the matching devices.
                    analyseNativeModel(nativemodel1, pipeline);
                    analyseNativeModel(nativemodel2, pipeline);
                }
            }
        }

        void analyseNativeModel(String nativeModel, Pipeline pipeline) throws NoValueException {
            // Create the FlowData instance.
            FlowData data = pipeline.createFlowData();
            // Add the native model key as evidence.
            data.addEvidence(EVIDENCE_QUERY_NATIVE_MODEL_KEY, nativeModel);
            // Process the supplied evidence.
            data.process();
            // Get result data from the flow data.
            MultiDeviceDataCloud result = data.get(MultiDeviceDataCloud.class);
            printf("Which devices are associated with the " +
                "native model name '%s'?\n", nativeModel);
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
                }
                else {
                    println(vendor.getNoValueMessage());
                }
            }
        }
    }
}
