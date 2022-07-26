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

package fiftyone.devicedetection.examples.shared;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.engines.data.AspectEngineDataFile;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneDataFile;
import fiftyone.pipeline.util.FileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import static fiftyone.pipeline.util.FileFinder.getFilePath;

public class DataFileHelper {
    static Logger logger = LoggerFactory.getLogger(DataFileHelper.class);

public static class DatafileInfo {
        FiftyOneDataFile fileInfo;
        String tier;

        public DatafileInfo(FiftyOneDataFile fileInfo, String dataSourceTier) {
            this.fileInfo = fileInfo;
            this.tier = dataSourceTier;
        }

        public AspectEngineDataFile getFileInfo(){
            return fileInfo;
        }
        public String getTier() {
            return tier;
        }
    }
    public static DatafileInfo getDatafileMetaData(String dataFileLocation) throws Exception {
        try(DeviceDetectionHashEngine ddhe = new DeviceDetectionHashEngineBuilder()
                .setAutoUpdate(false)
                .build(dataFileLocation,false)) {
            return new DatafileInfo((FiftyOneDataFile) ddhe.getDataFileMetaData(), ddhe.getDataSourceTier());
        }
    }

    public static void logDataFileInfo(DeviceDetectionHashEngine engine) {
        // Lite or Enterprise
        String dataTier = engine.getDataSourceTier();
        // date of creation
        Date fileDate = engine.getDataFilePublishedDate();
        String dataFileLocation = engine.getDataFileMetaData().getDataFilePath();

        long daysOld = ChronoUnit.DAYS.between(fileDate.toInstant(), Instant.now());
        String displayDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(fileDate);
        logger.info("Used a '{}' data file, created {}, {} days ago, from location '{}'",
                dataTier, displayDate, daysOld, dataFileLocation);
        if (dataTier.equals("Lite")) {
            logger.warn("This example is using the 'Lite ' data file. " +
                    "This is used for illustration, and has limited " +
                    "accuracy and capabilities. Find out about the " +
                    "Enterprise data file on our pricing page: " +
                    "https://51degrees.com/pricing");
        }
        if (daysOld > 28) {
            logger.warn("This example is using a data file that is more " +
                    "than {} days old. A more recent data file " +
                    "may be needed to correctly detect the latest " +
                    "devices, browsers, etc.", daysOld);
            logger.info("The latest 'Lite' data file is available " +
                    "from the device-detection-data repository on GitHub " +
                    "https://github.com/51Degrees/device-detection-data. " +
                    "Find out about the Enterprise data file, which " +
                    "includes automatic daily updates, on our pricing " +
                    "page: https://51degrees.com/pricing");
        }
    }

    public static void cantFindDataFile(String dataFile) {
        logger.error("Could not find the data file '{}' which must be " +
                "somewhere in the project space to be found.", dataFile);
        logger.info("The latest 'Lite' data file is available " +
                "from the device-detection-data repository on GitHub " +
                "https://github.com/51Degrees/device-detection-data. " +
                "Find out about the Enterprise data file, which includes " +
                "automatic daily updates, on our pricing " +
                "page: https://51degrees.com/pricing");
    }

    /**
     * Tries to find the passed file, or if null a default file
     * @param evidenceFilename a filename to find
     * @return a File object
     * @throws Exception if the file was not found
     */

    @SuppressWarnings("RedundantThrows")
    public static File getEvidenceFile(String evidenceFilename) throws Exception {
        if (Objects.isNull(evidenceFilename)) {
            evidenceFilename = FileUtils.EVIDENCE_FILE_NAME;
        }

        File evidenceFile;
        try {
            evidenceFile = FileFinder.getFilePath(evidenceFilename);
        } catch (Exception e) {
            logger.error("Could not find evidence file {}", evidenceFilename);
            throw e;
        }
        return evidenceFile;
    }

    /**
     * Tries to find the passed file, or if null a default file
     * @param dataFilename a filename to find
     * @return a full pathname
     * @throws Exception if the file was not found
     */
    @SuppressWarnings("RedundantThrows")
    public static String getDataFileLocation(String dataFilename) throws Exception {
        if (Objects.isNull(dataFilename)) {
            dataFilename = FileUtils.getHashFileName();
        }
        String dataFileLocation;
        try {
            dataFileLocation = getFilePath(dataFilename).getAbsolutePath();
        } catch (Exception e) {
            DataFileHelper.cantFindDataFile(dataFilename);
            throw e;
        }
        return dataFileLocation;
    }
}
