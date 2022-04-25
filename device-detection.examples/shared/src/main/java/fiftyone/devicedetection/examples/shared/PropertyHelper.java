package fiftyone.devicedetection.examples.shared;

import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class PropertyHelper {
    /**
     * Try to carry out a 'get' on a property getter, and catch a
     * {@link PropertyMissingException} to avoid the example breaking if the
     * resource key, or data file are not configured correctly by the user.
     *
     * @param supplier to use e.g. DeviceData::getIsMobile()
     * @return value
     */
    public static <T> AspectPropertyValue<T> tryGet(Supplier<AspectPropertyValue<T>> supplier) {
        try {
            return supplier.get();
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

    /**
     * Helper to get the value of a property as a string
     * @param value the property value
     * @param <T> the type
     * @return a string representation of the value or a "no value" message
     */
    public static <T> String asString(AspectPropertyValue<T> value) {
        if (value.hasValue()) {
            Object object = value.getValue();
            if (object instanceof List) {
                return ((List<?>) object).stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
            }
            return value.getValue().toString();
        }
        return "Unknown. " + value.getNoValueMessage();
    }

}
