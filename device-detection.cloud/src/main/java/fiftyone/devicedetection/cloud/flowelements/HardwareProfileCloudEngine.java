package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.devicedetection.cloud.data.DeviceDataCloud;
import fiftyone.devicedetection.cloud.data.MultiDeviceDataCloud;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.cloudrequestengine.flowelements.PropertyKeyedCloudEngineBase;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import org.slf4j.Logger;

public class HardwareProfileCloudEngine
    extends PropertyKeyedCloudEngineBase<MultiDeviceDataCloud, DeviceData> {

    public HardwareProfileCloudEngine(
        Logger logger,
        ElementDataFactory<MultiDeviceDataCloud> aspectDataFactory) {
        super(logger, aspectDataFactory);
    }

    @Override
    protected DeviceData createProfileData(FlowData flowData) {
        return new DeviceDataCloudInternal(
            logger,
            flowData ,
            this,
            missingPropertyService);
    }

    @Override
    public String getElementDataKey() {
        return "hardware";
    }

    @Override
    protected void unmanagedResourcesCleanup() {

    }
}
