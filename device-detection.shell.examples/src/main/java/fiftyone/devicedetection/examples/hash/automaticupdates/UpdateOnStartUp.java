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
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.DataUpdateService.DataUpdateCompleteArgs;
import fiftyone.pipeline.engines.services.DataUpdateServiceDefault;
import fiftyone.pipeline.engines.services.HttpClient;
import fiftyone.pipeline.engines.services.HttpClientDefault;
import fiftyone.pipeline.engines.services.OnUpdateComplete;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.LoggerFactory;

/**
 * @example hash/automaticupdates/UpdateOnStartUp.java
 *
 * @include{doc} example-automatic-updates-on-startup-hash.txt
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.shell.examples/src/main/java/fiftyone/devicedetection/examples/hash/automaticupdates/UpdateOnStartUp.java).
 *
 * @include{doc} example-require-licensekey.txt
 * @include{doc} example-require-datafile.txt
 * 
 * Expected output:
 * ```
 * Using data file at '51Degrees-LiteV4.1.hash'
 * Data file published date: 13/04/2020 00:00:00
 * Creating pipeline and updating device data.....
 * Data file published date: 02/07/2020 00:00:00
 * ```
 */

/**
 * Update on startup example.
 */
public class UpdateOnStartUp extends ProgramBase {

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

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile, String licenseKey) throws Exception {
            println("Using data file at " + dataFile);
            Date initialPublishedDate;
            
            // Create a temporary Device Detection 'Hash' Engine to check the initial published date of the data file.
            // There is no need to do this in production, it is for demonstration purposes only.
            // This also higlights the added simplicity of the Device Detection Pipeline builder.
            HttpClient httpClient = new HttpClientDefault();
            try(DataUpdateService tmpDataUpdateService = new DataUpdateServiceDefault(
                    LoggerFactory.getLogger(DataUpdateServiceDefault.class.getName()),
                    httpClient)) {
                try(DeviceDetectionHashEngine deviceDetectionHashEngine = new DeviceDetectionHashEngineBuilder(
                        LoggerFactory.getILoggerFactory(), tmpDataUpdateService).build(dataFile, true)) {
                    // Get the published date of the device data file. Engines can have multiple data files but 
                    // for the Device Detection 'Hash' engine we can guarantee there will only be one.
                    initialPublishedDate = deviceDetectionHashEngine.getDataFilePublishedDate();
                }
            }
            
            println("Data file published date: " + initialPublishedDate.toString());
            println("Creating pipeline and updating device data");
            
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                while(!executor.isShutdown()){
                    print(".");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        // do nothing
                    }
                }
            });
               
            DataUpdateService dataUpdateService = new DataUpdateServiceDefault(
                    LoggerFactory.getLogger(DataUpdateServiceDefault.class.getName()),
                    httpClient);
            try {
            	dataUpdateService.onUpdateComplete(new GetAutoUpdateStatus());
            	// Build the device detection pipeline using the builder that comes with the
            	// fiftyone.devicedetection package and pass in the desired settings to configure
            	// automatic updates.
            	Pipeline pipeline = new DeviceDetectionPipelineBuilder(
            			LoggerFactory.getILoggerFactory(), httpClient, dataUpdateService)
            			.useOnPremise(dataFile, true)
            			// For automatic updates to work you will need to provide a license key.
            			// A license key can be obtained with a subscription from https://51degrees.com/pricing
            			.setDataUpdateLicenseKey(licenseKey)
            			// Enable automatic updates.
            			.setAutoUpdate(true)
            			// Watch the data file on disk and refresh the engine 
            			// as soon as that file is updated. 
            			.setDataFileSystemWatcher(true)
            			// Enable update on startup, the auto update system 
            			// will be used to check for an update before the
            			// device detection engine is created. This will block 
            			// creation of the pipeline.
            			.setDataUpdateOnStartup(true)
            			.build();
            
            	executor.shutdownNow();
            
            	Date updatedPublishedDate = pipeline
            			.getElement(DeviceDetectionHashEngine.class)
            			.getDataFilePublishedDate();
            	
            	if(initialPublishedDate.equals(updatedPublishedDate))
            	{
            		println("There was no update available at this time.");
            	}
            	println("Data file published date: " + updatedPublishedDate.toString());
            	System.in.read();
            }
            finally {
            	// Shutdown data update service
            	dataUpdateService.close();
            }
        }
        
        class GetAutoUpdateStatus implements OnUpdateComplete {

            @Override
            public void call(Object o, DataUpdateCompleteArgs duca) {
                System.out.println("Update completed. Status " + duca.getStatus());
            }
        }
    }
}
