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

package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.devicedetection.cloud.data.MultiDeviceDataCloud;
import fiftyone.pipeline.annotations.ElementBuilder;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.core.flowelements.FlowElement;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.flowelements.AspectEngineBuilderBase;
import fiftyone.pipeline.engines.services.MissingPropertyServiceDefault;
import org.slf4j.ILoggerFactory;

import java.util.List;


@ElementBuilder
public class HardwareProfileCloudEngineBuilder extends AspectEngineBuilderBase<
    HardwareProfileCloudEngineBuilder,
    HardwareProfileCloudEngine> {
    private final ILoggerFactory loggerFactory;

    public HardwareProfileCloudEngineBuilder(ILoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    public HardwareProfileCloudEngine build() throws Exception {
        return buildEngine();
    }

    @Override
    protected HardwareProfileCloudEngine newEngine(List<String> properties) {
        return new HardwareProfileCloudEngine(
            loggerFactory.getLogger(HardwareProfileCloudEngine.class.getSimpleName()),
            new ElementDataFactory<MultiDeviceDataCloud>() {
                @Override
                public MultiDeviceDataCloud create(FlowData flowData, FlowElement<MultiDeviceDataCloud, ?> flowElement) {
                    return new MultiDeviceDataCloud(
                        loggerFactory.getLogger(MultiDeviceDataCloud.class.getSimpleName()),
                        flowData,
                        (AspectEngine<MultiDeviceDataCloud, ?>) flowElement,
                        MissingPropertyServiceDefault.getInstance());
                }
            });
    }
}