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
