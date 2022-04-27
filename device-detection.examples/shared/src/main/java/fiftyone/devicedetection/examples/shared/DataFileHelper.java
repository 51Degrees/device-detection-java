package fiftyone.devicedetection.examples.shared;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DataFileHelper {
    static Logger logger = LoggerFactory.getLogger(DataFileHelper.class);

    public static void logDataFileInfo(String dataFileLocation, DeviceDetectionHashEngine engine) {
        // Lite or Enterprise
        String dataTier = engine.getDataSourceTier();
        // date of creation
        Date fileDate = engine.getDataFilePublishedDate();

        long daysOld = ChronoUnit.DAYS.between(fileDate.toInstant(), Instant.now());
        String displayDate = new SimpleDateFormat("yyyy-MM-dd").format(fileDate);
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
}
