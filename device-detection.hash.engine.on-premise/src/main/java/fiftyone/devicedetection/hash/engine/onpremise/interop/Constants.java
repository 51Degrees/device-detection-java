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

package fiftyone.devicedetection.hash.engine.onpremise.interop;

public class Constants {
    private static final String USE_PACKAGE_MANAGER =
        "use your Linux " +
        "package manager to install it on your system. If this is not " +
        "possible, you may be able to use a different library by " +
        "rebuilding the binary on the target system using the source " +
        "code and instructions available on " +
        "https://github.com/51degrees/device-detection-java.";

    /**
     * Message returned when the native library could not be loaded.
     */
    public static final String UNSATISFIED_LINK_MESSAGE =
        "The native library failed to load. This may mean that either " +
        "the library itself or one of its dependencies is not " +
        "accessible or not in the correct format for the target OS or " +
        "architecture. Common reasons: /tmp mounted with noexec flag, or libatomic is missing. " +
        "In the former case provide a different dir (with execute permissions) via `java.io.tmpdir` option. " +
        "In the latter case, " + USE_PACKAGE_MANAGER ;

    /**
     * Message returned when the native library could not be loaded, and it is
     * obvious that libatomic is missing.
     */
    public static final String UNSATISFIED_LINK_LIBATOMIC_MESSAGE =
        "The libatomic1 library is missing. This is a required 3rd party " +
        "dependency for 51Degrees device detection. You should " +
        USE_PACKAGE_MANAGER;
}
