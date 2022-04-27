package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.examples.shared.ExampleTestHelper;
import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import org.junit.Test;

public class GettingStartedOnPremTest {

    @Test
    public void gettingStartedOnPremTest() throws Exception {
        GettingStartedOnPrem.run(FileUtils.LITE_HASH_DATA_FILE_NAME,
                ExampleTestHelper.setUpEvidence(), System.out);
    }
}