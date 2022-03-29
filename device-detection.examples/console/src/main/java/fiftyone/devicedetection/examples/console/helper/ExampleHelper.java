package fiftyone.devicedetection.examples.console.helper;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExampleHelper {
    static Logger logger = LoggerFactory.getLogger(ExampleHelper.class);

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


    public static List<Map<String, String>> setUpEvidence() {
        Map<String, String> evidence1 = new HashMap<>();
        evidence1.put("header.user-agent",
                "Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-G960U) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "SamsungBrowser/10.1 Chrome/71.0.3578.99 Mobile " +
                        "Safari/537.36");
        Map<String, String> evidence2 = new HashMap<>();
        evidence2.put("header.user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/78.0.3904.108 Safari/537.36");
        Map<String, String> evidence3 = new HashMap<>();
        evidence3.put("header.user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/98.0.4758.102 Safari/537.36");
        evidence3.put("header.sec-ch-ua-mobile", "?0");
        evidence3.put("header.sec-ch-ua",
                "\" Not A; Brand\";v=\"99\", \"Chromium\";v=\"98\", " +
                        "\"Google Chrome\";v=\"98\"");
        evidence3.put("header.sec-ch-ua-platform", "\"Windows\"");
        evidence3.put("header.sec-ch-ua-platform-version", "\"14.0.0\"");
        List<Map<String, String>> evidence = new ArrayList<>();
        evidence.add(evidence1);
        evidence.add(evidence2);
        evidence.add(evidence3);

        return evidence;
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
     * Obtain a resource key either from environment variable or from a property.
     */
    public static String getResourceKey() {
        String resourceKey = System.getenv("ResourceKey");
        if (Objects.isNull(resourceKey)) {
            resourceKey = System.getProperty("ResourceKey");
        }
        return resourceKey;
    }

    public static void checkResourceKey(String resourceKey) {
        if (Objects.isNull(resourceKey) || resourceKey.isEmpty()) {
            logger.error(
                    "A resource key must be provided. \nFor more details see " +
                            "http://51degrees.com/documentation/4.3/_info__resource_keys.html.\n" +
                            "A free resource key with the properties required by this example can be " +
                            "configured at https://configure.51degrees.com/jqz435Nc. \n" +
                            "Once you have obtained a resource key you can provide it as an argument to this " +
                            "program, as an environment variable or system property named \"ResourceKey\".");
        }
    }
}
