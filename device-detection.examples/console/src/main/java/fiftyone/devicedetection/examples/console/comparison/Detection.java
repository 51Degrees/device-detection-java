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

package fiftyone.devicedetection.examples.console.comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstraction containing interfaces nad classes that allow comparison between different
 * device detection vendors. Subclass the inner interfaces for each vendor.
 */
public interface Detection {

    /**
     * The result of carrying out a benchmark on a single thread
     */
    class BenchmarkResult {
        int count;
        long elapsedMillis;
        List<Properties> properties;
        public BenchmarkResult(int limit){
            properties = new ArrayList<>(limit);
        }
    }


    /**
     * Request for detection - means HTTP headers
     */
    class Request {
        private final Map<String, String> evidence;

        public Request(Map<String, String> evidence) {
            this.evidence = evidence;
        }

        public Map<String, String> getEvidence() {
            return evidence;
        }
    }

    /**
     * The results of a detection
     */
    interface Properties {
        String getVendorId();

        Request getRequest();

        String isMobile();

        String getHardwareVendor();

        String getHardwareModel();

        String getBrowserVendor();

        String getBrowserVersion();

        String getDeviceType();

        int getCount();

        public abstract class Base implements Properties {
            private final Detection.Request request;
            String isMobile;
            String hardwareVendor;
            String hardwareModel;
            String browserVendor;
            String browserVersion;
            String deviceType;

            Base(Detection.Request request){
                this.request = request;
            }

            @Override
            public Detection.Request getRequest() {
                return request;
            }

            @Override
            public String isMobile() {
                return isMobile;
            }

            @Override
            public String getHardwareVendor() {
                return hardwareVendor;
            }

            @Override
            public String getHardwareModel() {
                return hardwareModel;
            }

            @Override
            public String getBrowserVendor() {
                return browserVendor;
            }

            @Override
            public String getBrowserVersion() {
                return browserVersion;
            }

            @Override
            public String getDeviceType() {
                return deviceType;
            }

            @Override
            public int getCount() {
                return 0;
            }
        }
    }

    /**
     * The thing that actually does the detection
     */
    interface Solution extends AutoCloseable {

        void initialise(int numberOfThreads) throws Exception;

        Properties detect(Detection.Request request) throws Exception;

        String getVendorId();
    }
}
