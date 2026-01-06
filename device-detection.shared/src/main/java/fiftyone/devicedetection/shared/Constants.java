/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2026 51 Degrees Mobile Experts Limited, Davidson House,
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

package fiftyone.devicedetection.shared;

import static fiftyone.pipeline.core.Constants.EVIDENCE_QUERY_PREFIX;
import static fiftyone.pipeline.core.Constants.EVIDENCE_SEPERATOR;

public class Constants {

    /**
     * The suffix that is used to identify a TAC in evidence.
     * https://en.wikipedia.org/wiki/Type_Allocation_Code
     */
    public static final String EVIDENCE_TAC_SUFFIX = "tac";

    /**
     * The complete key for supplying a TAC as evidence.
     */
    public static final String EVIDENCE_QUERY_TAC_KEY =
        EVIDENCE_QUERY_PREFIX +
        EVIDENCE_SEPERATOR +
        EVIDENCE_TAC_SUFFIX;

    /**
     * The suffix that is used to identify a native model name in evidence.
     * This is the text returned by
     * https://developer.android.com/reference/android/os/Build#MODEL
     * for Android devices and by
     * https://gist.github.com/soapyigu/c99e1f45553070726f14c1bb0a54053b#file-machinename-swift
     * for iOS devices.
     */
    public static final String EVIDENCE_NATIVE_MODEL_SUFFIX = "nativemodel";

    /**
     * The complete key for supplying a native model name as evidence.
     */
    public static final String EVIDENCE_QUERY_NATIVE_MODEL_KEY =
        EVIDENCE_QUERY_PREFIX +
        EVIDENCE_SEPERATOR +
        EVIDENCE_NATIVE_MODEL_SUFFIX;
}
