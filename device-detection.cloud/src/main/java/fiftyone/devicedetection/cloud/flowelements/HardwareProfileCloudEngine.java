package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.devicedetection.cloud.data.MultiDeviceDataCloud;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.cloudrequestengine.flowelements.PropertyKeyedCloudEngineBase;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.engines.services.MissingPropertyServiceDefault;

import org.slf4j.Logger;

/**
 * Engine that takes the JSON response from the {@link CloudRequestEngine} and
 * uses it populate a {@link MultiDeviceDataCloud} instance for easier consumption.
 * @see <a href="https://github.com/51Degrees/specifications/blob/main/device-detection-specification/pipeline-elements/hardware-profile-lookup-cloud.md">Specification</a>
 */
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
            MissingPropertyServiceDefault.getInstance());
    }

    @Override
    public String getElementDataKey() {
        return "hardware";
    }

    @Override
    protected void unmanagedResourcesCleanup() {

    }
}
