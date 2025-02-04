/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2025 51 Degrees Mobile Experts Limited, Davidson House,
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

package fiftyone.devicedetection.hash.engine.onpremise.interop;

import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.VectorStringSwig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Static methods to help interoperability through the SWIG layer to C++.
 */
public class Swig {
    /**
     * Copy the values in the native string vector to a {@link List}.
     * @param vector native instance to copy
     * @return list with values from the vector
     */
    public static List<String> asList(VectorStringSwig vector) {
        List<String> result = new ArrayList<>();
        long size = vector.size();
        for (long i = 0; i < size; i++) {
            result.add(vector.get((int) i));
        }
        return result;
    }

    /**
     * The same as {@link #asList(VectorStringSwig)} but makes the returned list
     * unmodifiable.
     * @param vector native instance to copy
     * @return unmodifiable list with values from the vector
     */
    public static List<String> asUnmodifiableList(VectorStringSwig vector) {
        return Collections.unmodifiableList(asList(vector));
    }

    /**
     * Copy the values in the {@link List} to a new native string vector.
     * @param list the list to copy
     * @return new native vector with values from the list
     */
    public static VectorStringSwig asVector(List<String> list) {
        VectorStringSwig vector = new VectorStringSwig();
        vector.addAll(list);
        return vector;
    }

    /**
     * Convert a string to a byte array, including the null terminator.
     * This differs from the .getBytes method by adding the null terminator.
     * @param str string to convert
     * @return byte array containing the input string
     */
    public static byte[] asBytes(String str) {
        // The Arrays.copyOf method pads with zeros, so there is no need to
        // manually set the null terminator.
        byte[] result = Arrays.copyOf(
            str.getBytes(StandardCharsets.US_ASCII),
            str.length() + 1);
        return result;
    }
}
