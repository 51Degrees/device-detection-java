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

package fiftyone.devicedetection.examples.hash.automaticupdates;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.DataUpdateServiceDefault;
import fiftyone.pipeline.engines.services.HttpClient;
import fiftyone.pipeline.engines.services.HttpClientDefault;
import fiftyone.pipeline.engines.services.OnUpdateComplete;
import java.util.Date;
import org.slf4j.LoggerFactory;

/**
 * @example hash/automaticupdates/UpdatePollingInterval.java
 *
 * @include{doc} example-automatic-updates-polling-interval-hash.txt
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.shell.examples/src/main/java/fiftyone/devicedetection/examples/hash/automaticupdates/UpdatePollingInterval.java).
 *
 * @include{doc} example-require-licensekey.txt
 * @include{doc} example-require-datafile.txt
 * 
 * Expected output:
 * ```
 * Using data file at G:\Workspace\device-detection-java\device-detection-cxx\device-detection-data\51Degrees-LiteV4.1.hash
 * Initial data file published date: Wed May 13 09:39:27 BST 2020
 * The pipeline has now been set up to poll for updates every 10 seconds, a random ammount of time up to 10 seconds will be added.
 * Press a key to end the program.
 * Update completed. Status AUTO_UPDATE_SUCCESS
 * Data file published date: Wed Aug 05 09:40:14 BST 2020
 * ```
 */

/**
 * Update polling interval example.
 */
public class UpdatePollingInterval extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV4.1.hash").getAbsolutePath();
        
        String licenseKey = args.length > 1 ? args[1] : 
                "!!Your license key!!";

        if(licenseKey.startsWith("!!")) {
            System.out.println("You need a license key to run this example, "
                    + "you can obtain one by subscribing to a 51Degrees bundle: "
                    + "https://51degrees.com/pricing");
            System.in.read();
            return;
        }
        
        new Example(true).run(dataFile, licenseKey);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {

        static Pipeline pipeline;
        int updatePollingInterval = 10;
        int pollingIntervalRandomisation = 10;
        
        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile, String licenseKey) throws Exception {
            println("Using data file at " + dataFile);
            
            HttpClient httpClient = new HttpClientDefault();
            DataUpdateService dataUpdateService = new DataUpdateServiceDefault(
                    LoggerFactory.getLogger(DataUpdateServiceDefault.class.getName()),
                    httpClient);
            
            dataUpdateService.onUpdateComplete(new GetAutoUpdateStatus());
            
            // Build the device detection pipeline using the builder that comes with the
            // fiftyone.devicedetection package and pass in the desired settings to configure
            // automatic updates.
            pipeline = new DeviceDetectionPipelineBuilder(LoggerFactory.getILoggerFactory(), httpClient, dataUpdateService)
                .useOnPremise(dataFile, true)
                // For automatic updates to work you will need to provide a license key.
                // A license key can be obtained with a subscription from https://51degrees.com/pricing
                .setDataUpdateLicenseKey(licenseKey)
                // Enable automatic updates.
                .setAutoUpdate(true)
                // Set the frequency in seconds that the pipeline should
                // check for updates to data files. A recommended 
                // polling interval in a production environment is
                // around 30 minutes or 1800 seconds.   
                .setUpdatePollingInterval(updatePollingInterval)
                // Set the max ammount of time in seconds that should be 
                // added to the polling interval. This is useful in datacenter
                // applications where mulitple instances may be polling for 
                // updates at the same time. A recommended ammount in production 
                // environments is 600 seconds.
                .setUpdateRandomisationMax(pollingIntervalRandomisation)
                .build();
            
            // Get the published date of the data file from the Hash engine 
            // after building the pipeline.
            Date publishedDate = pipeline
                .getElement(DeviceDetectionHashEngine.class)
                .getDataFilePublishedDate();
            println("Initial data file published date: "+ publishedDate);

            println("The pipeline has now been set up to poll for updates every " 
                    + updatePollingInterval + " seconds, a random ammount of "
                    + "time up to " + pollingIntervalRandomisation + " seconds will be added.");
            println("Press a key to end the program.");
            System.in.read();
        }
        
        public static class GetAutoUpdateStatus implements OnUpdateComplete {

            @Override
            public void call(Object o, DataUpdateService.DataUpdateCompleteArgs duca) {
                if(pipeline != null) {
                    Date publishedDate = pipeline
                            .getElement(DeviceDetectionHashEngine.class)
                            .getDataFilePublishedDate();
                    System.out.println("Data file published date: " + publishedDate.toString());
                }
                
                System.out.println("Update completed. Status " + duca.getStatus());
            }
        }
    }
}
