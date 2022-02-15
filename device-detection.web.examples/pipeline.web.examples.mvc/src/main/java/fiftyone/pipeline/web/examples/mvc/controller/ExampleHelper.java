package fiftyone.pipeline.web.examples.mvc.controller;

import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import org.slf4j.LoggerFactory;

public class ExampleHelper {

    /**
     * Try to carry out a 'get' on a property getter, and catch a
     * {@link PropertyMissingException} to avoid the example breaking if the
     * resource key, or data file are not configured correctly by the user.
     * @param getter to use e.g. DeviceData.getIsMobile()
     * @return value
     */
    @SuppressWarnings("rawtypes")
    public static AspectPropertyValue tryGet(
        PropertyGetter getter) {
        try {
            return getter.getValue();
        }
        catch (PropertyMissingException e) {
            String message =
                "The property '" + e.getPropertyName() + "' is not " +
                "available in this data file. See data file options " +
                "<a href=\"https://51degrees.com/pricing\">here</a>";
            AspectPropertyValue<String> result = new AspectPropertyValueDefault<>();
            result.setNoValueMessage(message);
            return result;
        }
    }

    public interface PropertyGetter<T> {
        AspectPropertyValue<T> getValue();
    }
}
