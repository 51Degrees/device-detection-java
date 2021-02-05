package fiftyone.devicedetection.cloud.data;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.engines.data.AspectData;
import fiftyone.pipeline.engines.data.AspectDataBase;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.data.MultiProfileData;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.services.MissingPropertyService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a list of {@link DeviceData} instances which can be returned
 * by the 51Degrees cloud service when certain evidence is provided (e.g. TAC)
 */
public class MultiDeviceDataCloud
    extends AspectDataBase
    implements MultiProfileData<DeviceData> {
    private static final String DEVICE_LIST_KEY = "profiles";

    /**
     * Constructs a new instance with a non-thread-safe, case-insensitive
     * {@link Map} as the underlying storage.
     * @param logger used for logging
     * @param flowData the {@link FlowData} instance this element data will be
     *                 associated with
     * @param engine the engine which created the instance
     */
    public MultiDeviceDataCloud(
        Logger logger,
        FlowData flowData,
        AspectEngine<? extends AspectData, ? extends AspectPropertyMetaData> engine) {
        super(logger, flowData, engine);
        this.put(DEVICE_LIST_KEY, new ArrayList<DeviceData>());
    }

    /**
     * Constructs a new instance with a non-thread-safe, case-insensitive
     * {@link Map} as the underlying storage.
     * @param logger used for logging
     * @param flowData the {@link FlowData} instance this element data will be
     *                 associated with
     * @param engine the engine which created the instance
     * @param missingPropertyService service used to determine the reason for
     *                               a property value being missing
     */
    public MultiDeviceDataCloud(
        Logger logger,
        FlowData flowData,
        AspectEngine<? extends AspectData, ? extends AspectPropertyMetaData> engine,
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

    /**
     * Internal methof to get the list of devices form the underlying data.
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<DeviceData> getDeviceList() {
        return (List<DeviceData>)this.get(DEVICE_LIST_KEY);
    }
}
