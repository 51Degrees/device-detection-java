package fiftyone.devicedetection.hash.engine.onpremise;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;

public class Util {

    /**
     * Work around for operation of {@link DeviceDetectionHashEngine#getDataSourceTier()}
     * <p>
     * Determine if a data file is Lite or Enterprise
     * @return true if it is a lite file
     */
    public static boolean isLiteFile(DeviceDetectionHashEngine ddhe){
        // Lite files have few properties
        return ddhe.getProperties().size() < 25;
    }
}
