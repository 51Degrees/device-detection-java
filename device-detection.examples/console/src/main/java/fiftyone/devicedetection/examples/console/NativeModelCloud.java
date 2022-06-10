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

import fiftyone.devicedetection.cloud.data.MultiDeviceDataCloud;
import fiftyone.devicedetection.cloud.flowelements.HardwareProfileCloudEngineBuilder;
import fiftyone.devicedetection.shared.Constants;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineBuilder;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.core.flowelements.PipelineBuilder;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.shared.PropertyHelper.asString;
import static fiftyone.devicedetection.examples.shared.KeyHelper.getOrSetSuperResourceKey;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

public class NativeModelCloud {
    // Example values to use when looking up device details from native model names.
    private static final String NATIVE_MODEL_1 = "SC-03L";
    private static final String NATIVE_MODEL_2 = "iPhone11,8";
    public static final String NATIVE_MODEL_EXAMPLE_RESOURCE_KEY_NAME = "SuperResourceKey";

    public static void main (String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String resourceKey = args.length > 0 ? args[0] : null;
        run(resourceKey, System.out);
    }

    public static void run(String resourceKey, OutputStream os) throws Exception {
        // This example creates the pipeline and engines in code. For a demonstration
        // of how to do this using a configuration file instead, see the TacCloud example.
        // For more information about builders in general see the documentation at
        // http://51degrees.com/documentation/_concepts__configuration__builders__index.html
        resourceKey = getOrSetSuperResourceKey(resourceKey, NATIVE_MODEL_EXAMPLE_RESOURCE_KEY_NAME);

        try (PrintWriter output = new PrintWriter(os)) {
            output.println("This example shows the details of devices " +
                    "associated with a given 'native model name'.");
            output.println("The native model name can be retrieved by " +
                    "code running on the device (For example, a mobile app).");
            output.println("For Android devices, see " +
                    "https://developer.android.com/reference/android/os/Build#MODEL");
            output.println("For iOS devices, see " +
                    "https://gist.github.com/soapyigu/c99e1f45553070726f14c1bb0a54053b#file" +
                    "-machinename-swift");
            output.println("----------------------------------------");

            // Create the pipeline and the engines we need.
            try (Pipeline pipeline = new PipelineBuilder(LoggerFactory.getILoggerFactory())
                // Create the cloud request engine and add to the pipeline.
                .addFlowElement(new CloudRequestEngineBuilder(LoggerFactory.getILoggerFactory())
                        .setResourceKey(resourceKey)
                        .build())
                // Create the hardware profile engine to process the response from the
                // request engine - and add to the pipleine
                .addFlowElement(new HardwareProfileCloudEngineBuilder(LoggerFactory.getILoggerFactory())
                        .build())
                // very important to ask pipeline to close the engines we added since
                //   we are not closing them ourselves
                .setAutoCloseElements(true)
                .build()) {
                    // Pass a native model into the pipeline and list the matching devices.
                    analyseNativeModel(NATIVE_MODEL_1, pipeline, output);
                    // Repeat for an alternative native model name.
                    analyseNativeModel(NATIVE_MODEL_2, pipeline, output);
            }
        }
    }

    static void analyseNativeModel(String nativemodel, Pipeline pipeline, PrintWriter output) throws Exception {
        // Create the FlowData instance. This is wrapped in a try with resources to ensure
        // that resources are disposed correctly.
        try (FlowData data = pipeline.createFlowData()) {
            // Add the native model key as evidence.
            data.addEvidence(Constants.EVIDENCE_QUERY_NATIVE_MODEL_KEY, nativemodel);
            // Process the supplied evidence.
            data.process();
            // Get result data from the flow data.
            MultiDeviceDataCloud result = data.get(MultiDeviceDataCloud.class);
            // The 'MultiDeviceDataCloud' object contains one or more instances
            // implementing 'DeviceData' which is the same interface used for standard device
            // detection, so we have access to all the same properties.
            output.format("Which devices are associated with the " +
                    "native model name '%s'?%n", nativemodel);
            for (DeviceData device : result.getProfiles()) {
                // output the result, taking care to deal with "no value" cases
                output.format("\t%s; %s; %s%n", asString(device.getHardwareVendor()),
                        asString(device.getHardwareName()),
                        asString(device.getHardwareModel()));

            }
        }
    }
}
/*!
 * @example @example console/NativeModel.java
 *
 * This example shows how to use the 51Degrees Cloud service to look up the details of a device
 * based on a given 'native model name'. Native model name is a string of characters that are
 * returned from a query to the device's OS.
 *
 * There are different mechanisms to get native model names for
 * [Android devices](https://developer.android.com/reference/android/os/Build#MODEL) and
 * [iOS devices](https://gist.github.com/soapyigu/c99e1f45553070726f14c1bb0a54053b#file-machinename-swift)
 *
 * Unlike other examples, use of this example requires a license key which can be purchased from [our
 * pricing page](http://51degrees.com/pricing). Once this is done, a resource key with the
 * properties required by this example can be created at [here](//configure.51degrees.com/QKyYH5XT).
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/NativeModelCloud.java).
 */