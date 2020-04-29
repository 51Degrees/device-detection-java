package fiftyone.devicedetection.cloud.data;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectDataBase;
import fiftyone.pipeline.engines.data.MultiProfileData;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MultiDeviceDataCloud
    extends AspectDataBase
    implements MultiProfileData<DeviceData> {
    private static final String DEVICE_LIST_KEY = "profiles";

    public MultiDeviceDataCloud(
        Logger logger,
        FlowData flowData,
        AspectEngine engine) {
        super(logger, flowData, engine);
        this.put(DEVICE_LIST_KEY, new ArrayList<DeviceData>());
    }

    public MultiDeviceDataCloud(
        Logger logger,
        FlowData flowData,
        AspectEngine engine,
        MissingPropertyService missingPropertyService) {
        super(logger, flowData, engine, missingPropertyService);
        this.put(DEVICE_LIST_KEY, new ArrayList<DeviceData>());
    }

    @Override
    public List<DeviceData> getProfiles() {
        return getDeviceList();
    }

    @Override
    public void addProfile(DeviceData profile) {
        getDeviceList().add(profile);
    }

    private List<DeviceData> getDeviceList() {
        return (List<DeviceData>)this.get(DEVICE_LIST_KEY);
    }
}
