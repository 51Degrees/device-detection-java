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

package fiftyone.pipeline.uach.examples.mvc.controller;

import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import org.slf4j.LoggerFactory;

public class UACHExampleHelper {

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
            LoggerFactory.getILoggerFactory()
                    .getLogger(UACHExampleHelper.class.getSimpleName())
                    .info(message);
            AspectPropertyValue<String> result = new AspectPropertyValueDefault<>();
            result.setNoValueMessage(message);
            return result;
        }
    }

    public interface PropertyGetter<T> {
        AspectPropertyValue<T> getValue();
    }
}
