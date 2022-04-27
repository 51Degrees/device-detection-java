package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.ExampleTestHelper;
import fiftyone.devicedetection.shared.testhelpers.ResourceKeyHelper;
import org.junit.Test;

public class GettingStartedCloudTest {
    @Test
    public void gettingStartedCloudTest() throws Exception {
        GettingStartedCloud.run(ResourceKeyHelper.getResourceKey(),
                ExampleTestHelper.setUpEvidence(), System.out);
    }
}