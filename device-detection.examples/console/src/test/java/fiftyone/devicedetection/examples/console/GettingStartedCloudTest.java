package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.ExampleTestHelper;
import fiftyone.devicedetection.examples.shared.ResourceKeyHelper;

import org.junit.Test;

public class GettingStartedCloudTest {
    @Test
    public void gettingStartedCloudTest() throws Exception {
        GettingStartedCloud.run(ResourceKeyHelper.getOrSetTestResourceKey(),
                ExampleTestHelper.setUpEvidence(), System.out);
    }
}