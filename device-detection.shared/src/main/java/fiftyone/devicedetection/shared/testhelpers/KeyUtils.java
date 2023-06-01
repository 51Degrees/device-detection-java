/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2023 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 * (EUPL) v.1.2 and is subject to its terms as set out below.
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

package fiftyone.devicedetection.shared.testhelpers;

import java.util.Base64;
import java.util.Objects;

/**
 * Helpers to obtain keys from the environment
 */
public class KeyUtils {
    /**
     * Obtain a key either from environment variable or from a property.
     * <p>
     * Try resource key as env var, then as upper case env var, the system property
     */
    public static String getNamedKey(String keyName) {
        String resourceKey = System.getenv(keyName);
        if (Objects.isNull(resourceKey)) {
            resourceKey = System.getenv(keyName.toUpperCase());
            if (Objects.isNull(resourceKey)) {
                resourceKey = System.getProperty(keyName);
            }
        }
        return resourceKey;
    }

    /**
     * Evaluate whether a key might be valid
     * @param keyValue value to test
     * @return boolean
     */
    public static boolean isInvalidKey(String keyValue){
        Base64.Decoder decoder = Base64.getUrlDecoder();
        try {
            return Objects.isNull(keyValue) ||
                    keyValue.trim().length() < 19 ||
                    decoder.decode(keyValue).length < 14;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
