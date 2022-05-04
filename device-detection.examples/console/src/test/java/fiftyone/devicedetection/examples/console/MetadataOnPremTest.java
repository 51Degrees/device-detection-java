package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import org.junit.Test;

public class MetadataOnPremTest {
    @Test
    public void gettingMetaDataOnPremTest() throws Exception {
        MetadataOnPrem.run(FileUtils.LITE_HASH_DATA_FILE_NAME, System.out);
    }
}