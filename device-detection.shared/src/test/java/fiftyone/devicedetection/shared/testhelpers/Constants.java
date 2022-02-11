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

package fiftyone.devicedetection.shared.testhelpers;

import java.io.File;
import java.util.Objects;

public class Constants {
    public static final int UAS_TO_TEST = 10;
    public static final String ENTERPRISE_HASH_DATA_FILE_NAME = "Enterprise-HashV41.hash";
    public static final String LITE_HASH_DATA_FILE_NAME = "51Degrees-LiteV4.1.hash";
    public static final String HASH_DATA_FILE_NAME;

    static {
        File f;
        try {
            f = Utils.getFilePath(ENTERPRISE_HASH_DATA_FILE_NAME);
        } catch (Exception e) {
            try {
                f = Utils.getFilePath(LITE_HASH_DATA_FILE_NAME);
            } catch (Exception ex) {
                f = null;
            }
        }
        HASH_DATA_FILE_NAME = Objects.nonNull(f) ? f.getPath() : null;
    }

    public static final String UA_FILE_NAME = "20000 User Agents.csv";

    public static final String[] ExcludedProperties = {"JavascriptImageOptimiser", "JavascriptBandwidth"};

    public static final String MobileUserAgent =
        "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1 like Mac OS X) " +
            "AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile" +
            "/11D167 Safari/9537.53";
    
    public static final String RESOURCE_KEY_ENV_VAR = "SuperResourceKey";
	public static final String PLATFORM_RESOURCE_KEY_ENV_VAR = "AcceptChPlatformKey";
	public static final String HARDWARE_RESOURCE_KEY_ENV_VAR = "AcceptChHardwareKey";
	public static final String BROWSER_RESOURCE_KEY_ENV_VAR = "AcceptChBrowserKey";
	public static final String NO_ACCEPTCH_RESOURCE_KEY_ENV_VAR = "AcceptChNoneKey";
}
