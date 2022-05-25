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

import fiftyone.devicedetection.shared.testhelpers.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class KeyHelper {
    public static final String TEST_RESOURCE_KEY = "TestResourceKey";
    static Logger logger = LoggerFactory.getLogger(KeyHelper.class);

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
            value = KeyUtils.getNamedKey(variableName);

        }
        if (KeyUtils.isInvalidKey(value)) {
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
