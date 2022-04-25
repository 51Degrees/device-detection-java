package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.BuilderClassPathTestRunner;
import fiftyone.devicedetection.examples.shared.ResourceKeyHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fiftyone.devicedetection.examples.shared.ResourceKeyHelper.getNamedResourceKey;
import static org.junit.Assume.assumeFalse;

@RunWith(BuilderClassPathTestRunner.class)
public class TacCloudTest {

    @Test
    public void run() throws Exception {
        String resourceKey = getNamedResourceKey(TacCloud.TAC_EXAMPLE_RESOURCE_KEY_NAME);
        assumeFalse("Skipping test, no resource key found",
                ResourceKeyHelper.isInvalidResourceKey(resourceKey));
        TacCloud.run(resourceKey, System.out);
    }
}