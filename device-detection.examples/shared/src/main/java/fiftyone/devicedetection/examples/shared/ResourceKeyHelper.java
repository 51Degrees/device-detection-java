package fiftyone.devicedetection.examples.shared;

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
        Base64.Decoder decoder = Base64.getUrlDecoder();
        try {
            return Objects.isNull(resourceKeyValue) ||
                    resourceKeyValue.trim().length() < 19 ||
                    decoder.decode(resourceKeyValue).length < 14;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }


    /**
     * Obtain a resource key either from environment variable or from a property.
     */
    public static String getOrSetTestResourceKey() {
        return getOrSetTestResourceKey(null);
    }

    public static String getOrSetTestResourceKey(String value) {
        return getOrSetResourceKey(value, TEST_RESOURCE_KEY,
                "A free resource key configured with the " +
                        "properties required by this example may be obtained from " +
                        "https://configure.51degrees.com/jqz435Nc ");
    }
    /**
     * Obtain a resource key from the passed argument,
     * from environment variable or from a property. Store
     * as System Property TEST_RESOURCE_KEY
     */
    public static String getOrSetResourceKey(String value, String variableName,
                                                 String errorMessage) {
        if (Objects.isNull(value)) {
            value = getNamedResourceKey(variableName);

        }
        if (isInvalidResourceKey(value)) {
            logger.error("\nTo access Cloud Services you must supply a " +
                    "\"ResourceKey\" in one of the following ways: \n - in the " +
                    "configuration file of an example,\n - as a command line parameter of a " +
                    "runnable example,\n - as an Environment Variable named \"\u001B[36m{}\u001B[0m\"," +
                    "\n - as a System Property named \"\u001B[36m{}\u001B[0m\").", variableName,
                    variableName);
            logger.error(errorMessage);
            throw new IllegalStateException("\"" + value + "\" is not a valid resource key");
        }

        // capture the passed parameter for next time called
        System.setProperty(variableName, value);
        return value;
    }

    public static String getOrSetSuperResourceKey(String value, String variablename) {
        return getOrSetResourceKey(value, variablename, "TAC lookup and Native Model are not " +
                "available as a free service.\nThis means " +
                "that you will first need a license key, which can be purchased " +
                "from our pricing page: http://51degrees.com/pricing. \nOnce this is " +
                "done, a resource key with the properties required by this example " +
                "can be created at https://configure.51degrees.com/QKyYH5XT. ");
    }
}
