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

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.shared.DataFileHelper;
import fiftyone.devicedetection.shared.testhelpers.KeyUtils;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.DataUpdateServiceDefault;
import fiftyone.pipeline.engines.services.OnUpdateComplete;
import fiftyone.pipeline.util.FileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Scanner;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.shared.DataFileHelper.getDatafileMetaData;
import static fiftyone.devicedetection.shared.testhelpers.FileUtils.ENTERPRISE_HASH_DATA_FILE_NAME;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

/*!
 * @example console/UpdateDataFile.java
 * This example illustrates various parameters that can be adjusted when using the on-premise
 * device detection engine, and controls when a new data file is sought and when it is loaded by
 * the device detection software.
 * <p>
 * Three main aspects are demonstrated:
 * <ol>
 *     <li>Update on Start-Up</li>
 *     <li>Filesystem Watcher</li>
 *     <li>Daily auto-update</li>
 * </ol>
 * ## License Key
 * In order to test this example you will need a 51Degrees Enterprise license which can be
 * purchased from our [pricing page](//51degrees.com/pricing/annual). Look for our "Bigger" or
 * "Biggest" options.
 * # Data Files
 * You can find out more about data files, licenses etc. at our (FAQ page)[//51degrees.com/resources/faqs]
 * ## Enterprise Data File
 * Enterprise (fully-featured) data files are typically released by 51Degrees four days a week
 * (Mon-Thu) and on-premise deployments can fetch and download those files automatically. Equally,
 * customers may choose to download the files themselves and move them into place to be detected
 * by the 51Degrees filesystem watcher.
 * ### Manual Download
 * If you prefer to download files yourself, you may do so here:
 * ```
 * https://distributor.51degrees.com/api/v2/download?LicenseKeys=<your_license_key>&Type=27&Download=True&Product=22
 * ```
 * ## Lite Data File
 * Lite data files (free-to-use, limited capabilities, no license key required) are created roughly
 * once a month and cannot be updated using auto-update, they may be downloaded from
 * (Github)[href=https://github.com/51Degrees/device-detection-data] and are included with
 * source distributions of this software.
 * # Update on Start-Up
 * You can configure the pipeline builder to download an Enterprise data file on start-up.
 * ## Pre-Requisites
 * - a license key
 * - a file location for the download
 *      - this may be an existing file - which will be overwritten
 *      - or if it does not exist must end in ".hash" and must be in an existing directory
 * ## Configuration
 * - the pipeline must be configured to use a temp file
 * ``` {java}
 *    .useOnPremise(dataFilename, true)
 * ```
 * - a DataUpdateService must be supplied
 * ``` {java}
 *      UpdateCompletionListener completionListener = new UpdateCompletionListener();
 *      try (DataUpdateService dataUpdateService = new DataUpdateServiceDefault()) {
 *          dataUpdateService.onUpdateComplete(completionListener);
 *  ...
 *      .setDataUpdateService(dataUpdateService)
 * ```
 * - update on start-up must be specified, which will cause pipeline creation to block until a
 * file is downloaded
 * ``` {java}
 *      .setDataUpdateOnStartup(true)
 * ```
 * # File System Watcher
 * You can configure the pipeline builder to watch for changes to the currently loaded device
 * detection data file, and to replace the file currently in use with the new one. This is
 * useful, for example, if you wish to download and update the device detection file "manually" -
 * i.e. you would download it then drop it into place with the same path as the currently loaded
 * file. That location is checked periodically (by default every 30 mins) and this frequency can be
 * configured.
 *
 * ## Pre-Requisites
 * - a license key
 * - the file location of the existing file
 * ## Configuration
 * - the pipeline must be configured to use a temp file
 * ``` {java}
 *    .useOnPremise(dataFilename, true)
 * ```
 * - a DataUpdateService must be supplied
 * ``` {java}
 *      UpdateCompletionListener completionListener = new UpdateCompletionListener();
 *      try (DataUpdateService dataUpdateService = new DataUpdateServiceDefault()) {
 *          dataUpdateService.onUpdateComplete(completionListener);
 *  ...
 *      .setDataUpdateService(dataUpdateService)
 * ```
 * - configure the frequency with which the location is checked, in seconds (10 mins as shown)
 *                     .setUpdatePollingInterval(10*60)
 * ## Daily auto-update
 * Enterprise data files are usually created four times a week. Each data file contains a date
 * for when the next data file is expected. You can configure the pipeline so that it starts
 * looking for a newer data file after that time, by connecting to the 51Degrees distributor to
 * see if an update is available. If one is, then it is downloaded and will replace the existing
 * device detection file, which is currently in use.
 *
 * ## Pre-Requisites
 * - a license key
 * - the file location of the existing file
 * ## Configuration
 * - the pipeline must be configured to use a temp file
 * ``` {java}
 *    .useOnPremise(dataFilename, true)
 * ```
 * - a DataUpdateService must be supplied
 * ``` {java}
 *      UpdateCompletionListener completionListener = new UpdateCompletionListener();
 *      try (DataUpdateService dataUpdateService = new DataUpdateServiceDefault()) {
 *          dataUpdateService.onUpdateComplete(completionListener);
 *  ...
 *      .setDataUpdateService(dataUpdateService)
 * ```
 * - Set the frequency in seconds that the pipeline should check for updates to data files.
 * A recommended polling interval in a production environment is around 30 minutes.
 * ``` {java}
 *                     .setUpdatePollingInterval(30*60)
 * ```
 * - Set the max amount of time in seconds that should be added to the polling interval. This is
 * useful in datacenter applications where multiple instances may be polling for  updates at the
 * same time. A recommended ammount in production  environments is 600 seconds.
 * ``` {java}
            			.setUpdateRandomisationMax(10*60)
 * ```
 * # Location
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/UpdateDataFile.java).
 *
 * @include{doc} example-require-licensekey.txt
 */

/**
 * This example illustrates various parameters that can be adjusted when using the on-premise
 * device detection engine, and controls when a new data file is sought and when it is loaded by
 * the device detection software.
 * <p>
 * Three main aspects are demonstrated:
 * <ol>
 *     <li>Update on Start-Up</li>
 *     <li>Filesystem Watcher</li>
 *     <li>Daily auto-update</li>
 * </ol>
 * <p>
 * To run this example you must obtain a license key purchased from our
 * <a href="http://51degrees.com/pricing/annual">pricing page</a>. Look for our "Bigger" or
 * "Biggest" options. This license key must be supplied as a command line argument or by setting
 * an environment variable or system property called {@link UpdateDataFile#UPDATE_EXAMPLE_LICENSE_KEY_NAME}
 */
public class UpdateDataFile {

    public static final String UPDATE_EXAMPLE_LICENSE_KEY_NAME = "LicenseKey";
    public static final String DEFAULT_DATA_FILENAME =
            System.getProperty("user.dir") + File.separator +  ENTERPRISE_HASH_DATA_FILE_NAME;

    private static final Logger logger = LoggerFactory.getLogger(UpdateDataFile.class);

    static final UpdateCompletionListener completionListener = new UpdateCompletionListener();
    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String licenseKey = args.length > 0 ? args[0] : null;
        String dataFilename = args.length > 1 ? args[1] : null;
        run(dataFilename, licenseKey, true);
    }

    /**
     * Run the UpdateDataFile example. Checks that an existing "Lite" data file won't be
     * overwritten.
     * @param dataFilename the path for a datafile to download, must end in ".hash", directory
     *                     must exist. If null
     *                     {@link UpdateDataFile#DEFAULT_DATA_FILENAME} is used.
     * @param licenseKey a license key for Enterprise "on premise" data. If null environment
     *                   variable or system property name
     *                   {@link UpdateDataFile#UPDATE_EXAMPLE_LICENSE_KEY_NAME} is checked.
     * @throws Exception in the event of file not found, license key not found etc.
     */
    static void run(String dataFilename, String licenseKey, boolean interactive) throws Exception {
        logger.info("Starting example");

        // try to find a license key
        if (Objects.isNull(licenseKey)) {
            licenseKey = KeyUtils.getNamedKey(UPDATE_EXAMPLE_LICENSE_KEY_NAME);
        }
        if (Objects.isNull(licenseKey) || KeyUtils.isInvalidKey(licenseKey)) {
            logger.error("In order to test this example you will need a 51Degrees Enterprise " +
                    "license which can be obtained on a trial basis or purchased from our\n" +
                    "pricing page http://51degrees.com/pricing. You must supply the license " +
                    "key as an argument to this program, or as an environment or system variable " +
                    "named '{}'", UPDATE_EXAMPLE_LICENSE_KEY_NAME);
            throw new IllegalArgumentException("No license key available");
        }

        // work out where the downloaded file will be put, directory must exist
        if (Objects.nonNull(dataFilename)) {
            try {
                dataFilename = FileFinder.getFilePath(dataFilename).getAbsolutePath();
            } catch (Exception e) {
                if (Objects.isNull(Paths.get(dataFilename).getParent())){
                    logger.error("The directory must exist when specifying a location for a new " +
                            "file to be downloaded. Path specified was '{}'", dataFilename);
                    throw new IllegalArgumentException("Directory for new file must exist");
                }
                logger.warn("File {} not found, a file will be downloaded to that location on " +
                        "start-up", dataFilename);
            }
        }
        // no filename specified use the default
        if (Objects.isNull(dataFilename)) {
            dataFilename = Paths.get(DEFAULT_DATA_FILENAME).toFile().getCanonicalPath();
            logger.warn("No filename specified. Using default '{}' which will be downloaded to " +
                    "that location on start-up, if it does not exist already", dataFilename);
        }

        String copyDataFilename = dataFilename + ".bak";
        if (new File(dataFilename).exists()) {
            //let's check this file out
            DataFileHelper.DatafileInfo metadata = getDatafileMetaData(dataFilename);
            // and output the results
            logFileInfo(metadata);
            if (metadata.getTier().equals("Lite")) {
                logger.error("Will not download an 'Enterprise' data file over the top of " +
                        "a 'Lite' data file, please supply another location.");
                throw new IllegalArgumentException("File supplied has wrong data tier");
            }
            logger.info("Existing data file will be replaced with downloaded data file");
            logger.info("Existing data file will be copied to {}", copyDataFilename);
        }

        // do we really want to do this
        if (interactive) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please note - this example will use available downloads " +
                    "in your licensed allocation.");
            System.out.print("Do you wish to continue with this example (y)? ");
            String input = scanner.nextLine();
            if ((input.isEmpty() || input.toLowerCase().startsWith("y")) == false) {
                logger.info("Stopping example without download");
                return;
            }
        }
        logger.info("Checking file exists");
        if (Files.exists(Paths.get(dataFilename))) {
            logger.info("Existing data file copied to {}", copyDataFilename);
            Files.copy(Paths.get(dataFilename), Paths.get(copyDataFilename),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        logger.info("Creating pipeline and initiating update on start-up - please wait for that " +
                "to complete");

        // create update service and add listener
        try (DataUpdateService dataUpdateService = new DataUpdateServiceDefault()) {
            dataUpdateService.onUpdateComplete(completionListener);

            // Build the device detection pipeline  and pass in the desired settings to configure
            // automatic updates.
            try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                    // specify the filename for the data file. When using update on start-up
                    // the file need not exist, but the directory it is in must exist.
                    // Any file that is present is overwritten. Because the file will be
                    // overwritten the pipeline must be configured to copy the supplied
                    // file to a temporary file (createTempDataCopy parameter == true.
                    .useOnPremise(dataFilename, true)
                    // pass in the update listener which has been configured
                    // to notify when update complete
                    .setDataUpdateService(dataUpdateService)
                    // For automatic updates to work you will need to provide a license key.
                    // A license key can be obtained with a subscription from https://51degrees.com/pricing
                    .setDataUpdateLicenseKey(licenseKey)
                    // Enable update on startup, the auto update system
                    // will be used to check for an update before the
                    // device detection engine is created. This will block
                    // creation of the pipeline.
                    .setDataUpdateOnStartup(true)
                    // Enable automatic updates once the pipeline has started
                    .setAutoUpdate(true)
                    // Watch the data file on disk and refresh the engine
                    // as soon as that file is updated.
                    .setDataFileSystemWatcher(true)
                    // for the purposes of this example we are setting the time
                    // between checks to see if the file has changed to 1 second
                    // by default this is 30 mins
                    .setUpdatePollingInterval(1)
                    // build the pipeline
                    .build()) {

                // thread blocks till update checking is complete - or if there is an
                // exception we don't get this far
                logger.info("Update on start-up complete - status - {}", completionListener.args.getStatus());

                if (completionListener.args.getStatus().equals(
                        DataUpdateService.AutoUpdateStatus.AUTO_UPDATE_SUCCESS)) {

                    logger.info("Modifying downloaded file to trigger reload ... please wait for that" +
                            " to complete");
                    File file = new File(dataFilename).getAbsoluteFile();
                     // wait for the dataUpdateService to notify us that it has updated
                    completionListener.setComplete(false);
                    synchronized (completionListener) {
                        // it's the same file but changing the file metadata will trigger reload,
                        // demonstrating that if you download a new file and replace the
                        // existing one, then it will be loaded
                        if (file.setLastModified(System.currentTimeMillis() + 1_000_000) == false) {
                            throw new IllegalStateException("Could not modify file time, abandoning " +
                                    "example");
                        }

                        completionListener.wait(20_000);
                    }
                    logger.info("Update on file modification complete, status: {}, complete: {}",
                            completionListener.args.getStatus(), completionListener.isComplete());
                } else {
                    logger.error("Auto update was not successful, abandoning example");
                    throw new IllegalStateException("Auto update failed: " +
                            completionListener.args.getStatus());
                }

                logger.info("Finished Example");
            }
        }
    }

    private static void logFileInfo(DataFileHelper.DatafileInfo metadata) {
        logger.info("Current data file is a {} tier data file, last updated at {}, at " +
                        "location {}. Next update expected {}.", metadata.getTier(),
                simpleDateFormat.format(metadata.getFileInfo().getDataPublishedDateTime()),
                metadata.getFileInfo().getDataFilePath(),
                simpleDateFormat.format(metadata.getFileInfo().getUpdateAvailableTime()));
     }

    static class UpdateCompletionListener implements OnUpdateComplete {

        private volatile boolean complete;
        DataUpdateService.DataUpdateCompleteArgs args;

        public boolean isComplete() {
            return complete;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }

        @Override
        public void call(Object sender, DataUpdateService.DataUpdateCompleteArgs args) {
            this.args = args;
            this.complete = true;
            logger.info("notifying {} ...", args.getStatus());
            synchronized (completionListener) {
                completionListener.notify();
            }
            logger.info("... done notifying");
        }
    }
}
