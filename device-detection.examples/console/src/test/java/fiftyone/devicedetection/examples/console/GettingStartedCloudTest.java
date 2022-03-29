package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.console.helper.ExampleHelper;
import fiftyone.devicedetection.shared.testhelpers.ResourceKeyHelper;
import org.junit.Test;

public class GettingStartedCloudTest {
    @Test
    public void gettingStartedCloudTest() throws Exception {
        GettingStartedCloud.run(ResourceKeyHelper.getResourceKey(), ExampleHelper.setUpEvidence(), System.out);
    }
}