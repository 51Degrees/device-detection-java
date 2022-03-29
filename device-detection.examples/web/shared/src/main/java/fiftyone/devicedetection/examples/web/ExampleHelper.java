package fiftyone.devicedetection.examples.web;

import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineDefault;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;


public class ExampleHelper {
    /**
     * Try to carry out a 'get' on a property getter, and catch a
     * {@link PropertyMissingException} to avoid the example breaking if the
     * resource key, or data file are not configured correctly by the user.
     *
     * @param getter to use e.g. DeviceData::getIsMobile()
     * @return value
     */
    public static <T> AspectPropertyValue<T> tryGet(PropertyGetter<T> getter) {
        try {
            return getter.getValue();
        } catch (PropertyMissingException e) {
            String message =
                    "The property '" + e.getPropertyName() + "' is not " +
                            "available in this data file. See data file options " +
                            "<a href=\"https://51degrees.com/pricing\">here</a>";
            AspectPropertyValue<T> result = new AspectPropertyValueDefault<>();
            result.setNoValueMessage(message);
            return result;
        }
    }

    public interface PropertyGetter<T> {
        AspectPropertyValue<T> getValue();
    }

    /**
     * Helper to get the value of a property as a string
     * @param property the property value
     * @param <T> the type
     * @return a string representation of the value or a "no value" message
     */
    public static <T> String asString(AspectPropertyValue<T> property) {
        if (property.hasValue()) {
            return property.toString();
        } else {
            return property.getNoValueMessage();
        }
    }

    /**
     * Obtain a resource key either from environment variable or from a property.
     */
    public static String getResourceKey() {
        return getResourceKey(null);
    }

    /**
     * Obtain a resource key from the passed argument, from environment variable or from a property.
     */
    public static String getResourceKey(String resourceKey) {
        if (Objects.isNull(resourceKey)) {
            if (Objects.isNull(resourceKey = System.getenv("ResourceKey"))) {
                if (Objects.isNull(resourceKey = System.getProperty("ResourceKey"))) {
                    throw new IllegalStateException("\nTo access Cloud Services you must supply a " +
                            "\"ResourceKey\" in one of the following ways: \n - in the " +
                            "configuration file of an example,\n - as a command line parameter of a " +
                            "runnable example,\n - as an Environment Variable named \"ResourceKey\"," +
                            "\n - as a System Property named \"ResourceKey\"). \n " +
                            "A free resource key configured with the " +
                            "properties required by this example may be obtained from " +
                            "https://configure.51degrees.com/jqz435Nc ");
                }
            }
        } else {
            // capture the passed parameter for next time called
            System.setProperty("ResourceKey", resourceKey);
        }
        return resourceKey;
    }

    static Logger logger = LoggerFactory.getLogger(ExampleHelper.class);

    /**
     * Helper to find a resource key from one of:
     * <ul>
     *     <li>the pipeline options file specified</li>
     *     <li>an array of strings presumed to be command line args</li>
     *     <li>environment variable "ResourceKey"</li>
     *     <li>system property "ResourceKey"</li>
     * </ul>
     * @param pipelineConfig
     * @param args
     * @throws Exception
     */
    static void findResourceKey(String pipelineConfig, String[] args) throws Exception {
        OptionsHelper oh = new OptionsHelper(pipelineConfig);
        String resourceKey = oh.find("CloudRequestEngine", "ResourceKey");
        if (Objects.isNull(resourceKey) || resourceKey.startsWith("!!")){
            logger.warn("Default config file contains no resource key, trying command " +
                    "line, environment and system options");
            if (Objects.nonNull(getResourceKey(args.length > 0 ? args[0] : null))) {
                CloudRequestEngineDefault.resourceKeySupplier = ExampleHelper::getResourceKey;
                resourceKey = CloudRequestEngineDefault.resourceKeySupplier.get();
            }
        }
        logger.info("Using resource key {}", resourceKey);
    }
}
