package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.ResourceKeyHelper;
import org.junit.Test;

public class MetadataCloudTest {
    @Test
    public void gettingMetaDataCloudTest() throws Exception {
        MetadataCloud.run(ResourceKeyHelper.getOrSetTestResourceKey(), System.out);
    }

}