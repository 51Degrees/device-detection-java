package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.ResourceKeyHelper;
import org.junit.Test;

import static fiftyone.devicedetection.examples.shared.ResourceKeyHelper.getNamedResourceKey;
import static org.junit.Assume.assumeFalse;

public class NativeModelCloudTest {
    @Test
    public void run() throws Exception {
        String resourceKey =
                getNamedResourceKey(NativeModelCloud.NATIVE_MODEL_EXAMPLE_RESOURCE_KEY_NAME);
        assumeFalse("Skipping test, no resource key found",
                ResourceKeyHelper.isInvalidResourceKey(resourceKey));
        NativeModelCloud.run(resourceKey, System.out);

    }
}