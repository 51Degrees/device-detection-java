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

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.shared.DataFileHelper;
import fiftyone.devicedetection.examples.shared.PropertyHelper;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.util.FileFinder;

import java.util.Objects;

import static fiftyone.devicedetection.examples.shared.DataFileHelper.HIGHER_TIER_FILE_REQUIRED;
import static fiftyone.devicedetection.examples.shared.DataFileHelper.getDatafileMetaData;

/**
 * Implementation of {@link Detection} for 51Degrees.
 */
public class DetectionImplFiftyOneDegrees {
    public static final String FIFTY_ONE_DEGREES = "FiftyOneDegrees";
    public static final String ENTERPRISE_HASH_DATA_FILE_NAME = "Enterprise-HashV41.hash";

    public static class FiftyOneConfig {
        String dataFile;

        public FiftyOneConfig() throws IllegalArgumentException {
            dataFile = FileFinder.getFilePath(ENTERPRISE_HASH_DATA_FILE_NAME).getAbsolutePath();
        }
    }

    public static class FiftyOneProperties extends Detection.Properties.Base {

        FiftyOneProperties(Detection.Request request, FlowData flowData) {
            super(request);
            // transcribe properties so flowData can be freed
            DeviceData device = flowData.get(DeviceData.class);
            isMobile = PropertyHelper.asString(device.getIsMobile());
            hardwareVendor = PropertyHelper.asString(device.getHardwareVendor());
            hardwareModel = PropertyHelper.asString(device.getHardwareModel());
            browserVendor = PropertyHelper.asString(device.getBrowserVendor());
            browserVersion = PropertyHelper.asString(device.getBrowserVersion());
            deviceType = PropertyHelper.asString(device.getDeviceType());
        }

        @Override
        public String getVendorId() {
            return FIFTY_ONE_DEGREES;
        }
    }

    public static class FiftyOneSolution implements Detection.Solution {
        Pipeline pipeline;
        FiftyOneConfig config;


        @Override
        public void initialise(int numberOfThreads) throws Exception {
            try {
                this.config = new FiftyOneConfig();
                DataFileHelper.DatafileInfo metadata = getDatafileMetaData(config.dataFile);
                if(metadata.getTier().equals("Lite")){
                    throw new IllegalStateException(HIGHER_TIER_FILE_REQUIRED);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Could not find Enterprise Data file", e);
            }

            pipeline = new DeviceDetectionPipelineBuilder()
                    .useOnPremise(this.config.dataFile, false)
                    .setPerformanceProfile(Constants.PerformanceProfiles.MaxPerformance)
                    .setUsePerformanceGraph(true)
                    .setUsePredictiveGraph(false)
                    .setShareUsage(false)
                    .setAutoUpdate(false)
                    .setConcurrency(numberOfThreads)
                    .setProperty("hardwareVendor")
                    .setProperty("hardwareModel")
                    .setProperty("browserVendor")
                    .setProperty("browserVersion")
                    .setProperty("isMobile")
                    .setProperty("deviceType")
                    .build();
        }

        @Override
        public Detection.Properties detect(Detection.Request request) throws Exception {
            try (FlowData flowData = pipeline.createFlowData()){
                flowData.addEvidence(request.getEvidence()).process();
                return new FiftyOneProperties(request, flowData);
            }
        }

        @Override
        public String getVendorId() {
            return FIFTY_ONE_DEGREES;
        }

        @Override
        public void close() throws Exception {
            if (Objects.nonNull(pipeline)) {
                pipeline.close();
            }
        }
    }
}
