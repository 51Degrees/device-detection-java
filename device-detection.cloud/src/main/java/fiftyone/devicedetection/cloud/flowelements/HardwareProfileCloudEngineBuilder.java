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