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
        "architecture. A common missing library is libatomic1 on Linux. " +
        "If this is the case, " + USE_PACKAGE_MANAGER ;

    /**
     * Message returned when the native library could not be loaded, and it is
     * obvious that libatomic is missing.
     */
    public static final String UNSATISFIED_LINK_LIBATOMIC_MESSAGE =
        "The libatomic1 library is missing. This is a required 3rd party " +
        "dependency for 51Degrees device detection. You should " +
        USE_PACKAGE_MANAGER;
}
