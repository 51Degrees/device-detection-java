package fiftyone.devicedetection.examples.shared;

import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ResourceKeyHelper {
    public static final String TEST_RESOURCE_KEY = "TestResourceKey";
    static Logger logger = LoggerFactory.getLogger(ResourceKeyHelper.class);

    /**
     * Obtain a resource key either from environment variable or from a property.
     */
    public static String getNamedResourceKey(String resourceKeyName) {
        String resourceKey = System.getenv(resourceKeyName);
        if (Objects.isNull(resourceKey)) {
            resourceKey = System.getProperty(resourceKeyName);
        }
        return resourceKey;
    }

    /**
     * Evaluate whether a resource key might be valid
     * @param resourceKeyValue value to test
     * @return boolean
     */
    public static boolean isInvalidResourceKey(String resourceKeyValue){
        return Objects.isNull(resourceKeyValue) || resourceKeyValue.trim().length() == 0;
    }


    /**
     * Obtain a resource key either from environment variable or from a property.
     */
    public static String getTestResourceKey() {
        return getTestResourceKey(null);
    }

    /**
     * Obtain a resource key from the passed argument, from environment variable or from a property.
     */
    public static String getTestResourceKey(String value) {
        if (Objects.isNull(value)) {
            if (Objects.isNull(value = System.getenv(TEST_RESOURCE_KEY))) {
                if (Objects.isNull(value = System.getProperty(TEST_RESOURCE_KEY))) {
                    throw new IllegalStateException("\nTo access Cloud Services you must supply a " +
                            "\"ResourceKey\" in one of the following ways: \n - in the " +
                            "configuration file of an example,\n - as a command line parameter of a " +
                            "runnable example,\n - as an Environment Variable named " +
                            "\"TestResourceKey\"," +
                            "\n - as a System Property named \"TestResourceKey\"). \n " +
                            "A free resource key configured with the " +
                            "properties required by this example may be obtained from " +
                            "https://configure.51degrees.com/jqz435Nc ");
                }
            }
        } else {
            // capture the passed parameter for next time called
            System.setProperty(TEST_RESOURCE_KEY, value);
        }
        return value;
    }

    /**
     * Helper to find a resource key from one of:
     * <ul>
     *     <li>the pipeline options file specified</li>
     *     <li>an array of strings presumed to be command line args</li>
     *     <li>environment variable "TestResourceKey"</li>
     *     <li>system property "TestResourceKey"</li>
     * </ul>
     * @param pipelineConfig pipeline options file
     * @param args command line args
     */
    public static void findResourceKey(String pipelineConfig, String[] args) throws Exception {
        OptionsHelper oh = new OptionsHelper(pipelineConfig);
        String resourceKey = oh.find("CloudRequestEngine", "ResourceKey");
        if (Objects.isNull(resourceKey) || resourceKey.startsWith("!!")){
            logger.warn("Default config file contains no resource key, trying command " +
                    "line, environment and system options");
            if (Objects.nonNull(getTestResourceKey(args.length > 0 ? args[0] : null))) {
                CloudRequestEngineDefault.resourceKeySupplier =
                        ResourceKeyHelper::getTestResourceKey;
                resourceKey = CloudRequestEngineDefault.resourceKeySupplier.get();
            }
        }
        logger.info("Using resource key {}", resourceKey);
    }

    public static void checkResourceKey(String resourceKey) {
        if (isInvalidResourceKey(resourceKey)) {
            logger.error(
                    "A resource key must be provided. \nFor more details see " +
                            "http://51degrees.com/documentation/_info__resource_keys.html.\n" +
                            "A free resource key with the properties required by this example can be " +
                            "configured at https://configure.51degrees.com/jqz435Nc. \n" +
                            "Once you have obtained a resource key you can provide it as an argument to this " +
                            "program, as an environment variable or system property named \"TestResourceKey\".");
        }
    }

    public static void mustSupplySuperResourceKey(String resourceKeyName) {
        logger.error("No resource key specified on the command line or in " +
                "the environment variable '{}'. ", resourceKeyName);

        logger.error("TAC lookup and Native Model are not available as a free service. This means " +
                "that you will first need a license key, which can be purchased " +
                "from our pricing page: http://51degrees.com/pricing. Once this is " +
                "done, a resource key with the properties required by this example " +
                "can be created at https://configure.51degrees.com/QKyYH5XT. You " +
                "can now populate the environment variable mentioned at the start " +
                "of this message with the resource key or pass it as the first " +
                "argument on the command line.");

    }
}
