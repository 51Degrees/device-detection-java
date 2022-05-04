package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.ResourceKeyHelper;
import org.junit.Test;

import static fiftyone.devicedetection.examples.shared.ResourceKeyHelper.getNamedResourceKey;
import static org.junit.Assume.assumeFalse;

public class TacCloudTest {

    @Test
    public void run() throws Exception {
        String resourceKey = getNamedResourceKey(TacCloud.TAC_EXAMPLE_RESOURCE_KEY_NAME);
        assumeFalse("Skipping test, no resource key found",
                ResourceKeyHelper.isInvalidResourceKey(resourceKey));
        TacCloud.run(resourceKey, System.out);
    }
}